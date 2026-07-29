// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import first.robot.AprilTagAssistController.DriveCommand;
import first.robot.vision.AprilTagTarget;
import first.robot.vision.AprilTagVision;
import first.robot.vision.AprilTagVision.VisionStatus;
import java.util.Optional;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop
public class DefaultTeleMode extends PeriodicOpMode {
  // -1 accepts SystemCore's best visible target, regardless of tag ID.
  private static final int DESIRED_TAG_ID = -1;

  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();
  private final AprilTagAssistController tagController = new AprilTagAssistController();
  private final AprilTagAssistTelemetry tagTelemetry = new AprilTagAssistTelemetry();

  private AprilTagVision tagVision;
  private boolean assistFaultReported;
  private boolean previousTagAssistHeld;

  public DefaultTeleMode(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    statusTimer.restart();
    robot.disableA301s();
    tagVision = new AprilTagVision(DESIRED_TAG_ID);
    assistFaultReported = false;
    previousTagAssistHeld = false;
    System.out.println(
        "AprilTag assist ready: hold gamepad 0 RIGHT BUMPER; each press logs camera diagnostics.");
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);
    boolean tagAssistHeld = gamepad.getRightBumperButton();
    VisionStatus visionStatus = tagVision == null ? null : tagVision.getStatus();
    Optional<AprilTagTarget> target =
        tagVision == null
            ? Optional.empty()
            : tagVision.getTarget().filter(AprilTagTarget::hasUsablePose);
    DriveCommand tagCommand = null;

    if (tagAssistHeld != previousTagAssistHeld) {
      System.out.printf(
          "RIGHT BUMPER %s: %s%n",
          tagAssistHeld ? "PRESSED" : "RELEASED",
          visionStatus == null ? "AprilTagVision is not started" : visionStatus.summary());
      if (tagAssistHeld
          && visionStatus != null
          && !visionStatus.resultTopicConnected()
          && tagVision != null) {
        System.out.printf(
            "Published vision topics: %s%n", tagVision.publishedVisionTopicsSummary());
      }
      previousTagAssistHeld = tagAssistHeld;
    }

    if (tagAssistHeld) {
      // The assist constants already limit each axis, so do not apply the driver's trigger scale
      // a second time.
      robot.drive.setMaxOutput(1.0);
      robot.drive.setDeadband(0.0);

      if (target.isPresent()) {
        try {
          tagCommand = tagController.calculate(target.get());
          robot.drive.driveCartesian(
              tagCommand.forward(), tagCommand.strafe(), tagCommand.turn());
        } catch (RuntimeException error) {
          tagCommand = null;
          stopAfterAssistFault(error);
        }
      } else {
        // Never drive blind. Releasing the bumper immediately restores manual mecanum control.
        robot.drive.stopMotor();
      }
    } else {
      assistFaultReported = false;
      double scale = 0.15 + (0.35 * gamepad.getRightTriggerAxis());
      robot.drive.setMaxOutput(scale);
      robot.drive.setDeadband(0.08);
      robot.drive.driveCartesian(
          -applyDeadband(gamepad.getLeftY()),
          applyDeadband(gamepad.getLeftX()),
          applyDeadband(gamepad.getRightX()));
    }

    tagTelemetry.update(tagAssistHeld, visionStatus, target, tagCommand);

    if (statusTimer.advanceIfElapsed(1.0)) {
      robot.printA301Status();
      printTagStatus(tagAssistHeld, visionStatus, target, tagCommand);
    }
  }

  @Override
  public void end() {
    robot.drive.stopMotor();
    robot.drive.setDeadband(0.08);
    robot.disableA301s();
    if (tagVision != null) {
      tagVision.close();
      tagVision = null;
    }
  }

  private static double applyDeadband(double value) {
    return Math.abs(value) < 0.08 ? 0.0 : value;
  }

  private void stopAfterAssistFault(RuntimeException error) {
    try {
      robot.drive.stopMotor();
    } catch (RuntimeException stopError) {
      error.addSuppressed(stopError);
    }

    if (!assistFaultReported) {
      assistFaultReported = true;
      System.err.println("AprilTag assist disabled while right bumper is held:");
      error.printStackTrace();
    }
  }

  private static void printTagStatus(
      boolean assistHeld,
      VisionStatus visionStatus,
      Optional<AprilTagTarget> target,
      DriveCommand command) {
    if (target.isEmpty()) {
      System.out.printf(
          "AprilTag assist: held=%s target=none; %s%n",
          assistHeld,
          visionStatus == null ? "vision not started" : visionStatus.summary());
      return;
    }

    AprilTagTarget tag = target.get();
    if (command == null) {
      System.out.printf(
          "AprilTag visible: id=%d range=%.3fm bearing=%.1fdeg yaw=%.1fdeg assist=off%n",
          tag.id(),
          tag.rangeMeters(),
          Math.toDegrees(tag.bearingRadians()),
          Math.toDegrees(tag.yawRadians()));
      return;
    }

    System.out.printf(
        "AprilTag assist: id=%d range=%.3fm bearing=%.1fdeg yaw=%.1fdeg"
            + " -> forward=%.2f strafe=%.2f turn=%.2f%n",
        tag.id(),
        tag.rangeMeters(),
        Math.toDegrees(tag.bearingRadians()),
        Math.toDegrees(tag.yawRadians()),
        command.forward(),
        command.strafe(),
        command.turn());
  }
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import com.revrobotics.REVLibError;
import com.revrobotics.util.Signal;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
    name = "Swerve Pod Encoder Test",
    group = "Test",
    description = "Left-stick absolute steering with drive telemetry")
public class SwervePodEncoderTest extends PeriodicOpMode {
  private static final double kStraightAbsolutePosition = 0.042;
  private static final double kStickDeadband = 0.12;
  private static final double kThetaSpeedRpm = 15.0;
  private static final double kThetaToleranceRotations = 0.04;
  private static final double kDriveMaxThrottle = 0.25;

  // Change either sign after the first physical direction check if needed.
  private static final double kThetaDirection = 1.0;
  private static final double kDriveDirection = 1.0;

  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();

  private boolean hasDirectionCommand;
  private double targetAbsolutePosition;
  private double driveStartPosition;
  private double lastDriveThrottle;
  private REVLibError lastThetaCommandError = REVLibError.kOk;

  public SwervePodEncoderTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    robot.disableA301s();
    hasDirectionCommand = false;
    lastDriveThrottle = 0.0;

    Signal<Double> thetaPosition = robot.swerveTheta.getAbsoluteEncoderPosition();
    targetAbsolutePosition =
        thetaPosition.isValid() ? thetaPosition.get() : kStraightAbsolutePosition;

    Signal<Double> drivePosition = robot.swerveDrive.getRelativeEncoderPosition();
    driveStartPosition = drivePosition.isValid() ? drivePosition.get() : 0.0;

    REVLibError continuousInputError = robot.swerveTheta.enableAbsolutePositionContinuousInput();
    System.out.printf(
        "Swerve encoder test start: continuousInput=%s thetaAbsolute=%s driveStart=%s%n",
        continuousInputError, format(thetaPosition), format(drivePosition));
    statusTimer.restart();
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);
    double x = gamepad.getLeftX();
    double y = -gamepad.getLeftY();
    double magnitude = Math.min(1.0, Math.hypot(x, y));

    if (magnitude >= kStickDeadband) {
      double directionRotations = Math.atan2(x, y) / (2.0 * Math.PI);
      targetAbsolutePosition =
          wrapRotations(kStraightAbsolutePosition + kThetaDirection * directionRotations);
      hasDirectionCommand = true;
    }

    Signal<Double> thetaPosition = robot.swerveTheta.getAbsoluteEncoderPosition();
    if (!hasDirectionCommand || !thetaPosition.isValid()) {
      robot.swerveTheta.disable();
      stopDrive();
      printStatusIfDue(magnitude, thetaPosition);
      return;
    }

    lastThetaCommandError =
        robot.swerveTheta.setAbsolutePositionWithSpeed(
            targetAbsolutePosition, kThetaSpeedRpm);
    if (lastThetaCommandError != REVLibError.kOk) {
      robot.swerveTheta.disable();
      stopDrive();
      printStatusIfDue(magnitude, thetaPosition);
      return;
    }

    double thetaError = wrapRotations(targetAbsolutePosition - thetaPosition.get());
    if (magnitude >= kStickDeadband
        && Math.abs(thetaError) <= kThetaToleranceRotations) {
      lastDriveThrottle = kDriveDirection * magnitude * kDriveMaxThrottle;
      robot.swerveDrive.setThrottle(lastDriveThrottle);
    } else {
      stopDrive();
    }

    printStatusIfDue(magnitude, thetaPosition);
  }

  @Override
  public void end() {
    robot.swerveTheta.disable();
    robot.swerveDrive.disable();
    lastDriveThrottle = 0.0;
  }

  private void stopDrive() {
    robot.swerveDrive.disable();
    lastDriveThrottle = 0.0;
  }

  private void printStatusIfDue(double magnitude, Signal<Double> thetaPosition) {
    if (!statusTimer.advanceIfElapsed(0.25)) {
      return;
    }

    Signal<Double> drivePosition = robot.swerveDrive.getRelativeEncoderPosition();
    Signal<Double> driveVelocity = robot.swerveDrive.getEncoderVelocity();
    String thetaError =
        thetaPosition.isValid()
            ? String.format(
                "%.4f", wrapRotations(targetAbsolutePosition - thetaPosition.get()))
            : "invalid";
    String driveDelta =
        drivePosition.isValid()
            ? String.format("%.4f", drivePosition.get() - driveStartPosition)
            : "invalid";

    System.out.printf(
        "Swerve encoder test: active=%s stick=%.3f target=%.4f theta=%s error=%s "
            + "thetaCommand=%s driveThrottle=%.3f drivePosition=%s driveDelta=%s driveVelocity=%s%n",
        hasDirectionCommand,
        magnitude,
        targetAbsolutePosition,
        format(thetaPosition),
        thetaError,
        lastThetaCommandError,
        lastDriveThrottle,
        format(drivePosition),
        driveDelta,
        format(driveVelocity));
  }

  private static double wrapRotations(double rotations) {
    return rotations - Math.floor(rotations + 0.5);
  }

  private static String format(Signal<Double> signal) {
    return signal.isValid()
        ? String.format("%.4f", signal.get())
        : "invalid(" + signal.getError() + ")";
  }
}

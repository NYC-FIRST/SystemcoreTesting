// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
    name = "Swerve Pod 2 Manual Test",
    group = "Test",
    description = "Open-loop D13 steering and D12 drive test; no encoders")
public class SwervePod2ManualTest extends PeriodicOpMode {
  private static final double kDeadband = 0.08;
  private static final double kThetaMaxThrottle = 1.0;
  private static final double kDriveMaxThrottle = 1.0;

  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();

  public SwervePod2ManualTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    robot.disableA301s();
    statusTimer.restart();
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);

    double thetaThrottle = applyDeadband(gamepad.getRightX()) * kThetaMaxThrottle;
    double driveThrottle = -applyDeadband(gamepad.getLeftY()) * kDriveMaxThrottle;

    robot.swerveTheta2.setThrottle(thetaThrottle);
    robot.swerveDrive2.setThrottle(driveThrottle);

    if (statusTimer.advanceIfElapsed(0.5)) {
      robot.printSwerveStatus();
    }
  }

  @Override
  public void end() {
    robot.swerveTheta2.disable();
    robot.swerveDrive2.disable();
  }

  private static double applyDeadband(double value) {
    return Math.abs(value) < kDeadband ? 0.0 : value;
  }
}

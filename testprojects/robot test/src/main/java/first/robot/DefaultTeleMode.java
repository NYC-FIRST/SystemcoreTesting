// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop
public class DefaultTeleMode extends PeriodicOpMode {
  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();

  public DefaultTeleMode(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    statusTimer.restart();
    robot.disableA301s();
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);

    double scale = 0.15 + (0.35 * gamepad.getRightTriggerAxis());
    robot.drive.setMaxOutput(scale);
    robot.drive.driveCartesian(
        -applyDeadband(gamepad.getLeftY()),
        applyDeadband(gamepad.getLeftX()),
        applyDeadband(gamepad.getRightX()));

    if (statusTimer.advanceIfElapsed(1.0)) {
      robot.printA301Status();
    }

  }

  @Override
  public void end() {
    robot.drive.stopMotor();
    robot.disableA301s();
  }

  private static double applyDeadband(double value) {
    return Math.abs(value) < 0.08 ? 0.0 : value;
  }
}

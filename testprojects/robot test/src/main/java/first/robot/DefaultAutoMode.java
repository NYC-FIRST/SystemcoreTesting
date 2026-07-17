// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.system.Timer;

@Autonomous
public class DefaultAutoMode extends PeriodicOpMode {
  private final Robot robot;
  private final Timer timer = new Timer();

  public DefaultAutoMode(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void start() {
    timer.reset();
    timer.start();
    robot.disableA301s();
  }

  @Override
  public void periodic() {
    double time = timer.get();
    if (time < 1.0) {
      robot.setAllThrottles(0.15, 0.0, 0.0, 0.0);
    } else if (time < 2.0) {
      robot.setAllThrottles(0.0, 0.15, 0.0, 0.0);
    } else if (time < 3.0) {
      robot.setAllThrottles(0.0, 0.0, 0.15, 0.0);
    } else if (time < 4.0) {
      robot.setAllThrottles(0.0, 0.0, 0.0, 0.15);
    } else {
      robot.setAllThrottles(0.0, 0.0, 0.0, 0.0);
    }
  }

  @Override
  public void end() {
    robot.disableA301s();
  }
}

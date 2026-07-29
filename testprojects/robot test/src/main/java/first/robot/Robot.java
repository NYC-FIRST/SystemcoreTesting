// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.UserControlsInstance;
import org.wpilib.drive.MecanumDrive;
import org.wpilib.framework.OpModeRobot;
import org.wpilib.hardware.hal.CANBusMap;
import org.wpilib.net.WebServer;
import org.wpilib.system.Filesystem;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;

/**
 * A bring-up program for A301 motors connected to Motioncore CAN channels.
 */
@UserControlsInstance(DefaultUserControls.class)
public class Robot extends OpModeRobot {
  public final A301 frontLeft = new A301(CANBusMap.CAN_D0);
  public final A301 rearLeft = new A301(CANBusMap.CAN_D1);
  public final A301 frontRight = new A301(CANBusMap.CAN_D2);
  public final A301 rearRight = new A301(CANBusMap.CAN_D3);
  public final A301 armJoint0 = new A301(CANBusMap.CAN_D4);
  public final A301 swerveTheta = new A301(CANBusMap.CAN_D11);
  public final A301 swerveDrive = new A301(CANBusMap.CAN_D10);

  public final MecanumDrive drive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
  public final A301[] motors = {frontLeft, rearLeft, frontRight, rearRight};
  public final String[] motorNames = {"frontLeft D0", "rearLeft D1", "frontRight D2", "rearRight D3"};

  /** Called once at the beginning of the robot program. */
  public Robot() {
    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

    // Start with the common FRC drivetrain convention. Flip these if the mecanum test shows a
    // wheel driving opposite its matching side.
    frontRight.setInverted(true);
    rearRight.setInverted(true);
    drive.setDeadband(0.08);

    for (int i = 0; i < motors.length; i++) {
      motors[i]
          .busVoltagePeriodMs(100)
          .motorCurrentPeriodMs(100)
          .relativeEncoderPositionPeriodMs(100)
          .encoderVelocityPeriodMs(100)
          .faultsPeriodMs(250)
          .warningsPeriodMs(250);

      System.out.printf(
          "A301 %s registered: bus=%d device=%d%n",
          motorNames[i], motors[i].getBusId(), motors[i].getDeviceId());
    }

    armJoint0
        .busVoltagePeriodMs(100)
        .motorCurrentPeriodMs(100)
        .relativeEncoderPositionPeriodMs(20)
        .absoluteEncoderPositionPeriodMs(20)
        .encoderVelocityPeriodMs(100)
        .faultsPeriodMs(250)
        .warningsPeriodMs(250);
    System.out.printf(
        "A301 armJoint0 D4 registered: bus=%d device=%d%n",
        armJoint0.getBusId(), armJoint0.getDeviceId());

    configureOpenLoopTestMotor(swerveTheta);
    swerveTheta.absoluteEncoderPositionPeriodMs(20);
    configureOpenLoopTestMotor(swerveDrive);
    swerveDrive.relativeEncoderPositionPeriodMs(20).encoderVelocityPeriodMs(100);
    System.out.printf(
        "A301 swerveTheta D11 registered: bus=%d device=%d%n",
        swerveTheta.getBusId(), swerveTheta.getDeviceId());
    System.out.printf(
        "A301 swerveDrive D10 registered: bus=%d device=%d%n",
        swerveDrive.getBusId(), swerveDrive.getDeviceId());
  }

  public void setAllThrottles(double... throttles) {
    for (int i = 0; i < motors.length; i++) {
      motors[i].setThrottle(i < throttles.length ? throttles[i] : 0.0);
    }
  }

  public void disableA301s() {
    for (A301 motor : motors) {
      motor.disable();
    }
    armJoint0.disable();
    swerveTheta.disable();
    swerveDrive.disable();
  }

  public void printSwerveStatus() {
    printOpenLoopStatus("swerveTheta D11", swerveTheta);
    printOpenLoopStatus("swerveDrive D10", swerveDrive);
  }

  public void printA301Status() {
    for (int i = 0; i < motors.length; i++) {
      A301 motor = motors[i];
      System.out.printf(
          "A301 %s: throttle=%.2f volts=%s current=%s pos=%s vel=%s fault=%s warning=%s%n",
          motorNames[i],
          motor.getThrottle(),
          format(motor.getBusVoltage()),
          format(motor.getMotorCurrent()),
          format(motor.getRelativeEncoderPosition()),
          format(motor.getEncoderVelocity()),
          format(motor.hasActiveFault()),
          format(motor.hasActiveWarning()));
    }
  }

  private static <T> String format(Signal<T> signal) {
    if (!signal.isValid()) {
      return "invalid(" + signal.getError() + ")";
    }
    return String.valueOf(signal.get());
  }

  private static void configureOpenLoopTestMotor(A301 motor) {
    motor
        .busVoltagePeriodMs(100)
        .motorCurrentPeriodMs(100)
        .faultsPeriodMs(250)
        .warningsPeriodMs(250);
  }

  private static void printOpenLoopStatus(String name, A301 motor) {
    System.out.printf(
        "A301 %s: throttle=%.2f volts=%s current=%s fault=%s warning=%s%n",
        name,
        motor.getThrottle(),
        format(motor.getBusVoltage()),
        format(motor.getMotorCurrent()),
        format(motor.hasActiveFault()),
        format(motor.hasActiveWarning()));
  }

  public static Object getBooleanTopic(String string) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getBooleanTopic'");
  }
}

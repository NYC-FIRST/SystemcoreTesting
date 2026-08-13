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
  public final A301 swerveTheta = rearLeft;
  public final A301 swerveDrive = new A301(CANBusMap.CAN_D11);
  public final A301 swerveTheta2 = frontRight;
  public final A301 swerveDrive2 = new A301(CANBusMap.CAN_D12);
  public final A301 swerveFrontRightDrive = new A301(CANBusMap.CAN_D10);
  public final A301 swerveFrontRightTheta = frontLeft;
  public final A301 swerveBackLeftDrive = new A301(CANBusMap.CAN_D13);
  public final A301 swerveBackLeftTheta = rearRight;

  // Four-pod layout: FL D10/D0, FR D11/D1, BL D12/D2, BR D13/D3 (drive/steering).
  public final A301[] swerveDriveMotors = {
      swerveFrontRightDrive, swerveDrive, swerveDrive2, swerveBackLeftDrive
  };
  public final A301[] swerveSteeringMotors = {
      swerveFrontRightTheta, swerveTheta, swerveTheta2, swerveBackLeftTheta
  };
  public final String[] swerveModuleNames = {"Front Left", "Front Right", "Back Left", "Back Right"};

  /**
   * Raw absolute-encoder rotations measured with every steering module pointed robot-forward.
   * The order matches {@link #swerveModuleNames} and {@link #swerveSteeringMotors}:
   * FL D0, FR D1, BL D2, BR D3.
   */
  public static final double[] SWERVE_STEERING_STRAIGHT_OFFSETS_ROTATIONS = {
      -0.37, 0.00, -0.44, 0.11
  };

  public final MecanumDrive drive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
  public final A301[] motors = {frontLeft, rearLeft, frontRight, rearRight};
  public final String[] motorNames = {"frontLeft D0", "rearLeft D1", "frontRight D2", "rearRight D3"};

  /** Called once at the beginning of the robot program. */
  public Robot() {
    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());

    // D0-D3 are steering motors in the active swerve wiring. Do not apply legacy mecanum
    // inversions to them. The legacy MecanumDrive is not used by the swerve OpModes, so its
    // watchdog must not emit timeout errors while those modes deliberately do not feed it.
    drive.setSafetyEnabled(false);
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
    configureOpenLoopTestMotor(swerveTheta2);
    swerveTheta2.absoluteEncoderPositionPeriodMs(20);
    configureOpenLoopTestMotor(swerveDrive2);
    swerveDrive2.relativeEncoderPositionPeriodMs(20).encoderVelocityPeriodMs(100);
    configureOpenLoopTestMotor(swerveFrontRightDrive);
    swerveFrontRightDrive.relativeEncoderPositionPeriodMs(20).encoderVelocityPeriodMs(100);
    configureOpenLoopTestMotor(swerveFrontRightTheta);
    swerveFrontRightTheta.absoluteEncoderPositionPeriodMs(20);
    configureOpenLoopTestMotor(swerveBackLeftDrive);
    swerveBackLeftDrive.relativeEncoderPositionPeriodMs(20).encoderVelocityPeriodMs(100);
    configureOpenLoopTestMotor(swerveBackLeftTheta);
    swerveBackLeftTheta.absoluteEncoderPositionPeriodMs(20);
    System.out.printf(
        "A301 swerveTheta D1 registered: bus=%d device=%d%n",
        swerveTheta.getBusId(), swerveTheta.getDeviceId());
    System.out.printf(
        "A301 swerveDrive D11 registered: bus=%d device=%d%n",
        swerveDrive.getBusId(), swerveDrive.getDeviceId());
    System.out.printf(
        "A301 swerveTheta2 D2 registered: bus=%d device=%d%n",
        swerveTheta2.getBusId(), swerveTheta2.getDeviceId());
    System.out.printf(
        "A301 swerveDrive2 D12 registered: bus=%d device=%d%n",
        swerveDrive2.getBusId(), swerveDrive2.getDeviceId());
    System.out.printf("Four-pod map: FL D10/D0, FR D11/D1, BL D12/D2, BR D13/D3 (drive/steering)%n");
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
    swerveTheta2.disable();
    swerveDrive2.disable();
    swerveFrontRightDrive.disable();
    swerveFrontRightTheta.disable();
    swerveBackLeftDrive.disable();
    swerveBackLeftTheta.disable();
  }

  /** Stops all temporary four-pod motors, including the steering motors. */
  public void disableFourSwervePods() {
    for (A301 motor : swerveDriveMotors) {
      motor.disable();
    }
    for (A301 motor : swerveSteeringMotors) {
      motor.disable();
    }
  }

  public void printSwerveStatus() {
    printOpenLoopStatus("swerveTheta D1", swerveTheta);
    printOpenLoopStatus("swerveDrive D11", swerveDrive);
    printOpenLoopStatus("swerveTheta2 D2", swerveTheta2);
    printOpenLoopStatus("swerveDrive2 D12", swerveDrive2);
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

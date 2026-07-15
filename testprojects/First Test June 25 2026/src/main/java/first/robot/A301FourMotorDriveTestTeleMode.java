// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.networktables.BooleanPublisher;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.StringPublisher;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

@Teleop(name = "A301 Four Motor Direct Test")
public class A301FourMotorDriveTestTeleMode extends PeriodicOpMode {
  private static final int FRONT_LEFT_BUS = 5; // Motioncore D0
  private static final int FRONT_RIGHT_BUS = 6; // Motioncore D1
  private static final int BACK_RIGHT_BUS = 7; // Motioncore D2
  private static final int BACK_LEFT_BUS = 8; // Motioncore D3
  private static final int A301_CAN_ID = 3;

  private static final double DEADBAND = 0.05;
  private static final double BUTTON_TEST_THROTTLE = 0.20;

  private final DefaultUserControls userControls;

  private final NetworkTable table =
      NetworkTableInstance.getDefault().getTable("A301FourMotorDirectTest");
  private final DoublePublisher loopCountPublisher =
      table.getDoubleTopic("loopCount").publish();
  private final DoublePublisher activeLoopCountPublisher =
      table.getDoubleTopic("activeLoopCount").publish();
  private final DoublePublisher inactiveLoopCountPublisher =
      table.getDoubleTopic("inactiveLoopCount").publish();
  private final DoublePublisher startCountPublisher =
      table.getDoubleTopic("startCount").publish();
  private final DoublePublisher endCountPublisher =
      table.getDoubleTopic("endCount").publish();
  private final DoublePublisher commandPublisher =
      table.getDoubleTopic("command").publish();
  private final DoublePublisher rightStickYPublisher =
      table.getDoubleTopic("rightStickY").publish();
  private final DoublePublisher leftTriggerPublisher =
      table.getDoubleTopic("leftTrigger").publish();
  private final DoublePublisher rightTriggerPublisher =
      table.getDoubleTopic("rightTrigger").publish();
  private final BooleanPublisher nonZeroCommandPublisher =
      table.getBooleanTopic("nonZeroCommand").publish();
  private final StringPublisher modePublisher =
      table.getStringTopic("mode").publish();
  private final BooleanPublisher opModeAlivePublisher =
      table.getBooleanTopic("opModeAlive").publish();
  private final BooleanPublisher opModeActivePublisher =
      table.getBooleanTopic("opModeActive").publish();
  private final StringPublisher lifecyclePublisher =
      table.getStringTopic("lifecycle").publish();
  private final StringPublisher statusPublisher =
      table.getStringTopic("status").publish();

  private final MotorTelemetry frontLeftTelemetry = new MotorTelemetry("frontLeft");
  private final MotorTelemetry frontRightTelemetry = new MotorTelemetry("frontRight");
  private final MotorTelemetry backRightTelemetry = new MotorTelemetry("backRight");
  private final MotorTelemetry backLeftTelemetry = new MotorTelemetry("backLeft");

  private A301 frontLeft;
  private A301 frontRight;
  private A301 backRight;
  private A301 backLeft;

  private int loopCount;
  private int activeLoopCount;
  private int inactiveLoopCount;
  private int startCount;
  private int endCount;
  private boolean opModeActive;

  public A301FourMotorDriveTestTeleMode(DefaultUserControls userControls) {
    System.out.println("A301 four motor constructor called");
    this.userControls = userControls;
  }

  @Override
  public void start() {
    System.out.println("A301 FOUR MOTOR START");
    loopCount = 0;
    activeLoopCount = 0;
    inactiveLoopCount = 0;
    startCount++;
    opModeActive = true;
    opModeAlivePublisher.set(true);
    opModeActivePublisher.set(true);
    startCountPublisher.set(startCount);
    lifecyclePublisher.set("start() called; creating motor handles");
    statusPublisher.set("start() called");

    closeAllMotors();
    createAllMotors();

    stopAllMotors();
    publishAllMotorTelemetry();
    publishSummaryStatus();
  }

  @Override
  public void periodic() {
    loopCount++;
    loopCountPublisher.set(loopCount);
    opModeAlivePublisher.set(true);

    var gamepad = userControls.getGamepad(0);
    double throttle;
    String mode;
    int targetMode;

    /*
     * Simple bring-up controls:
     *   Triangle / north face: all four forward slowly
     *   Cross / south face: all four reverse slowly
     *   Left bumper + right stick Y: front left only
     *   Right bumper + right stick Y: front right only
     *   Left trigger + right stick Y: back left only
     *   Right trigger + right stick Y: back right only
     *   No selector: right joystick Y controls all four together
     */
    if (gamepad.getNorthFaceButton()) {
      throttle = BUTTON_TEST_THROTTLE;
      mode = "north button: all forward";
      targetMode = 0;
    } else if (gamepad.getSouthFaceButton()) {
      throttle = -BUTTON_TEST_THROTTLE;
      mode = "south button: all reverse";
      targetMode = 0;
    } else {
      throttle = -gamepad.getRightY();
      double leftTrigger = gamepad.getLeftTriggerAxis();
      double rightTrigger = gamepad.getRightTriggerAxis();

      if (Math.abs(throttle) < DEADBAND) {
        throttle = 0.0;
      }

      if (rightTrigger > 0.5) {
        mode = "right trigger + right stick Y: back right only";
        targetMode = 4;
      } else if (leftTrigger > 0.5) {
        mode = "left trigger + right stick Y: back left only";
        targetMode = 3;
      } else if (gamepad.getRightBumperButton()) {
        mode = "right bumper + right stick Y: front right only";
        targetMode = 2;
      } else if (gamepad.getLeftBumperButton()) {
        mode = "left bumper + right stick Y: front left only";
        targetMode = 1;
      } else {
        mode = "right stick Y: all motors";
        targetMode = 0;
      }
    }

    boolean commandRequested = Math.abs(throttle) >= DEADBAND;
    if (commandRequested) {
      opModeActive = true;
      activeLoopCount++;
      activeLoopCountPublisher.set(activeLoopCount);

      if (anyMotorMissing()) {
        lifecyclePublisher.set("command requested after handles were closed; recreating motors");
        createAllMotors();
      }

      commandTarget(targetMode, throttle);
    } else {
      inactiveLoopCount++;
      inactiveLoopCountPublisher.set(inactiveLoopCount);
      if (opModeActive && !anyMotorMissing()) {
        setAllMotors(0.0);
      }
    }

    opModeActivePublisher.set(opModeActive);
    rightStickYPublisher.set(gamepad.getRightY());
    leftTriggerPublisher.set(gamepad.getLeftTriggerAxis());
    rightTriggerPublisher.set(gamepad.getRightTriggerAxis());
    commandPublisher.set(throttle);
    nonZeroCommandPublisher.set(Math.abs(throttle) >= DEADBAND);
    modePublisher.set(mode);
    publishAllMotorTelemetry();
    publishSummaryStatus();
  }

  @Override
  public void end() {
    endCount++;
    opModeActive = false;
    opModeActivePublisher.set(false);
    endCountPublisher.set(endCount);
    lifecyclePublisher.set("end() called; stopping and closing motor handles");
    statusPublisher.set("end() called; stopping motors");
    stopAllMotors();
    closeAllMotors();
    commandPublisher.set(0.0);
    nonZeroCommandPublisher.set(false);
    modePublisher.set("end() called: motors stopped");
    publishAllMotorTelemetry();
  }

  @Override
  public void close() {
    opModeActive = false;
    opModeActivePublisher.set(false);
    lifecyclePublisher.set("close() called; stopping and closing motor handles");
    statusPublisher.set("close() called; stopping and closing motors");
    stopAllMotors();
    closeAllMotors();
    commandPublisher.set(0.0);
    nonZeroCommandPublisher.set(false);
    modePublisher.set("close() called: motors stopped and closed");
  }

  private A301 createMotor(
      String name, String portName, int busId, MotorTelemetry telemetry) {
    System.out.println("Creating " + name + " on Motioncore " + portName + " bus " + busId);
    telemetry.portPublisher.set(portName);
    telemetry.busPublisher.set(busId);
    telemetry.connectedPublisher.set(false);
    telemetry.commandAcceptedPublisher.set(false);
    telemetry.respondingPublisher.set(false);
    telemetry.lastErrorPublisher.set("not created yet");

    try {
      A301 motor = new A301(busId, A301_CAN_ID);
      System.out.println(name + " CREATED");

      String firmware = motor.getFirmwareString();
      System.out.println(name + " FW: " + firmware);

      motor.setThrottle(0.0);
      telemetry.connectedPublisher.set(true);
      telemetry.commandAcceptedPublisher.set(true);
      telemetry.firmwarePublisher.set(firmware);
      telemetry.lastErrorPublisher.set("");
      return motor;
    } catch (Throwable t) {
      System.out.println(name + " FAILED");
      t.printStackTrace();

      telemetry.connectedPublisher.set(false);
      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
      telemetry.firmwarePublisher.set("");
      telemetry.lastErrorPublisher.set(getThrowableText(t));
      return null;
    }
  }

  private void createAllMotors() {
    frontLeft = createMotor("frontLeft", "D0", FRONT_LEFT_BUS, frontLeftTelemetry);
    frontRight = createMotor("frontRight", "D1", FRONT_RIGHT_BUS, frontRightTelemetry);
    backRight = createMotor("backRight", "D2", BACK_RIGHT_BUS, backRightTelemetry);
    backLeft = createMotor("backLeft", "D3", BACK_LEFT_BUS, backLeftTelemetry);
  }

  private boolean anyMotorMissing() {
    return frontLeft == null || frontRight == null || backRight == null || backLeft == null;
  }

  private void commandTarget(int targetMode, double throttle) {
    if (targetMode == 1) {
      setFrontLeftOnly(throttle);
    } else if (targetMode == 2) {
      setFrontRightOnly(throttle);
    } else if (targetMode == 3) {
      setBackLeftOnly(throttle);
    } else if (targetMode == 4) {
      setBackRightOnly(throttle);
    } else {
      setAllMotors(throttle);
    }
  }

  private void setAllMotors(double throttle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, throttle);
    setMotor("frontRight", frontRight, frontRightTelemetry, throttle);
    setMotor("backRight", backRight, backRightTelemetry, throttle);
    setMotor("backLeft", backLeft, backLeftTelemetry, throttle);
  }

  private void setLeftRight(double leftThrottle, double rightThrottle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, leftThrottle);
    setMotor("backLeft", backLeft, backLeftTelemetry, leftThrottle);
    setMotor("frontRight", frontRight, frontRightTelemetry, rightThrottle);
    setMotor("backRight", backRight, backRightTelemetry, rightThrottle);
  }

  private void setFrontLeftOnly(double throttle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, throttle);
    setMotor("frontRight", frontRight, frontRightTelemetry, 0.0);
    setMotor("backRight", backRight, backRightTelemetry, 0.0);
    setMotor("backLeft", backLeft, backLeftTelemetry, 0.0);
  }

  private void setFrontRightOnly(double throttle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, 0.0);
    setMotor("frontRight", frontRight, frontRightTelemetry, throttle);
    setMotor("backRight", backRight, backRightTelemetry, 0.0);
    setMotor("backLeft", backLeft, backLeftTelemetry, 0.0);
  }

  private void setBackRightOnly(double throttle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, 0.0);
    setMotor("frontRight", frontRight, frontRightTelemetry, 0.0);
    setMotor("backRight", backRight, backRightTelemetry, throttle);
    setMotor("backLeft", backLeft, backLeftTelemetry, 0.0);
  }

  private void setBackLeftOnly(double throttle) {
    setMotor("frontLeft", frontLeft, frontLeftTelemetry, 0.0);
    setMotor("frontRight", frontRight, frontRightTelemetry, 0.0);
    setMotor("backRight", backRight, backRightTelemetry, 0.0);
    setMotor("backLeft", backLeft, backLeftTelemetry, throttle);
  }

  private void stopAllMotors() {
    setAllMotors(0.0);
  }

  private void setMotor(String name, A301 motor, MotorTelemetry telemetry, double throttle) {
    telemetry.commandPublisher.set(throttle);

    if (motor == null) {
      telemetry.connectedPublisher.set(false);
      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
      telemetry.lastErrorPublisher.set("motor object is null");
      return;
    }

    try {
      motor.setThrottle(throttle);
      telemetry.connectedPublisher.set(true);
      telemetry.commandAcceptedPublisher.set(true);
      telemetry.lastErrorPublisher.set("");
    } catch (Throwable t) {
      System.out.println(name + " setThrottle FAILED");
      t.printStackTrace();

      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
      telemetry.lastErrorPublisher.set("setThrottle failed: " + getThrowableText(t));
    }
  }

  private void publishAllMotorTelemetry() {
    publishMotorTelemetry("frontLeft", frontLeft, frontLeftTelemetry);
    publishMotorTelemetry("frontRight", frontRight, frontRightTelemetry);
    publishMotorTelemetry("backRight", backRight, backRightTelemetry);
    publishMotorTelemetry("backLeft", backLeft, backLeftTelemetry);
  }

  private void publishMotorTelemetry(String name, A301 motor, MotorTelemetry telemetry) {
    if (motor == null) {
      telemetry.connectedPublisher.set(false);
      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
      return;
    }

    telemetry.connectedPublisher.set(true);
    telemetry.respondingPublisher.set(false);
    readDoubleSignal(
        name,
        "busVoltage",
        () -> motor.getBusVoltage(),
        telemetry.busVoltagePublisher,
        telemetry.busVoltageValidPublisher,
        telemetry.busVoltageErrorPublisher,
        telemetry);
    readDoubleSignal(
        name,
        "appliedOutput",
        () -> motor.getAppliedOutput(),
        telemetry.appliedOutputPublisher,
        telemetry.appliedOutputValidPublisher,
        telemetry.appliedOutputErrorPublisher,
        telemetry);
    readDoubleSignal(
        name,
        "motorCurrent",
        () -> motor.getMotorCurrent(),
        telemetry.motorCurrentPublisher,
        telemetry.motorCurrentValidPublisher,
        telemetry.motorCurrentErrorPublisher,
        telemetry);
    readDoubleSignal(
        name,
        "encoderVelocity",
        () -> motor.getEncoderVelocity(),
        telemetry.encoderVelocityPublisher,
        telemetry.encoderVelocityValidPublisher,
        telemetry.encoderVelocityErrorPublisher,
        telemetry);
  }

  private void readDoubleSignal(
      String name,
      String signalName,
      DoubleSignalSupplier signalSupplier,
      DoublePublisher valuePublisher,
      BooleanPublisher validPublisher,
      StringPublisher errorPublisher,
      MotorTelemetry telemetry) {
    try {
      Signal<Double> signal = signalSupplier.get();
      double value = signal.get(0.0);
      boolean valid = signal.isValid();
      String error = String.valueOf(signal.getError());

      valuePublisher.set(value);
      validPublisher.set(valid);
      errorPublisher.set(error);

      if (valid) {
        telemetry.respondingPublisher.set(true);
      }
    } catch (Throwable t) {
      System.out.println(name + " " + signalName + " read FAILED");
      t.printStackTrace();

      validPublisher.set(false);
      errorPublisher.set(getThrowableText(t));
      telemetry.respondingPublisher.set(false);
      telemetry.lastErrorPublisher.set(signalName + " read failed: " + getThrowableText(t));
    }
  }

  private void publishSummaryStatus() {
    statusPublisher.set(
        "loops="
            + loopCount
            + " active="
            + opModeActive
            + " activeLoops="
            + activeLoopCount
            + " inactiveLoops="
            + inactiveLoopCount
            + " FL="
            + statusText(frontLeft, frontLeftTelemetry)
            + " FR="
            + statusText(frontRight, frontRightTelemetry)
            + " BR="
            + statusText(backRight, backRightTelemetry)
            + " BL="
            + statusText(backLeft, backLeftTelemetry));
  }

  private String statusText(A301 motor, MotorTelemetry telemetry) {
    if (motor == null) {
      return "null";
    }

    return "created";
  }

  private void closeMotor(String name, A301 motor, MotorTelemetry telemetry) {
    if (motor == null) {
      telemetry.connectedPublisher.set(false);
      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
      return;
    }

    try {
      motor.close();
      telemetry.connectedPublisher.set(false);
      telemetry.commandAcceptedPublisher.set(false);
      telemetry.respondingPublisher.set(false);
    } catch (Throwable t) {
      System.out.println(name + " close FAILED");
      t.printStackTrace();

      telemetry.lastErrorPublisher.set("close failed: " + getThrowableText(t));
    }
  }

  private void closeAllMotors() {
    closeMotor("frontLeft", frontLeft, frontLeftTelemetry);
    closeMotor("frontRight", frontRight, frontRightTelemetry);
    closeMotor("backRight", backRight, backRightTelemetry);
    closeMotor("backLeft", backLeft, backLeftTelemetry);

    frontLeft = null;
    frontRight = null;
    backRight = null;
    backLeft = null;
  }

  private String getThrowableText(Throwable t) {
    String message = t.getMessage();
    if (message == null || message.isBlank()) {
      return t.getClass().getName();
    }

    return t.getClass().getName() + ": " + message;
  }

  @FunctionalInterface
  private interface DoubleSignalSupplier {
    Signal<Double> get();
  }

  private final class MotorTelemetry {
    private final StringPublisher portPublisher;
    private final DoublePublisher busPublisher;
    private final DoublePublisher commandPublisher;
    private final BooleanPublisher connectedPublisher;
    private final BooleanPublisher commandAcceptedPublisher;
    private final BooleanPublisher respondingPublisher;
    private final StringPublisher firmwarePublisher;
    private final StringPublisher lastErrorPublisher;
    private final DoublePublisher busVoltagePublisher;
    private final BooleanPublisher busVoltageValidPublisher;
    private final StringPublisher busVoltageErrorPublisher;
    private final DoublePublisher appliedOutputPublisher;
    private final BooleanPublisher appliedOutputValidPublisher;
    private final StringPublisher appliedOutputErrorPublisher;
    private final DoublePublisher motorCurrentPublisher;
    private final BooleanPublisher motorCurrentValidPublisher;
    private final StringPublisher motorCurrentErrorPublisher;
    private final DoublePublisher encoderVelocityPublisher;
    private final BooleanPublisher encoderVelocityValidPublisher;
    private final StringPublisher encoderVelocityErrorPublisher;

    private MotorTelemetry(String prefix) {
      portPublisher = table.getStringTopic(prefix + "/port").publish();
      busPublisher = table.getDoubleTopic(prefix + "/bus").publish();
      commandPublisher = table.getDoubleTopic(prefix + "/command").publish();
      connectedPublisher = table.getBooleanTopic(prefix + "/connected").publish();
      commandAcceptedPublisher = table.getBooleanTopic(prefix + "/commandAccepted").publish();
      respondingPublisher = table.getBooleanTopic(prefix + "/responding").publish();
      firmwarePublisher = table.getStringTopic(prefix + "/firmware").publish();
      lastErrorPublisher = table.getStringTopic(prefix + "/lastError").publish();
      busVoltagePublisher = table.getDoubleTopic(prefix + "/busVoltage").publish();
      busVoltageValidPublisher = table.getBooleanTopic(prefix + "/busVoltageValid").publish();
      busVoltageErrorPublisher = table.getStringTopic(prefix + "/busVoltageError").publish();
      appliedOutputPublisher = table.getDoubleTopic(prefix + "/appliedOutput").publish();
      appliedOutputValidPublisher =
          table.getBooleanTopic(prefix + "/appliedOutputValid").publish();
      appliedOutputErrorPublisher =
          table.getStringTopic(prefix + "/appliedOutputError").publish();
      motorCurrentPublisher = table.getDoubleTopic(prefix + "/motorCurrent").publish();
      motorCurrentValidPublisher = table.getBooleanTopic(prefix + "/motorCurrentValid").publish();
      motorCurrentErrorPublisher = table.getStringTopic(prefix + "/motorCurrentError").publish();
      encoderVelocityPublisher = table.getDoubleTopic(prefix + "/encoderVelocity").publish();
      encoderVelocityValidPublisher =
          table.getBooleanTopic(prefix + "/encoderVelocityValid").publish();
      encoderVelocityErrorPublisher =
          table.getStringTopic(prefix + "/encoderVelocityError").publish();
    }
  }
}

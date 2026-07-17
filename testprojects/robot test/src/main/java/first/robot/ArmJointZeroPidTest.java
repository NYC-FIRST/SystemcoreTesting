// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import com.revrobotics.util.Signal;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
    name = "Arm J0 Relative Throttle",
    group = "Test",
    description = "D4 A301 relative encoder/open-loop test")
public class ArmJointZeroPidTest extends PeriodicOpMode {
  private static final double kTravelRotations = 1.0;
  private static final double kThrottle = 0.15;
  private static final double kPositionToleranceRotations = 0.10;
  private static final double kPauseSeconds = 3.0;
  private static final double kMaxMoveSeconds = 6.0;
  private static final Path kSystemCoreLogPath = Path.of("/home/systemcore/deploy/arm_joint0_log.csv");
  private static final Path kFallbackLogPath = Path.of("build/arm_joint0_log.csv");

  // Flip this to -1.0 if you want the first move to go the other physical direction.
  private static final double kOutThrottleDirection = -1.0;

  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();

  private State state = State.IDLE;
  private double startPosition;
  private double outTarget;
  private double backTarget;
  private double pauseUntilTimestamp;
  private double moveStartedTimestamp;
  private double lastCommandedThrottle;
  private boolean wasSquarePressed;
  private BufferedWriter logWriter;

  private enum State {
    IDLE,
    MOVING_OUT,
    PAUSING,
    MOVING_BACK,
    DONE
  }

  public ArmJointZeroPidTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    openLog();
    stopMotor();
    startPosition = readRelativePositionOrZero();
    backTarget = startPosition;
    outTarget = startPosition + kTravelRotations;
    pauseUntilTimestamp = 0.0;
    moveStartedTimestamp = 0.0;
    lastCommandedThrottle = 0.0;
    wasSquarePressed = false;
    state = State.IDLE;
    statusTimer.restart();
    printStatus("start");
    writeLogRow("start", false, false);
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);

    boolean squarePressed = gamepad.getWestFaceButton();
    if (squarePressed && !wasSquarePressed) {
      startSequence();
    }
    wasSquarePressed = squarePressed;

    if (gamepad.getStartButtonPressed()) {
      stopMotor();
      state = State.IDLE;
      printStatus("disabled");
      writeLogRow("disabled", squarePressed, true);
    }

    updateSequence();
    writeLogRow("loop", squarePressed, gamepad.getStartButton());

    if (statusTimer.advanceIfElapsed(0.25)) {
      printStatus("periodic");
    }
  }

  @Override
  public void end() {
    stopMotor();
    state = State.IDLE;
    writeLogRow("end", false, false);
    closeLog();
  }

  private void startSequence() {
    startPosition = readRelativePositionOrZero();
    backTarget = startPosition;
    outTarget = startPosition + kTravelRotations;
    pauseUntilTimestamp = 0.0;
    moveStartedTimestamp = Timer.getTimestamp();
    state = State.MOVING_OUT;
    printStatus("sequence start");
    writeLogRow("sequence_start", true, false);
  }

  private void updateSequence() {
    Signal<Double> positionSignal = robot.armJoint0.getRelativeEncoderPosition();
    if (!positionSignal.isValid()) {
      stopMotor();
      return;
    }

    double position = positionSignal.get();
    switch (state) {
      case IDLE, DONE -> stopMotor();

      case MOVING_OUT -> driveOut(position);

      case PAUSING -> {
        stopMotor();
        if (pauseUntilTimestamp == 0.0) {
          pauseUntilTimestamp = Timer.getTimestamp() + kPauseSeconds;
          printStatus("pause start");
          writeLogRow("pause_start", false, false);
        } else if (Timer.getTimestamp() >= pauseUntilTimestamp) {
          pauseUntilTimestamp = 0.0;
          moveStartedTimestamp = Timer.getTimestamp();
          state = State.MOVING_BACK;
          printStatus("return start");
          writeLogRow("return_start", false, false);
        }
      }

      case MOVING_BACK -> driveBack(position);
    }
  }

  private void driveOut(double position) {
    double traveled = Math.abs(position - startPosition);
    if (traveled >= kTravelRotations - kPositionToleranceRotations) {
      stopMotor();
      state = State.PAUSING;
      printStatus("out reached");
      writeLogRow("out_reached", false, false);
      return;
    }

    if (Timer.getTimestamp() - moveStartedTimestamp > kMaxMoveSeconds) {
      stopMotor();
      state = State.IDLE;
      printStatus("out timeout");
      writeLogRow("out_timeout", false, false);
      return;
    }

    setArmThrottle(kOutThrottleDirection * kThrottle);
  }

  private void driveBack(double position) {
    double errorToStart = position - startPosition;
    if (Math.abs(errorToStart) <= kPositionToleranceRotations) {
      stopMotor();
      state = State.DONE;
      printStatus("back reached");
      writeLogRow("back_reached", false, false);
      return;
    }

    if (Timer.getTimestamp() - moveStartedTimestamp > kMaxMoveSeconds) {
      stopMotor();
      state = State.IDLE;
      printStatus("back timeout");
      writeLogRow("back_timeout", false, false);
      return;
    }

    setArmThrottle(-kOutThrottleDirection * kThrottle);
  }

  private double readRelativePositionOrZero() {
    Signal<Double> position = robot.armJoint0.getRelativeEncoderPosition();
    return position.isValid() ? position.get() : 0.0;
  }

  private void stopMotor() {
    robot.armJoint0.disable();
    lastCommandedThrottle = 0.0;
  }

  private void setArmThrottle(double throttle) {
    lastCommandedThrottle = throttle;
    robot.armJoint0.setThrottle(throttle);
  }

  private void printStatus(String source) {
    Signal<Double> position = robot.armJoint0.getRelativeEncoderPosition();
    Signal<Double> velocity = robot.armJoint0.getEncoderVelocity();
    Signal<Double> current = robot.armJoint0.getMotorCurrent();
    System.out.printf(
        "ArmJoint0 %s: state=%s start=%.3f outTarget=%.3f backTarget=%.3f position=%s outError=%s backError=%s velocity=%s current=%s%n",
        source,
        state,
        startPosition,
        outTarget,
        backTarget,
        format(position),
        formatError(position, outTarget),
        formatError(position, backTarget),
        format(velocity),
        format(current));
  }

  private void openLog() {
    closeLog();
    try {
      logWriter = openLogAt(kSystemCoreLogPath);
    } catch (IOException systemCoreError) {
      try {
        logWriter = openLogAt(kFallbackLogPath);
      } catch (IOException fallbackError) {
        System.out.printf(
            "ArmJoint0 log open failed: %s; fallback failed: %s%n",
            systemCoreError.getMessage(), fallbackError.getMessage());
        logWriter = null;
      }
    }
  }

  private BufferedWriter openLogAt(Path path) throws IOException {
    Path parent = path.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }

    BufferedWriter writer = Files.newBufferedWriter(path);
    writer.write(
        "time,event,state,square,startButton,startPosition,outTarget,backTarget,pauseUntil,"
            + "moveStarted,lastCommandedThrottle,reportedThrottle,relativeValid,relativePosition,"
            + "relativeErrorOut,relativeErrorBack,absoluteValid,absolutePosition,velocityValid,"
            + "velocity,currentValid,current,busVoltageValid,busVoltage,appliedOutputValid,"
            + "appliedOutput,temperatureValid,temperature,activeFaultValid,activeFault,"
            + "stickyFaultValid,stickyFault,activeWarningValid,activeWarning,stickyWarningValid,"
            + "stickyWarning\n");
    writer.flush();
    System.out.println("ArmJoint0 logging to " + path);
    return writer;
  }

  private void closeLog() {
    if (logWriter == null) {
      return;
    }

    try {
      logWriter.flush();
      logWriter.close();
    } catch (IOException error) {
      System.out.println("ArmJoint0 log close failed: " + error.getMessage());
    } finally {
      logWriter = null;
    }
  }

  private void writeLogRow(String event, boolean squarePressed, boolean startButtonPressed) {
    if (logWriter == null) {
      return;
    }

    Signal<Double> relativePosition = robot.armJoint0.getRelativeEncoderPosition();
    Signal<Double> absolutePosition = robot.armJoint0.getAbsoluteEncoderPosition();
    Signal<Double> velocity = robot.armJoint0.getEncoderVelocity();
    Signal<Double> current = robot.armJoint0.getMotorCurrent();
    Signal<Double> busVoltage = robot.armJoint0.getBusVoltage();
    Signal<Double> appliedOutput = robot.armJoint0.getAppliedOutput();
    Signal<Double> temperature = robot.armJoint0.getMotorTemperature();
    Signal<Boolean> activeFault = robot.armJoint0.hasActiveFault();
    Signal<Boolean> stickyFault = robot.armJoint0.hasStickyFault();
    Signal<Boolean> activeWarning = robot.armJoint0.hasActiveWarning();
    Signal<Boolean> stickyWarning = robot.armJoint0.hasStickyWarning();

    try {
      logWriter.write(
          String.format(
              Locale.US,
              "%.6f,%s,%s,%s,%s,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
              Timer.getTimestamp(),
              event,
              state,
              squarePressed,
              startButtonPressed,
              startPosition,
              outTarget,
              backTarget,
              pauseUntilTimestamp,
              moveStartedTimestamp,
              lastCommandedThrottle,
              robot.armJoint0.getThrottle(),
              relativePosition.isValid(),
              csvValue(relativePosition),
              csvError(relativePosition, outTarget),
              csvError(relativePosition, backTarget),
              absolutePosition.isValid(),
              csvValue(absolutePosition),
              velocity.isValid(),
              csvValue(velocity),
              current.isValid(),
              csvValue(current),
              busVoltage.isValid(),
              csvValue(busVoltage),
              appliedOutput.isValid(),
              csvValue(appliedOutput),
              temperature.isValid(),
              csvValue(temperature),
              activeFault.isValid(),
              csvValue(activeFault),
              stickyFault.isValid(),
              csvValue(stickyFault),
              activeWarning.isValid(),
              csvValue(activeWarning),
              stickyWarning.isValid(),
              csvValue(stickyWarning)));
      logWriter.flush();
    } catch (IOException error) {
      System.out.println("ArmJoint0 log write failed: " + error.getMessage());
      closeLog();
    }
  }

  private static String format(Signal<Double> signal) {
    if (!signal.isValid()) {
      return "invalid(" + signal.getError() + ")";
    }
    return String.format("%.3f", signal.get());
  }

  private static String formatError(Signal<Double> position, double target) {
    if (!position.isValid()) {
      return "invalid(" + position.getError() + ")";
    }
    return String.format("%.3f", target - position.get());
  }

  private static <T> String csvValue(Signal<T> signal) {
    if (!signal.isValid()) {
      return "";
    }
    return String.valueOf(signal.get());
  }

  private static String csvError(Signal<Double> position, double target) {
    if (!position.isValid()) {
      return "";
    }
    return String.format(Locale.US, "%.6f", target - position.get());
  }
}

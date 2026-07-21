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
    name = "Swerve Pod Software PID Test",
    group = "Test",
    description = "D11 absolute encoder read + software PID throttle only")
public class SwervePodSoftwarePidTest extends PeriodicOpMode {
  private static final double kForwardAbsolutePosition = 0.298;
  private static final double kStickDirectionDeadband = 0.12;
  private static final double kManualThetaDeadband = 0.08;
  private static final double kQuarterTurnRotations = 0.25;
  private static final double kThetaToleranceRotations = 0.015;

  private static final double kP = 0.80;
  private static final double kI = 0.0;
  private static final double kD = 0.0;
  private static final double kIntegralLimit = 0.15;
  private static final double kMinThetaThrottle = 0.10;
  private static final double kMaxThetaThrottle = 0.20;
  private static final double kMinThrottleErrorRotations = 0.04;

  private static final double kMaxPlausibleVelocityRpm = 1500.0;
  private static final double kMaxPlausiblePositionStepRotations = 0.25;
  private static final double kNoEncoderMovementSeconds = 0.60;
  private static final double kNoEncoderMovementThrottle = 0.08;
  private static final double kMinObservedPositionChangeRotations = 0.002;

  private static final Path kSystemCoreLogPath =
      Path.of("/home/systemcore/deploy/swerve_theta_software_pid_log.csv");
  private static final Path kFallbackLogPath = Path.of("build/swerve_theta_software_pid_log.csv");

  private final Robot robot;
  private final DefaultUserControls userControls;
  private final Timer statusTimer = new Timer();

  private boolean hasTarget;
  private boolean tripped;
  private String tripReason = "";
  private double targetAbsolutePosition;
  private double previousError;
  private double errorIntegral;
  private double previousTimestamp;
  private double previousPosition;
  private boolean hasPreviousPosition;
  private double lastCommandedThrottle;
  private double lastLeftStickMagnitude;
  private double lastRightX;
  private double lastManualTheta;
  private boolean wasManualThetaActive;
  private boolean movementCheckActive;
  private double movementCheckStartPosition;
  private double movementCheckStartTimestamp;
  private BufferedWriter logWriter;

  public SwervePodSoftwarePidTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    openLog();
    robot.disableA301s();
    robot.swerveDrive.disable();

    Signal<Double> thetaPosition = robot.swerveTheta.getAbsoluteEncoderPosition();

    hasTarget = thetaPosition.isValid();
    tripped = false;
    tripReason = "";
    targetAbsolutePosition =
        thetaPosition.isValid() ? thetaPosition.get() : kForwardAbsolutePosition;
    previousError = 0.0;
    errorIntegral = 0.0;
    previousTimestamp = Timer.getTimestamp();
    previousPosition = 0.0;
    hasPreviousPosition = false;
    lastCommandedThrottle = 0.0;
    lastLeftStickMagnitude = 0.0;
    lastRightX = 0.0;
    lastManualTheta = 0.0;
    wasManualThetaActive = false;
    movementCheckActive = false;
    movementCheckStartPosition = 0.0;
    movementCheckStartTimestamp = 0.0;

    statusTimer.restart();
    System.out.println(
        "Swerve software PID start: no A301 position-control APIs will be called");
    writeLogRow("start");
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);

    if (gamepad.getStartButtonPressed()) {
      trip("driver_start_button");
    }

    Signal<Double> thetaPosition = robot.swerveTheta.getAbsoluteEncoderPosition();
    Signal<Double> thetaVelocity = robot.swerveTheta.getEncoderVelocity();
    Signal<Boolean> activeFault = robot.swerveTheta.hasActiveFault();
    Signal<Boolean> activeWarning = robot.swerveTheta.hasActiveWarning();

    double now = Timer.getTimestamp();
    double dtSeconds = clamp(now - previousTimestamp, 0.001, 0.05);
    previousTimestamp = now;

    double x = gamepad.getLeftX();
    double y = -gamepad.getLeftY();
    double magnitude = Math.min(1.0, Math.hypot(x, y));
    double rightX = gamepad.getRightX();
    double manualTheta = applyDeadband(rightX, kManualThetaDeadband);
    boolean manualThetaActive = manualTheta != 0.0;
    lastLeftStickMagnitude = magnitude;
    lastRightX = rightX;
    lastManualTheta = manualTheta;

    if (!tripped && thetaPosition.isValid()) {
      if (magnitude >= kStickDirectionDeadband) {
        double directionRotations = Math.atan2(x, y) / (2.0 * Math.PI);
        targetAbsolutePosition = wrapRotations(kForwardAbsolutePosition + directionRotations);
        hasTarget = true;
        errorIntegral = 0.0;
        previousError = 0.0;
        resetMovementCheck();
      } else if (manualThetaActive && !wasManualThetaActive) {
        if (manualTheta > 0.0) {
          targetAbsolutePosition =
              wrapRotations(kForwardAbsolutePosition + kQuarterTurnRotations);
        } else {
          targetAbsolutePosition =
              wrapRotations(kForwardAbsolutePosition - kQuarterTurnRotations);
        }
        hasTarget = true;
        errorIntegral = 0.0;
        previousError = 0.0;
        resetMovementCheck();
      }
    }
    wasManualThetaActive = manualThetaActive;

    if (!isTelemetrySafe(thetaPosition, thetaVelocity, activeFault, activeWarning)) {
      trip("unsafe_telemetry");
    }

    if (tripped || !hasTarget || !thetaPosition.isValid()) {
      stopTheta();
      writeLogRow(tripped ? "tripped" : "idle");
      printStatusIfDue(magnitude, thetaPosition, thetaVelocity, activeFault, activeWarning);
      return;
    }

    double error = wrapRotations(targetAbsolutePosition - thetaPosition.get());
    if (Math.abs(error) <= kThetaToleranceRotations) {
      errorIntegral = 0.0;
      previousError = error;
      resetMovementCheck();
      stopTheta();
      writeLogRow("at_target");
      printStatusIfDue(magnitude, thetaPosition, thetaVelocity, activeFault, activeWarning);
      return;
    }

    errorIntegral = clamp(errorIntegral + error * dtSeconds, -kIntegralLimit, kIntegralLimit);
    double derivative = wrapRotations(error - previousError) / dtSeconds;
    previousError = error;

    double throttle = clamp(
        (kP * error) + (kI * errorIntegral) + (kD * derivative),
        -kMaxThetaThrottle,
        kMaxThetaThrottle);
    if (Math.abs(error) >= kMinThrottleErrorRotations) {
      throttle = Math.copySign(
          Math.max(Math.abs(throttle), kMinThetaThrottle),
          throttle);
    }
    if (!isEncoderMovingWhenCommanded(thetaPosition, throttle, now)) {
      trip("encoder_not_moving");
      writeLogRow("tripped");
      printStatusIfDue(magnitude, thetaPosition, thetaVelocity, activeFault, activeWarning);
      return;
    }
    setThetaThrottle(throttle);
    writeLogRow("pid");
    printStatusIfDue(magnitude, thetaPosition, thetaVelocity, activeFault, activeWarning);
  }

  @Override
  public void end() {
    stopTheta();
    robot.swerveDrive.disable();
    writeLogRow("end");
    closeLog();
  }

  private boolean isTelemetrySafe(
      Signal<Double> thetaPosition,
      Signal<Double> thetaVelocity,
      Signal<Boolean> activeFault,
      Signal<Boolean> activeWarning) {
    if (!thetaPosition.isValid()) {
      tripReason = "absolute_position_invalid:" + thetaPosition.getError();
      return false;
    }

    double position = thetaPosition.get();
    if (position < -0.55 || position > 0.55) {
      tripReason = String.format(Locale.US, "absolute_position_out_of_range:%.6f", position);
      return false;
    }

    if (hasPreviousPosition) {
      double positionStep = Math.abs(wrapRotations(position - previousPosition));
      if (positionStep > kMaxPlausiblePositionStepRotations) {
        tripReason = String.format(Locale.US, "absolute_position_jump:%.6f", positionStep);
        return false;
      }
    }
    previousPosition = position;
    hasPreviousPosition = true;

    if (activeFault.isValid() && activeFault.get()) {
      tripReason = "active_fault";
      return false;
    }

    return true;
  }

  private void trip(String reason) {
    if (!tripped) {
      tripped = true;
      if (tripReason.isBlank()) {
        tripReason = reason;
      }
      System.out.println("Swerve software PID tripped: " + tripReason);
    }
    stopTheta();
  }

  private void stopTheta() {
    robot.swerveTheta.disable();
    lastCommandedThrottle = 0.0;
  }

  private boolean isEncoderMovingWhenCommanded(
      Signal<Double> thetaPosition,
      double throttle,
      double now) {
    if (!thetaPosition.isValid() || Math.abs(throttle) < kNoEncoderMovementThrottle) {
      resetMovementCheck();
      return true;
    }

    if (!movementCheckActive) {
      movementCheckActive = true;
      movementCheckStartPosition = thetaPosition.get();
      movementCheckStartTimestamp = now;
      return true;
    }

    double observedMovement =
        Math.abs(wrapRotations(thetaPosition.get() - movementCheckStartPosition));
    if (observedMovement >= kMinObservedPositionChangeRotations) {
      movementCheckStartPosition = thetaPosition.get();
      movementCheckStartTimestamp = now;
      return true;
    }

    if (now - movementCheckStartTimestamp >= kNoEncoderMovementSeconds) {
      tripReason =
          String.format(
              Locale.US,
              "encoder_not_moving:throttle=%.3f observed=%.6f",
              throttle,
              observedMovement);
      return false;
    }

    return true;
  }

  private void resetMovementCheck() {
    movementCheckActive = false;
    movementCheckStartPosition = 0.0;
    movementCheckStartTimestamp = 0.0;
  }

  private void setThetaThrottle(double throttle) {
    lastCommandedThrottle = throttle;
    robot.swerveTheta.setThrottle(throttle);
  }

  private void printStatusIfDue(
      double magnitude,
      Signal<Double> thetaPosition,
      Signal<Double> thetaVelocity,
      Signal<Boolean> activeFault,
      Signal<Boolean> activeWarning) {
    if (!statusTimer.advanceIfElapsed(0.25)) {
      return;
    }

    String thetaError =
        thetaPosition.isValid()
            ? String.format(
                Locale.US, "%.4f", wrapRotations(targetAbsolutePosition - thetaPosition.get()))
            : "invalid";
    String velocityStatus =
        isVelocityPlausible(thetaVelocity) ? "ok" : "implausible";

    System.out.printf(
        "Swerve software PID: targetActive=%s tripped=%s reason=%s stick=%.3f "
            + "rightX=%.3f manualTheta=%.3f target=%.4f theta=%s error=%s throttle=%.3f "
            + "velocity=%s velocityStatus=%s fault=%s warning=%s%n",
        hasTarget,
        tripped,
        tripReason,
        magnitude,
        lastRightX,
        lastManualTheta,
        targetAbsolutePosition,
        format(thetaPosition),
        thetaError,
        lastCommandedThrottle,
        format(thetaVelocity),
        velocityStatus,
        format(activeFault),
        format(activeWarning));
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
            "Swerve software PID log open failed: %s; fallback failed: %s%n",
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
        "time,event,targetActive,tripped,tripReason,leftStickMagnitude,rightX,manualTheta,"
            + "targetAbsolutePosition,lastCommandedThrottle,reportedThrottle,absoluteValid,"
            + "absolutePosition,error,velocityValid,velocity,currentValid,current,busVoltageValid,"
            + "busVoltage,activeFaultValid,activeFault,activeWarningValid,activeWarning\n");
    writer.flush();
    System.out.println("Swerve software PID logging to " + path);
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
      System.out.println("Swerve software PID log close failed: " + error.getMessage());
    } finally {
      logWriter = null;
    }
  }

  private void writeLogRow(String event) {
    if (logWriter == null) {
      return;
    }

    Signal<Double> thetaPosition = robot.swerveTheta.getAbsoluteEncoderPosition();
    Signal<Double> thetaVelocity = robot.swerveTheta.getEncoderVelocity();
    Signal<Double> current = robot.swerveTheta.getMotorCurrent();
    Signal<Double> busVoltage = robot.swerveTheta.getBusVoltage();
    Signal<Boolean> activeFault = robot.swerveTheta.hasActiveFault();
    Signal<Boolean> activeWarning = robot.swerveTheta.hasActiveWarning();
    String positionError =
        thetaPosition.isValid()
            ? String.format(
                Locale.US, "%.6f", wrapRotations(targetAbsolutePosition - thetaPosition.get()))
            : "";

    try {
      logWriter.write(
          String.format(
              Locale.US,
              "%.6f,%s,%s,%s,%s,%.6f,%.6f,%.6f,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
              Timer.getTimestamp(),
              event,
              hasTarget,
              tripped,
              csvText(tripReason),
              lastLeftStickMagnitude,
              lastRightX,
              lastManualTheta,
              targetAbsolutePosition,
              lastCommandedThrottle,
              robot.swerveTheta.getThrottle(),
              thetaPosition.isValid(),
              csvValue(thetaPosition),
              positionError,
              thetaVelocity.isValid(),
              csvValue(thetaVelocity),
              current.isValid(),
              csvValue(current),
              busVoltage.isValid(),
              csvValue(busVoltage),
              activeFault.isValid(),
              csvValue(activeFault),
              activeWarning.isValid(),
              csvValue(activeWarning)));
      logWriter.flush();
    } catch (IOException ioError) {
      System.out.println("Swerve software PID log write failed: " + ioError.getMessage());
      closeLog();
    }
  }

  private static double wrapRotations(double rotations) {
    return rotations - Math.floor(rotations + 0.5);
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  private static double applyDeadband(double value, double deadband) {
    return Math.abs(value) < deadband ? 0.0 : value;
  }

  private static boolean isVelocityPlausible(Signal<Double> velocity) {
    return !velocity.isValid() || Math.abs(velocity.get()) <= kMaxPlausibleVelocityRpm;
  }

  private static String format(Signal<?> signal) {
    return signal.isValid() ? String.valueOf(signal.get()) : "invalid(" + signal.getError() + ")";
  }

  private static <T> String csvValue(Signal<T> signal) {
    if (!signal.isValid()) {
      return "";
    }
    return String.valueOf(signal.get());
  }

  private static String csvText(String value) {
    return "\"" + value.replace("\"", "\"\"") + "\"";
  }
}

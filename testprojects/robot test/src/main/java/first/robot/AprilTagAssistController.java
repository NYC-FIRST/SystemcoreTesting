package first.robot;

import first.robot.vision.AprilTagTarget;

/**
 * Converts one AprilTag pose into mecanum drive commands.
 *
 * <p>This intentionally follows the FTC SDK {@code RobotAutoDriveToAprilTagOmni} sample:
 *
 * <ul>
 *   <li>range error controls forward/backward motion,
 *   <li>tag yaw controls left/right strafe, and
 *   <li>tag bearing controls robot rotation.
 * </ul>
 *
 * <p>The FTC sample is a proportional-only PID controller: output = error * gain. Keeping the
 * calculation here makes the three pose-to-motion relationships easy to find and tune.
 */
public final class AprilTagAssistController {
  public static final double DESIRED_RANGE_METERS = 0.30;

  public static final double RANGE_GAIN = 1.2; // forward command per meter of range error
  public static final double YAW_GAIN = 1.0; // strafe command per radian of yaw error
  public static final double BEARING_GAIN = 1.0; // turn command per radian of bearing error

  public static final double MAX_FORWARD = 0.25;
  public static final double MAX_STRAFE = 0.25;
  public static final double MAX_TURN = 0.20;

  public static final double RANGE_TOLERANCE_METERS = 0.02;
  public static final double ANGLE_TOLERANCE_RADIANS = Math.toRadians(2.0);

  /**
   * Calculates one robot-relative command.
   *
   * <p>{@link org.wpilib.drive.MecanumDrive#driveCartesian(double, double, double)} uses positive X
   * forward, positive Y left, and positive rotation counterclockwise.
   */
  public DriveCommand calculate(AprilTagTarget tag) {
    if (!tag.hasUsablePose()) {
      throw new IllegalArgumentException("AprilTag pose contains invalid drive values: " + tag);
    }

    double rangeError = tag.rangeMeters() - DESIRED_RANGE_METERS;
    double bearingError = tag.bearingRadians();
    double yawError = tag.yawRadians();

    double forward =
        clamp(
            outsideTolerance(rangeError, RANGE_TOLERANCE_METERS) * RANGE_GAIN,
            -MAX_FORWARD,
            MAX_FORWARD);
    double strafe =
        clamp(
            -outsideTolerance(yawError, ANGLE_TOLERANCE_RADIANS) * YAW_GAIN,
            -MAX_STRAFE,
            MAX_STRAFE);
    double turn =
        clamp(
            outsideTolerance(bearingError, ANGLE_TOLERANCE_RADIANS) * BEARING_GAIN,
            -MAX_TURN,
            MAX_TURN);

    return new DriveCommand(
        forward, strafe, turn, rangeError, bearingError, yawError);
  }

  /** Pose errors and the mecanum command produced from them. */
  public record DriveCommand(
      double forward,
      double strafe,
      double turn,
      double rangeErrorMeters,
      double bearingErrorRadians,
      double yawErrorRadians) {}

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }

  private static double outsideTolerance(double error, double tolerance) {
    return Math.abs(error) <= tolerance ? 0.0 : error;
  }
}

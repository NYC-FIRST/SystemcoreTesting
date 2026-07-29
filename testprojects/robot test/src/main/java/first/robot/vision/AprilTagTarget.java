package first.robot.vision;

/**
 * Camera measurement used by the drive-to-tag controller.
 *
 * @param id AprilTag ID, or -1 when the SystemCore best-target topic does not expose the ID
 * @param rangeMeters horizontal distance from camera to tag
 * @param bearingRadians tag direction; positive is camera-left
 * @param yawRadians tag face angle; positive is counterclockwise
 * @param timestampMicros camera frame timestamp
 */
public record AprilTagTarget(
    int id,
    double rangeMeters,
    double bearingRadians,
    double yawRadians,
    long timestampMicros) {

  /** True when every value needed by the drivetrain is finite and physically usable. */
  public boolean hasUsablePose() {
    return rangeMeters > 0.0
        && Double.isFinite(rangeMeters)
        && Double.isFinite(bearingRadians)
        && Double.isFinite(yawRadians);
  }
}

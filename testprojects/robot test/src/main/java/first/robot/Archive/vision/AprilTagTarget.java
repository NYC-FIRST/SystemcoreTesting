package first.robot.Archive.vision;

/**
 * Camera measurement used by the drive-to-tag controller.
 *
 * @param id AprilTag ID
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
    long timestampMicros) {}

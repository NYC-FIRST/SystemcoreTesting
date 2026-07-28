package first.robot.YGSLSwerveTemplate.vision;

/**
 * Camera + AprilTag configuration.
 *
 * <p>The FX/FY/CX/CY values are a bring-up starting point only. Replace them with a real
 * calibration for the physical camera at the resolution below, or range/pose estimates will be
 * inaccurate.
 */
public final class VisionConstants {

    private VisionConstants() {}

    public static final int CAMERA_INDEX = 0;
    public static final int IMAGE_WIDTH = 640;
    public static final int IMAGE_HEIGHT = 480;
    public static final int FPS = 30;

    public static final String TAG_FAMILY = "tag36h11";
    public static final double TAG_SIZE_METERS = 0.1651; // 6.5 in; change for your tags.

    // Placeholder camera calibration. Replace with a real calibration for this camera/resolution.
    public static final double FX = 578.0;
    public static final double FY = 578.0;
    public static final double CX = 320.0;
    public static final double CY = 240.0;

    // A target is considered stale (and getEstimatedPose/hasTargets should ignore it)
    // if it hasn't been updated in this long.
    public static final long TARGET_TIMEOUT_MICROS = 250_000;

    // Use -1 to accept the closest visible tag regardless of ID.
    public static final int DEFAULT_DESIRED_TAG_ID = -1;
}
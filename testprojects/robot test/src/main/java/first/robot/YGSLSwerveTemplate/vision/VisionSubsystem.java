package first.robot.YGSLSwerveTemplate.vision;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.vision.apriltag.AprilTagDetection;
import org.wpilib.vision.apriltag.AprilTagDetector;
import org.wpilib.vision.apriltag.AprilTagPoseEstimator;
import org.wpilib.vision.camera.CvSink;
import org.wpilib.vision.camera.UsbCamera;
import org.wpilib.vision.stream.CameraServer;

/**
 * USB camera + AprilTag detection subsystem.
 *
 * <p>Runs the camera grab/detect loop on a background thread so {@link #periodic()} stays cheap;
 * {@code periodic()} just republishes whatever the worker thread last found. Ported from the
 * standalone {@code AprilTagVision} bring-up class used elsewhere in this repo.
 *
 * <p>Camera calibration and tag size live in {@link VisionConstants} - update those for your
 * actual camera before trusting range/pose numbers out of this class.
 */
public class VisionSubsystem extends SubsystemBase implements AutoCloseable {

    /** One camera-relative target measurement. */
    public record Target(
            int id,
            double rangeMeters,
            double bearingRadians,
            double yawRadians,
            long timestampMicros) {}

    private final int desiredTagId;
    private final AprilTagDetector detector = new AprilTagDetector();
    private final AprilTagPoseEstimator poseEstimator =
            new AprilTagPoseEstimator(
                    new AprilTagPoseEstimator.Config(
                            VisionConstants.TAG_SIZE_METERS,
                            VisionConstants.FX,
                            VisionConstants.FY,
                            VisionConstants.CX,
                            VisionConstants.CY));

    private final UsbCamera camera;
    private final CvSink sink;
    private final Thread worker;

    private volatile Target latestTarget;
    private volatile boolean running = true;

    private final VisionTelemetry telemetry;

    /** Uses {@link VisionConstants#DEFAULT_DESIRED_TAG_ID} (accept any visible tag). */
    public VisionSubsystem() {
        this(VisionConstants.DEFAULT_DESIRED_TAG_ID);
    }

    /** @param desiredTagId tag ID to prefer, or -1 to accept the closest visible tag. */
    public VisionSubsystem(int desiredTagId) {
        this.desiredTagId = desiredTagId;
        detector.addFamily(VisionConstants.TAG_FAMILY);

        camera = CameraServer.startAutomaticCapture(VisionConstants.CAMERA_INDEX);
        camera.setResolution(VisionConstants.IMAGE_WIDTH, VisionConstants.IMAGE_HEIGHT);
        camera.setFPS(VisionConstants.FPS);
        sink = CameraServer.getVideo(camera);

        worker = new Thread(this::processFrames, "VisionSubsystem");
        worker.setDaemon(true);
        worker.start();

        telemetry = new VisionTelemetry();
    }

    @Override
    public void periodic() {
        // Camera capture/detection happens on the worker thread; periodic() just republishes
        // whatever the worker last found, so it stays cheap and safe to call every robot loop.
        telemetry.update();
    }

    /** True if a target has been seen recently (see {@link VisionConstants#TARGET_TIMEOUT_MICROS}). */
    public boolean hasTargets() {
        return getTarget() != null;
    }

    /** ID of the best currently-tracked tag, or -1 if none is visible/fresh. */
    public int getBestTagID() {
        Target target = getTarget();
        return target == null ? -1 : target.id();
    }

    /**
     * Camera-relative pose of the best tracked tag, projected onto the ground plane: X is
     * straight ahead of the camera, Y is to the camera's left, and rotation is the tag's yaw.
     * Returns an identity {@link Pose2d} if no target is currently visible/fresh - always check
     * {@link #hasTargets()} first if "no data" needs to be distinguished from "tag at the origin".
     */
    public Pose2d getEstimatedPose() {
        Target target = getTarget();
        if (target == null) {
            return new Pose2d();
        }
        double x = target.rangeMeters() * Math.cos(target.bearingRadians());
        double y = target.rangeMeters() * Math.sin(target.bearingRadians());
        return new Pose2d(x, y, new Rotation2d(target.yawRadians()));
    }

    /** Full target record (range/bearing/yaw/timestamp), if a fresh one exists. */
    public Target getTarget() {
        Target target = latestTarget;
        if (target == null
                || (nowMicros() - target.timestampMicros()) > VisionConstants.TARGET_TIMEOUT_MICROS) {
            return null;
        }
        return target;
    }

    private void processFrames() {
        Mat bgr = new Mat();
        Mat gray = new Mat();
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                if (sink.grabFrame(bgr) == 0) {
                    latestTarget = null;
                    continue;
                }
                long receivedTimestamp = nowMicros();

                Imgproc.cvtColor(bgr, gray, Imgproc.COLOR_BGR2GRAY);
                Target best = null;
                for (AprilTagDetection detection : detector.detect(gray)) {
                    if (desiredTagId >= 0 && detection.getId() != desiredTagId) {
                        continue;
                    }

                    Transform3d cameraToTag = poseEstimator.estimate(detection);
                    double x = cameraToTag.getX();
                    double y = cameraToTag.getY();
                    double range = Math.hypot(x, y);
                    Target candidate =
                            new Target(
                                    detection.getId(),
                                    range,
                                    Math.atan2(y, x),
                                    cameraToTag.getRotation().getZ(),
                                    receivedTimestamp);

                    if (best == null || candidate.rangeMeters() < best.rangeMeters()) {
                        best = candidate;
                    }
                }
                latestTarget = best;
            }
        } catch (RuntimeException error) {
            latestTarget = null;
            System.err.println("VisionSubsystem stopped: " + error.getMessage());
        } finally {
            bgr.release();
            gray.release();
        }
    }

    private static long nowMicros() {
        return System.nanoTime() / 1_000;
    }

    @Override
    public void close() {
        running = false;
        worker.interrupt();
        sink.close();
        camera.close();
        detector.close();
    }

    // Minimal telemetry holder; extend if you need to publish values to dashboards.
    private static class VisionTelemetry {
        public VisionTelemetry() {}

        public void update() {
            // no-op for now; worker thread updates latestTarget directly
        }
    }
}
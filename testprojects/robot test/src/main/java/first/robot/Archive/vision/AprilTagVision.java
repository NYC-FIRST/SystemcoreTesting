package first.robot.Archive.vision;

import java.util.Optional;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.vision.apriltag.AprilTagDetection;
import org.wpilib.vision.apriltag.AprilTagDetector;
import org.wpilib.vision.apriltag.AprilTagPoseEstimator;
import org.wpilib.vision.camera.CvSink;
import org.wpilib.vision.camera.UsbCamera;
import org.wpilib.vision.stream.CameraServer;

/**
 * Small SystemCore AprilTag camera service.
 *
 * <p>The camera calibration values must match the selected resolution and physical camera. The
 * defaults are only a bring-up starting point for a typical 640x480 USB camera.
 */
public final class AprilTagVision implements AutoCloseable {
  public static final int IMAGE_WIDTH = 640;
  public static final int IMAGE_HEIGHT = 480;
  public static final int FPS = 30;
  public static final double TAG_SIZE_METERS = 0.1651; // 6.5 inches; change for your tags.

  // Replace these four values with a calibration for your camera at 640x480.
  public static final double FX = 578.0;
  public static final double FY = 578.0;
  public static final double CX = 320.0;
  public static final double CY = 240.0;

  private final int desiredTagId;
  private final AprilTagDetector detector = new AprilTagDetector();
  private final AprilTagPoseEstimator poseEstimator =
      new AprilTagPoseEstimator(
          new AprilTagPoseEstimator.Config(TAG_SIZE_METERS, FX, FY, CX, CY));
  private final UsbCamera camera;
  private final CvSink sink;
  private final Thread worker;

  private volatile AprilTagTarget latestTarget;
  private volatile boolean running = true;

  /**
   * Starts USB camera 0 and looks for the requested tag. Use -1 to accept the closest visible tag.
   */
  public AprilTagVision(int desiredTagId) {
    this.desiredTagId = desiredTagId;
    detector.addFamily("tag36h11");

    camera = CameraServer.startAutomaticCapture(0);
    camera.setResolution(IMAGE_WIDTH, IMAGE_HEIGHT);
    camera.setFPS(FPS);
    sink = CameraServer.getVideo(camera);

    worker = new Thread(this::processFrames, "AprilTagVision");
    worker.setDaemon(true);
    worker.start();
  }

  /** Returns the newest target, or empty when no matching tag was seen recently. */
  public Optional<AprilTagTarget> getTarget() {
    AprilTagTarget target = latestTarget;
    if (target == null || (nowMicros() - target.timestampMicros()) > 250_000) {
      return Optional.empty();
    }
    return Optional.of(target);
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
        AprilTagTarget best = null;
        for (AprilTagDetection detection : detector.detect(gray)) {
          if (desiredTagId >= 0 && detection.getId() != desiredTagId) {
            continue;
          }

          Transform3d cameraToTag = poseEstimator.estimate(detection);
          double x = cameraToTag.getX();
          double y = cameraToTag.getY();
          double range = Math.hypot(x, y);
          AprilTagTarget candidate =
              new AprilTagTarget(
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
      System.err.println("AprilTag vision stopped: " + error.getMessage());
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
}

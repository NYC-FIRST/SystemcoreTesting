package first.robot.vision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.wpilib.networktables.DoubleArraySubscriber;
import org.wpilib.networktables.DoubleSubscriber;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.NetworkTablesJNI;
import org.wpilib.networktables.RawSubscriber;
import org.wpilib.networktables.TimestampedDouble;
import org.wpilib.networktables.TimestampedDoubleArray;
import org.wpilib.networktables.TimestampedRaw;
import org.wpilib.networktables.Topic;

/**
 * Reads the SystemCore Limelight service through its Classic NetworkTables API.
 *
 * <p>Enable {@code Use Classic NT API} in the pipeline's Output & Crosshair tab. The table name is
 * discovered at runtime, so the camera does not need to be renamed for this robot program.
 */
public final class AprilTagVision implements AutoCloseable {
  public static final String DEFAULT_TABLE_NAME = "limelight";
  public static final long TARGET_TIMEOUT_MICROS = 250_000;

  private final int desiredTagId;
  private final NetworkTableInstance networkTables = NetworkTableInstance.getDefault();

  private String tableName;
  private DoubleSubscriber targetValid;
  private DoubleSubscriber targetId;
  private DoubleArraySubscriber targetPose;
  private RawSubscriber packedResults;

  /** Subscribes to the best visible tag, or to one ID when {@code desiredTagId >= 0}. */
  public AprilTagVision(int desiredTagId) {
    this.desiredTagId = desiredTagId;
    bind(DEFAULT_TABLE_NAME);
    refreshBinding();
  }

  /** Returns the newest matching target, or empty when no fresh 3D target pose is available. */
  public Optional<AprilTagTarget> getTarget() {
    refreshBinding();
    if (packedResults.exists()) {
      TimestampedRaw packedSample = packedResults.getAtomic();
      if (isFresh(packedSample.timestamp)) {
        return decodePackedTarget(packedSample.value, desiredTagId, packedSample.timestamp).target();
      }
      return Optional.empty();
    }

    if (targetValid.get() < 0.5) {
      return Optional.empty();
    }

    int id = (int) Math.round(targetId.get());
    if (desiredTagId >= 0 && id != desiredTagId) {
      return Optional.empty();
    }

    TimestampedDoubleArray sample = targetPose.getAtomic();
    if (!isFresh(sample.timestamp)) {
      return Optional.empty();
    }
    return targetFromLimelightRobotSpacePose(sample.value, id, sample.timestamp);
  }

  /** Returns raw connection state for button-press diagnostics. */
  public VisionStatus getStatus() {
    refreshBinding();
    TimestampedDouble detection = targetValid.getAtomic();
    TimestampedDoubleArray pose = targetPose.getAtomic();
    TimestampedRaw packed = packedResults.getAtomic();
    long now = NetworkTablesJNI.now();
    PackedTarget packedTarget =
        packedResults.exists()
            ? decodePackedTarget(packed.value, desiredTagId, packed.timestamp)
            : new PackedTarget(Optional.empty(), false, "not published");
    boolean usingPackedResults = packedResults.exists();
    return new VisionStatus(
        tableName,
        targetValid.exists() || usingPackedResults,
        usingPackedResults ? packedTarget.cameraReportsTarget() : detection.value >= 0.5,
        ageMillis(now, usingPackedResults ? packed.timestamp : detection.timestamp),
        targetPose.exists() || usingPackedResults,
        usingPackedResults
            ? packedResults.getTopic().getTypeString()
            : targetPose.getTopic().getTypeString(),
        ageMillis(now, usingPackedResults ? packed.timestamp : pose.timestamp),
        usingPackedResults ? packedTarget.detail() : "Classic scalar topics");
  }

  /**
   * Lists relevant published topics. This is logged when the expected Limelight table is absent.
   */
  public String publishedVisionTopicsSummary() {
    String topics =
        Arrays.stream(networkTables.getTopics())
            .map(Topic::getName)
            .filter(AprilTagVision::looksLikeVisionTopic)
            .sorted()
            .limit(40)
            .collect(Collectors.joining(", "));
    return topics.isEmpty() ? "<no Limelight/vision topics published>" : topics;
  }

  /**
   * Converts Limelight's {@code targetpose_robotspace}:
   * {@code [forward, right, up, roll, pitch, yaw]} in meters and degrees.
   *
   * <p>In this coordinate system the final rotation is the tag face angle about the robot's
   * vertical axis. A square-on tag is approximately 180 degrees, which is converted to FTC-style
   * zero yaw error.
   */
  static Optional<AprilTagTarget> targetFromLimelightRobotSpacePose(
      double[] pose, int id, long timestampMicros) {
    if (pose.length < 6) {
      return Optional.empty();
    }
    for (int i = 0; i < 6; i++) {
      if (!Double.isFinite(pose[i])) {
        return Optional.empty();
      }
    }

    double forward = pose[0];
    double right = pose[1];
    double range = Math.hypot(forward, right);
    double bearing = Math.atan2(-right, forward);
    double yaw = wrapRadians(Math.toRadians(pose[5]) - Math.PI);
    AprilTagTarget target =
        new AprilTagTarget(id, range, bearing, yaw, timestampMicros);
    return target.hasUsablePose() ? Optional.of(target) : Optional.empty();
  }

  private void refreshBinding() {
    String discovered = discoverLimelightTable();
    if (discovered != null && !discovered.equals(tableName)) {
      bind(discovered);
    }
  }

  private String discoverLimelightTable() {
    String validTargetFallback = null;
    for (Topic topic : networkTables.getTopics()) {
      String topicName = topic.getName();
      if (topicName.endsWith("/results_msgpack")
          || topicName.endsWith("/targetpose_robotspace")) {
        return tableNameFromTopic(topicName);
      }
      if (validTargetFallback == null && topicName.endsWith("/tv")) {
        validTargetFallback = tableNameFromTopic(topicName);
      }
    }
    return validTargetFallback;
  }

  private void bind(String newTableName) {
    if (targetValid != null) {
      targetValid.close();
      targetId.close();
      targetPose.close();
      packedResults.close();
    }

    tableName = newTableName;
    NetworkTable table = networkTables.getTable(tableName);
    targetValid = table.getDoubleTopic("tv").subscribe(0.0);
    targetId = table.getDoubleTopic("tid").subscribe(-1.0);
    targetPose =
        table.getDoubleArrayTopic("targetpose_robotspace").subscribe(new double[0]);
    String packedType = table.getTopic("results_msgpack").getTypeString();
    packedResults =
        table
            .getRawTopic("results_msgpack")
            .subscribe(packedType == null || packedType.isBlank() ? "msgpack" : packedType, new byte[0]);
    System.out.printf("AprilTagVision: using Classic Limelight table /%s%n", tableName);
  }

  private static String tableNameFromTopic(String topicName) {
    int end = topicName.lastIndexOf('/');
    return topicName.substring(topicName.startsWith("/") ? 1 : 0, end);
  }

  private static boolean looksLikeVisionTopic(String topicName) {
    String lower = topicName.toLowerCase();
    return lower.contains("limelight")
        || lower.contains("photonvision")
        || lower.contains("targetpose")
        || lower.endsWith("/tv")
        || lower.endsWith("/tid");
  }

  private static double ageMillis(long nowMicros, long timestampMicros) {
    return timestampMicros == 0
        ? Double.NaN
        : Math.max(0L, nowMicros - timestampMicros) / 1_000.0;
  }

  private static boolean isFresh(long timestampMicros) {
    return timestampMicros != 0
        && (NetworkTablesJNI.now() - timestampMicros) <= TARGET_TIMEOUT_MICROS;
  }

  private static PackedTarget decodePackedTarget(
      byte[] packed, int desiredTagId, long timestampMicros) {
    if (packed.length == 0) {
      return new PackedTarget(Optional.empty(), false, "empty results_msgpack");
    }

    try {
      Object decoded = MessagePackReader.decode(packed);
      if (!(decoded instanceof Map<?, ?> root)) {
        return new PackedTarget(
            Optional.empty(), false, "decoded root=" + decoded.getClass().getSimpleName());
      }

      Map<?, ?> results = mapValue(root.get("Results")).orElse(root);
      Object fiducialsValue = firstPresent(results, "Fiducial", "Fiducials", "fiducial");
      if (!(fiducialsValue instanceof List<?> fiducials)) {
        return new PackedTarget(
            Optional.empty(), booleanValue(results.get("v")), "keys=" + results.keySet());
      }

      boolean cameraReportsTarget = booleanValue(results.get("v")) || !fiducials.isEmpty();
      for (Object value : fiducials) {
        if (!(value instanceof Map<?, ?> fiducial)) {
          continue;
        }
        int id = intValue(firstPresent(fiducial, "fID", "fid", "id"), -1);
        if (desiredTagId >= 0 && id != desiredTagId) {
          continue;
        }

        double[] robotSpacePose =
            doubleArrayValue(firstPresent(fiducial, "t6t_rs", "targetpose_robotspace"));
        Optional<AprilTagTarget> target =
            targetFromLimelightRobotSpacePose(robotSpacePose, id, timestampMicros);
        if (target.isPresent()) {
          return new PackedTarget(
              target, cameraReportsTarget, "decoded Fiducial id=" + id);
        }
      }
      return new PackedTarget(
          Optional.empty(),
          cameraReportsTarget,
          "fiducials=" + fiducials.size() + " but no usable matching t6t_rs");
    } catch (RuntimeException error) {
      return new PackedTarget(
          Optional.empty(),
          false,
          "decode error=" + error.getClass().getSimpleName() + ": " + error.getMessage());
    }
  }

  static Optional<AprilTagTarget> targetFromPackedResults(
      byte[] packed, int desiredTagId, long timestampMicros) {
    return decodePackedTarget(packed, desiredTagId, timestampMicros).target();
  }

  private static Object firstPresent(Map<?, ?> values, String... keys) {
    for (String key : keys) {
      if (values.containsKey(key)) {
        return values.get(key);
      }
    }
    return null;
  }

  private static Optional<Map<?, ?>> mapValue(Object value) {
    return value instanceof Map<?, ?> map ? Optional.of(map) : Optional.empty();
  }

  private static boolean booleanValue(Object value) {
    if (value instanceof Boolean booleanValue) {
      return booleanValue;
    }
    return value instanceof Number number && number.doubleValue() != 0.0;
  }

  private static int intValue(Object value, int defaultValue) {
    return value instanceof Number number ? number.intValue() : defaultValue;
  }

  private static double[] doubleArrayValue(Object value) {
    if (!(value instanceof List<?> list)) {
      return new double[0];
    }
    double[] result = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      if (!(list.get(i) instanceof Number number)) {
        return new double[0];
      }
      result[i] = number.doubleValue();
    }
    return result;
  }

  private record PackedTarget(
      Optional<AprilTagTarget> target, boolean cameraReportsTarget, String detail) {}

  private static double wrapRadians(double angle) {
    return Math.atan2(Math.sin(angle), Math.cos(angle));
  }

  /** NetworkTables diagnostics for the SystemCore Limelight feed. */
  public record VisionStatus(
      String tableName,
      boolean resultTopicConnected,
      boolean cameraReportsTarget,
      double detectionAgeMillis,
      boolean targetPoseTopicConnected,
      String targetPoseType,
      double poseAgeMillis,
      String decoderDetail) {

    /** Compact text intended for the robot log when the driver presses the assist button. */
    public String summary() {
      return String.format(
          "camera=/%s resultTopic=%s cameraSeesTarget=%s"
              + " detectionAge=%s targetPoseTopic=%s targetPoseType=%s poseAge=%s decoder=%s",
          tableName,
          resultTopicConnected,
          cameraReportsTarget,
          formatAge(detectionAgeMillis),
          targetPoseTopicConnected,
          targetPoseType == null || targetPoseType.isBlank() ? "<none>" : targetPoseType,
          formatAge(poseAgeMillis),
          decoderDetail);
    }

    private static String formatAge(double ageMillis) {
      return Double.isFinite(ageMillis) ? String.format("%.1fms", ageMillis) : "never";
    }
  }

  @Override
  public void close() {
    targetValid.close();
    targetId.close();
    targetPose.close();
    packedResults.close();
  }
}

package first.robot;

import first.robot.AprilTagAssistController.DriveCommand;
import first.robot.vision.AprilTagTarget;
import first.robot.vision.AprilTagVision.VisionStatus;
import java.util.Optional;
import org.wpilib.networktables.BooleanPublisher;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.IntegerPublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.StringPublisher;

/** Publishes AprilTag measurements and their resulting drive commands for Elastic/dashboard use. */
final class AprilTagAssistTelemetry {
  private final BooleanPublisher assistHeld;
  private final BooleanPublisher cameraReportsTarget;
  private final BooleanPublisher hasTarget;
  private final BooleanPublisher poseTopicConnected;
  private final StringPublisher poseTopicType;
  private final DoublePublisher poseAgeMillis;
  private final IntegerPublisher tagId;
  private final DoublePublisher rangeMeters;
  private final DoublePublisher bearingDegrees;
  private final DoublePublisher yawDegrees;
  private final DoublePublisher rangeErrorMeters;
  private final DoublePublisher forwardCommand;
  private final DoublePublisher strafeCommand;
  private final DoublePublisher turnCommand;

  AprilTagAssistTelemetry() {
    NetworkTable table =
        NetworkTableInstance.getDefault().getTable("Elastic").getSubTable("AprilTag Assist");

    assistHeld = table.getBooleanTopic("Right Bumper Held").publish();
    cameraReportsTarget = table.getBooleanTopic("Camera Reports Target").publish();
    hasTarget = table.getBooleanTopic("Has Fresh Target").publish();
    poseTopicConnected = table.getBooleanTopic("Pose Topic Connected").publish();
    poseTopicType = table.getStringTopic("Pose Topic Type").publish();
    poseAgeMillis = table.getDoubleTopic("Pose Age (ms)").publish();
    tagId = table.getIntegerTopic("Tag ID").publish();
    rangeMeters = table.getDoubleTopic("Range (m)").publish();
    bearingDegrees = table.getDoubleTopic("Bearing (deg)").publish();
    yawDegrees = table.getDoubleTopic("Yaw (deg)").publish();
    rangeErrorMeters = table.getDoubleTopic("Range Error (m)").publish();
    forwardCommand = table.getDoubleTopic("Forward Command").publish();
    strafeCommand = table.getDoubleTopic("Strafe Command").publish();
    turnCommand = table.getDoubleTopic("Turn Command").publish();
  }

  void update(
      boolean rightBumperHeld,
      VisionStatus visionStatus,
      Optional<AprilTagTarget> target,
      DriveCommand command) {
    assistHeld.set(rightBumperHeld);
    hasTarget.set(target.isPresent());
    cameraReportsTarget.set(visionStatus != null && visionStatus.cameraReportsTarget());
    poseTopicConnected.set(visionStatus != null && visionStatus.targetPoseTopicConnected());
    String poseType = visionStatus == null ? null : visionStatus.targetPoseType();
    poseTopicType.set(
        visionStatus == null
            ? "<vision not started>"
            : poseType == null || poseType.isBlank() ? "<none>" : poseType);
    poseAgeMillis.set(visionStatus == null ? Double.NaN : visionStatus.poseAgeMillis());

    if (target.isPresent()) {
      AprilTagTarget tag = target.get();
      tagId.set(tag.id());
      rangeMeters.set(tag.rangeMeters());
      bearingDegrees.set(Math.toDegrees(tag.bearingRadians()));
      yawDegrees.set(Math.toDegrees(tag.yawRadians()));
    } else {
      tagId.set(-1);
      rangeMeters.set(Double.NaN);
      bearingDegrees.set(Double.NaN);
      yawDegrees.set(Double.NaN);
    }

    if (command != null) {
      rangeErrorMeters.set(command.rangeErrorMeters());
      forwardCommand.set(command.forward());
      strafeCommand.set(command.strafe());
      turnCommand.set(command.turn());
    } else {
      rangeErrorMeters.set(Double.NaN);
      forwardCommand.set(0.0);
      strafeCommand.set(0.0);
      turnCommand.set(0.0);
    }
  }
}

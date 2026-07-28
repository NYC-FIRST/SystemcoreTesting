package first.robot.YGSLSwerveTemplate.vision;

import org.wpilib.networktables.BooleanPublisher;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.IntegerPublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;

/** Publishes {@link VisionSubsystem} state to NetworkTables/Elastic for on-robot debugging. */
public class VisionTelemetry {

    private final VisionSubsystem vision;

    private final BooleanPublisher hasTargets;
    private final IntegerPublisher bestTagId;
    private final DoublePublisher rangeMeters;
    private final DoublePublisher bearingDegrees;
    private final DoublePublisher yawDegrees;

    public VisionTelemetry(VisionSubsystem vision) {
        this.vision = vision;

        NetworkTable table =
                NetworkTableInstance.getDefault().getTable("Elastic").getSubTable("Vision Telemetry");

        hasTargets = table.getBooleanTopic("Has Target").publish();
        bestTagId = table.getIntegerTopic("Best Tag ID").publish();
        rangeMeters = table.getDoubleTopic("Range (m)").publish();
        bearingDegrees = table.getDoubleTopic("Bearing (deg)").publish();
        yawDegrees = table.getDoubleTopic("Yaw (deg)").publish();
    }

    public void update() {
        VisionSubsystem.Target target = vision.getTarget();

        hasTargets.set(target != null);
        bestTagId.set(target == null ? -1 : target.id());
        rangeMeters.set(target == null ? 0.0 : target.rangeMeters());
        bearingDegrees.set(target == null ? 0.0 : Math.toDegrees(target.bearingRadians()));
        yawDegrees.set(target == null ? 0.0 : Math.toDegrees(target.yawRadians()));
    }
}
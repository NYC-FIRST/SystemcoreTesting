package first.robot.YGSLSwerveTemplate.vision;

import org.wpilib.command2.SubsystemBase;
import org.wpilib.math.geometry.Pose2d;

public class VisionSubsystem extends SubsystemBase {

    public VisionSubsystem() {

    }

    @Override
    public void periodic() {

    }

    public boolean hasTargets() {
        return false;
    }

    public int getBestTagID() {
        return -1;
    }

    public Pose2d getEstimatedPose() {
        return new Pose2d();
    }
}
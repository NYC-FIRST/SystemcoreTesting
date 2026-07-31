package first.robot.fieldcentric;

import org.wpilib.math.geometry.Pose2d;

public class Odometry {

    private Pose2d pose = new Pose2d();

    public Odometry() {

    }

    public void update() {

        // TODO
        // Read gyro
        // Read wheel positions
        // Update pose

    }

    public Pose2d getPose() {
        return pose;
    }

    public void resetPose(Pose2d pose) {
        this.pose = pose;
    }

}

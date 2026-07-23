package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import org.wpilib.math.geometry.Rotation2d;
import first.robot.YGSLSwerveTemplate.Constants;

/**
 * Test harness for one swerve pod using the same module wrappers as the full template.
 */
public class SinglePodDriveSubsystem {

    private final SwerveModule module;

    public SinglePodDriveSubsystem(A301 driveMotor, A301 steeringMotor) {
        module = new SwerveModule(driveMotor, steeringMotor);
    }

    public void driveToward(double x, double y) {
        double magnitude = Math.min(1.0, Math.hypot(x, y));
        if (magnitude < Constants.SinglePod.STICK_DIRECTION_DEADBAND) {
            stop();
            return;
        }

        double directionRotations = stickDirectionToRotations(x, y);
        double targetRotations = wrapRotations(
                Constants.SinglePod.FORWARD_ABSOLUTE_POSITION_ROTATIONS + directionRotations);

        double driveThrottle =
                Constants.SinglePod.DRIVE_DIRECTION
                        * magnitude
                        * Constants.SinglePod.MAX_DRIVE_THROTTLE;

        module.setDesiredThrottle(driveThrottle, Rotation2d.fromRotations(targetRotations));
    }

    public void stop() {
        module.stop();
    }

    private static double wrapRotations(double rotations) {
        return rotations - Math.floor(rotations + 0.5);
    }

    private static double stickDirectionToRotations(double x, double y) {
        return Math.atan2(x, y) / (2.0 * Math.PI);
    }
}

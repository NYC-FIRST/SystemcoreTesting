package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
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

        double directionRotations = Math.atan2(x, y) / (2.0 * Math.PI);
        double targetRotations = wrapRotations(
                Constants.SinglePod.FORWARD_ABSOLUTE_POSITION_ROTATIONS + directionRotations);

        module.setDesiredVelocity(
                new SwerveModuleVelocity(
                        magnitude * Constants.Drive.MAX_SPEED,
                        Rotation2d.fromRotations(targetRotations)));
    }

    public void stop() {
        module.stop();
    }

    private static double wrapRotations(double rotations) {
        return rotations - Math.floor(rotations + 0.5);
    }
}

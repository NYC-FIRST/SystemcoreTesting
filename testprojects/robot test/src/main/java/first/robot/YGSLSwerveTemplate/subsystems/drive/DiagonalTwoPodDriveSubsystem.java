package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import first.robot.YGSLSwerveTemplate.Constants;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveDriveKinematics;
import org.wpilib.math.kinematics.SwerveModuleVelocity;

/**
 * Drives a chassis with powered swerve pods at front-left and back-right and passive swivel
 * wheels at the other two corners.
 */
public class DiagonalTwoPodDriveSubsystem {

    private final SwerveModule frontLeft;
    private final SwerveModule backRight;

    public DiagonalTwoPodDriveSubsystem(
            A301 frontLeftDrive,
            A301 frontLeftSteer,
            A301 backRightDrive,
            A301 backRightSteer) {
        frontLeft = new SwerveModule(frontLeftDrive, frontLeftSteer);
        backRight = new SwerveModule(backRightDrive, backRightSteer);
    }

    /**
     * Commands robot-relative motion. Positive forward is robot-forward, positive left is
     * robot-left, and positive rotation is counterclockwise.
     */
    public void drive(double forward, double left, double rotation) {
        if (Math.abs(forward) < Constants.Operator.DEADBAND
                && Math.abs(left) < Constants.Operator.DEADBAND
                && Math.abs(rotation) < Constants.Operator.DEADBAND) {
            stop();
            return;
        }

        ChassisVelocities chassisVelocities = new ChassisVelocities(
                applyDeadband(forward) * Constants.Drive.MAX_SPEED,
                applyDeadband(left) * Constants.Drive.MAX_SPEED,
                applyDeadband(rotation) * Constants.Drive.MAX_ANGULAR_SPEED);

        SwerveModuleVelocity[] moduleVelocities =
                Constants.Drive.DIAGONAL_TWO_POD_KINEMATICS
                        .toSwerveModuleVelocities(chassisVelocities);
        moduleVelocities = SwerveDriveKinematics.desaturateWheelVelocities(
                moduleVelocities,
                Constants.Drive.MAX_SPEED);

        setModuleVelocity(
                frontLeft,
                moduleVelocities[0],
                Constants.SinglePod.POD_1_FORWARD_ABSOLUTE_POSITION_ROTATIONS,
                Constants.SinglePod.POD_1_STEERING_DIRECTION,
                Constants.SinglePod.POD_1_DRIVE_DIRECTION);
        setModuleVelocity(
                backRight,
                moduleVelocities[1],
                Constants.SinglePod.POD_2_FORWARD_ABSOLUTE_POSITION_ROTATIONS,
                Constants.SinglePod.POD_2_STEERING_DIRECTION,
                Constants.SinglePod.POD_2_DRIVE_DIRECTION);
    }

    public void stop() {
        frontLeft.stop();
        backRight.stop();
    }

    private static void setModuleVelocity(
            SwerveModule module,
            SwerveModuleVelocity velocity,
            double forwardAbsolutePositionRotations,
            double steeringDirection,
            double driveDirection) {
        double targetRotations = wrapRotations(
                forwardAbsolutePositionRotations
                        + steeringDirection * velocity.angle.getRotations());
        double driveThrottle =
                driveDirection
                        * velocity.velocity
                        / Constants.Drive.MAX_SPEED
                        * Constants.SinglePod.MAX_DRIVE_THROTTLE;

        module.setDesiredThrottle(
                driveThrottle,
                Rotation2d.fromRotations(targetRotations));
    }

    private static double applyDeadband(double value) {
        return Math.abs(value) < Constants.Operator.DEADBAND ? 0.0 : value;
    }

    private static double wrapRotations(double rotations) {
        return rotations - Math.floor(rotations + 0.5);
    }
}

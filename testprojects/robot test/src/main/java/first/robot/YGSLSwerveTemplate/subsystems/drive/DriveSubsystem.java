package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveDriveKinematics;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import first.robot.YGSLSwerveTemplate.Constants;

/**
 * Main swerve drive subsystem.
 * Owns all four swerve modules.
 */
public class DriveSubsystem extends SubsystemBase {

    private final SwerveModule frontLeft;
    private final SwerveModule frontRight;
    private final SwerveModule backLeft;
    private final SwerveModule backRight;

    public DriveSubsystem(
            A301 frontLeftDrive,
            A301 frontLeftSteer,
            A301 frontRightDrive,
            A301 frontRightSteer,
            A301 backLeftDrive,
            A301 backLeftSteer,
            A301 backRightDrive,
            A301 backRightSteer) {

        this(
                new SwerveModule(frontLeftDrive, frontLeftSteer),
                new SwerveModule(frontRightDrive, frontRightSteer),
                new SwerveModule(backLeftDrive, backLeftSteer),
                new SwerveModule(backRightDrive, backRightSteer));
    }

    public DriveSubsystem(
            SwerveModule frontLeft,
            SwerveModule frontRight,
            SwerveModule backLeft,
            SwerveModule backRight) {

        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    /**
     * Drives the robot using chassis speeds.
     */
    public void drive(ChassisVelocities speeds) {

        SwerveModuleVelocity[] velocities =
                Constants.Drive.KINEMATICS.toSwerveModuleVelocities(speeds);
        velocities = SwerveDriveKinematics.desaturateWheelVelocities(
                velocities,
                Constants.Drive.MAX_SPEED);

        frontLeft.setDesiredVelocity(velocities[0]);
        frontRight.setDesiredVelocity(velocities[1]);
        backLeft.setDesiredVelocity(velocities[2]);
        backRight.setDesiredVelocity(velocities[3]);
    }

    /**
     * Stops every module.
     */
    public void stop() {

        frontLeft.stop();
        frontRight.stop();
        backLeft.stop();
        backRight.stop();
    }

    @Override
    public void periodic() {
        // Future:
        // - Update odometry
        // - Read gyro
        // - Publish telemetry
    }
}

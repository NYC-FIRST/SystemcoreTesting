package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import org.wpilib.command2.SubsystemBase;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.SwerveDriveKinematics;
import org.wpilib.math.kinematics.SwerveDriveOdometry;
import org.wpilib.math.kinematics.SwerveModulePosition;
import org.wpilib.math.kinematics.SwerveModuleVelocity;

import first.robot.YGSLSwerveTemplate.Constants;

public class DriveSubsystem extends SubsystemBase {

    private final SwerveModule frontLeft;
    private final SwerveModule frontRight;
    private final SwerveModule backLeft;
    private final SwerveModule backRight;

    // TODO: Replace with the actual SystemCore IMU wrapper later
    // private final SystemCoreGyro gyro;

    private SwerveDriveOdometry odometry;

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

        odometry = new SwerveDriveOdometry(
                Constants.Drive.KINEMATICS,
                Rotation2d.kZero,
                new SwerveModulePosition[]{
                        frontLeft.getPosition(),
                        frontRight.getPosition(),
                        backLeft.getPosition(),
                        backRight.getPosition()
                });
    }

    public void drive(ChassisVelocities speeds) {

        SwerveModuleVelocity[] velocities =
                Constants.Drive.KINEMATICS.toSwerveModuleVelocities(speeds);

        SwerveDriveKinematics.desaturateWheelVelocities(
                velocities,
                Constants.Drive.MAX_SPEED);

        frontLeft.setDesiredVelocity(velocities[0]);
        frontRight.setDesiredVelocity(velocities[1]);
        backLeft.setDesiredVelocity(velocities[2]);
        backRight.setDesiredVelocity(velocities[3]);
    }

    public void stop() {
        frontLeft.stop();
        frontRight.stop();
        backLeft.stop();
        backRight.stop();
    }

    public Pose2d getPose() {
        return odometry.getPose();
    }

    public void resetPose(Pose2d pose) {
        odometry.resetPosition(
                Rotation2d.kZero,
                new SwerveModulePosition[]{
                        frontLeft.getPosition(),
                        frontRight.getPosition(),
                        backLeft.getPosition(),
                        backRight.getPosition()
                },
                pose);
    }

    public Rotation2d getHeading() {
        // Replace with SystemCore IMU heading
        return Rotation2d.kZero;
    }

    public void zeroHeading() {
        // TODO: gyro.reset();
    }

    @Override
    public void periodic() {

        odometry.update(
                getHeading(),
                new SwerveModulePosition[]{
                        frontLeft.getPosition(),
                        frontRight.getPosition(),
                        backLeft.getPosition(),
                        backRight.getPosition()
                });

        // TODO:
        // ElasticTelemetry.publishPose(getPose());
        // ElasticTelemetry.publishModules(...);
    }
}
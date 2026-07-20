package first.robot.YGSLSwerveTemplate.subsystems.drive;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
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

    public DriveSubsystem() {

        frontLeft = new SwerveModule(
                Constants.CAN.CAN_D0,
                Constants.CAN.FRONT_LEFT_DRIVE,
                Constants.CAN.CAN_D0,
                Constants.CAN.FRONT_LEFT_STEER);

        frontRight = new SwerveModule(
                Constants.CAN.CAN_D1,
                Constants.CAN.FRONT_RIGHT_DRIVE,
                Constants.CAN.CAN_D1,
                Constants.CAN.FRONT_RIGHT_STEER);

        backLeft = new SwerveModule(
                Constants.CAN.CAN_D2,
                Constants.CAN.BACK_LEFT_DRIVE,
                Constants.CAN.CAN_D2,
                Constants.CAN.BACK_LEFT_STEER);

        backRight = new SwerveModule(
                Constants.CAN.CAN_D3,
                Constants.CAN.BACK_RIGHT_DRIVE,
                Constants.CAN.CAN_D3,
                Constants.CAN.BACK_RIGHT_STEER);
    }

    /**
     * Drives the robot using chassis speeds.
     */
    public void drive(ChassisSpeeds speeds) {

        SwerveModuleState[] states =
                Constants.Drive.KINEMATICS.toSwerveModuleStates(speeds);

        frontLeft.setDesiredState(states[0]);
        frontRight.setDesiredState(states[1]);
        backLeft.setDesiredState(states[2]);
        backRight.setDesiredState(states[3]);
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

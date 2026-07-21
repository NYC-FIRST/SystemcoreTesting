package first.robot.YGSLSwerveTemplate.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * Represents one complete swerve module.
 * Each module contains:
 * - One drive motor
 * - One steering motor
 */
public class SwerveModule {

    private final A301DriveMotor driveMotor;
    private final A301SteeringMotor steeringMotor;

    public SwerveModule(
            int driveBus,
            int driveCAN,
            int steerBus,
            int steerCAN) {

        driveMotor = new A301DriveMotor(driveBus, driveCAN);
        steeringMotor = new A301SteeringMotor(steerBus, steerCAN);
    }

    /**
     * Sets the desired state for this module.
     */
    public void setDesiredState(SwerveModuleState desiredState) {

        steeringMotor.setDesiredAngle(
                desiredState.angle.getDegrees());

        driveMotor.setVelocity(
                desiredState.speedMetersPerSecond);
    }

    /**
     * Returns the current module state.
     */
    public SwerveModuleState getState() {

        return new SwerveModuleState(
                driveMotor.getVelocity(),
                Rotation2d.fromRotations(
                        steeringMotor.getAngle()));
    }

    /**
     * Returns the module position.
     */
    public SwerveModulePosition getPosition() {

        return new SwerveModulePosition(
                driveMotor.getPosition(),
                Rotation2d.fromRotations(
                        steeringMotor.getAngle()));
    }

    /**
     * Stops the module.
     */
    public void stop() {

        driveMotor.stop();
        steeringMotor.stop();
    }
}
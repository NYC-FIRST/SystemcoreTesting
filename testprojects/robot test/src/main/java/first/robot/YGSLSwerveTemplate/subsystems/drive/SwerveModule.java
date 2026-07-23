package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.kinematics.SwerveModulePosition;
import org.wpilib.math.kinematics.SwerveModuleVelocity;
import first.robot.YGSLSwerveTemplate.Constants;

/**
 * Represents one complete swerve module.
 * Each module contains:
 * - One drive motor
 * - One steering motor
 */
public class SwerveModule {

    private final A301DriveMotor driveMotor;
    private final A301SteeringMotor steeringMotor;

    public SwerveModule(A301 driveMotor, A301 steeringMotor) {

        this.driveMotor = new A301DriveMotor(driveMotor);
        this.steeringMotor = new A301SteeringMotor(steeringMotor);
    }

    /**
     * Sets the desired velocity for this module.
     */
    public void setDesiredVelocity(SwerveModuleVelocity desiredVelocity) {

        steeringMotor.setDesiredAngleRotations(
                desiredVelocity.angle.getRotations());

        driveMotor.setThrottle(
                desiredVelocity.velocity / Constants.Drive.MAX_SPEED);
    }

    /**
     * Sets the steering angle and open-loop drive throttle for bring-up testing.
     */
    public void setDesiredThrottle(double driveThrottle, Rotation2d desiredAngle) {

        steeringMotor.setDesiredAngleRotations(desiredAngle.getRotations());
        driveMotor.setThrottle(driveThrottle);
    }

    /**
     * Returns the current module velocity.
     */
    public SwerveModuleVelocity getVelocity() {

        return new SwerveModuleVelocity(
                driveMotor.getWheelVelocityMetersPerSecond(),
                Rotation2d.fromRotations(
                        steeringMotor.getAngleRotations()));
    }

    /**
     * Returns the module position.
     */
    public SwerveModulePosition getPosition() {

        return new SwerveModulePosition(
                driveMotor.getWheelPositionMeters(),
                Rotation2d.fromRotations(
                        steeringMotor.getAngleRotations()));
    }

    /**
     * Stops the module.
     */
    public void stop() {

        driveMotor.stop();
        steeringMotor.stop();
    }
}

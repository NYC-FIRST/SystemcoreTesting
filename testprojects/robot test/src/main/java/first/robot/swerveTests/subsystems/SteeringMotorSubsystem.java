package first.robot.swerveTests.subsystems;

import com.revrobotics.spark.A301;

import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.A301SteeringMotor;
import org.wpilib.command2.SubsystemBase;

public class SteeringMotorSubsystem extends SubsystemBase {

    // Physical steering motor (CAN ID 8)
    private final A301SteeringMotor steeringMotor =
            new A301SteeringMotor(
                    new A301(
                            8,
                            Constants.CAN.CAN_D0));

    /**
     * Rotate to a desired angle.
     *
     * @param rotations Desired angle in rotations (-0.5 to 0.5)
     */
    public void setDesiredAngle(double rotations) {
        steeringMotor.setDesiredAngleRotations(rotations);
    }

    /**
     * Stop the steering motor.
     */
    public void stop() {
        steeringMotor.stop();
    }

    /**
     * Current absolute encoder position.
     */
    public double getAngleRotations() {
        return steeringMotor.getAngleRotations();
    }

    /**
     * Returns the underlying wrapper.
     */
    public A301SteeringMotor getSteeringMotor() {
        return steeringMotor;
    }
}
package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import edu.wpi.first.math.controller.PIDController;

/**
 * Controls one A301 steering motor.
 */
public class A301SteeringMotor {

    private final A301 motor;
    private final PIDController pid;

    /**
     * Creates a steering motor.
     *
     * @param busId MotionCore bus ID
     * @param canId Motor CAN ID
     */
    public A301SteeringMotor(int busId, int canId) {

        motor = new A301(busId, canId);

        pid = new PIDController(
                1.0,
                0.0,
                0.05);

        pid.enableContinuousInput(-180.0, 180.0);
    }

    /**
     * Rotate to a desired angle.
     */
    public void setDesiredAngle(double desiredAngle) {

        double currentAngle = getAngle();

        double output = pid.calculate(currentAngle, desiredAngle);

        motor.setThrottle(output);
    }

    /**
     * Returns the absolute encoder position.
     */
    public double getAngle() {
        return motor.getAbsoluteEncoderPosition().get();
    }

    /**
     * Stop steering motor.
     */
    public void stop() {
        motor.setThrottle(0.0);
    }

    /**
     * Returns the underlying A301 object.
     */
    public A301 getMotor() {
        return motor;
    }
}
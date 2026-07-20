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
     *
     * NOTE:
     * Encoder feedback will be added once the
     * A301 encoder API is finalized.
     */
    public void setDesiredAngle(double desiredAngle) {

        double currentAngle = getAngle();

        double output = pid.calculate(currentAngle, desiredAngle);

        motor.setThrottle(output);
    }

    /**
     * Placeholder until encoder support is added.
     */
    public double getAngle() {
        return 0.0;
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
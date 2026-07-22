package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;
import org.wpilib.math.controller.PIDController;

/**
 * Controls one A301 steering motor.
 */
public class A301SteeringMotor {

    private final A301 motor;
    private final PIDController pid;
    private static final double kMaxSteeringThrottle = 0.90;

    /**
     * Wraps an A301 steering motor owned by the active robot.
     */
    public A301SteeringMotor(A301 motor) {

        this.motor = motor;

        pid = new PIDController(
                1.0,
                0.0,
                0.05);

        pid.enableContinuousInput(-0.5, 0.5);
    }

    /**
     * Rotate to a desired angle.
     */
    public void setDesiredAngleRotations(double desiredAngleRotations) {

        double currentAngleRotations = getAngleRotations();

        double output = pid.calculate(currentAngleRotations, desiredAngleRotations);

        motor.setThrottle(clamp(output, -kMaxSteeringThrottle, kMaxSteeringThrottle));
    }

    /**
     * Returns the absolute encoder position.
     */
    public double getAngleRotations() {
        Signal<Double> angle = motor.getAbsoluteEncoderPosition();
        return angle.isValid() ? angle.get() : 0.0;
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

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}

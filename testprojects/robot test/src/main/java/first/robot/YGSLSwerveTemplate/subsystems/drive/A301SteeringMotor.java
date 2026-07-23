package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;
import first.robot.YGSLSwerveTemplate.Constants;
import org.wpilib.math.controller.PIDController;

/**
 * Controls one A301 steering motor.
 */
public class A301SteeringMotor {

    private final A301 motor;
    private final PIDController pid;
    private static final double kSteeringToleranceRotations = 0.010;
    private static final double kMinSteeringThrottle = 0.15;
    private static final double kMaxSteeringThrottle = 0.90;

    /**
     * Wraps an A301 steering motor owned by the active robot.
     */
    public A301SteeringMotor(A301 motor) {

        this.motor = motor;

        pid = new PIDController(
                Constants.PID.STEER_kP,
                Constants.PID.STEER_kI,
                Constants.PID.STEER_kD);

        pid.enableContinuousInput(-0.5, 0.5);
    }

    /**
     * Rotate to a desired angle using PID.
     */
    public void setDesiredAngleRotations(double desiredAngleRotations) {

        double currentAngleRotations = getAngleRotations();
        double targetAngleRotations = wrapRotations(desiredAngleRotations);

        double output = pid.calculate(currentAngleRotations, targetAngleRotations);
        double errorRotations = wrapRotations(targetAngleRotations - currentAngleRotations);
        if (Math.abs(errorRotations) <= kSteeringToleranceRotations) {
            pid.reset();
            motor.setThrottle(0.0);
            return;
        }

        output = Math.copySign(
                Math.max(Math.abs(output), kMinSteeringThrottle),
                errorRotations);

        motor.setThrottle(
                clamp(output,
                        -kMaxSteeringThrottle,
                        kMaxSteeringThrottle));
    }

    /**
     * Open-loop steering motor control.
     * Used for standalone hardware testing.
     */
    public void setThrottle(double throttle) {

        motor.setThrottle(
                clamp(throttle,
                        -kMaxSteeringThrottle,
                        kMaxSteeringThrottle));
    }

    /**
     * Returns the absolute encoder position in rotations.
     */
    public double getAngleRotations() {
        Signal<Double> angle = motor.getAbsoluteEncoderPosition();
        return angle.isValid() ? wrapRotations(angle.get() / Constants.GearRatios.STEER) : 0.0;
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

    private static double wrapRotations(double rotations) {
        return rotations - Math.floor(rotations + 0.5);
    }
}

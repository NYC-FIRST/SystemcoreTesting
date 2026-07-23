package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;
import first.robot.YGSLSwerveTemplate.Constants;

/**
 * Controls one A301 drive motor.
 */
public class A301DriveMotor {

    private final A301 motor;

    /** Stores the last commanded throttle. */
    private double throttle = 0.0;

    /**
     * Wraps an A301 drive motor owned by the active robot.
     */
    public A301DriveMotor(A301 motor) {
        this.motor = motor;
    }

    /**
     * Open-loop control.
     */
    public void setThrottle(double throttle) {

        this.throttle = clamp(
                throttle,
                -Constants.Drive.MAX_OPEN_LOOP_THROTTLE,
                Constants.Drive.MAX_OPEN_LOOP_THROTTLE);

        motor.setThrottle(this.throttle);
    }

    /**
     * Stops the motor.
     */
    public void stop() {
        throttle = 0.0;
        motor.setThrottle(0.0);
    }

    /**
     * Returns the last commanded throttle.
     */
    public double getThrottle() {
        return throttle;
    }

    /**
     * Returns the relative encoder position in rotations.
     */
    public double getMotorPositionRotations() {
        return getOrZero(motor.getRelativeEncoderPosition());
    }

    /**
     * Returns the encoder velocity in RPM.
     */
    public double getMotorVelocityRpm() {
        return getOrZero(motor.getEncoderVelocity());
    }

    /**
     * Returns wheel position in meters.
     */
    public double getWheelPositionMeters() {
        double wheelRotations = getMotorPositionRotations() / Constants.GearRatios.DRIVE;
        return wheelRotations * Constants.Drive.WHEEL_CIRCUMFERENCE;
    }

    /**
     * Returns wheel velocity in meters per second.
     */
    public double getWheelVelocityMetersPerSecond() {
        double wheelRpm = getMotorVelocityRpm() / Constants.GearRatios.DRIVE;
        return wheelRpm * Constants.Drive.WHEEL_CIRCUMFERENCE / 60.0;
    }

    /**
     * Returns the absolute encoder position in rotations.
     */
    public double getAbsolutePosition() {
        return getOrZero(motor.getAbsoluteEncoderPosition());
    }

    /**
     * Returns the underlying A301 object.
     */
    public A301 getMotor() {
        return motor;
    }

    private static double getOrZero(Signal<Double> signal) {
        return signal.isValid() ? signal.get() : 0.0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
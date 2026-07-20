package first.robot.YGSLSwerveTemplate.subsystems.drive;

import com.revrobotics.spark.A301;

/**
 * Controls one A301 drive motor.
 */
public class A301DriveMotor {

    private final A301 motor;

    /**
     * Creates a drive motor.
     *
     * @param busId MotionCore bus ID
     * @param canId Motor CAN ID
     */
    public A301DriveMotor(int busId, int canId) {
        motor = new A301(busId, canId);
    }

    /**
     * Open-loop control.
     */
    public void setThrottle(double throttle) {
        motor.setThrottle(throttle);
    }

    /**
     * Closed-loop velocity control.
     */
    public void setVelocity(double velocity) {
        motor.setVelocity(velocity);
    }

    /**
     * Stops the motor.
     */
    public void stop() {
        motor.setThrottle(0.0);
    }

    /**
     * Placeholder until encoder API is finalized.
     */
    public double getPosition() {
        return 0.0;
    }

    /**
     * Placeholder until encoder API is finalized.
     */
    public double getVelocity() {
        return 0.0;
    }

    /**
     * Returns the underlying A301 object.
     */
    public A301 getMotor() {
        return motor;
    }
}
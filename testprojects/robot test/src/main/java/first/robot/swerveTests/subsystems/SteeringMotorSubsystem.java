package first.robot.swerveTests.subsystems;

import com.revrobotics.spark.A301;

import first.robot.YGSLSwerveTemplate.subsystems.drive.A301SteeringMotor;
import org.wpilib.command2.SubsystemBase;

public class SteeringMotorSubsystem extends SubsystemBase {

    private final A301SteeringMotor steeringMotor;

    public SteeringMotorSubsystem(A301 motor) {
        steeringMotor = new A301SteeringMotor(motor);
    }

    /**
     * Open-loop steering control.
     */
    public void setThrottle(double throttle) {
        steeringMotor.setThrottle(throttle);
    }

    /**
     * PID steering control.
     */
    public void setDesiredAngle(double rotations) {
        steeringMotor.setDesiredAngleRotations(rotations);
    }

    /**
     * Stops the steering motor.
     */
    public void stop() {
        steeringMotor.stop();
    }

    /**
     * Current steering angle (rotations).
     */
    public double getAngleRotations() {
        return steeringMotor.getAngleRotations();
    }

    /**
     * Current steering angle (degrees).
     */
    public double getAngleDegrees() {
        return getAngleRotations() * 360.0;
    }

    /**
     * Absolute encoder position (rotations).
     */
    public double getAbsolutePosition() {
        return steeringMotor.getAbsolutePosition();
    }

    /**
     * Absolute encoder position (degrees).
     */
    public double getAbsolutePositionDegrees() {
        return getAbsolutePosition() * 360.0;
    }

    /**
     * Steering motor velocity (RPM).
     */
    public double getMotorVelocityRpm() {
        return steeringMotor.getMotorVelocityRpm();
    }

    /**
     * Current commanded motor output (-1.0 to 1.0).
     */
    public double getMotorOutput() {
        return steeringMotor.getThrottle();
    }
}

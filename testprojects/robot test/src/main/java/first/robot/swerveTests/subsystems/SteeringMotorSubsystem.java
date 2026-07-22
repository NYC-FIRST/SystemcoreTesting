package first.robot.swerveTests.subsystems;

import com.revrobotics.spark.A301;

import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.A301SteeringMotor;
import org.wpilib.command2.SubsystemBase;

public class SteeringMotorSubsystem extends SubsystemBase {

    private final A301SteeringMotor steeringMotor =
            new A301SteeringMotor(
                    new A301(
                            8,
                            Constants.CAN.CAN_D0));

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

    public void stop() {
        steeringMotor.stop();
    }

    public double getAngleRotations() {
        return steeringMotor.getAngleRotations();
    }

    public double getAngleDegrees() {
        return getAngleRotations() * 360.0;
    }
}
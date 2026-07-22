package first.robot.swerveTests.subsystems;

import com.revrobotics.spark.A301;

import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.A301DriveMotor;
import org.wpilib.command2.SubsystemBase;

public class DriveMotorSubsystem extends SubsystemBase {

    // Physical drive motor (Device ID 9)
    private final A301DriveMotor driveMotor =
            new A301DriveMotor(
                    new A301(
                            9,
                            Constants.CAN.CAN_D0));

    /**
     * Sets the drive motor throttle.
     */
    public void setThrottle(double throttle) {
        driveMotor.setThrottle(throttle);
    }

    /**
     * Stops the drive motor.
     */
    public void stop() {
        driveMotor.stop();
    }

    public double getMotorPositionRotations() {
        return driveMotor.getMotorPositionRotations();
    }

    public double getMotorVelocityRpm() {
        return driveMotor.getMotorVelocityRpm();
    }

    public double getWheelPositionMeters() {
        return driveMotor.getWheelPositionMeters();
    }

    public double getWheelVelocityMetersPerSecond() {
        return driveMotor.getWheelVelocityMetersPerSecond();
    }

    public double getAbsolutePosition() {
        return driveMotor.getAbsolutePosition();
    }
}
package first.robot.swerveTests.subsystems;

import com.revrobotics.spark.A301;

import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.A301DriveMotor;
import org.wpilib.command2.SubsystemBase;

public class DriveMotorSubsystem extends SubsystemBase {

    private final A301DriveMotor driveMotor =
        new A301DriveMotor(
            new A301(
                Constants.CAN.MOTIONCORE_CAN_D0_BUS_ID,
                Constants.CAN.DRIVE_TEST_CAN_ID));

    public void setThrottle(double throttle) {
        driveMotor.setThrottle(throttle);
    }

    public void stop() {
        driveMotor.stop();
    }

    // -------------------------
    // Telemetry
    // -------------------------

    /** Motor position (rotations). */
    public double getMotorPositionRotations() {
        return driveMotor.getMotorPositionRotations();
    }

    /** Motor position (degrees). */
    public double getMotorPositionDegrees() {
        return getMotorPositionRotations() * 360.0;
    }

    /** Motor velocity (RPM). */
    public double getMotorVelocityRpm() {
        return driveMotor.getMotorVelocityRpm();
    }

    /** Wheel position (meters). */
    public double getWheelPositionMeters() {
        return driveMotor.getWheelPositionMeters();
    }

    /** Wheel velocity (m/s). */
    public double getWheelVelocityMetersPerSecond() {
        return driveMotor.getWheelVelocityMetersPerSecond();
    }

    /** Absolute encoder position (rotations). */
    public double getAbsolutePosition() {
        return driveMotor.getAbsolutePosition();
    }

    /** Absolute encoder position (degrees). */
    public double getAbsolutePositionDegrees() {
        return getAbsolutePosition() * 360.0;
    }

    /** Current motor output (-1.0 to 1.0). */
    public double getMotorOutput() {
        return driveMotor.getThrottle();
    }
}
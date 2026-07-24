package first.robot.telemetry;

import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;

import first.robot.swerveTests.subsystems.DriveMotorSubsystem;
import first.robot.swerveTests.subsystems.SteeringMotorSubsystem;

public class ElasticTelemetry {

    private final NetworkTable root =
            NetworkTableInstance.getDefault().getTable("Elastic");

    private final DriveMotorSubsystem drive;
    private final SteeringMotorSubsystem steering;

    public ElasticTelemetry(
            DriveMotorSubsystem drive,
            SteeringMotorSubsystem steering) {

        this.drive = drive;
        this.steering = steering;
    }

    /**
     * Call once every robot loop.
     */
    public void update() {

        publishDrive();

        publishSteering();
    }

    private void publishDrive() {

        NetworkTable table = root.getSubTable("Drive");

        table.getDoubleTopic("MotorPositionRotations")
                .publish()
                .set(drive.getMotorPositionRotations());

        table.getDoubleTopic("MotorPositionDegrees")
                .publish()
                .set(drive.getMotorPositionDegrees());

        table.getDoubleTopic("MotorVelocityRPM")
                .publish()
                .set(drive.getMotorVelocityRpm());

        table.getDoubleTopic("WheelPositionMeters")
                .publish()
                .set(drive.getWheelPositionMeters());

        table.getDoubleTopic("WheelVelocityMetersPerSecond")
                .publish()
                .set(drive.getWheelVelocityMetersPerSecond());

        table.getDoubleTopic("AbsolutePosition")
                .publish()
                .set(drive.getAbsolutePosition());

        table.getDoubleTopic("AbsolutePositionDegrees")
                .publish()
                .set(drive.getAbsolutePositionDegrees());

        table.getDoubleTopic("MotorOutput")
                .publish()
                .set(drive.getMotorOutput());
    }

    private void publishSteering() {

        NetworkTable table = root.getSubTable("Steering");

        table.getDoubleTopic("AngleRotations")
                .publish()
                .set(steering.getAngleRotations());

        table.getDoubleTopic("AngleDegrees")
                .publish()
                .set(steering.getAngleDegrees());

        table.getDoubleTopic("AbsolutePosition")
                .publish()
                .set(steering.getAbsolutePosition());

        table.getDoubleTopic("AbsolutePositionDegrees")
                .publish()
                .set(steering.getAbsolutePositionDegrees());

        table.getDoubleTopic("MotorVelocityRPM")
                .publish()
                .set(steering.getMotorVelocityRpm());

        table.getDoubleTopic("MotorOutput")
                .publish()
                .set(steering.getMotorOutput());
    }
}
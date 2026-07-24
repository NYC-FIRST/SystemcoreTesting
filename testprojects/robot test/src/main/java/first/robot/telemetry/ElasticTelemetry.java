package first.robot.telemetry;

import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.StringPublisher;

import first.robot.swerveTests.subsystems.DriveMotorSubsystem;
import first.robot.swerveTests.subsystems.SteeringMotorSubsystem;

public class ElasticTelemetry {

    private final DriveMotorSubsystem drive;
    private final SteeringMotorSubsystem steering;

    // Drive publishers
    private final DoublePublisher driveMotorPositionRotations;
    private final DoublePublisher driveMotorPositionDegrees;
    private final DoublePublisher driveMotorVelocityRPM;
    private final DoublePublisher driveWheelPositionMeters;
    private final DoublePublisher driveWheelVelocityMetersPerSecond;
    private final DoublePublisher driveAbsolutePosition;
    private final DoublePublisher driveAbsolutePositionDegrees;
    private final DoublePublisher driveMotorOutput;

    // Steering publishers
    private final DoublePublisher steeringAngleRotations;
    private final DoublePublisher steeringAngleDegrees;
    private final DoublePublisher steeringAbsolutePosition;
    private final DoublePublisher steeringAbsolutePositionDegrees;
    private final DoublePublisher steeringMotorVelocityRPM;
    private final DoublePublisher steeringMotorOutput;

    // Robot publishers
    private final StringPublisher driveMode;

    public ElasticTelemetry(
            DriveMotorSubsystem drive,
            SteeringMotorSubsystem steering) {

        this.drive = drive;
        this.steering = steering;

        NetworkTable root =
                NetworkTableInstance.getDefault().getTable("Elastic");

        NetworkTable driveTable =
                root.getSubTable("Drive Motor Telemetry");

        NetworkTable steeringTable =
                root.getSubTable("Steering Motor Telemetry");

        NetworkTable robotTable =
                root.getSubTable("Robot Telemetry");

        // Robot
        driveMode =
                robotTable.getStringTopic("Drive Mode").publish();

        // Drive
        driveMotorPositionRotations =
                driveTable.getDoubleTopic("Motor Rotations").publish();

        driveMotorPositionDegrees =
                driveTable.getDoubleTopic("Motor Degrees").publish();

        driveMotorVelocityRPM =
                driveTable.getDoubleTopic("Motor RPM").publish();

        driveWheelPositionMeters =
                driveTable.getDoubleTopic("Wheel Position (m)").publish();

        driveWheelVelocityMetersPerSecond =
                driveTable.getDoubleTopic("Wheel Velocity (m/s)").publish();

        driveAbsolutePosition =
                driveTable.getDoubleTopic("Absolute Encoder").publish();

        driveAbsolutePositionDegrees =
                driveTable.getDoubleTopic("Absolute Encoder Degrees").publish();

        driveMotorOutput =
                driveTable.getDoubleTopic("Motor Output").publish();

        // Steering
        steeringAngleRotations =
                steeringTable.getDoubleTopic("Motor Rotations").publish();

        steeringAngleDegrees =
                steeringTable.getDoubleTopic("Motor Degrees").publish();

        steeringAbsolutePosition =
                steeringTable.getDoubleTopic("Absolute Encoder").publish();

        steeringAbsolutePositionDegrees =
                steeringTable.getDoubleTopic("Absolute Encoder Degrees").publish();

        steeringMotorVelocityRPM =
                steeringTable.getDoubleTopic("Motor RPM").publish();

        steeringMotorOutput =
                steeringTable.getDoubleTopic("Motor Output").publish();
    }

    public void update() {
        publishDrive();
        publishSteering();
    }

    public void setDriveMode(String mode) {
        driveMode.set(mode);
    }

    private void publishDrive() {

        driveMotorPositionRotations.set(drive.getMotorPositionRotations());
        driveMotorPositionDegrees.set(drive.getMotorPositionDegrees());
        driveMotorVelocityRPM.set(drive.getMotorVelocityRpm());
        driveWheelPositionMeters.set(drive.getWheelPositionMeters());
        driveWheelVelocityMetersPerSecond.set(drive.getWheelVelocityMetersPerSecond());
        driveAbsolutePosition.set(drive.getAbsolutePosition());
        driveAbsolutePositionDegrees.set(drive.getAbsolutePositionDegrees());
        driveMotorOutput.set(drive.getMotorOutput());
    }

    private void publishSteering() {

        steeringAngleRotations.set(steering.getAngleRotations());
        steeringAngleDegrees.set(steering.getAngleDegrees());
        steeringAbsolutePosition.set(steering.getAbsolutePosition());
        steeringAbsolutePositionDegrees.set(steering.getAbsolutePositionDegrees());
        steeringMotorVelocityRPM.set(steering.getMotorVelocityRpm());
        steeringMotorOutput.set(steering.getMotorOutput());
    }
}
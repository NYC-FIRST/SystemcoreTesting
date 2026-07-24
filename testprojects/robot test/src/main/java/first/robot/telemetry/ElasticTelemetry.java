package first.robot.telemetry;

import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;

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

    public ElasticTelemetry(
            DriveMotorSubsystem drive,
            SteeringMotorSubsystem steering) {

        this.drive = drive;
        this.steering = steering;

        NetworkTable root = NetworkTableInstance.getDefault().getTable("Elastic");

        NetworkTable driveTable = root.getSubTable("Drive");
        NetworkTable steeringTable = root.getSubTable("Steering");

        // Drive publishers
        driveMotorPositionRotations =
                driveTable.getDoubleTopic("MotorPositionRotations").publish();

        driveMotorPositionDegrees =
                driveTable.getDoubleTopic("MotorPositionDegrees").publish();

        driveMotorVelocityRPM =
                driveTable.getDoubleTopic("MotorVelocityRPM").publish();

        driveWheelPositionMeters =
                driveTable.getDoubleTopic("WheelPositionMeters").publish();

        driveWheelVelocityMetersPerSecond =
                driveTable.getDoubleTopic("WheelVelocityMetersPerSecond").publish();

        driveAbsolutePosition =
                driveTable.getDoubleTopic("AbsolutePosition").publish();

        driveAbsolutePositionDegrees =
                driveTable.getDoubleTopic("AbsolutePositionDegrees").publish();

        driveMotorOutput =
                driveTable.getDoubleTopic("MotorOutput").publish();

        // Steering publishers
        steeringAngleRotations =
                steeringTable.getDoubleTopic("AngleRotations").publish();

        steeringAngleDegrees =
                steeringTable.getDoubleTopic("AngleDegrees").publish();

        steeringAbsolutePosition =
                steeringTable.getDoubleTopic("AbsolutePosition").publish();

        steeringAbsolutePositionDegrees =
                steeringTable.getDoubleTopic("AbsolutePositionDegrees").publish();

        steeringMotorVelocityRPM =
                steeringTable.getDoubleTopic("MotorVelocityRPM").publish();

        steeringMotorOutput =
                steeringTable.getDoubleTopic("MotorOutput").publish();
    }

    /**
     * Call once every robot loop.
     */
    public void update() {
        publishDrive();
        publishSteering();
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
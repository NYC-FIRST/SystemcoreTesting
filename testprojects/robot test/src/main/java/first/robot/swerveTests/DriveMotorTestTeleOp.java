package first.robot.swerveTests;

import first.robot.Robot;
import first.robot.telemetry.ElasticTelemetry;
import first.robot.swerveTests.subsystems.DriveMotorSubsystem;
import first.robot.swerveTests.subsystems.SteeringMotorSubsystem;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

@Teleop(name = "Drive Motor Test")
public class DriveMotorTestTeleOp extends PeriodicOpMode {

    private final DriveMotorSubsystem driveSubsystem;
    private final SteeringMotorSubsystem steeringSubsystem;
    private final DefaultUserControls userControls;
    private final ElasticTelemetry telemetry;

    public DriveMotorTestTeleOp(
            Robot robot,
            DefaultUserControls userControls) {

        System.out.println(">>> Constructing Drive Motor Test <<<");

        this.userControls = userControls;

        driveSubsystem = new DriveMotorSubsystem(robot.swerveDrive);
        steeringSubsystem = new SteeringMotorSubsystem(robot.swerveTheta);

        telemetry = new ElasticTelemetry(
                driveSubsystem,
                steeringSubsystem);

        telemetry.setDriveMode("Drive Motor Test");

        System.out.println(">>> Elastic Telemetry Created <<<");
    }

    @Override
    public void start() {
        System.out.println("========== DRIVE MOTOR TEST STARTED ==========");
    }

    @Override
    public void periodic() {

        System.out.println("Periodic Running");

        Gamepad gamepad = userControls.getGamepad(0);

        double throttle = -gamepad.getLeftY();

        driveSubsystem.setThrottle(throttle);

        telemetry.setDriveMode("Drive Motor Test");
        telemetry.update();

        System.out.printf(
                "Throttle: %.2f | Abs: %.4f | RPM: %.2f%n",
                throttle,
                driveSubsystem.getAbsolutePosition(),
                driveSubsystem.getMotorVelocityRpm());
    }

    @Override
    public void end() {

        System.out.println("========== DRIVE MOTOR TEST ENDED ==========");

        driveSubsystem.stop();
    }
}

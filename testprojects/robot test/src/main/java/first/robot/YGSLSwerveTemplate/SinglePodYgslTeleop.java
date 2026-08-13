package first.robot.YGSLSwerveTemplate;

import first.robot.Robot;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DiagonalTwoPodDriveSubsystem;
import first.robot.swerveTests.subsystems.DriveMotorSubsystem;
import first.robot.swerveTests.subsystems.SteeringMotorSubsystem;
import first.robot.telemetry.ElasticTelemetry;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
        name = "YGSL Diagonal Two Pod Test",
        group = "Test",
        description = "Front-left D10/D11 and back-right D12/D13 powered pods")
public class SinglePodYgslTeleop extends PeriodicOpMode {

    private final Robot robot;
    private final DefaultUserControls userControls;
    private final DiagonalTwoPodDriveSubsystem diagonalDrive;
    private final ElasticTelemetry telemetry;
    private final Timer statusTimer = new Timer();

    public SinglePodYgslTeleop(Robot robot, DefaultUserControls userControls) {
        this.robot = robot;
        this.userControls = userControls;
        diagonalDrive = new DiagonalTwoPodDriveSubsystem(
                robot.swerveDrive,
                robot.swerveTheta,
                robot.swerveDrive2,
                robot.swerveTheta2);
        telemetry = new ElasticTelemetry(
                new DriveMotorSubsystem(robot.swerveDrive),
                new SteeringMotorSubsystem(robot.swerveTheta));
    }

    @Override
    public void start() {
        robot.disableA301s();
        statusTimer.restart();
    }

    @Override
    public void periodic() {
        Gamepad gamepad = userControls.getGamepad(Constants.Operator.DRIVER_CONTROLLER);

        double forward = -gamepad.getLeftY();
        double left = -gamepad.getLeftX();
        double rotation = -gamepad.getRightX();
        diagonalDrive.drive(forward, left, rotation);
        telemetry.setDriveMode("YGSL Diagonal Two Pod Test");
        telemetry.update();

        if (statusTimer.advanceIfElapsed(0.5)) {
            robot.printSwerveStatus();
        }
    }

    @Override
    public void end() {
        diagonalDrive.stop();
    }
}

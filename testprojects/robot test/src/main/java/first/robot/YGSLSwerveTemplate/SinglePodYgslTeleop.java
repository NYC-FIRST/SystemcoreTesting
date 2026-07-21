package first.robot.YGSLSwerveTemplate;

import first.robot.Robot;
import first.robot.YGSLSwerveTemplate.subsystems.drive.SinglePodDriveSubsystem;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
        name = "YGSL Single Pod Test",
        group = "Test",
        description = "YGSL wrapper on Robot single swerve pod")
public class SinglePodYgslTeleop extends PeriodicOpMode {

    private final Robot robot;
    private final DefaultUserControls userControls;
    private final SinglePodDriveSubsystem singlePod;
    private final Timer statusTimer = new Timer();

    public SinglePodYgslTeleop(Robot robot, DefaultUserControls userControls) {
        this.robot = robot;
        this.userControls = userControls;
        singlePod = new SinglePodDriveSubsystem(robot.swerveDrive, robot.swerveTheta);
    }

    @Override
    public void start() {
        robot.disableA301s();
        statusTimer.restart();
    }

    @Override
    public void periodic() {
        Gamepad gamepad = userControls.getGamepad(Constants.Operator.DRIVER_CONTROLLER);

        double x = gamepad.getLeftX();
        double y = -gamepad.getLeftY();
        singlePod.driveToward(x, y);

        if (statusTimer.advanceIfElapsed(0.5)) {
            robot.printSwerveStatus();
        }
    }

    @Override
    public void end() {
        singlePod.stop();
    }
}

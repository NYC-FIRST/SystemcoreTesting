package first.robot.swerveTests;

import first.robot.Robot;
import first.robot.swerveTests.subsystems.DriveMotorSubsystem;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

@Teleop(name = "Drive Motor Test")
public class DriveMotorTestTeleOp extends PeriodicOpMode {

    private final DriveMotorSubsystem driveSubsystem;
    private final DefaultUserControls userControls;

    public DriveMotorTestTeleOp(Robot robot,
                                DefaultUserControls userControls) {

        this.userControls = userControls;
        this.driveSubsystem = new DriveMotorSubsystem();
    }

    @Override
    public void start() {
        System.out.println("=== Drive Motor Test Started ===");
    }

    @Override
    public void periodic() {

        Gamepad gamepad = userControls.getGamepad(0);

        double throttle = -gamepad.getLeftY();

        driveSubsystem.setThrottle(throttle);

        System.out.printf(
                "Throttle: %.2f | Position: %.2f rot | Velocity: %.2f RPM%n",
                throttle,
                driveSubsystem.getMotorPositionRotations(),
                driveSubsystem.getMotorVelocityRpm());
    }

    @Override
    public void end() {
        driveSubsystem.stop();
    }
}
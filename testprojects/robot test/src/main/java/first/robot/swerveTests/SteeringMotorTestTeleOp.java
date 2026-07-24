package first.robot.swerveTests;

import first.robot.Robot;
import first.robot.swerveTests.subsystems.SteeringMotorSubsystem;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

@Teleop(name = "Steering Motor Test")
public class SteeringMotorTestTeleOp extends PeriodicOpMode {

    private final SteeringMotorSubsystem steeringSubsystem;
    private final DefaultUserControls userControls;

    public SteeringMotorTestTeleOp(Robot robot,
                                   DefaultUserControls userControls) {

        this.userControls = userControls;
        this.steeringSubsystem = new SteeringMotorSubsystem(robot.swerveTheta);
    }

    @Override
    public void start() {
        System.out.println("=== Steering Motor Test Started ===");
    }

    @Override
    public void periodic() {

        Gamepad gamepad = userControls.getGamepad(0);

        double throttle = -gamepad.getLeftY();

        steeringSubsystem.setThrottle(throttle);

        System.out.printf(
                "Throttle: %.2f | Encoder: %.3f rotations%n",
                throttle,
                steeringSubsystem.getAngleRotations());
    }

    @Override
    public void end() {
        steeringSubsystem.stop();
    }
}

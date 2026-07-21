package first.robot.YGSLSwerveTemplate.commands;

import java.util.function.DoubleSupplier;
import org.wpilib.command2.Command;
import org.wpilib.math.kinematics.ChassisVelocities;
import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DriveSubsystem;

/**
 * Default command for driving the robot.
 */
public class DriveCommand extends Command {

    private final DriveSubsystem driveSubsystem;

    private final DoubleSupplier xSpeed;
    private final DoubleSupplier ySpeed;
    private final DoubleSupplier rotation;

    public DriveCommand(
            DriveSubsystem driveSubsystem,
            DoubleSupplier xSpeed,
            DoubleSupplier ySpeed,
            DoubleSupplier rotation) {

        this.driveSubsystem = driveSubsystem;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.rotation = rotation;

        addRequirements(driveSubsystem);
    }

    @Override
    public void execute() {

        double x = applyDeadband(
                xSpeed.getAsDouble(),
                Constants.Operator.DEADBAND);

        double y = applyDeadband(
                ySpeed.getAsDouble(),
                Constants.Operator.DEADBAND);

        double omega = applyDeadband(
                rotation.getAsDouble(),
                Constants.Operator.DEADBAND);

        ChassisVelocities speeds = new ChassisVelocities(
                x * Constants.Drive.MAX_SPEED,
                y * Constants.Drive.MAX_SPEED,
                omega * Constants.Drive.MAX_ANGULAR_SPEED);

        driveSubsystem.drive(speeds);
    }

    @Override
    public void end(boolean interrupted) {
        driveSubsystem.stop();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    private static double applyDeadband(double value, double deadband) {
        return Math.abs(value) < deadband ? 0.0 : value;
    }
}

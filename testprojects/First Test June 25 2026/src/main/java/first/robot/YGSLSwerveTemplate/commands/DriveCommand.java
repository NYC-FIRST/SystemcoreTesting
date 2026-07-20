package first.robot.YGSLSwerveTemplate.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.Command;
import first.robot.YGSLSwerveTemplate.Constants;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DriveSubsystem;

import java.util.function.DoubleSupplier;

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

        double x = MathUtil.applyDeadband(
                xSpeed.getAsDouble(),
                Constants.Operator.DEADBAND);

        double y = MathUtil.applyDeadband(
                ySpeed.getAsDouble(),
                Constants.Operator.DEADBAND);

        double omega = MathUtil.applyDeadband(
                rotation.getAsDouble(),
                Constants.Operator.DEADBAND);

        ChassisSpeeds speeds = new ChassisSpeeds(
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
}
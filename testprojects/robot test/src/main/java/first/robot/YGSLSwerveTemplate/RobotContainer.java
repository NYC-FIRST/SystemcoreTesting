package first.robot.YGSLSwerveTemplate;

import java.util.function.DoubleSupplier;
import first.robot.YGSLSwerveTemplate.commands.DriveCommand;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DriveSubsystem;

public class RobotContainer {

    private final DriveSubsystem driveSubsystem;
    private final DriveCommand driveCommand;

    public RobotContainer(
            DriveSubsystem driveSubsystem,
            DoubleSupplier xSpeed,
            DoubleSupplier ySpeed,
            DoubleSupplier rotation) {

        this.driveSubsystem = driveSubsystem;
        driveCommand = new DriveCommand(driveSubsystem, xSpeed, ySpeed, rotation);

        driveSubsystem.setDefaultCommand(driveCommand);
    }

    public DriveSubsystem getDriveSubsystem() {
        return driveSubsystem;
    }

    public DriveCommand getDriveCommand() {
        return driveCommand;
    }
}

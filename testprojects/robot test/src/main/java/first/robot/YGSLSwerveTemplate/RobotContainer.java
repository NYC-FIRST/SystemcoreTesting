package first.robot.YGSLSwerveTemplate;

import java.util.function.DoubleSupplier;
import first.robot.YGSLSwerveTemplate.commands.DriveCommand;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DriveSubsystem;
import first.robot.YGSLSwerveTemplate.vision.VisionSubsystem;

public class RobotContainer {

    private final DriveSubsystem driveSubsystem;
    private final DriveCommand driveCommand;
    private final VisionSubsystem visionSubsystem;

    public RobotContainer(
            DriveSubsystem driveSubsystem,
            DoubleSupplier xSpeed,
            DoubleSupplier ySpeed,
            DoubleSupplier rotation) {

        this.driveSubsystem = driveSubsystem;
        driveCommand = new DriveCommand(driveSubsystem, xSpeed, ySpeed, rotation);
        visionSubsystem = new VisionSubsystem();

        driveSubsystem.setDefaultCommand(driveCommand);
    }

    public DriveSubsystem getDriveSubsystem() {
        return driveSubsystem;
    }

    public DriveCommand getDriveCommand() {
        return driveCommand;
    }

    public VisionSubsystem getVisionSubsystem() {
        return visionSubsystem;
    }
}
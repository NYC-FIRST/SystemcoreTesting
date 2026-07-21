package first.robot.YGSLSwerveTemplate;

import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import first.robot.YGSLSwerveTemplate.commands.DriveCommand;
import first.robot.YGSLSwerveTemplate.subsystems.drive.DriveSubsystem;

public class RobotContainer {

    private final DriveSubsystem driveSubsystem = new DriveSubsystem();

    // PS5 controller on USB port 0
    private final CommandPS5Controller driver =
            new CommandPS5Controller(Constants.Operator.DRIVER_CONTROLLER);

    public RobotContainer() {

        driveSubsystem.setDefaultCommand(
                new DriveCommand(
                        driveSubsystem,

                        // Forward / Backward
                        () -> -driver.getLeftY(),

                        // Left / Right
                        () -> -driver.getLeftX(),

                        // Rotation
                        () -> -driver.getRightX()
                )
        );
    }
}
package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

/** Open-loop, low-speed test for determining whether each of the four drive motors is reversed. */
@Teleop(
    name = "Four Swerve Drive Direction Test",
    group = "Test",
    description = "Left stick drives at 15%; steering motors stay disabled")
public class FourSwerveDriveDirectionTest extends PeriodicOpMode {
  private static final double DEADBAND = 0.08;
  private static final double MAX_DRIVE_THROTTLE = 0.15;

  private final Robot robot;
  private final DefaultUserControls userControls;

  public FourSwerveDriveDirectionTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    this.userControls = userControls;
  }

  @Override
  public void start() {
    robot.disableA301s();
    robot.disableFourSwervePods();
  }

  @Override
  public void periodic() {
    Gamepad gamepad = userControls.getGamepad(0);
    double stick = -gamepad.getLeftY();
    double driveThrottle = Math.abs(stick) < DEADBAND ? 0.0 : stick * MAX_DRIVE_THROTTLE;

    // Keep steering unpowered while drive direction is being identified.
    for (int i = 0; i < robot.swerveSteeringMotors.length; i++) {
      robot.swerveSteeringMotors[i].disable();
      robot.swerveDriveMotors[i].setThrottle(driveThrottle);
    }
  }

  @Override
  public void end() {
    robot.disableFourSwervePods();
  }
}

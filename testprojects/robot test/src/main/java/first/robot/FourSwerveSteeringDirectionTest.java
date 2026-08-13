package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

/**
 * Low-speed, one-module-at-a-time steering direction test.
 *
 * <p>Hold a face button and move the left stick left/right to rotate only its steering motor:
 * north = D0 (front left), east = D1 (front right), south = D2 (back left), and west = D3
 * (back right). All drive motors remain disabled.
 */
@Teleop(
    name = "Four Swerve Steer Direction Test",
    group = "Test",
    description = "Face button selects motor; stick left/right turns at 15%")
public class FourSwerveSteeringDirectionTest extends PeriodicOpMode {
  private static final double DEADBAND = 0.08;
  private static final double MAX_STEERING_THROTTLE = 0.15;

  private final Robot robot;
  private final DefaultUserControls userControls;

  public FourSwerveSteeringDirectionTest(Robot robot, DefaultUserControls userControls) {
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
    int steeringIndex = selectedSteeringIndex(gamepad);
    double stick = gamepad.getLeftX();
    double throttle = Math.abs(stick) < DEADBAND ? 0.0 : stick * MAX_STEERING_THROTTLE;

    // Reassert the safe state each loop so only the selected steering motor can move.
    for (int i = 0; i < robot.swerveDriveMotors.length; i++) {
      robot.swerveDriveMotors[i].disable();
      robot.swerveSteeringMotors[i].disable();
    }

    if (steeringIndex >= 0) {
      robot.swerveSteeringMotors[steeringIndex].setThrottle(throttle);
    }
  }

  @Override
  public void end() {
    robot.disableFourSwervePods();
  }

  private static int selectedSteeringIndex(Gamepad gamepad) {
    // swerveSteeringMotors order: FL D0, FR D1, BL D2, BR D3.
    if (gamepad.getNorthFaceButton()) {
      return 0; // D0, front left
    }
    if (gamepad.getEastFaceButton()) {
      return 1; // D1, front right
    }
    if (gamepad.getSouthFaceButton()) {
      return 2; // D2, back left
    }
    if (gamepad.getWestFaceButton()) {
      return 3; // D3, back right
    }
    return -1;
  }
}

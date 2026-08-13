package first.robot;

import com.revrobotics.util.Signal;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

/**
 * Slow, translation-only four-module swerve bring-up test.
 *
 * <p>The left stick selects the desired robot-relative travel direction. The test turns all four
 * modules toward that direction using open-loop software proportional control, then enables the
 * drive motors only after every module is close to its target. It deliberately does not use A301
 * onboard position control.
 */
@Teleop(
    name = "Four Swerve Slow Drive Test",
    group = "Test",
    description = "Left stick steers then drives all modules at 12%")
public class FourSwerveSlowDriveTest extends PeriodicOpMode {
  private static final double STICK_DEADBAND = 0.12;
  private static final double MAX_DRIVE_THROTTLE = 0.12;
  private static final double STEERING_KP = 1.2;
  private static final double MAX_STEERING_THROTTLE = 0.15;
  private static final double MIN_STEERING_THROTTLE = 0.06;
  private static final double STEERING_TOLERANCE_ROTATIONS = 0.04;

  private final Robot robot;
  private final DefaultUserControls userControls;

  public FourSwerveSlowDriveTest(Robot robot, DefaultUserControls userControls) {
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
    double forward = -gamepad.getLeftY();
    double right = gamepad.getLeftX();
    double magnitude = Math.hypot(forward, right);

    if (magnitude < STICK_DEADBAND) {
      robot.disableFourSwervePods();
      return;
    }

    // WPILib's positive Y axis is robot-left, so negate the driver's positive-right X axis.
    double desiredAngleRotations = Math.atan2(-right, forward) / (2.0 * Math.PI);
    boolean everyModuleAligned = true;

    for (int i = 0; i < robot.swerveSteeringMotors.length; i++) {
      Signal<Double> reading = robot.swerveSteeringMotors[i].getAbsoluteEncoderPosition();
      if (!reading.isValid()) {
        robot.swerveSteeringMotors[i].disable();
        robot.swerveDriveMotors[i].disable();
        everyModuleAligned = false;
        continue;
      }

      double currentAngleRotations =
          wrapRotations(reading.get() - Robot.SWERVE_STEERING_STRAIGHT_OFFSETS_ROTATIONS[i]);
      double errorRotations = wrapRotations(desiredAngleRotations - currentAngleRotations);

      if (Math.abs(errorRotations) <= STEERING_TOLERANCE_ROTATIONS) {
        robot.swerveSteeringMotors[i].setThrottle(0.0);
      } else {
        double steeringThrottle = clamp(STEERING_KP * errorRotations,
            -MAX_STEERING_THROTTLE, MAX_STEERING_THROTTLE);
        steeringThrottle = Math.copySign(
            Math.max(Math.abs(steeringThrottle), MIN_STEERING_THROTTLE), steeringThrottle);
        robot.swerveSteeringMotors[i].setThrottle(steeringThrottle);
        everyModuleAligned = false;
      }
      robot.swerveDriveMotors[i].disable();
    }

    if (everyModuleAligned) {
      double driveThrottle = Math.min(magnitude, 1.0) * MAX_DRIVE_THROTTLE;
      for (int i = 0; i < robot.swerveDriveMotors.length; i++) {
        robot.swerveDriveMotors[i].setThrottle(driveThrottle);
      }
    }
  }

  @Override
  public void end() {
    robot.disableFourSwervePods();
  }

  private static double wrapRotations(double rotations) {
    return rotations - Math.floor(rotations + 0.5);
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}

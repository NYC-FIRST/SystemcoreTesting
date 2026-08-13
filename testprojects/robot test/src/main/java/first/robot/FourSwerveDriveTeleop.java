package first.robot;

import com.revrobotics.util.Signal;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

/** Normal robot-relative four-module swerve teleop using open-loop software steering control. */
@Teleop(
    name = "Four Swerve Drive",
    group = "Teleop",
    description = "Left stick drives; right stick rotates; full output")
public class FourSwerveDriveTeleop extends PeriodicOpMode {
  private static final double STICK_DEADBAND = 0.10;
  private static final double MAX_DRIVE_THROTTLE = 1.0;
  private static final double MAX_ROTATION = 1.0;
  private static final double STEERING_KP = 4.0;
  private static final double MAX_STEERING_THROTTLE = 1.0;
  private static final double MIN_STEERING_THROTTLE = 0.15;
  private static final double STEERING_TOLERANCE_ROTATIONS = 0.04;

  // Module order is FL, FR, BL, BR, matching Robot's motor and offset arrays.
  private static final double[] MODULE_X = {1.0, 1.0, -1.0, -1.0};
  private static final double[] MODULE_Y = {1.0, -1.0, 1.0, -1.0};

  private final Robot robot;
  private final DefaultUserControls userControls;

  public FourSwerveDriveTeleop(Robot robot, DefaultUserControls userControls) {
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
    double forward = applyDeadband(-gamepad.getLeftY());
    // The physical steering-angle convention is mirrored across the robot's forward axis, so
    // both horizontal driver inputs use the same sign: strafe and yaw must be flipped together.
    double robotLeft = applyDeadband(gamepad.getLeftX());
    double rotation = applyDeadband(gamepad.getRightX()) * MAX_ROTATION;

    if (forward == 0.0 && robotLeft == 0.0 && rotation == 0.0) {
      stopOutputs();
      return;
    }

    double[] wheelForward = new double[4];
    double[] wheelLeft = new double[4];
    double maxWheelMagnitude = 1.0;
    for (int i = 0; i < wheelForward.length; i++) {
      // Positive rotation is counter-clockwise: front wheels travel left, rear wheels right.
      wheelForward[i] = forward - rotation * MODULE_Y[i];
      wheelLeft[i] = robotLeft + rotation * MODULE_X[i];
      maxWheelMagnitude = Math.max(maxWheelMagnitude, Math.hypot(wheelForward[i], wheelLeft[i]));
    }

    for (int i = 0; i < robot.swerveSteeringMotors.length; i++) {
      Signal<Double> reading = robot.swerveSteeringMotors[i].getAbsoluteEncoderPosition();
      if (!reading.isValid() || !Double.isFinite(reading.get())) {
        robot.swerveSteeringMotors[i].disable();
        robot.swerveDriveMotors[i].disable();
        continue;
      }

      double targetAngle = Math.atan2(wheelLeft[i], wheelForward[i]) / (2.0 * Math.PI);
      double currentAngle =
          wrapRotations(reading.get() - Robot.SWERVE_STEERING_STRAIGHT_OFFSETS_ROTATIONS[i]);
      double error = wrapRotations(targetAngle - currentAngle);

      // Turning a module 180 degrees and reversing its drive direction reduces steering travel.
      double wheelMagnitude = Math.hypot(wheelForward[i], wheelLeft[i]) / maxWheelMagnitude;
      if (Math.abs(error) > 0.25) {
        error = wrapRotations(error - Math.copySign(0.5, error));
        wheelMagnitude = -wheelMagnitude;
      }

      if (Math.abs(error) <= STEERING_TOLERANCE_ROTATIONS) {
        robot.swerveSteeringMotors[i].setThrottle(0.0);
      } else {
        double steeringThrottle = clamp(
            STEERING_KP * error, -MAX_STEERING_THROTTLE, MAX_STEERING_THROTTLE);
        steeringThrottle = Math.copySign(
            Math.max(Math.abs(steeringThrottle), MIN_STEERING_THROTTLE), steeringThrottle);
        robot.swerveSteeringMotors[i].setThrottle(steeringThrottle);
      }

      // This is the essential drive behavior: every module drives continuously while steering.
      // Cosine compensation smoothly removes only this wheel's power when it points sideways
      // relative to its requested direction, instead of stopping the complete drivetrain.
      double alignmentScale = Math.max(0.0, Math.cos(error * 2.0 * Math.PI));
      robot.swerveDriveMotors[i].setThrottle(
          wheelMagnitude * MAX_DRIVE_THROTTLE * alignmentScale);
    }
  }

  @Override
  public void end() {
    robot.disableFourSwervePods();
  }

  private void stopOutputs() {
    for (int i = 0; i < robot.swerveDriveMotors.length; i++) {
      robot.swerveDriveMotors[i].setThrottle(0.0);
      robot.swerveSteeringMotors[i].setThrottle(0.0);
    }
  }

  private static double applyDeadband(double value) {
    return Math.abs(value) < STICK_DEADBAND ? 0.0 : value;
  }

  private static double wrapRotations(double rotations) {
    return rotations - Math.floor(rotations + 0.5);
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}

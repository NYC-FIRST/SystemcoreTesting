package first.robot;

import first.robot.vision.AprilTagTarget;
import first.robot.vision.AprilTagVision;
import java.util.Optional;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;

/**
 * FTC SDK DriveToAprilTagOmni concept ported to the working A301 mecanum drivetrain.
 *
 * <p>For initial testing, put one tag in front of the robot, lift the wheels, and verify every axis
 * before allowing the robot to drive on the floor.
 */
@Autonomous
public class AprilTagDriveAutoMode extends PeriodicOpMode {
  // SystemCore's lightweight best-target topic does not expose the tag ID.
  private static final int DESIRED_TAG_ID = -1;
  private static final double DESIRED_RANGE_METERS = 0.30;

  // FTC sample-style proportional gains. Tune slowly for this robot.
  private static final double SPEED_GAIN = 1.2; // command per meter
  private static final double STRAFE_GAIN = 1.0; // command per radian
  private static final double TURN_GAIN = 1.0; // command per radian

  private static final double MAX_FORWARD = 0.25;
  private static final double MAX_STRAFE = 0.25;
  private static final double MAX_TURN = 0.20;

  private final Robot robot;
  private AprilTagVision vision;

  public AprilTagDriveAutoMode(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void start() {
    robot.drive.stopMotor();
    vision = new AprilTagVision(DESIRED_TAG_ID);
  }

  @Override
  public void periodic() {
    Optional<AprilTagTarget> target = vision.getTarget();
    if (target.isEmpty()) {
      // Fail safe: unlike the FTC driver-assisted sample, autonomous mode does not search blindly.
      robot.drive.stopMotor();
      return;
    }

    AprilTagTarget tag = target.get();
    double rangeError = tag.rangeMeters() - DESIRED_RANGE_METERS;
    double headingError = tag.bearingRadians();
    double yawError = tag.yawRadians();

    double forward = clamp(rangeError * SPEED_GAIN, -MAX_FORWARD, MAX_FORWARD);
    double strafe = clamp(-yawError * STRAFE_GAIN, -MAX_STRAFE, MAX_STRAFE);
    double turn = clamp(headingError * TURN_GAIN, -MAX_TURN, MAX_TURN);

    // MecanumDrive: +X forward, +Y left, +rotation counterclockwise.
    robot.drive.driveCartesian(forward, strafe, turn);
  }

  @Override
  public void end() {
    robot.drive.stopMotor();
    robot.disableA301s();
    if (vision != null) {
      vision.close();
      vision = null;
    }
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}

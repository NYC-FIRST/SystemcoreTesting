package first.robot.Archive;

import java.util.Optional;
import org.wpilib.opmode.Autonomous;
import org.wpilib.opmode.PeriodicOpMode;

import first.robot.Robot;

/**
 * FTC SDK DriveToAprilTagOmni concept ported to the working A301 mecanum drivetrain.
 *
 * <p>For initial testing, put tag 1 in front of the robot, lift the wheels, and verify every axis
 * before allowing the robot to drive on the floor.
 */
@Autonomous
public class AprilTagDriveAutoMode extends PeriodicOpMode {
  private static final int DESIRED_TAG_ID = 1;
  private static final double DESIRED_RANGE_METERS = 0.30;

  // FTC sample-style proportional gains. Tune slowly for this robot.
  private static final double SPEED_GAIN = 1.2; // command per meter
  private static final double STRAFE_GAIN = 1.0; // command per radian
  private static final double TURN_GAIN = 1.0; // command per radian

  private static final double MAX_FORWARD = 0.25;
  private static final double MAX_STRAFE = 0.25;
  private static final double MAX_TURN = 0.20;

  private final Robot robot;

  public AprilTagDriveAutoMode(Robot robot) {
    this.robot = robot;
  }

  @Override
  public void start() {
    robot.drive.stopMotor();
  }

  @Override
  public void periodic() {
    robot.drive.stopMotor();
  }

  @Override
  public void end() {
    robot.drive.stopMotor();
    robot.disableA301s();
  }

  private static double clamp(double value, double min, double max) {
    return Math.max(min, Math.min(max, value));
  }
}

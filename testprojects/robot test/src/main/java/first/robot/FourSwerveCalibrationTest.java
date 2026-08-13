package first.robot;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;
import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;

/**
 * Read-only steering calibration mode. Hand-turn each module to robot-forward and record its
 * raw absolute-encoder rotation value from Elastic. No motor receives a throttle command.
 */
@Teleop(
    name = "Four Swerve Calibration Reader",
    group = "Test",
    description = "Hand-turn pods; publishes raw steering encoders to Elastic")
public class FourSwerveCalibrationTest extends PeriodicOpMode {
  private final Robot robot;
  private final DoublePublisher[] absoluteRotations = new DoublePublisher[4];
  private final DoublePublisher[] absoluteDegrees = new DoublePublisher[4];

  public FourSwerveCalibrationTest(Robot robot, DefaultUserControls userControls) {
    this.robot = robot;
    NetworkTable root = NetworkTableInstance.getDefault().getTable("Elastic").getSubTable("Swerve Calibration");
    for (int i = 0; i < robot.swerveModuleNames.length; i++) {
      NetworkTable module = root.getSubTable(robot.swerveModuleNames[i]);
      absoluteRotations[i] = module.getDoubleTopic("Raw Absolute Rotations").publish();
      absoluteDegrees[i] = module.getDoubleTopic("Raw Absolute Degrees").publish();
    }
  }

  @Override
  public void start() {
    robot.disableA301s();
    robot.disableFourSwervePods();
  }

  @Override
  public void periodic() {
    // Re-disable here as a deliberate safety measure: this mode must remain read-only.
    robot.disableFourSwervePods();
    for (int i = 0; i < robot.swerveSteeringMotors.length; i++) {
      Signal<Double> reading = robot.swerveSteeringMotors[i].getAbsoluteEncoderPosition();
      double rotations = reading.isValid() ? reading.get() : Double.NaN;
      absoluteRotations[i].set(rotations);
      absoluteDegrees[i].set(rotations * 360.0);
    }
  }

  @Override
  public void end() {
    robot.disableFourSwervePods();
  }
}

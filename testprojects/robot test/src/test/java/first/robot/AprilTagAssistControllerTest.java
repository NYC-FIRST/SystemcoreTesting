package first.robot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import first.robot.AprilTagAssistController.DriveCommand;
import first.robot.vision.AprilTagTarget;
import org.junit.jupiter.api.Test;

class AprilTagAssistControllerTest {
  private static final double EPSILON = 1e-9;
  private final AprilTagAssistController controller = new AprilTagAssistController();

  @Test
  void mapsRangeBearingAndYawToTheExpectedMecanumAxes() {
    AprilTagTarget target = new AprilTagTarget(7, 0.40, 0.10, 0.20, 0);

    DriveCommand command = controller.calculate(target);

    assertEquals(0.12, command.forward(), EPSILON);
    assertEquals(-0.20, command.strafe(), EPSILON);
    assertEquals(0.10, command.turn(), EPSILON);
  }

  @Test
  void limitsEachAutomaticAxis() {
    AprilTagTarget target = new AprilTagTarget(7, 3.0, 2.0, -2.0, 0);

    DriveCommand command = controller.calculate(target);

    assertEquals(AprilTagAssistController.MAX_FORWARD, command.forward(), EPSILON);
    assertEquals(AprilTagAssistController.MAX_STRAFE, command.strafe(), EPSILON);
    assertEquals(AprilTagAssistController.MAX_TURN, command.turn(), EPSILON);
  }

  @Test
  void stopsAdvancingAtTheDesiredRange() {
    AprilTagTarget target =
        new AprilTagTarget(
            7, AprilTagAssistController.DESIRED_RANGE_METERS, 0.0, 0.0, 0);

    DriveCommand command = controller.calculate(target);

    assertEquals(0.0, command.forward(), EPSILON);
    assertEquals(0.0, command.strafe(), EPSILON);
    assertEquals(0.0, command.turn(), EPSILON);
  }

  @Test
  void ignoresSmallPoseNoiseInsideTheAlignmentTolerances() {
    AprilTagTarget target =
        new AprilTagTarget(
            7,
            AprilTagAssistController.DESIRED_RANGE_METERS + 0.01,
            Math.toRadians(1.0),
            Math.toRadians(-1.0),
            0);

    DriveCommand command = controller.calculate(target);

    assertEquals(0.0, command.forward(), EPSILON);
    assertEquals(0.0, command.strafe(), EPSILON);
    assertEquals(0.0, command.turn(), EPSILON);
  }

  @Test
  void rejectsNonFinitePoseBeforeItCanReachTheDrivetrain() {
    AprilTagTarget target = new AprilTagTarget(7, 0.40, Double.NaN, 0.0, 0);

    assertThrows(IllegalArgumentException.class, () -> controller.calculate(target));
  }
}

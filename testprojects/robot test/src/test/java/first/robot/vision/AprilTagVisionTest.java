package first.robot.vision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AprilTagVisionTest {
  private static final double EPSILON = 1e-9;

  @Test
  void convertsSystemCoreTargetPoseToRangeBearingAndYaw() {
    double expectedYaw = 0.20;
    double[] pose = {
      1.0, -0.25, 0.0, 0.0, 0.0, 180.0 + Math.toDegrees(expectedYaw)
    };

    Optional<AprilTagTarget> result =
        AprilTagVision.targetFromLimelightRobotSpacePose(pose, 583, 1234);

    assertTrue(result.isPresent());
    AprilTagTarget target = result.get();
    assertEquals(583, target.id());
    assertEquals(Math.hypot(1.0, 0.25), target.rangeMeters(), EPSILON);
    assertEquals(Math.atan2(0.25, 1.0), target.bearingRadians(), EPSILON);
    assertEquals(expectedYaw, target.yawRadians(), EPSILON);
    assertEquals(1234, target.timestampMicros());
  }

  @Test
  void convertsHeadOnLimelightRotationToZeroYawError() {
    double[] pose = {1.0, 0.0, 0.0, 0.0, 0.0, 180.0};

    Optional<AprilTagTarget> result =
        AprilTagVision.targetFromLimelightRobotSpacePose(pose, 583, 1234);

    assertTrue(result.isPresent());
    assertEquals(0.0, result.get().yawRadians(), EPSILON);
  }

  @Test
  void rejectsMissingOrNonFinitePoseData() {
    assertTrue(
        AprilTagVision
            .targetFromLimelightRobotSpacePose(new double[0], 583, 1234)
            .isEmpty());
    assertTrue(
        AprilTagVision
            .targetFromLimelightRobotSpacePose(
                new double[] {Double.NaN, 0.0, 0.0, 0.0, 0.0, 180.0}, 583, 1234)
            .isEmpty());
  }

  @Test
  void decodesSystemCoreMessagePackFiducialResult() throws IOException {
    byte[] packed = packedFiducialResult(583, 0.37, 0.04, 0.02, 5.13, -41.69, 171.28);

    Optional<AprilTagTarget> result =
        AprilTagVision.targetFromPackedResults(packed, -1, 4321);

    assertTrue(result.isPresent());
    AprilTagTarget target = result.get();
    assertEquals(583, target.id());
    assertEquals(Math.hypot(0.37, 0.04), target.rangeMeters(), EPSILON);
    assertEquals(Math.atan2(-0.04, 0.37), target.bearingRadians(), EPSILON);
    assertEquals(Math.toRadians(-8.72), target.yawRadians(), EPSILON);
    assertEquals(4321, target.timestampMicros());
  }

  private static byte[] packedFiducialResult(int id, double... pose) throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (DataOutputStream output = new DataOutputStream(bytes)) {
      output.writeByte(0x82); // two-entry map
      writePackedString(output, "v");
      output.writeByte(1);
      writePackedString(output, "Fiducial");
      output.writeByte(0x91); // one-entry array
      output.writeByte(0x82); // two-entry map
      writePackedString(output, "fID");
      output.writeByte(0xcd); // uint16
      output.writeShort(id);
      writePackedString(output, "t6t_rs");
      output.writeByte(0x90 | pose.length);
      for (double value : pose) {
        output.writeByte(0xcb);
        output.writeDouble(value);
      }
    }
    return bytes.toByteArray();
  }

  private static void writePackedString(DataOutputStream output, String value)
      throws IOException {
    byte[] utf8 = value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    output.writeByte(0xa0 | utf8.length);
    output.write(utf8);
  }
}

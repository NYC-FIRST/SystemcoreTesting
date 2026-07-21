# Issue Title

```text
[A301] Position control triggers invalid encoder telemetry and Sensor Fault after otherwise-correct read-only/open-loop behavior
```

# Issue Body

## Summary

We are bringing up A301 motors on Systemcore + Motioncore while building and testing a single swerve-module pod. The pod uses one A301 for steering (theta) and another A301 for wheel drive.

Both encoders appear to work correctly in REV Hardware Client 2 when no robot program is deployed/running. They also update plausibly while running ordinary open-loop robot code that only calls `setThrottle(...)` and does not use encoder-based position control.

The failure appears when we try to use the A301 position-control APIs from robot code:

- Earlier relative-position/relative-feedback testing on another A301 produced unusable feedback and could not stop at the requested travel.
- The current swerve theta test uses the absolute encoder and the documented absolute-position APIs. Running it causes the encoder telemetry to become physically impossible, the controller to pulse, and `Sensor Fault` / `Other Fault` to appear.
- Reinstalling `27.0.0-prerelease-15` restores normal-looking encoder telemetry. Running the absolute-position test causes the failure to return.

This makes the problem look specific to using encoder-based position control through REVLib, rather than a disconnected encoder or an encoder that is always defective.

> [!IMPORTANT]
> We stopped using the position-control test because the reported feedback cannot be trusted and the motor can react to fictional position error.

## Environment

| Component | Version / Configuration |
| --- | --- |
| Controller | Systemcore + Motioncore |
| WPILib / GradleRIO | `2027.0.0-alpha-6` |
| REVLib | `2027.0.0-alpha-4` |
| A301 firmware | `27.0.0-prerelease-15` |
| A301 bootloader | `1.3` |
| REV Hardware Client 2 | `1.2.1` on Systemcore |
| Theta A301 | Motioncore `D11`, default CAN ID `3`, 500 RPM gearbox |
| Drive A301 | Motioncore `D10`, default CAN ID `3` |
| Earlier relative test A301 | Motioncore `D4`, default CAN ID `3` |
| Supply voltage visible in recording | Approximately `16.46-16.48 V` |
| Language | Java |

All A301 objects use `CANBusMap` rather than raw integer bus IDs.

```java
public final A301 armJoint0 = new A301(CANBusMap.CAN_D4);
public final A301 swerveDrive = new A301(CANBusMap.CAN_D10);
public final A301 swerveTheta = new A301(CANBusMap.CAN_D11);
```

## Intended Swerve-Pod Behavior

The current test is intended to prove one swerve pod before expanding to a complete drivetrain:

1. Use the left joystick as a unit circle.
2. Map joystick direction to the theta motor's single-turn absolute position.
3. Treat absolute encoder position `0.042` as straight ahead.
4. Use continuous absolute input so the theta motor takes the shortest path across the `-0.5` / `0.5` boundary.
5. Drive the wheel from joystick magnitude after theta is close to its target.
6. Read the drive A301's relative encoder as distance from its starting position.

Expected angle mapping with the current calibration:

| Joystick direction | Expected absolute target |
| --- | ---: |
| Forward | `0.042` |
| Right | Approximately `0.292` |
| Left | Approximately `-0.208` |
| Backward | Approximately `-0.458` after wrapping |

## What Works Before Position Control

### RHC2 with no robot code controlling the motors

Before deploying/running the position-control code, RHC2 telemetry behaves as expected:

- The theta absolute encoder reports a stable position while the pod is stationary.
- The theta absolute encoder changes with physical steering rotation and wraps normally between `-0.5` and `0.5`.
- The drive relative encoder changes when the drive motor physically rotates.
- Encoder movement corresponds to actual motor movement.
- The telemetry does not continuously run around the entire range while the motor is stationary.

### Open-loop robot code

We also have a separate `Swerve Pod Manual Test` OpMode that only sends throttle commands:

```java
double thetaThrottle = applyDeadband(gamepad.getRightX()) * kThetaMaxThrottle;
double driveThrottle = -applyDeadband(gamepad.getLeftY()) * kDriveMaxThrottle;

robot.swerveTheta.setThrottle(thetaThrottle);
robot.swerveDrive.setThrottle(driveThrottle);
```

During this ordinary open-loop test:

- Both motors respond to joystick commands.
- RHC2 encoder telemetry responds to physical movement.
- The absolute and relative values update plausibly.
- We do not see the same continuous impossible encoder motion caused by the position-control test.

This is an important distinction: the D10/D11 encoder hardware and read-only telemetry appear functional before encoder-based position control is engaged.

## Absolute-Position Failure: Swerve Theta on D11

The swerve test follows the documented continuous absolute-position workflow:

```java
REVLibError continuousInputError =
    robot.swerveTheta.enableAbsolutePositionContinuousInput();

REVLibError commandError =
    robot.swerveTheta.setAbsolutePositionWithSpeed(targetAbsolutePosition, 15.0);
```

The target is always normalized into the documented `-0.5` to `0.5` range:

```java
private static double wrapRotations(double rotations) {
  return rotations - Math.floor(rotations + 0.5);
}
```

The test also checks `Signal.isValid()` before using the absolute position.

### Observed result

After running the absolute-position test, RHC2 shows the absolute encoder continuously moving around the single-turn range. Representative values from a short recording are:

```text
 0.234
-0.047
-0.344
 0.359
 0.094
-0.219
-0.359
```

At the same time:

| Signal | Observed behavior |
| --- | --- |
| Absolute position | Continuously cycles through the single-turn range |
| Relative position | Changes even though the displayed current is `0.00 A` |
| Velocity | Remains near `104857.594 RPM` |
| Motor current | Remains `0.00 A` in the recording |
| Gearbox rating | `500 RPM` |
| Faults | `Sensor Fault` and `Other Fault` appear intermittently |
| Warning | `Has Reset Warning` appears as sticky |

`104857.594 RPM` is physically impossible for the 500 RPM A301 and looks like an invalid, saturated, sentinel, or incorrectly decoded value rather than real velocity.

The motor/controller also pulses as the control loop reacts to the changing feedback.

## Relative-Feedback Failure: Earlier D4 Test

Before the swerve-pod test, we tested another A301 on Motioncore D4 as the first arm joint. We initially attempted both absolute and relative position-control approaches. The physical behavior did not follow the requested position targets, so we reduced the test to low open-loop throttle with encoder logging and a six-second safety timeout.

During the logged D4 run:

| Signal | Observed value / behavior |
| --- | --- |
| Commanded throttle | `-0.150000` |
| Reported throttle | `-0.150000` |
| Applied output | Approximately `-0.149988` |
| Relative position | `486685.3125`, remained fixed during motion |
| Absolute position | `-0.25`, remained fixed during motion |
| Velocity | Approximately `-104000 RPM` |
| Current | `0.0 A` |
| Sticky fault | Present |
| Sticky warning | Present |

Because the relative position did not change with physical movement, code could not stop after one requested rotation. The timeout did execute and returned the state machine to idle, showing that the OpMode itself continued to run.

This was a different A301 and port from the current swerve theta motor, but it showed a similar impossible velocity scale and unusable encoder feedback when attempting encoder-dependent control.

## Reproduction After Firmware Reinstall

We reinstalled A301 firmware `27.0.0-prerelease-15` through RHC2.

1. Use RHC2 `1.2.1` on Systemcore.
2. Reinstall A301 firmware `27.0.0-prerelease-15`.
3. Power-cycle the A301.
4. Observe normal/stable encoder telemetry in RHC2.
5. Deploy and enable the absolute-position swerve test.
6. Move the joystick to request a theta target.
7. Observe continuously cycling absolute position, impossible velocity, pulsing, and sensor/other faults.

The reflash recovers normal-looking telemetry, but using absolute-position control causes the failure to return.

## Expected Behavior

- `enableAbsolutePositionContinuousInput()` enables shortest-path behavior across the single-turn boundary.
- `setAbsolutePositionWithSpeed(target, 15.0)` moves toward the target at no more than 15 RPM.
- Absolute position remains a valid measurement of physical theta position.
- Relative position and velocity remain physically plausible.
- Disabling the OpMode stops both A301s.
- No sensor fault occurs when the encoder is physically connected and readable in RHC2.

## Actual Behavior

- The read-only/open-loop telemetry works before engaging encoder position control.
- Engaging absolute-position control causes encoder telemetry to become physically impossible.
- The theta motor/controller pulses rather than settling at the target.
- RHC2 reports `Sensor Fault` and `Other Fault`.
- Reinstalling the firmware restores normal behavior, but the position-control test reproduces the problem.
- Earlier relative-feedback testing on another A301 also produced implausible position/velocity feedback and prevented encoder-based stopping.

## Questions

1. Is this a known issue in A301 firmware `27.0.0-prerelease-15` or REVLib `2027.0.0-alpha-4`?
2. Is `104857.594 RPM` a sentinel/error value that should cause the returned `Signal<Double>` to be invalid?
3. Does `Signal.isValid()` indicate only CAN-frame freshness, or should it also become false when the A301 reports `Sensor Fault`?
4. Can enabling continuous absolute input put the A301 into a persistent bad sensor/control state?
5. Is there another required configuration or calibration step before using A301 absolute-position control?
6. Should application code explicitly inspect `getFaults().sensor` before accepting encoder values, even when the `Signal` itself is valid?
7. Is reinstalling or force-reflashing currently the expected recovery after this failure occurs?

## Minimal Absolute-Control Reproduction

```java
public final A301 theta = new A301(CANBusMap.CAN_D11);

public void start() {
  theta.absoluteEncoderPositionPeriodMs(20);
  System.out.println(theta.enableAbsolutePositionContinuousInput());
}

public void periodic(double target) {
  Signal<Double> position = theta.getAbsoluteEncoderPosition();
  if (!position.isValid()) {
    theta.disable();
    return;
  }

  target = target - Math.floor(target + 0.5);
  System.out.printf(
      "position=%f target=%f commandResult=%s%n",
      position.get(),
      target,
      theta.setAbsolutePositionWithSpeed(target, 15.0));
}

public void end() {
  theta.disable();
}
```

## Attachments

- [ ] Screen recording showing cycling absolute position and intermittent faults
- [ ] Full `SwervePodEncoderTest.java`
- [ ] Full `SwervePodManualTest.java` for the working open-loop comparison
- [ ] D4 CSV log showing the earlier relative-feedback failure
- [ ] Screenshot of A301 firmware `27.0.0-prerelease-15` and bootloader `1.3`
- [ ] RHC2 version screenshot
- [ ] Note whether the advanced forced bootloader update option was used

## Related Documentation

- [SystemcoreTesting A301 instructions](https://github.com/wpilibsuite/SystemcoreTesting/blob/main/A301.md)
- [REVLib A301 Java API](https://codedocs.revrobotics.com/java/com/revrobotics/spark/a301)

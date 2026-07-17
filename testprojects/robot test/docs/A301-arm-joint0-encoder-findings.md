# A301 Arm Joint 0 Encoder Findings

This note records what we found while testing the A301 connected as `armJoint0` on Motioncore channel `D4`.

## Test Setup

- Motor object: `armJoint0 = new A301(CANBusMap.CAN_D4)`
- Test OpMode: `Arm J0 Relative Throttle`
- Start button: square / west face button
- Commanded motion during the logged test:
  - Open-loop throttle command: `-0.15`
  - Intended travel: `1.0` relative rotation
  - Timeout: `6.0` seconds
  - CSV log path on SystemCore: `/home/systemcore/deploy/arm_joint0_log.csv`

The test was changed away from absolute-position control and relative-position control because the motor behavior did not match the commanded position targets.

## What The Log Showed

During the logged run, the code did start the sequence and did command the motor:

- `lastCommandedThrottle` was `-0.150000`
- `reportedThrottle` was `-0.150000`
- `appliedOutput` was approximately `-0.149988`

However, the sensor values reported by the A301 did not look physically believable:

- `relativePosition` stayed fixed at approximately `486685.3125`
- `absolutePosition` stayed fixed at approximately `-0.25`
- `velocity` reported approximately `-104000 RPM`
- `current` reported `0.0 A`
- Sticky fault and sticky warning were present

The important part is that the relative encoder position did not change while the motor was being commanded. Because of that, the code could not reliably stop after one rotation based on relative encoder position.

## Current Conclusion

At the moment, we should not trust either encoder signal from this `D4` A301 for motion control:

- The relative encoder value appears stuck or stale.
- The absolute encoder value also appears stuck.
- The velocity value is not realistic.
- Current reporting did not show motor load during the command.

This means position-based stopping, absolute-position moves, and relative-position moves are not currently reliable for `armJoint0`.

The test code did reach its timeout and return to `IDLE`, so the state machine was running. The failure is that the position feedback did not provide usable movement information.

## Practical Implication

Until the encoder reporting problem is fixed, the safest next tests are time-based open-loop tests, for example:

1. Press square.
2. Run the motor at a very low throttle for a short fixed time.
3. Stop the motor.
4. Pause.
5. Run the opposite direction for a short fixed time.
6. Stop the motor again.

That kind of test verifies whether throttle commands and stop commands work without depending on encoder data.

## Things To Check Next

- Confirm in REV Hardware Client 2 that the A301 connected to physical `D4` shows changing relative and absolute encoder values while it moves.
- Confirm that `CANBusMap.CAN_D4` is the correct channel for the physical motor being tested.
- Check whether the sticky fault and sticky warning give more detail in REV Hardware Client 2.
- Add raw fault and warning logging if the REVLib API exposes detailed fields or raw bits.
- Consider clearing sticky faults only after recording what they are.
- Confirm firmware and REVLib compatibility:
  - A301 firmware `27.0.0-prerelease-15` or later
  - REVLib `2027.0.0-alpha-4` or later

## Related Code

- `src/main/java/first/robot/Robot.java`
- `src/main/java/first/robot/ArmJointZeroPidTest.java`
- `build.gradle`, artifact `armJoint0Log`


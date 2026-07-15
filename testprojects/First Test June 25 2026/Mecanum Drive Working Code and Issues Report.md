# Mecanum Drive Working Code and Issues Report

## Summary

We added a working A301 mecanum drive OpMode for the Systemcore + Motioncore test robot while keeping the direct A301 motor test OpMode available for troubleshooting.

The mecanum drive code is here:

```text
src/main/java/first/robot/A301MecanumDriveTeleMode.java
```

The known-good direct motor test code is here:

```text
src/main/java/first/robot/A301FourMotorDriveTestTeleMode.java
```

## Hardware Mapping

The final motor order used for mecanum drive is:

| Motioncore Port | Raw Bus ID | Mecanum Position |
| --- | ---: | --- |
| D0 | 5 | Front left |
| D1 | 6 | Front right |
| D2 | 7 | Back right |
| D3 | 8 | Back left |

All A301s were tested with CAN ID `3`.

## REVLib Version

The project is using:

```text
REVLib 2027.0.0-alpha-3
```

This is configured in:

```text
vendordeps/REVLib.json
```

## Initial Symptoms

During bring-up, the A301 motors showed several confusing symptoms:

- A motor would sometimes move once after deploying fresh code.
- After disabling the OpMode, the same code would stop driving motors.
- Re-enabling the OpMode did not reliably restore movement.
- Power cycling Systemcore did not always restore the working behavior.
- Deploying fresh code from VS Code made the motors work again for one run.
- NetworkTables values continued updating even after the OpMode was disabled.
- Motors sometimes made a small tick sound when initialized but did not move.

The repeatable pattern was:

```text
Fresh deploy -> motor works once -> disable OpMode -> motor no longer responds -> fresh deploy fixes it again
```

## Important Discovery

The direct A301 test proved that creating a fresh A301 object and commanding it directly could work:

```java
motor = new A301(5, 3);
motor.setThrottle(...);
```

That told us the basic bus mapping, CAN ID, and REVLib API path were capable of working.

The problem was not only mecanum math. The failure appeared to be related to OpMode lifecycle and/or A301 object/backend state after disable.

## NetworkTables Debugging

We added detailed NetworkTables output under:

```text
A301FourMotorDirectTest
```

The most useful values were:

```text
loopCount
startCount
endCount
lifecycle
frontLeft/command
frontLeft/commandAccepted
frontLeft/responding
frontLeft/appliedOutput
frontLeft/lastError
```

Equivalent values were also published for:

```text
frontRight
backRight
backLeft
```

This helped separate three different cases:

| Symptom | Meaning |
| --- | --- |
| `loopCount` keeps increasing | The robot code is still running |
| `commandAccepted = false` | `setThrottle()` is throwing or failing |
| `commandAccepted = true` but `appliedOutput = 0` | Command reached REVLib, but output was not being applied |

## Lifecycle Fix

The working fix was to stop reusing A301 objects across OpMode disable/enable cycles.

The direct test was changed so that when `end()` runs, it:

1. Stops all motors.
2. Calls `close()` on each A301 object.
3. Sets each A301 reference to `null`.

Then, when a new nonzero command is requested, the code recreates the A301 objects before commanding the motors again.

This mimics the useful part of a fresh deploy without requiring a full redeploy every time.

## Mecanum Drive Code

After the direct motor test was stable, we added:

```text
A301 Mecanum Drive
```

The mecanum drive publishes NetworkTables under:

```text
A301MecanumDrive
```

Controls:

| Control | Action |
| --- | --- |
| Left stick X | Strafe |
| Left stick Y | Forward/back |
| Right stick X | Rotate |
| Left bumper | Precision mode |

## Right Side Inversion

The direct motor test used this joystick convention:

```java
double throttle = -gamepad.getRightY();
```

With that convention, pushing the stick forward produced a positive throttle command.

During testing, pushing the right stick forward made the left side move forward. Because the right side needed to match that physical direction, the mecanum code inverted the right-side motors:

```java
private static final double FRONT_LEFT_SIGN = 1.0;
private static final double FRONT_RIGHT_SIGN = -1.0;
private static final double BACK_RIGHT_SIGN = -1.0;
private static final double BACK_LEFT_SIGN = 1.0;
```

## Final Notes

The direct A301 test should be kept in the project. It is useful for checking whether a problem is caused by:

- a motor,
- a Motioncore port,
- a CAN bus mapping issue,
- REVLib/A301 lifecycle behavior,
- or the mecanum drive math.

The mecanum drive code should be used once the direct test confirms all four motors can be created, commanded, and closed/recreated successfully.

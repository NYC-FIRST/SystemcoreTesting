# Mecanum Drive Setup

This project uses four A301 motors with WPILib's `MecanumDrive` helper.

## Motor Layout

The drivetrain is configured in `Robot.java`:

```java
public final A301 frontLeft = new A301(CANBusMap.CAN_D0);
public final A301 rearLeft = new A301(CANBusMap.CAN_D1);
public final A301 frontRight = new A301(CANBusMap.CAN_D2);
public final A301 rearRight = new A301(CANBusMap.CAN_D3);

public final MecanumDrive drive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);
```

Current channel mapping:

| Position | Motioncore Channel |
| --- | --- |
| Front left | `D0` |
| Rear left | `D1` |
| Front right | `D2` |
| Rear right | `D3` |

## Motor Inversion

The right-side motors are currently inverted:

```java
frontRight.setInverted(true);
rearRight.setInverted(true);
```

This matches the common drivetrain convention where left and right motors face opposite physical directions. If forward/backward works but strafe or rotation is backwards, the first thing to check is usually the joystick axis signs in teleop, not necessarily motor inversion.

In this project, forward drive was correct after keeping right-side inversion and adjusting the joystick inputs.

## Teleop Controls

Teleop control is in `DefaultTeleMode.java`:

```java
double scale = 0.15 + (0.35 * gamepad.getRightTriggerAxis());
robot.drive.setMaxOutput(scale);
robot.drive.driveCartesian(
    -applyDeadband(gamepad.getLeftY()),
    applyDeadband(gamepad.getLeftX()),
    applyDeadband(gamepad.getRightX()));
```

The controls are:

| Joystick input | Mecanum input | Robot action |
| --- | --- | --- |
| Left stick Y | `ySpeed` | Forward/backward |
| Left stick X | `xSpeed` | Strafe left/right |
| Right stick X | `zRotation` | Rotate left/right |
| Right trigger | `setMaxOutput` scale | More available speed |

The left Y axis is negated because gamepads usually report pushing the stick forward as a negative value. Negating it makes pushing the stick forward drive the robot forward.

## Speed Limiting

The drivetrain output is intentionally limited during bring-up:

```java
double scale = 0.15 + (0.35 * gamepad.getRightTriggerAxis());
robot.drive.setMaxOutput(scale);
```

That gives:

- No trigger: `15%` max output
- Full right trigger: `50%` max output

This keeps early mecanum testing slower and easier to control.

## Deadband

There are two deadbands:

- `drive.setDeadband(0.08)` in `Robot.java`
- `applyDeadband(..., 0.08)` behavior in `DefaultTeleMode.java`

The goal is to ignore tiny joystick values near zero so the robot does not creep when the sticks are released.

## Direction Debugging

If the robot does not move as expected, test one behavior at a time:

1. Push left stick forward.
   - Expected: robot drives forward.
2. Push left stick right.
   - Expected: robot strafes right.
3. Push right stick right.
   - Expected: robot rotates clockwise.

If only strafe or rotation is backwards while forward is correct, change the sign of the corresponding joystick input in `driveCartesian`.

For example:

```java
robot.drive.driveCartesian(
    -applyDeadband(gamepad.getLeftY()),
    -applyDeadband(gamepad.getLeftX()),
    applyDeadband(gamepad.getRightX()));
```

would flip only strafe direction.

Changing motor inversion is a bigger change because it affects all drive directions for that wheel. Use motor inversion when an individual wheel spins opposite from what it should during single-wheel testing.

## Related Code

- `src/main/java/first/robot/Robot.java`
- `src/main/java/first/robot/DefaultTeleMode.java`
- `src/main/java/first/robot/DefaultAutoMode.java`


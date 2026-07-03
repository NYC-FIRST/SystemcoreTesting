# Kiwi Drive Case Study

`KiwiDriveExampleTeleMode.java` combines the main SystemCore concepts in one
realistic drivetrain OpMode. It drives a three-wheel Kiwi drivetrain with two angled
front wheels and one sideways back wheel.

## Robot Layout

```text
                 Front of robot

            motor3                 motor1
       front-left wheel       front-right wheel
         Hub port 3             Hub port 1

                      motor2
                    back wheel
                   Hub port 2

  motor0 / Hub port 0 is unused here.
```

## Step 1: Read Joystick Values

```java
var gamepad = userControls.getGamepad(0);

double x = applyDeadband(gamepad.getLeftX());
double y = applyDeadband(-gamepad.getLeftY());
double rotation = applyDeadband(gamepad.getRightX());
double speedScale = gamepad.getLeftBumperButton() ? PRECISION_SCALE : 1.0;
```

FTC translation:

- `x`: strafe or sideways command
- `y`: forward/back command
- `rotation`: turn command
- `speedScale`: slow mode

## Step 2: Apply Deadband

```java
private static double applyDeadband(double value) {
  if (Math.abs(value) < DEADBAND) {
    return 0.0;
  }
  return value;
}
```

Tiny stick values become `0.0`, which prevents joystick drift from moving the robot.

## Step 3: Calculate Wheel Powers

```java
double frontLeftPower = (0.5 * x) + (SQRT_3_OVER_2 * y) + rotation;
double frontRightPower = (0.5 * x) - (SQRT_3_OVER_2 * y) + rotation;
double backPower = -x + rotation;
```

This is the Kiwi equivalent of drivetrain math FTC students may have seen for
mecanum or omni drivetrains.

## Step 4: Normalize

```java
double maxMagnitude =
    Math.max(1.0,
        Math.max(Math.abs(frontLeftPower),
            Math.max(Math.abs(frontRightPower), Math.abs(backPower))));
```

If any wheel power is outside the safe range, all powers are scaled down together so
their ratios stay correct.

## Step 5: Apply Motor Signs and Speed Scaling

```java
double frontLeftThrottle =
    FRONT_LEFT_MOTOR_SIGN * speedScale * frontLeftPower / maxMagnitude;
```

The `*_MOTOR_SIGN` constants replace the FTC habit of using
`Direction.REVERSE` in this sample.

## Step 6: Send Throttle Values

```java
robot.motor0.setThrottle(0.0);
robot.motor3.setThrottle(frontLeftThrottle);
robot.motor1.setThrottle(frontRightThrottle);
robot.motor2.setThrottle(backThrottle);
```

The motor numbers follow the wiring, not the math names. `motor3` is the front-left
wheel because that wheel is plugged into hub port 3.

## Telemetry Variant

`KiwiDriveExampleWithNetworkTableTelemetryTeleMode.java` uses the same drive code
and then publishes inputs and outputs to NetworkTables:

```java
publishTelemetry(x, y, rotation, frontLeftThrottle, frontRightThrottle, backThrottle);
```

That makes it a useful comparison point: the drive-only sample teaches drivetrain
structure, and the telemetry variant shows how dashboard publishing is added.

# Motor and Servo Output

## FTC Pattern

```java
motor.setPower(0.5);
servo.setPosition(1.0);
```

## SystemCore Pattern

```java
robot.motor0.setThrottle(0.5);
robot.servo0.setPosition(1.0);
```

## Mapping Table

| SystemCore call | FTC equivalent | Meaning |
| --- | --- | --- |
| `setThrottle(0.5)` | `setPower(0.5)` | Run motor forward at partial power |
| `setThrottle(0.0)` | `setPower(0.0)` | Stop the motor |
| `setThrottle(-0.5)` | `setPower(-0.5)` | Run motor the opposite direction |
| `setPosition(0.0)` | `setPosition(0.0)` | Move servo to one end of travel |
| `setPosition(1.0)` | `setPosition(1.0)` | Move servo to the other end of travel |
| `setFloatOn0(false)` | zero-power behavior setup | Configure motor behavior when throttle is zero |

`setThrottle(...)` fills the same basic role as FTC `setPower(...)`: positive runs
one way, negative runs the other, and `0.0` stops. They are conceptually similar,
but do not assume they are identical internally.

## Motor Direction

In FTC, motor direction is often handled with:

```java
motor.setDirection(DcMotorSimple.Direction.REVERSE);
```

The Kiwi examples in this project use sign constants instead:

```java
private static final double FRONT_LEFT_MOTOR_SIGN = 1.0;
private static final double FRONT_RIGHT_MOTOR_SIGN = 1.0;
private static final double BACK_MOTOR_SIGN = 1.0;
```

If a wheel spins the wrong way on a real robot, change only that wheel's sign to
`-1.0` and test at low power.

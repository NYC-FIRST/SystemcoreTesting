# Project Map

All robot sample code lives in:

```text
testprojects/First Test June 25 2026/src/main/java/first/robot/
```

In the main repository, that folder is available at
[testprojects/First Test June 25 2026/src/main/java/first/robot](https://github.com/NYC-FIRST/SystemcoreTesting/tree/main/testprojects/First%20Test%20June%2025%202026/src/main/java/first/robot).

## Sample Files

| File | FTC mental equivalent | Role in this project |
| --- | --- | --- |
| `Robot.java` | Hardware class, or the `hardwareMap` setup copied into every OpMode | Declares the four motors and two servos once |
| `DefaultTeleMode.java` | The simplest possible `@TeleOp` | Direct gamepad-to-hardware test: sticks drive motors, triggers move servos |
| `DefaultAutoMode.java` | A simple timed `@Autonomous` | Runs two motors on a timed schedule, then stops |
| `KiwiDriveExampleTeleMode.java` | A drivetrain TeleOp | Full Kiwi drive math: deadband, kinematics, normalization, motor signs |
| `NetworkTableTelemetryExampleTeleMode.java` | An OpMode written just to test telemetry | Publishes gamepad numbers, booleans, and status strings; does not drive |
| `KiwiDriveExampleWithNetworkTableTelemetryTeleMode.java` | Drive OpMode plus telemetry lines | The Kiwi drive code plus NetworkTables publishing of inputs and outputs |

## What Is Different From FTC?

There is exactly one `Robot.java`, and every OpMode shares it.

In many FTC projects, each OpMode has its own private copy of hardware fields:

```java
private DcMotor leftMotor;
private Servo claw;
```

In this SystemCore project, those hardware fields are centralized in `Robot.java`,
and OpModes access them through the shared `robot` object:

```java
robot.motor1.setThrottle(0.5);
robot.servo0.setPosition(1.0);
```

That gives one place to update wiring and multiple OpModes that can reuse it.

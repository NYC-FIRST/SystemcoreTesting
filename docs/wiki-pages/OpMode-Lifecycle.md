# OpMode Lifecycle

This is the most important concept for FTC students moving into this project.

`PeriodicOpMode` is closest to FTC's iterative `OpMode`, the one with `init()`,
`start()`, and `loop()`. If you mostly wrote `LinearOpMode`, map your
`while (opModeIsActive())` body to `periodic()`.

## Lifecycle Mapping

| FTC Iterative OpMode | FTC LinearOpMode | SystemCore here |
| --- | --- | --- |
| `init()` | code before `waitForStart()` | constructor plus `Robot.java` setup |
| `start()` | code immediately after `waitForStart()` | `start()` |
| `loop()` | `while (opModeIsActive())` body | `periodic()` |
| `stop()` | code after the active loop | not shown in these samples |

## Registration

FTC:

```java
@TeleOp(name = "My TeleOp")
public class MyTeleOp extends OpMode {
}
```

SystemCore:

```java
@Teleop(name = "Kiwi Drive Example")
public class KiwiDriveExampleTeleMode extends PeriodicOpMode {
}
```

Notice the spelling difference: FTC uses `@TeleOp`, while this project uses
`@Teleop`. `@Autonomous` exists in both and serves the same broad purpose.

## Constructor Injection

FTC OpModes usually rely on inherited fields like `hardwareMap`, `gamepad1`, and
`telemetry`. In this project, an OpMode declares what it needs as constructor
parameters:

```java
private final Robot robot;
private final DefaultUserControls userControls;

public DefaultTeleMode(Robot robot, DefaultUserControls userControls) {
  this.robot = robot;
  this.userControls = userControls;
}
```

What each piece means:

- `Robot robot`: the shared hardware container from `Robot.java`
- `DefaultUserControls userControls`: the shared gamepad-access object
- `final`: this OpMode keeps the same references for its lifetime

You do not call the constructor yourself. SystemCore creates the OpMode and supplies
the arguments.

## `start()` and `periodic()`

Use `start()` for things that happen once when the mode begins:

- reset and start timers
- zero outputs
- configure motor behavior

Use `periodic()` for the repeated loop:

- read inputs
- calculate outputs
- command hardware
- publish telemetry

Just like FTC `loop()`, `periodic()` should finish quickly each time.

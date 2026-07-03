# Common FTC Assumptions That Change

These are the habits most likely to trip up FTC students when reading this
SystemCore project.

| FTC assumption | What changes here |
| --- | --- |
| Every OpMode does its own `hardwareMap` setup | Hardware is centralized in `Robot.java`; OpModes receive the `Robot` object |
| `gamepad1` is an inherited field | Gamepad access comes from `DefaultUserControls` via `getGamepad(0)` |
| `loop()` is the main repeated method | `periodic()` is the repeated method |
| `setPower(...)` commands motors | `setThrottle(...)` commands motors |
| `telemetry.update()` refreshes the Driver Station display | NetworkTables publishers update dashboard values with `.set(...)` |
| Motor names come from the FTC Robot Configuration | Motors are created by hub and port number in code |
| `@TeleOp` uses a capital O | This project uses `@Teleop` with a lowercase o |
| `ElapsedTime` starts counting on its own | `Timer` needs `reset()` and `start()` |
| `motor.setDirection(REVERSE)` flips a motor | These samples flip a per-wheel sign constant instead |
| OpModes never have constructors | OpModes declare what they need as constructor parameters |

The theme is consistent: familiar FTC ideas still exist, but this project makes the
dependencies more explicit and moves shared setup into `Robot.java`.

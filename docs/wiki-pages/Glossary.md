# Glossary

| Term | Quick definition |
| --- | --- |
| `OpModeRobot` | Base class that `Robot` extends; makes `Robot` the shared robot container SystemCore constructs and hands to OpModes |
| `Robot` | This project's hardware container: four `ExpansionHubMotor`s and two `ExpansionHubServo`s as `public final` fields |
| `PeriodicOpMode` | Base class for OpModes here; closest to FTC iterative `OpMode`, with `start()` and a repeatedly-called `periodic()` |
| `@Teleop` | Registers a class as a driver-controlled mode; FTC uses `@TeleOp`, with a different capital O |
| `@Autonomous` | Registers a class as an autonomous mode |
| `@UserControlsInstance` | Annotation on `Robot` naming which user-controls class SystemCore should provide to OpModes |
| `DefaultUserControls` | The gamepad-access object; `getGamepad(0)` maps to `gamepad1`, `getGamepad(1)` maps to `gamepad2` |
| `ExpansionHubMotor` | A motor on an Expansion Hub port, for example `new ExpansionHubMotor(0, 1)` means hub 0, port 1 |
| `ExpansionHubServo` | A servo on an Expansion Hub servo port |
| `setThrottle` | Commands motor output from `-1.0` to `1.0`; closest FTC equivalent is `setPower` |
| `setFloatOn0` | Configures motor behavior at zero throttle; `false` means resist motion |
| `setPosition` | Moves a servo across `0.0` to `1.0`, same broad idea as FTC |
| `Timer` | Elapsed-seconds timer; closest FTC equivalent is `ElapsedTime` |
| `NetworkTable` | A named group of values in the NetworkTables tree |
| `DoublePublisher` | Publishes a numeric value to a NetworkTables topic |
| `BooleanPublisher` | Publishes a true/false value to a NetworkTables topic |
| `StringPublisher` | Publishes a text value to a NetworkTables topic |
| `publishTelemetry` | Helper method in these samples that calls `.set(...)` on every publisher |
| `periodic` | The repeated OpMode method; closest FTC equivalent is `loop()` |
| `start` | Runs once when the mode begins |
| deadband | Treating tiny stick values as zero so a resting stick does not move the robot |
| normalization | Dividing all wheel powers by the largest magnitude so none exceed `1.0` while keeping ratios |
| motor sign | A `1.0` or `-1.0` constant that flips one wheel's direction |
| OutlineViewer | WPILib tool that shows the raw NetworkTables tree |
| Elastic | Dashboard tool for arranging NetworkTables values as widgets |

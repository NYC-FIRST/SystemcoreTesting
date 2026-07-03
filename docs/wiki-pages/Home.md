# FTC Student Guide to SystemCore

This wiki is for FTC students and mentors learning how familiar FTC OpMode ideas map
to the FRC-style SystemCore project structure used in this sample project.

Start here if you know concepts like `hardwareMap`, `gamepad1`, `loop()`,
`setPower(...)`, `ElapsedTime`, and `telemetry.addData(...)`, but are new to:

- `Robot extends OpModeRobot`
- `PeriodicOpMode`
- constructor injection
- `DefaultUserControls`
- `ExpansionHubMotor`
- `setThrottle(...)`
- NetworkTables publishers

## Recommended Reading Order

1. [FTC to SystemCore Overview](FTC-to-SystemCore-Overview)
2. [Project Map](Project-Map)
3. [Hardware Setup](Hardware-Setup)
4. [OpMode Lifecycle](OpMode-Lifecycle)
5. [Gamepad Input](Gamepad-Input)
6. [Motor and Servo Output](Motor-and-Servo-Output)
7. [Autonomous Timing](Autonomous-Timing)
8. [Telemetry with NetworkTables](Telemetry-with-NetworkTables)
9. [Kiwi Drive Case Study](Kiwi-Drive-Case-Study)
10. [Side-by-Side Mini Examples](Side-by-Side-Mini-Examples)
11. [Common FTC Assumptions That Change](Common-FTC-Assumptions-That-Change)
12. [Glossary](Glossary)

## Quick Translation Table

| FTC SDK Habit | SystemCore Project Habit |
| --- | --- |
| OpMode owns hardware fields directly | `Robot.java` owns hardware fields |
| `hardwareMap.get(...)` in `init()` | `new ExpansionHubMotor(hub, port)` in `Robot.java` |
| `gamepad1` inherited by OpMode | `userControls.getGamepad(0)` |
| `loop()` | `periodic()` |
| `motor.setPower(...)` | `robot.motorX.setThrottle(...)` |
| `telemetry.addData(...)` | NetworkTables publisher `.set(...)` |

The big idea: most FTC concepts still exist, but they move into different places.
Hardware moves into `Robot.java`, repeated OpMode logic moves into `periodic()`, and
dashboard data moves into NetworkTables publishers.

# FTC to SystemCore Overview

In FTC, one OpMode class usually does everything. It looks up hardware in `init()`,
reads `gamepad1` in `loop()`, commands motors, and calls `telemetry.update()` all in
the same file. If you wrote three OpModes, you probably copied the same
`hardwareMap.get(...)` lines into all three.

This SystemCore project splits those jobs apart:

- **Hardware** lives in one shared class, `Robot.java`.
- **OpModes** contain behavior. They receive the shared `Robot` object through their
  constructor.
- **Gamepad access** comes from a `DefaultUserControls` object instead of an
  inherited `gamepad1` field.
- **Telemetry** goes through NetworkTables publishers instead of
  `telemetry.addData(...)`.

None of the ideas are new to FTC students. What changes is where each idea lives.

## Mental Model

| FTC SDK Habit | SystemCore Project Habit |
| --- | --- |
| OpMode owns hardware fields directly | `Robot.java` owns hardware fields |
| `hardwareMap.get(...)` in `init()` | `new ExpansionHubMotor(hub, port)` in `Robot.java` |
| `gamepad1` inherited by OpMode | `userControls.getGamepad(0)` |
| `loop()` | `periodic()` |
| `motor.setPower(...)` | `robot.motorX.setThrottle(...)` |
| `telemetry.addData(...)` | NetworkTables publisher `.set(...)` |

## The Main Shift

FTC examples often teach students to make each OpMode self-contained. That works,
but it can duplicate wiring and setup code.

This SystemCore sample is more FRC-style: the robot hardware is centralized, and
OpModes describe behavior using shared robot and input objects.

That means an OpMode usually answers these questions:

- What robot hardware do I need?
- Do I need driver controls?
- What should happen once when the mode starts?
- What should happen repeatedly while the mode is enabled?

Those answers appear directly in the constructor, `start()`, and `periodic()`.

# Hardware Setup

## FTC Pattern: `hardwareMap`

In FTC, hardware lookup usually lived inside each OpMode, often in `init()`:

```java
private DcMotor leftMotor;
private Servo claw;

@Override
public void init() {
  leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
  claw = hardwareMap.get(Servo.class, "claw");
}
```

The string `"leftMotor"` had to match a device name in the FTC Robot Configuration.
If the name did not match, the OpMode failed at init.

## SystemCore Pattern: `Robot.java`

In this project, hardware lives in `Robot.java`:

```java
@UserControlsInstance(DefaultUserControls.class)
public class Robot extends OpModeRobot {
  public final ExpansionHubMotor motor0 = new ExpansionHubMotor(0, 0);
  public final ExpansionHubMotor motor1 = new ExpansionHubMotor(0, 1);
  public final ExpansionHubMotor motor2 = new ExpansionHubMotor(0, 2);
  public final ExpansionHubMotor motor3 = new ExpansionHubMotor(0, 3);

  public final ExpansionHubServo servo0 = new ExpansionHubServo(0, 0);
  public final ExpansionHubServo servo1 = new ExpansionHubServo(0, 1);
}
```

## Translation Table

| FTC | SystemCore here | Student translation |
| --- | --- | --- |
| `DcMotor` | `ExpansionHubMotor` | Motor object |
| `Servo` | `ExpansionHubServo` | Servo object |
| `hardwareMap.get(...)` | `new ExpansionHubMotor(0, port)` | Connect code to hub hardware |
| configured device name | hub and port number | How the hardware is identified |
| private field in each OpMode | `public final` field in `Robot.java` | Where the hardware object lives |

## What Changed?

- FTC usually maps hardware by configured device name.
- This project creates hardware objects by hub number and port number.
- FTC hardware fields are often private fields inside each OpMode.
- SystemCore hardware fields live in one shared `Robot` object.
- OpModes access hardware through `robot.motor1`, `robot.servo0`, and similar fields.

If you ever built a shared `RobotHardware` helper class in FTC so you would not
repeat `hardwareMap` calls, `Robot.java` is that same idea. Here it is the standard
structure instead of a team-created helper.

# Gamepad Input

## FTC Pattern

In FTC, `gamepad1` and `gamepad2` are inherited fields:

```java
double x = gamepad1.left_stick_x;
double y = -gamepad1.left_stick_y;
boolean slowMode = gamepad1.left_bumper;
```

## SystemCore Pattern

This project uses `DefaultUserControls`:

```java
var gamepad = userControls.getGamepad(0);

double x = gamepad.getLeftX();
double y = -gamepad.getLeftY();
boolean slowMode = gamepad.getLeftBumperButton();
```

`getGamepad(0)` is the equivalent of `gamepad1`. `getGamepad(1)` is the equivalent
of `gamepad2`.

## Mapping Table

| FTC | SystemCore here |
| --- | --- |
| `gamepad1` | `userControls.getGamepad(0)` |
| `gamepad2` | `userControls.getGamepad(1)` |
| `left_stick_x` | `getLeftX()` |
| `left_stick_y` | `getLeftY()` |
| `right_stick_x` | `getRightX()` |
| `right_stick_y` | `getRightY()` |
| `left_trigger` | `getLeftTriggerAxis()` |
| `right_trigger` | `getRightTriggerAxis()` |
| `left_bumper` | `getLeftBumperButton()` |
| `right_bumper` | `getRightBumperButton()` |
| `a` / PlayStation Cross | `getSouthFaceButton()` |

## Familiar FTC Habits That Carry Over

Pushing a stick forward still reads negative, so the samples still negate Y values:

```java
double y = -gamepad.getLeftY();
```

The samples also read inputs near the top of `periodic()`, just like FTC teams often
read `gamepad1` near the top of `loop()`.

`getSouthFaceButton()` describes the button's position instead of its label, so the
same code can work whether the controller labels that button `A` or Cross.

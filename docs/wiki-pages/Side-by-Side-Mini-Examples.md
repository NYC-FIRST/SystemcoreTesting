# Side-by-Side Mini Examples

Quick translations of common FTC tasks into the SystemCore patterns used in this
project.

## Read the Left Stick and Drive One Motor

FTC:

```java
motor.setPower(-gamepad1.left_stick_y);
```

SystemCore:

```java
robot.motor0.setThrottle(-userControls.getGamepad(0).getLeftY());
```

## Move a Servo with a Trigger

FTC:

```java
claw.setPosition(gamepad1.right_trigger);
```

SystemCore:

```java
robot.servo1.setPosition(userControls.getGamepad(0).getRightTriggerAxis());
```

## Timed Autonomous

FTC:

```java
if (timer.seconds() < 2.0) {
  motor.setPower(0.5);
} else {
  motor.setPower(0.0);
}
```

SystemCore:

```java
if (timer.get() < 2.0) {
  robot.motor0.setThrottle(0.5);
} else {
  robot.motor0.setThrottle(0.0);
}
```

## Publish Telemetry

FTC:

```java
telemetry.addData("leftX", leftX);
telemetry.update();
```

SystemCore:

```java
leftXPublisher.set(leftX);
```

The publisher is created once as a field. The `.set(...)` call happens repeatedly
inside `periodic()`.

# Autonomous Timing

## FTC Pattern

FTC students often use `ElapsedTime`:

```java
ElapsedTime timer = new ElapsedTime();

if (timer.seconds() < 2.0) {
  motor.setPower(0.5);
}
```

## SystemCore Pattern

This project uses `Timer`:

```java
private final Timer timer = new Timer();

@Override
public void start() {
  timer.reset();
  timer.start();
}

@Override
public void periodic() {
  if (timer.get() < 2.0) {
    robot.motor0.setThrottle(0.5);
  }
}
```

## Mapping Table

| FTC `ElapsedTime` | SystemCore `Timer` | Meaning |
| --- | --- | --- |
| `new ElapsedTime()` | `new Timer()` | Create the timer |
| `timer.reset()` | `timer.reset()` | Set elapsed time back to zero |
| starts automatically | `timer.start()` | Begin counting |
| `timer.seconds()` | `timer.get()` | Read elapsed seconds |

The idea is identical: pick behavior based on how many seconds have elapsed.

One real difference is that this `Timer` needs an explicit `timer.start()` after
`timer.reset()`. That is why `DefaultAutoMode.start()` calls both.

Resetting and starting the timer in `start()` matters because the constructor may
run before the mode actually begins. `start()` corresponds to the moment the mode
just started running.

# YGSL/YAGSL SystemCore Swerve Template Notes

This folder is the current SystemCore swerve bring-up area. It started from an
FRC/RoboRIO style swerve structure and was adapted to compile against the
SystemCore WPILib package layout and the REV A301 motor API.

Important naming note: the folder is currently named `YGSLSwerveTemplate`. The
intended reference is likely YAGSL, "Yet Another Generic Swerve Library", which
is an open source swerve library that can support CTRE Phoenix 6 hardware. This
folder is not currently using upstream YAGSL or Phoenix 6 directly.

## Current Status

We are currently using this folder to test a single swerve pod, not a complete
four-module drivetrain.

The active single-pod path is:

```text
SinglePodYgslTeleop
        |
        v
SinglePodDriveSubsystem
        |
        v
SwerveModule
        |
        +--> A301DriveMotor
        |
        +--> A301SteeringMotor
```

`SinglePodYgslTeleop` receives the already-created A301 objects from `Robot`:

- `robot.swerveDrive`
- `robot.swerveTheta`

This avoids constructing duplicate motor objects for the same hardware.

## What Was Changed From FRC/RoboRIO Code

The original structure followed a normal FRC Java style, where code commonly
imports packages under `edu.wpi.first.*`.

For SystemCore, those imports had to be remapped to the package layout available
in this project, mostly under `org.wpilib.*`.

Examples used in this folder include:

- `org.wpilib.command2.Command`
- `org.wpilib.command2.SubsystemBase`
- `org.wpilib.math.kinematics.ChassisVelocities`
- `org.wpilib.math.kinematics.SwerveDriveKinematics`
- `org.wpilib.math.kinematics.SwerveModuleVelocity`
- `org.wpilib.math.controller.PIDController`

The hardware side was also mapped to REV A301 classes:

- `com.revrobotics.spark.A301`
- `com.revrobotics.util.Signal`

## Phoenix 6 / CTRE Status

This folder does not currently use CTRE Phoenix 6.

During the repo scan, we did not find:

- `swervelib.*` imports
- `com.ctre.phoenix6.*` imports
- Phoenix 6 vendordeps
- `TalonFX`
- `CANcoder`
- `Pigeon2`
- YAGSL JSON configuration files for CTRE motors, encoders, or IMU hardware

So even though the folder name references a YAGSL-like template, the current
implementation is a local SystemCore/REV A301 bring-up path.

## Why The Steering Is Open Loop Right Now

At the moment, we are intentionally controlling the steering motor through
`setThrottle(...)`.

We tried paths that depended on deeper motor-object control and target setting,
but the motor behavior became unreliable. Observed failure modes included:

- The reported position fluctuating continuously between about `-0.5` and `0.5`
- The pod entering a zigzag or oscillating control pattern
- The absolute encoder value stopping or becoming unreliable
- Recovery sometimes requiring firmware flashing before the motor reported
  correctly again

Because of that, the safest current test path is:

```text
desired pod angle
        |
        v
software PID calculation
        |
        v
clamped throttle output
        |
        v
A301.setThrottle(...)
```

This is not the most accurate control method, but it lets us test the swerve
concept without depending on closed-loop motor APIs that are not yet behaving
reliably in this setup.

## Current Mechanical Limitation

The current pod has a gear reduction between the steering motor and the rotating
pod. The known reduction is:

```text
16 tooth gear -> 36 tooth gear
ratio: 36 / 16 = 2.25:1
```

That means the motor's absolute encoder does not directly represent the final
pod angle. One motor rotation does not equal one pod rotation.

This matters because many swerve control assumptions expect the steering
feedback to describe the actual final pod angle. If the encoder is on the motor
side of a reduction, then a normalized range such as `-0.5` to `0.5` motor
rotations is not the same thing as one full pod rotation.

Because of this, the current estimate of pod angle is poor when using only the
motor-side absolute encoder.

## Mechanical Direction

The next design direction is to move to a 1:1 steering ratio so that the built-in
absolute encoder more closely reports the real pod rotation.

Longer term, we may consider adding an external absolute encoder, such as a CAN
encoder, on the actual final steering shaft. That is the common FRC-style
approach because the control loop then closes around the real pod angle instead
of an inferred motor-side angle.

The tradeoff is that using an external encoder may reduce the value of the
built-in absolute encoder on the current motor. For now, the 1:1 design is the
cleaner intermediate step because it lets us keep using the built-in encoder
while improving the relationship between motor position and pod position.

## File Map

- `SinglePodYgslTeleop.java`
  - Current single-pod teleop test.
  - Reads driver stick input and commands the single pod.

- `subsystems/drive/SinglePodDriveSubsystem.java`
  - Converts joystick direction into a target pod angle and wheel speed for one
    module.

- `subsystems/drive/SwerveModule.java`
  - Combines one drive motor wrapper and one steering motor wrapper.
  - Applies desired wheel velocity and steering angle.

- `subsystems/drive/A301DriveMotor.java`
  - Hardware wrapper for the drive A301.
  - Uses throttle output and reads relative encoder velocity/position.

- `subsystems/drive/A301SteeringMotor.java`
  - Hardware wrapper for the steering A301.
  - Runs the software PID and sends clamped throttle to the motor.
  - Reads the absolute encoder value through the A301 API.

- `subsystems/drive/DriveSubsystem.java`
  - Four-module drivetrain skeleton.
  - Uses WPILib kinematics, but is not the primary active test path right now.

- `commands/DriveCommand.java`
  - Four-module command skeleton for converting controller inputs to chassis
    velocities.

- `Constants.java`
  - Drive dimensions, speed limits, kinematics, PID values, and single-pod
    tuning constants.

- `RobotContainer.java`
  - Command-based wiring skeleton for the full drivetrain.

## How To Use This Folder Safely

1. Start with `SinglePodYgslTeleop` for hardware testing.
2. Keep steering output open-loop through `setThrottle(...)` until the A301
   closed-loop behavior is better understood and repeatable.
3. Watch the absolute encoder reporting before and after each code change.
4. Do not assume motor rotations equal pod rotations unless the mechanical ratio
   is 1:1 or an explicit conversion is applied.
5. Treat the four-module `DriveSubsystem` as a structure skeleton until the
   single-pod control path is mechanically and electrically stable.

## Practical Summary

This folder is for proving the SystemCore swerve concept with REV A301 hardware.
It is not currently a Phoenix 6 implementation and it is not currently upstream
YAGSL. The immediate goal is to make one pod steer predictably using a safe
software-PID-to-throttle path, then move toward cleaner mechanical feedback and
eventually a full drivetrain.

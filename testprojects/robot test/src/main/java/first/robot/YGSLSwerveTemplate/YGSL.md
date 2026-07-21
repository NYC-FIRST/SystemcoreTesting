# YGSL A301 Swerve Drive Template

## Overview

The **YGSL A301 Swerve Drive Template** is a modular swerve drive framework designed for the REV Robotics A301 motor platform running on MotionCore/SystemCore.

The project is structured so that all hardware-specific functionality is isolated within dedicated wrapper classes (`A301DriveMotor` and `A301SteeringMotor`). This allows the higher-level swerve architecture to remain independent of the underlying motor implementation while making future API updates easier to integrate.

The current implementation follows the documented REV A301 Java API and serves as the foundation for future custom steering PID control, odometry, and autonomous functionality.

---

# REV Documentation References

## REV A301 Java API

https://codedocs.revrobotics.com/java/com/revrobotics/spark/a301

## REV Java Documentation Index

https://codedocs.revrobotics.com/java/

## REV Signal API

https://codedocs.revrobotics.com/java/com/revrobotics/util/Signal.html

---

# Project Goals

- Build a reusable swerve drive framework
- Follow SystemCore/org.wpilib subsystem architecture
- Keep drive and steering hardware isolated from drivetrain logic
- Use documented REV A301 Java API methods
- Allow future custom steering PID implementation
- Support future MotionCore and SystemCore updates

---

# Project Structure

```
YGSLSwerveTemplate
│
├── Constants.java
├── RobotContainer.java
│
├── commands
│   └── DriveCommand.java
│
├── subsystems
│   └── drive
│       ├── A301DriveMotor.java
│       ├── A301SteeringMotor.java
│       ├── SwerveModule.java
│       └── DriveSubsystem.java
│
└── util
    └── SwerveUtils.java
```

---

# Software Architecture

```
SystemCore OpMode / Controller Input
        │
        ▼
DriveCommand
        │
        ▼
DriveSubsystem
        │
        ▼
4 × SwerveModule
        │
 ┌──────┴──────┐
 ▼             ▼
Drive Motor   Steering Motor
   (A301)        (A301)
```

Each software layer has a single responsibility. Hardware-specific functionality is isolated inside the A301 wrapper classes.

---

# A301 Java API Usage

The wrapper classes use documented methods from the REV A301 Java API.

## Drive Control

- `setThrottle(double)`
- `disable()`

Drive output is open-loop for now. Do not call `A301.setVelocity(...)` until the expected units
are verified against the actual A301 firmware/API behavior.

## Encoder Feedback

- `getRelativeEncoderPosition()`
- `getEncoderVelocity()`
- `getAbsoluteEncoderPosition()`

Encoder values are retrieved through the documented `Signal<T>` interface after validity checks.
Invalid signals currently fall back to `0.0` so the skeleton compiles and runs defensively during
bring-up.

---

# Class Responsibilities

## Constants.java

Stores robot-wide constants.

Includes:

- Robot dimensions
- Swerve kinematics
- MotionCore bus IDs
- CAN IDs
- Gear ratios
- PID constants
- Driver configuration

---

## A301DriveMotor.java

Hardware wrapper around one A301 drive motor.

Responsibilities:

- Wrap an A301 instance owned by the active robot
- Set throttle output
- Read relative encoder position in motor rotations
- Read encoder velocity in motor RPM
- Convert drive position/velocity to wheel meters and meters per second
- Read absolute encoder position
- Stop motor

---

## A301SteeringMotor.java

Hardware wrapper around one A301 steering motor.

Responsibilities:

- Wrap an A301 instance owned by the active robot
- Read absolute encoder position in rotations
- Temporary steering PID control in rotations
- Stop motor

Future work:

- Replace temporary PID with a custom steering PID using absolute encoder feedback.

---

## SwerveModule.java

Represents one complete swerve module.

Contains:

- One drive motor
- One steering motor

Responsibilities:

- Apply desired wheel speed
- Apply desired steering angle
- Report module velocity
- Report module position

---

## DriveSubsystem.java

Controls the complete drivetrain.

Responsibilities:

- Convert chassis speeds into module states
- Command all four swerve modules
- Require injected A301 handles instead of constructing duplicate hardware wrappers
- Future odometry support
- Future gyro integration

---

## DriveCommand.java

Reads controller input.

Responsibilities:

- Apply joystick deadband
- Generate chassis velocities
- Command DriveSubsystem

---

## RobotContainer.java

Creates the robot configuration.

Responsibilities:

- Accept externally-created subsystems and input suppliers
- Configure default commands

---

# Current Implementation

Completed:

- Modular swerve architecture
- SystemCore/org.wpilib command framework imports
- Controller input supplier support
- MotionCore CAN mapping
- A301 hardware wrapper classes
- Relative encoder support
- Absolute encoder support
- Encoder velocity support
- Injected A301 ownership model
- Swerve module abstraction

---

# Planned Improvements

- Custom steering PID
- Steering calibration
- Closed-loop drive tuning
- Module optimization
- Gyro integration
- Swerve odometry
- Field-oriented driving
- Autonomous path following
- Motion profiling
- Telemetry
- Diagnostics

---

# MotionCore Bus Mapping

| Module | MotionCore Bus | Drive CAN | Steering CAN |
|---------|----------------|-----------|--------------|
| Front Left | CAN_D0 | 1 | 5 |
| Front Right | CAN_D1 | 2 | 6 |
| Back Left | CAN_D2 | 3 | 7 |
| Back Right | CAN_D3 | 4 | 8 |

---

# Driver Controls

| Control | Function |
|----------|----------|
| Left Stick Y | Forward / Reverse |
| Left Stick X | Strafe Left / Right |
| Right Stick X | Rotate Robot |

---

# Phoenix 6 Integration Strategy

The project is intentionally designed around hardware abstraction.

Higher-level swerve code interacts only with:

- Wheel speed
- Steering angle
- Module position
- Module state

The A301 wrapper classes provide the hardware-specific implementation using the documented REV API. This architecture mirrors the abstraction used by Phoenix 6, allowing the drivetrain logic to remain independent of the underlying motor hardware.

---

# Design Philosophy

The project emphasizes modular software design and hardware abstraction.

Hardware-specific functionality is isolated inside the A301 wrapper classes while the drivetrain logic remains hardware-independent. This minimizes the impact of future API changes and simplifies integration of additional MotionCore features, custom steering PID control, odometry, and autonomous functionality.

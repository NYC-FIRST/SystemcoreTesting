# YGSL A301 Swerve Drive Template

## Overview

The **YGSL A301 Swerve Drive Template** is a modular swerve drive framework designed for the REV A301 motor platform running on MotionCore/SystemCore.

The goal of this project is to establish a clean, reusable swerve architecture before integrating the complete A301 hardware implementation. By separating the software architecture from the hardware-specific implementation, the drivetrain can be developed, tested, and expanded more efficiently.

---

# Project Goals

- Create a reusable swerve drive framework
- Follow standard WPILib subsystem architecture
- Keep drive and steering motors independent
- Make hardware changes isolated to motor wrapper classes
- Allow future integration of A301 encoder feedback and MotionCore features

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
PS5 Controller
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

Each software layer has a single responsibility, making the system easier to maintain and expand.

---

# Class Responsibilities

## Constants.java

Stores robot-wide constants including:

- Robot dimensions
- Swerve kinematics
- CAN IDs
- MotionCore bus IDs
- PID constants
- Gear ratios
- Driver controller configuration

---

## A301DriveMotor.java

Wrapper around a single A301 drive motor.

Responsibilities:

- Initialize motor
- Set throttle
- Set velocity
- Stop motor
- Future encoder support

---

## A301SteeringMotor.java

Wrapper around a single A301 steering motor.

Responsibilities:

- Initialize steering motor
- Rotate wheel using PID control
- Future encoder feedback
- Stop motor

---

## SwerveModule.java

Represents one complete swerve module.

Contains:

- One drive motor
- One steering motor

Responsibilities:

- Receive desired module state
- Control wheel speed
- Control wheel angle
- Report module position

---

## DriveSubsystem.java

Controls the entire drivetrain.

Contains:

- Front Left Module
- Front Right Module
- Back Left Module
- Back Right Module

Responsibilities:

- Convert chassis speeds into module states
- Send commands to each module
- Future odometry
- Future gyro integration

---

## DriveCommand.java

Reads driver controller input.

Responsibilities:

- Read PS5 controller
- Apply joystick deadband
- Generate chassis speeds
- Command DriveSubsystem

---

## RobotContainer.java

Connects all robot components.

Responsibilities:

- Create subsystem
- Create controller
- Set default drive command
- Configure future button bindings

---

# Current Status

Implemented:

- Project architecture
- Swerve module abstraction
- Drive subsystem
- Driver command
- PS5 controller support
- MotionCore CAN bus structure

---

# Planned Improvements

Future development includes:

- A301 encoder integration
- Steering encoder calibration
- Closed-loop drive control
- Module optimization
- Field-oriented driving
- Gyro integration
- Swerve odometry
- Autonomous path following
- Motion profiling
- Telemetry and diagnostics

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

PS5 Controller

| Control | Function |
|----------|----------|
| Left Stick Y | Forward / Reverse |
| Left Stick X | Strafe Left / Right |
| Right Stick X | Rotate Robot |

---

# Design Philosophy

This project emphasizes modular software design.

Rather than embedding hardware logic throughout the codebase, each software layer has a clearly defined responsibility. This allows future hardware changes—including updates to the REV A301 API—to be isolated to the motor wrapper classes without requiring significant changes to the overall drivetrain architecture.

The result is a clean, maintainable, and extensible swerve drive framework suitable for future development and integration with MotionCore.
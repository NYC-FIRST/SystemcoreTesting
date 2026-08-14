<div align="center">

# Systemcore A301 Robot CAD

### Six weeks of drivetrain design, prototyping, testing, and iteration

[![Systemcore](https://img.shields.io/badge/Systemcore-Alpha-6f42c1?style=flat-square)](https://github.com/NYC-FIRST/SystemcoreTesting/tree/Revlib4-testing)
[![Motioncore](https://img.shields.io/badge/Motioncore-A301_Control-0a7f5a?style=flat-square)](https://github.com/NYC-FIRST/SystemcoreTesting/tree/Revlib4-testing)
[![CAD](https://img.shields.io/badge/CAD-Fusion_360_%2B_STEP-f57c00?style=flat-square)](#cad-collection)
[![Robot Code](https://img.shields.io/badge/Robot_Code-Java-007396?style=flat-square)](https://github.com/NYC-FIRST/SystemcoreTesting/tree/Revlib4-testing/testprojects/robot%20test/src/main/java/first/robot)

<img src="./A301%20Chassis%20Swechanum/images/updated-chassis-front.png" alt="Final A301 four-pod swerve robot" width="820">

This folder collects the CAD and build history for the A301 drivetrain prototypes developed during a six-week internship at the NYC FIRST Cornell Tech STEM Center.

[Explore the CAD](#cad-collection) | [View the development path](#six-week-development-path) | [Open the robot code](#robot-code)

</div>

---

## Project Overview

The project began with a simple mecanum chassis for early Systemcore and Motioncore testing. It then moved through several custom A301 swerve pod revisions, an experimental two-pod swerve chassis supported by caster wheels, and finally the complete four-pod swerve robot.

The final drivetrain uses **four custom A301 V3 swerve pods** and **eight A301 motors**. Each pod has one drive motor and one steering motor. The lower mecanum wheels on the hybrid chassis are passive and use zero motors.

| Final system | Configuration |
| --- | --- |
| Control hardware | Systemcore and Motioncore |
| Powered modules | Four A301 V3 swerve pods |
| Drive motors | Four A301 motors |
| Steering motors | Four A301 motors |
| Total powered motors | Eight |
| Passive support | Four unpowered mecanum wheels |
| Design tools | Fusion 360 and STEP exports |
| Development period | Six-week internship |

## CAD Collection

### 01 / [A301 Swerve Module](<A301 Swerve Module/>)

![A301 V3 swerve module](<A301 Swerve Module/A301 Ratchet Swerve .75mm inc v6.png>)

The complete CAD package for the custom A301 swerve module. The main folder contains **V3**, the slimmer final module used on the current robot, with a Fusion 360 archive, STEP export, reference renders, gearing details, and build notes. Earlier V1, V2, and V2.5 designs are preserved in `Older Versions` to show how the mechanism changed during development.

| Module specification | V3 design |
| --- | --- |
| Motors per pod | One drive and one steering A301 |
| Drive reduction | 3.1:1 offset bevel |
| Steering | Directly attached at the center of rotation |
| Structure | goBILDA parts and custom CAD |

[Open the A301 Swerve Module folder](<A301 Swerve Module/>)

---

### 02 / [A301 Mecanum Chassis](<A301 Mecanum Chassis/>)

![A301 mecanum chassis CAD](A301%20Mecanum%20Chassis/images/cad-lower-mecanum-section.png)

The simple mecanum prototype created during the first week of the internship. It provided a quick platform for testing Systemcore, Motioncore, A301 motors, and the 18-volt battery pack before the project moved deeper into custom swerve development.

During the final week, the chassis design was cut from metal so the nose drawer could be mounted above it. The folder contains the mecanum CAD renders and is organized for the full chassis CAD files and real-life build photos.

| Chassis specification | Mecanum prototype |
| --- | --- |
| Wheels | Four mecanum wheels |
| Early purpose | Systemcore and Motioncore drivetrain testing |
| Battery | 18-volt battery pack |
| First build | Designed and assembled during week one |
| Final structure | Cut from metal for the nose drawer |

[Open the A301 Mecanum Chassis folder](<A301 Mecanum Chassis/>)

---

### 03 / [A301 Chassis Swechanum](<A301 Chassis Swechanum/>)

![Updated A301 Swechanum chassis](A301%20Chassis%20Swechanum/images/updated-chassis-side.png)

The full hybrid prototype that combines the upper swerve structure with a lower mecanum base. This folder documents the earlier CAD concept and the updated physical robot. The current build has **four powered swerve pods, zero caster wheels, and zero powered mecanum motors**.

It also includes current chassis photos, the full hybrid CAD render, motor allocation, Systemcore and Motioncore layout, and the final robot video.

| Chassis specification | Final hybrid robot |
| --- | --- |
| Powered swerve pods | Four |
| Motors per pod | One drive and one steering A301 |
| Total A301 motors | Eight |
| Passive casters | Zero |
| Lower mecanum motors | Zero |
| Lower support wheels | Four passive mecanum wheels |

[Open the A301 Chassis Swechanum folder](<A301 Chassis Swechanum/>)

## Six-Week Development Path

| Time | Development stage |
| --- | --- |
| Week 1 | Built the simple mecanum chassis for early drivetrain testing |
| Weeks 2 through 4 | Designed, built, and revised the custom A301 swerve pod |
| Weeks 4 and 5 | Tested a two-pod swerve chassis with caster support |
| Week 6 | Completed the final four-pod swerve robot using eight A301 motors in the swerve assembly |

The CAD folders preserve the mechanical progression from the first mecanum base to the final four-pod robot. The Java project developed alongside the hardware preserves motor bring-up, pod testing, the earlier two-pod drive, mechanism testing, steering calibration, telemetry, field-centric experiments, vision work, and the final four-module drive mode.

### Final Robot Video

[![Watch the final four-pod A301 swerve robot on Instagram](A301%20Chassis%20Swechanum/images/instagram-reel-preview.jpg)](https://www.instagram.com/reel/Db8wJIyBqEo/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==)

> **Click the preview to watch the Instagram Reel.** This is the final four-pod swerve robot using eight A301 motors and the completed drive code linked below.

## Robot Code

> [!IMPORTANT]
> This code folder contains the robot work completed throughout the six-week internship. It includes the earlier prototypes, mechanism tests, and the final code used to drive the four-pod swerve robot shown in the Instagram Reel.

### [Open the complete robot code on GitHub](https://github.com/NYC-FIRST/SystemcoreTesting/tree/Revlib4-testing/testprojects/robot%20test/src/main/java/first/robot)

| Code area | Purpose |
| --- | --- |
| `FourSwerveDriveTeleop.java` | Final four-module swerve driving mode |
| `Robot.java` | A301 motor definitions, four-pod mapping, and robot hardware setup |
| `FourSwerveCalibrationTest.java` | Absolute encoder readings and pod alignment |
| `FourSwerveDriveDirectionTest.java` | Drive motor direction testing |
| `FourSwerveSteeringDirectionTest.java` | Steering direction testing |
| `diagonalswerve/` and `DiagonalTwoPodDriveSubsystem.java` | Earlier two-pod swerve experiments used with caster support |
| `swerveTests/` and pod test modes | Individual drive, steering, encoder, and PID bring-up |
| `ArmJointZeroPidTest.java` | Mechanism motor testing and logging |
| `fieldcentric/`, `vision/`, and telemetry classes | Field-centric, AprilTag, and dashboard experiments |

---

<div align="center">

Built during the NYC FIRST Cornell Tech STEM Center summer internship using Systemcore, Motioncore, A301 motors, and an iterative CAD-to-hardware workflow.

</div>

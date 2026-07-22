# SystemCore Swerve Bring-Up Notes

This folder documents the current swerve bring-up work for SystemCore using REV
A301 hardware. It is meant to help new contributors quickly understand what this
code is, what it is not, what is safe to test, and which assumptions still need
hardware validation.

## Read This First

| Topic | Current answer | Why it matters |
| --- | --- | --- |
| Primary test target | One swerve pod | Do not assume the full four-module drivetrain is validated. |
| Hardware API in use | REV A301 / REVLib | The code does not currently instantiate CTRE Phoenix 6 devices. |
| Steering control mode | Open-loop throttle from software PID | This is intentionally conservative after unstable behavior from deeper motor control paths. |
| Steering feedback | Built-in motor-side absolute encoder | This is a weak estimate when the steering motor is geared before the pod. |
| Current mechanical issue | 16T to 36T steering reduction, or 2.25:1 | Motor rotation does not map directly to pod rotation. |
| Near-term mechanical direction | 1:1 steering ratio | Makes the built-in absolute encoder more useful for pod-angle control. |

## What This Folder Is

This is a SystemCore-compatible swerve template and test area. It started from a
normal FRC/RoboRIO-style Java swerve structure and was remapped to compile
against the package layout available in this SystemCore project.

The original FRC style commonly uses imports under `edu.wpi.first.*`. In this
project, the relevant WPILib imports are under `org.wpilib.*`.

Examples used here:

| FRC concept | SystemCore import used here |
| --- | --- |
| Command base class | `org.wpilib.command2.Command` |
| Subsystem base class | `org.wpilib.command2.SubsystemBase` |
| Chassis speed object | `org.wpilib.math.kinematics.ChassisVelocities` |
| Swerve kinematics | `org.wpilib.math.kinematics.SwerveDriveKinematics` |
| Swerve module velocity | `org.wpilib.math.kinematics.SwerveModuleVelocity` |
| Software PID | `org.wpilib.math.controller.PIDController` |

The hardware layer is mapped to REV A301 classes:

| Hardware concept | API used here |
| --- | --- |
| Motor object | `com.revrobotics.spark.A301` |
| Sensor/status return wrapper | `com.revrobotics.util.Signal` |
| Safe output path currently used | `A301.setThrottle(...)` |

## Naming And Scope

The folder is currently named `YGSLSwerveTemplate`. The intended reference may
be YAGSL, "Yet Another Generic Swerve Library", which is an open source swerve
library that can support CTRE Phoenix 6 hardware.

That name is important, but it should not be read as proof that upstream YAGSL is
currently running in this repo.

At this snapshot:

- There are no `swervelib.*` imports.
- There are no `com.ctre.phoenix6.*` imports.
- There is no Phoenix 6 vendordep.
- There are no CTRE classes such as `TalonFX`, `CANcoder`, or `Pigeon2`.
- There are no YAGSL JSON configuration files defining CTRE motors, encoders, or
  IMU hardware.

Conclusion: this folder is currently a local SystemCore/REV A301 swerve bring-up
path. It is not an upstream YAGSL implementation and it is not a Phoenix 6
implementation.

## Current Runtime Path

The active hardware test path is the single-pod teleop mode:

```text
Robot
  owns robot.swerveDrive and robot.swerveTheta
        |
        v
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

That ownership model is intentional. It avoids constructing duplicate software
objects for the same physical motor controllers.

## Control Strategy

The steering motor is currently controlled in open loop:

```text
desired pod angle
        |
        v
software PID calculation
        |
        v
clamped throttle command
        |
        v
A301.setThrottle(...)
```

This is not the final desired control strategy. It is the current safe test
strategy.

The team observed unstable behavior when trying to depend on deeper motor-object
control paths or direct target-setting. Reported symptoms included:

- Position reporting repeatedly fluctuating around the `-0.5` to `0.5` range.
- Continuous zigzag or oscillating steering behavior.
- Absolute encoder values stopping or becoming unreliable.
- Recovery sometimes requiring firmware flashing before values reported
  correctly again.

Because of that, this code keeps the risky part outside the motor controller:

1. Read the absolute encoder through the A301 API.
2. Run the steering PID in Java.
3. Clamp the output.
4. Send only throttle to the motor.

This reduces precision, but it lets us test the steering concept without relying
on closed-loop APIs that have not yet behaved reliably on this hardware setup.

## Mechanical Feedback Constraint

The current steering design has a gear reduction between the rotation motor and
the actual pod:

```text
16 tooth gear -> 36 tooth gear
ratio: 36 / 16 = 2.25:1
```

This means the built-in absolute encoder is measuring motor-side rotation, not
the final pod angle directly.

For swerve steering, that distinction is critical. A normal pod-angle controller
wants feedback from the final steering shaft or from a mechanism that maps
cleanly to the final pod angle. With the current 2.25:1 ratio, one motor
rotation is not one pod rotation, so a normalized motor encoder range such as
`-0.5` to `0.5` cannot be treated as one clean pod revolution.

The practical result is poor pod-angle estimation when using only the built-in
motor-side absolute encoder.

## Mechanical Direction

The near-term mechanical fix is to test a 1:1 steering ratio. That should make
the built-in absolute encoder much closer to the actual pod rotation and remove
one major source of estimation error.

The longer-term FRC-style option is to place an external absolute encoder on the
final steering shaft or the rotating pod assembly. That would let the steering
loop close around the real pod angle instead of inferred motor-side rotation.

Tradeoff:

| Option | Benefit | Cost |
| --- | --- | --- |
| Built-in encoder with 1:1 steering | Simpler wiring and uses existing motor sensor | Still depends on motor sensor behavior and alignment. |
| External absolute encoder on final shaft | Best representation of actual pod angle | Additional hardware, wiring, configuration, and integration work. |

## File Map

| File | Role | Status |
| --- | --- | --- |
| `SinglePodYgslTeleop.java` | Current single-pod teleop test entry point | Active test path |
| `subsystems/drive/SinglePodDriveSubsystem.java` | Converts joystick direction into one module target | Active test path |
| `subsystems/drive/SwerveModule.java` | Combines drive and steering motor wrappers | Active test path |
| `subsystems/drive/A301DriveMotor.java` | Drive motor wrapper using A301 throttle and encoder reads | Active test path |
| `subsystems/drive/A301SteeringMotor.java` | Steering wrapper using software PID and A301 throttle | Active test path |
| `subsystems/drive/DriveSubsystem.java` | Four-module drivetrain skeleton using WPILib kinematics | Structural, not primary test path |
| `commands/DriveCommand.java` | Four-module command skeleton for chassis velocity control | Structural, not primary test path |
| `Constants.java` | Dimensions, kinematics, PID constants, and single-pod tuning | Shared |
| `RobotContainer.java` | Command-based wiring skeleton for full drivetrain | Structural |
| `util/SwerveUtils.java` | Placeholder utility class | Not implemented |

## How To Assess Changes

Use this checklist when reviewing future changes to this folder.

| Question | Good sign | Warning sign |
| --- | --- | --- |
| Is the change for single-pod testing or full drivetrain work? | The target is clearly stated. | Full drivetrain assumptions are mixed into single-pod bring-up. |
| Does it create new motor objects? | It reuses `Robot`-owned A301 instances. | It constructs duplicate objects for the same hardware. |
| Does it rely on closed-loop motor APIs? | It explains why the API is now safe. | It bypasses the current throttle-only safety constraint. |
| Does it account for steering ratio? | It uses 1:1 hardware or an explicit conversion. | It assumes motor rotations equal pod rotations on reduced hardware. |
| Does it claim Phoenix 6/YAGSL support? | It includes actual imports, vendordeps, and config files. | It relies on naming or architecture similarity only. |

## Safe Bring-Up Procedure

1. Start from `SinglePodYgslTeleop`.
2. Confirm `robot.swerveDrive` and `robot.swerveTheta` report valid status.
3. Confirm the absolute encoder reports a stable value before commanding motion.
4. Use the open-loop `setThrottle(...)` path for steering.
5. Keep steering PID output clamped.
6. Watch for repeated `-0.5` to `0.5` jumps, zigzag motion, or encoder dropout.
7. Stop testing if encoder reporting becomes unreliable.
8. Do not promote changes into `DriveSubsystem` until the single pod is stable.

## Known Risks

| Risk | Impact | Current mitigation |
| --- | --- | --- |
| Motor API instability during target-setting | Motor can behave unpredictably | Use software PID and `setThrottle(...)` only. |
| Motor-side encoder does not equal pod angle | Poor steering accuracy | Move toward 1:1 ratio or final-shaft encoder. |
| Folder name suggests YAGSL/Phoenix support | Contributors may assume unsupported features exist | Keep this README explicit about current dependencies. |
| Full drivetrain skeleton exists before full validation | Easy to overestimate readiness | Treat four-module code as structural until single pod is stable. |

## Next Milestones

| Priority | Milestone | Exit criteria |
| --- | --- | --- |
| P0 | Stable single-pod steering on throttle control | Pod can hold and move to target angles without oscillation or encoder dropout. |
| P0 | Validate 1:1 steering mechanism | Built-in absolute encoder maps predictably to pod rotation. |
| P1 | Tune software PID around real hardware | Steering response is repeatable across startup and direction changes. |
| P1 | Decide final steering feedback source | Team chooses built-in 1:1 encoder or external final-shaft absolute encoder. |
| P2 | Promote back into four-module drivetrain | `DriveSubsystem` is wired only after one-pod behavior is understood. |
| P2 | Re-evaluate YAGSL/Phoenix 6 integration | Only after dependency, licensing, and hardware access questions are resolved. |

## Bottom Line

This folder is for proving a SystemCore-compatible swerve control path with REV
A301 hardware. The current implementation is intentionally conservative:
single-pod first, software PID second, throttle output only, and no Phoenix
6/YAGSL dependency assumptions.

The main engineering question is not whether the code can produce a swerve-like
command. It can. The main question is whether the feedback signal represents the
real pod angle well enough to control the mechanism safely and repeatably.

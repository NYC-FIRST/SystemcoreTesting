# A301 Swerve Module CAD V1

> [!NOTE]
> CAD package for an A301-based swerve module used for Systemcore, Motioncore, and A301 testing.

> [!IMPORTANT]
> V1 is a legacy repository snapshot. Current CAD uploads moved to Google Drive.

![A301 swerve module](<Screenshot 2026-07-21 220702.png>)

## At A Glance

| Area | Detail |
| --- | --- |
| Drive motor | A301 |
| Steering motor | A301 |
| Structure | goBILDA |
| Drive output | Axon bevel gear |
| Steering output | Pulley-driven module rotation |
| CAD source | Fusion 360 archive |
| Neutral export | STEP |

## Module Layout

| System | Static power path |
| --- | --- |
| Wheel drive | `A301 drive motor` → `Axon bevel gear` → `Wheel axle` |
| Steering | `A301 steering motor` → `Pulley stage` → `Rotating module` |

## Files

| File | Purpose |
| --- | --- |
| [`V1 A301 Swerve Module.f3z`](<./V1 A301 Swerve Module.f3z>) | Fusion 360 archive |
| [`V1 A301 Swerve Module.step`](<./V1 A301 Swerve Module.step>) | STEP export |

## Pulley Options

| Version | Motor Pulley | Module Pulley | Notes |
| --- | ---: | ---: | --- |
| V1 | 16T | 36T | Metal motor pulley, 3D-printed module pulley |
| V2 | 36T | 36T | Both pulleys are 3D printed |

## Build Notes

- The module uses one A301 for wheel drive and one A301 for steering.
- The main structure is built around goBILDA hardware.
- The drive path uses an Axon bevel gear to transfer motor output to the wheel axle.
- The steering path rotates the module through a pulley stage.

<details>
<summary>Design Intent</summary>

This model is for bring-up and iteration, not a frozen production design. Use the CAD as a reference for A301 packaging, steering geometry, and early swerve-module testing.

</details>

[Back to the Legacy CAD index](../)

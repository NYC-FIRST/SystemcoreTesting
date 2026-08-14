<div align="center">

# A301 Swerve Module CAD

### V3 custom drive-and-steer pod for Systemcore and Motioncore testing

![Version](https://img.shields.io/badge/Version-V3-6f42c1?style=flat-square)
![Motors](https://img.shields.io/badge/Motors-2_A301-0a7f5a?style=flat-square)
![Reduction](https://img.shields.io/badge/Drive_Reduction-3.1%3A1-f57c00?style=flat-square)
![CAD](https://img.shields.io/badge/CAD-Fusion_360_%2B_STEP-0078d4?style=flat-square)

<img src="a301-swerve-pods-installed.png" alt="A301 V3 swerve pods installed on the robot" width="820">

The final custom swerve pod developed for the four-module A301 robot during the six-week internship.

[Watch the prototype](#prototype-video) | [View the CAD package](#cad-package) | [Browse older versions](#design-history) | [Back to CAD Models](../)

</div>

---

## Prototype Video

[![Play the A301 V3 swerve module video on YouTube](https://img.youtube.com/vi/2v5tu2qvoQQ/maxresdefault.jpg)](https://youtube.com/shorts/2v5tu2qvoQQ)

> **Click the preview to watch the V3 prototype.** The video opens on YouTube.

## Project Overview

The A301 V3 swerve module is a compact two-motor pod designed for the final Systemcore robot. One A301 motor drives the wheel through a fully geared transmission, while a second A301 motor rotates the pod directly at its center of rotation.

V3 is slimmer than the earlier designs and replaces the V2.5 Axon 2.8:1 bevel with a 3.1:1 offset bevel set. Four copies of this module were installed on the final robot, using eight A301 motors across the complete swerve assembly.

| Module specification | V3 configuration |
| --- | --- |
| Drive motor | One A301 |
| Steering motor | One A301 |
| Motors per pod | Two |
| Structure | goBILDA parts and custom plates |
| Drive output | Fully geared transmission |
| Steering output | Direct rotation at the pod center |
| Overall drive reduction | 3.1:1 |
| Main development period | Weeks 2 through 4 |

## Module Layout

| System | Static power path |
| --- | --- |
| Wheel drive | `A301 drive motor (64T)` -> `64T driven gear` -> `20T gear` -> `20T bevel input` -> `3.1:1 offset bevel` -> `Wheel axle` |
| Steering | `A301 steering motor` -> `Center of rotation` -> `Rotating module` |

## Drive Gear Path

| Stage | Gear pair or ratio | Purpose |
| --- | --- | --- |
| First spur stage | 64T to 64T | Transfers drive power at 1:1 |
| Second spur stage | 20T to 20T | Transfers power to the bevel input at 1:1 |
| Bevel stage | 3.1:1 | Turns the power path toward the wheel axle |
| Overall | 3.1:1 | The offset bevel provides the full reduction |

## Ratchet Status

> [!NOTE]
> The ratchet is not installed on the current physical prototype because it could not be ordered in time for the build. The mounting holes are already included in the module, so the ratchet can be added later without redesigning the main structure.

## CAD Package

| File | Format | Purpose |
| --- | --- | --- |
| [`A301 Ratchet Swerve .75mm inc.f3z`](<A301 Ratchet Swerve .75mm inc.f3z>) | Fusion 360 archive | Editable V3 module assembly |
| [`A301 Ratchet Swerve .75mm inc.step`](<A301 Ratchet Swerve .75mm inc.step>) | STEP | Neutral V3 module export |

### CAD Reference Views

<table>
  <tr>
    <td align="center"><img src="A301%20Ratchet%20Swerve%20.75mm%20inc%20v62.png" alt="A301 V3 swerve module underside CAD"><br><strong>Underside CAD</strong></td>
    <td align="center"><img src="A301%20Ratchet%20Swerve%20.75mm%20inc%20v6.png" alt="A301 V3 swerve module top CAD"><br><strong>Top CAD</strong></td>
  </tr>
</table>

## Physical Prototype Gallery

<table>
  <tr>
    <td align="center"><img src="a301-swerve-pods-installed.png" alt="A301 swerve pods installed on the chassis"><br><strong>Installed pods</strong></td>
    <td align="center"><img src="a301-swerve-module-gearing.png" alt="A301 swerve module motors and gearing"><br><strong>Motors and gearing</strong></td>
    <td align="center"><img src="a301-swerve-wheel-assembly.png" alt="A301 swerve wheel and steering assembly"><br><strong>Wheel assembly</strong></td>
  </tr>
</table>

## Design History

The module was revised throughout the internship as the team tested packaging, steering, and power transmission. Earlier releases are preserved in [`Older Versions`](<Older Versions/>), including V1, V2, and the archived [`V2.5 Swerve (Direct Turning)`](<Older Versions/V2.5 Swerve (Direct Turning)/>) design.

| Version | Main steering approach | Drive approach |
| --- | --- | --- |
| V1 | Pulley-driven rotation | Axon bevel output |
| V2 | Direct bolted center shaft | Fully geared 2.8:1 Axon bevel path |
| V2.5 | Direct center shaft | Revised geared packaging |
| V3 | Direct motor at the center of rotation | Slimmer fully geared 3.1:1 offset bevel path |

## Build Notes

- One A301 powers the wheel and one A301 controls steering.
- The two spur stages remain 1:1, so the offset bevel provides the complete 3.1:1 reduction.
- V3 uses a slimmer package than V2.5 while continuing to use goBILDA structural parts.
- The steering motor is directly attached at the center of pod rotation.
- Ratchet mounting holes are ready for a future installation.

---

<div align="center">

[Back to the complete CAD Models collection](../)

</div>

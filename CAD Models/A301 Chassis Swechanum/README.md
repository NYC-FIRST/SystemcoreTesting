# A301 Full Hybrid Chassis

> [!NOTE]
> This folder documents the updated A301 Swechanum prototype used for Systemcore, Motioncore, and A301 testing. It combines an upper swerve section with a lower mecanum base.

![Updated A301 Swechanum chassis from the front](images/updated-chassis-front.png)

## Full Chassis Prototype Video

[![Watch the A301 chassis rise on Instagram](images/instagram-reel-preview.jpg)](https://www.instagram.com/reel/Db8wJIyBqEo/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==)

> **Click the preview to watch the Instagram Reel.** To keep this page open, use **Ctrl+click** on Windows or Linux, **Cmd+click** on macOS, or middle-click to open it in a new tab.

## At a Glance

| Area | Current configuration |
| --- | --- |
| Project | A301 full hybrid Swechanum chassis |
| Upper drivetrain | Four powered A301 V3 swerve pods |
| Upper passive casters | Zero |
| Lower drivetrain | Four passive mecanum wheels |
| Swerve motors | Eight A301 motors, with one drive motor and one steering motor in each pod |
| Mecanum motors | Zero |
| Total powered motors | Eight |
| Controls | Systemcore and Motioncore |
| Structure | Metal goBILDA channel, plates, and hardware |

## Current Chassis Architecture

| Section | Static system flow |
| --- | --- |
| Powered swerve | `Systemcore + Motioncore` → `Upper swerve section` → `4 powered A301 V3 pods` → `8 A301 motors` |
| Passive mecanum | `Combined chassis` → `Lower mecanum base` → `4 passive mecanum wheels` → `0 mecanum motors` |

## Upper Swerve Section

The updated prototype uses **four A301 V3 swerve pods**. Each pod has one drive motor and one steering motor, so the upper section uses eight A301 motors altogether. The previous version used two powered swerve pods with passive casters. Those casters have been removed, and the current chassis has **zero passive caster or dead wheels**.

The current A301 V3 pod is documented in the neighboring [`A301 Swerve Module`](../A301%20Swerve%20Module/) folder.

![Four powered swerve pods on the updated chassis](images/updated-chassis-side.png)

## Lower Mecanum Section

The lower section still has four mecanum wheels, but they are no longer powered. The current prototype uses **zero motors on the mecanum drivetrain**. The mecanum wheels now act as passive support wheels while the four upper swerve pods provide the powered movement and steering.

![Passive mecanum wheels on the updated lower chassis](images/updated-chassis-angle.png)

## Motor Allocation

| Section | Configuration | A301 motors |
| --- | --- | ---: |
| Upper swerve | Four pods with one drive and one steering motor per pod | 8 |
| Lower mecanum | Four passive mecanum wheels | 0 |
| **Total** | Current physical prototype | **8** |

This replaces the earlier setup described in this folder. The current robot does not use the previous two-swerve-pod and two-caster layout, and it does not use four powered mecanum motors.

## Updated Prototype Photos

<table>
  <tr>
    <td align="center"><img src="images/updated-chassis-front.png" alt="Updated Swechanum front view"><br><strong>Front view</strong></td>
    <td align="center"><img src="images/updated-chassis-side.png" alt="Updated Swechanum side view"><br><strong>Side view</strong></td>
    <td align="center"><img src="images/updated-chassis-angle.png" alt="Updated Swechanum low angle view"><br><strong>Low angle view</strong></td>
  </tr>
</table>

## CAD Views

The included full chassis render documents an earlier design stage and may not show the four-pod, zero-caster configuration of the updated physical prototype.

![Full hybrid chassis CAD](images/cad-full-chassis.png)

## Build Notes

- The current physical chassis has four powered swerve pods and no passive casters.
- The four mecanum wheels are passive and use zero motors.
- Systemcore and Motioncore control the eight motors used by the four swerve pods.
- The new photos show the current physical configuration more accurately than the earlier CAD renders.

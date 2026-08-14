<div align="center">

# A301 Swechanum Chassis

### Final four-pod swerve robot on a passive mecanum support base

![Swerve](https://img.shields.io/badge/Swerve_Pods-4_Powered-6f42c1?style=flat-square)
![Motors](https://img.shields.io/badge/Motors-8_A301-0a7f5a?style=flat-square)
![Mecanum](https://img.shields.io/badge/Mecanum_Motors-0_Passive-f57c00?style=flat-square)
![CAD](https://img.shields.io/badge/CAD-Current_and_Archive-0078d4?style=flat-square)

<img src="images/final-swechanum.png" alt="Full four-pod A301 Swechanum robot CAD with the passive mecanum base" width="820">

The final integrated robot developed from the mecanum base, custom swerve pods, and six weeks of Systemcore testing.

[Watch the final robot](#final-robot-video) | [Review the architecture](#current-chassis-architecture) | [View CAD](#cad) | [Back to CAD Models](../)

</div>

---

## Final Robot Video

[![Watch the final four-pod A301 swerve robot on Instagram](images/instagram-reel-preview.jpg)](https://www.instagram.com/reel/Db8wJIyBqEo/?utm_source=ig_web_copy_link&igsh=MzRlODBiNWFlZA==)

> **Click the preview to watch the Instagram Reel.** The video shows the final four-pod robot using eight A301 motors and the completed swerve drive code.

## Project Overview

The A301 Swechanum chassis is the final integrated robot from the internship. It combines four powered A301 V3 swerve pods with the lower mecanum structure developed earlier in the project.

The current robot is driven entirely by the upper swerve assembly. Each of the four pods uses one A301 drive motor and one A301 steering motor, for eight powered motors total. The four lower mecanum wheels remain attached as passive support wheels and use zero motors.

| Chassis specification | Final configuration |
| --- | --- |
| Control hardware | Systemcore and Motioncore |
| Powered swerve pods | Four A301 V3 modules |
| Drive motors | Four A301 motors |
| Steering motors | Four A301 motors |
| Total powered motors | Eight |
| Passive casters | Zero |
| Lower support wheels | Four mecanum wheels |
| Lower mecanum motors | Zero |
| Structure | Metal goBILDA channel, plates, and hardware |
| Final development stage | Week 6 |

## Current Chassis Architecture

| Section | Static system flow |
| --- | --- |
| Powered swerve | `Systemcore + Motioncore` -> `Upper swerve section` -> `4 powered A301 V3 pods` -> `8 A301 motors` |
| Passive mecanum | `Combined chassis` -> `Lower mecanum base` -> `4 passive mecanum wheels` -> `0 mecanum motors` |

## Development from Two Pods to Four

The earlier chassis used two powered swerve pods and caster support while the team developed the steering system and diagonal drive behavior. The final version replaced that arrangement with four powered V3 pods.

| Development stage | Upper drivetrain | Support system |
| --- | --- | --- |
| Weeks 4 and 5 | Two powered swerve pods | Passive caster support |
| Week 6 final robot | Four powered swerve pods | Zero casters |

The lower mecanum wheels are not part of the powered drivetrain in the final robot. They remain as passive supports while the four swerve pods handle movement and steering.

## Motor Allocation

| Section | Configuration | A301 motors |
| --- | --- | ---: |
| Upper swerve | Four pods with one drive and one steering motor per pod | 8 |
| Lower mecanum | Four passive mecanum wheels | 0 |
| **Total** | Final physical robot | **8** |

## CAD

The latest Swechanum robot CAD is available through the preview below. The current files are maintained on Google Drive because the complete Fusion 360 and STEP assemblies are too large for normal GitHub storage.

<div align="center">
  <a href="https://drive.google.com/drive/folders/1VI5ETDA84Cjonxy1SA_M-wRaS0PLGz6y" target="_blank" rel="noopener noreferrer" title="Open the current A301 Swechanum chassis CAD downloads">
    <img src="images/final-swechanum.png" alt="Open the current final four-pod A301 Swechanum chassis CAD downloads on Google Drive" width="720">
  </a>
  <p><strong>Current A301 Swechanum Robot CAD</strong><br>Click the CAD preview to open the complete Swechanum robot files in Google Drive.</p>
</div>

> [!TIP]
> The image opens the current CAD download folder. Older repository CAD versions are available in the archive below.

### CAD Archive

The [`CAD archive`](<Legacy CAD/>) preserves the versioned repository files, including the Systemcore Alpha STEP reference used during chassis packaging.

<div align="center">
  <a href="Legacy%20CAD/" title="Open the archived two-pod A301 Swechanum CAD">
    <img src="images/cad-full-chassis.png" alt="Open the archived two-pod A301 Swechanum chassis CAD" width="720">
  </a>
  <p><strong>Archived Two-Pod A301 Swechanum CAD</strong><br>Click the older two-pod and caster-support preview to open its repository CAD archive.</p>
</div>

## Physical Prototype Gallery

<table>
  <tr>
    <td align="center"><img src="images/final-robot-front.jpeg" alt="Completed Swechanum robot viewed from the front"><br><strong>Completed robot</strong></td>
    <td align="center"><img src="images/final-robot-angle.jpeg" alt="Completed Swechanum robot showing the Systemcore and Motioncore installation"><br><strong>Final electronics and chassis packaging</strong></td>
  </tr>
  <tr>
    <td align="center"><img src="images/updated-chassis-front.png" alt="Earlier front view of the Swechanum physical prototype"><br><strong>Earlier front view</strong></td>
    <td align="center"><img src="images/updated-chassis-side.png" alt="Earlier side view of the Swechanum physical prototype"><br><strong>Earlier side view</strong></td>
  </tr>
  <tr>
    <td colspan="2" align="center"><img src="images/updated-chassis-angle.png" alt="Earlier low angle view of the Swechanum physical prototype"><br><strong>Earlier low angle view</strong></td>
  </tr>
</table>

These final and earlier build photos show the four-pod robot from multiple angles, including the installed Systemcore and Motioncore hardware, finished wiring, and passive lower mecanum wheels.

## Related Mini Projects

| Project | Relationship to this chassis |
| --- | --- |
| [`A301 Swerve Module`](../A301%20Swerve%20Module/) | Documents the V3 drive-and-steer pod used four times on the final robot |
| [`A301 Mecanum Chassis`](../A301%20Mecanum%20Chassis/) | Documents the earlier mecanum test base and final metal chassis work |

## Build Notes

- The final physical chassis uses four powered V3 swerve pods and no caster wheels.
- Each pod uses one drive motor and one steering motor.
- Systemcore and Motioncore control all eight powered A301 motors.
- The four lower mecanum wheels are passive and use zero motors.
- Current full Swechanum CAD uploads are maintained through the preview link above, while the repository keeps only the versioned CAD archive.
- The current download preview shows the final four-pod layout, while the CAD archive preserves the earlier two-pod render.

---

<div align="center">

[Back to the complete CAD Models collection](../)

</div>

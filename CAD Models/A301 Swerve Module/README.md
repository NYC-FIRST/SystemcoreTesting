# A301 Swerve Module CAD (V3)

> [!NOTE]
> CAD package for V3, the current main version of the A301-based swerve module used for Systemcore, Motioncore, and A301 testing.

## ▶ V3 Prototype Video

[![▶ Play the A301 V3 swerve module video on YouTube](https://img.youtube.com/vi/2v5tu2qvoQQ/maxresdefault.jpg)](https://youtube.com/shorts/2v5tu2qvoQQ)

> **This is a video preview.** Click the image to play it. To keep this page open, use **Ctrl+click** (Windows/Linux), **Cmd+click** (macOS), or middle-click to open the video in a new tab.

![A301 V3 swerve module, underside view](<A301 Ratchet Swerve .75mm inc v62.png>)
![A301 V3 swerve module, top view](<A301 Ratchet Swerve .75mm inc v6.png>)

## At a Glance

| Area | Detail |
| --- | --- |
| Version | V3 |
| Drive motor | A301 |
| Steering motor | A301 |
| Structure | goBILDA parts |
| Packaging | Slimmer than V2.5 |
| Drive output | Fully geared transmission |
| Steering output | Motor directly attached at the center of rotation |
| Overall drive reduction | 3.1:1 |
| CAD source | Fusion 360 archive |
| Neutral export | STEP |

## Module Layout

| System | Static power path |
| --- | --- |
| Wheel drive | `A301 drive motor (64T)` → `64T driven gear` → `20T gear` → `20T bevel input` → `3.1:1 offset bevel` → `Wheel axle` |
| Steering | `A301 steering motor` → `Center of rotation` → `Rotating module` |

## Drive Gear Path

| Stage | Gear pair or ratio | Notes |
| --- | --- | --- |
| First spur stage | 64T to 64T | 1:1 |
| Second spur stage | 20T to 20T | 1:1 |
| Bevel stage | 3.1:1 | Offset bevel set driving the wheel axle |
| Overall | 3.1:1 | The two spur stages are 1:1, so the offset bevel provides the full reduction |

## Files

| File | Purpose |
| --- | --- |
| [`A301 Ratchet Swerve .75mm inc.f3z`](<./A301 Ratchet Swerve .75mm inc.f3z>) | Fusion 360 archive |
| [`A301 Ratchet Swerve .75mm inc.step`](<./A301 Ratchet Swerve .75mm inc.step>) | STEP export |

## Build Notes

- V3 is slimmer than V2.5 while continuing to use goBILDA structural parts.
- One A301 powers the wheel drive, and a second A301 handles steering.
- The wheel drive remains completely geared. Its power path is 64T to 64T, then 20T to 20T, followed by a 3.1:1 offset bevel set.
- Unlike the Axon 2.8:1 bevel used in V2.5, V3 uses the offset 3.1:1 bevel.
- The steering motor is directly attached to the center of rotation.

## Older Versions

Previous releases are preserved in [`Older Versions`](<./Older Versions/>), including the archived [`V2.5 Swerve (Direct Turning)`](<./Older Versions/V2.5 Swerve (Direct Turning)/>) release.

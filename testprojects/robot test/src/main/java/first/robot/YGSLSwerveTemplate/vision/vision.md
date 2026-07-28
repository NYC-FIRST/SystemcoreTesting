# Vision Subsystem — What Changed & Why

Location: `testprojects/robot test/src/main/java/first/robot/YGSLSwerveTemplate/vision/`
(+ one wiring change in `RobotContainer.java`)


### 1. `VisionConstants.java`
Went from empty to holding real configuration:
- **Camera setup**: `CAMERA_INDEX = 0`, `640x480 @ 30fps`.
- **AprilTag setup**: family `tag36h11`, `TAG_SIZE_METERS = 0.1651` (6.5 in).
- **Camera calibration**: `FX/FY/CX/CY` — placeholder values, explicitly flagged in the comments as needing to be replaced with a real calibration for your camera. Until then, range/pose numbers won't be trustworthy.
- **`TARGET_TIMEOUT_MICROS = 250_000`**: how old a detection can be before it's treated as stale/"no target."
- **`DEFAULT_DESIRED_TAG_ID = -1`**: accept whatever tag is closest, rather than requiring one specific ID.

### 2. `VisionSubsystem.java`
This is where the actual work happens. It's now a real WPILib `SubsystemBase` that:

- **Opens a USB camera** (`CameraServer.startAutomaticCapture(...)`) at startup and configures resolution/FPS from `VisionConstants`.
- **Runs a background thread** (`processFrames()`) that continuously:
  1. Grabs a frame.
  2. Converts it to grayscale.
  3. Runs the AprilTag detector on it.
  4. For every detected tag (optionally filtered to `desiredTagId`), estimates its 3D pose relative to the camera and computes range/bearing/yaw.
  5. Keeps only the **closest** matching tag as `latestTarget`.
- **`periodic()`** runs on the main robot loop but does *no* camera work itself — it just calls `telemetry.update()` to publish whatever the background thread most recently found. This keeps the robot loop fast/safe.
- **Public methods now do real things instead of returning hardcoded values:**
  - `hasTargets()` → true only if a target was seen within the last `TARGET_TIMEOUT_MICROS`.
  - `getBestTagID()` → the ID of that fresh target, or `-1` if none.
  - `getEstimatedPose()` → converts the target's range/bearing/yaw into a `Pose2d` (X = forward, Y = left, rotation = tag yaw). This is **camera-relative**, not field-relative — there's no tag field layout wired in yet, so it's "where is the tag relative to me," not "where am I on the field."
  - `getTarget()` → new method exposing the full `Target` record (id, range, bearing, yaw, timestamp) if you need more than the three original getters.
- **Implements `AutoCloseable`** so the camera, capture sink, detector, and worker thread can all be shut down cleanly (mirrors the standalone `AprilTagVision` class from your other file).

### 3. `VisionTelemetry.java`
Went from empty to actually publishing to NetworkTables, following the same pattern as your existing `ElasticTelemetry.java`:
- Publishes under `Elastic/Vision Telemetry`: `Has Target`, `Best Tag ID`, `Range (m)`, `Bearing (deg)`, `Yaw (deg)`.
- `update()` is called automatically from `VisionSubsystem.periodic()` every loop — you don't need to call it yourself.

### 4. `RobotContainer.java`
One-line-of-substance change: it now actually **constructs a `VisionSubsystem`** (`visionSubsystem = new VisionSubsystem();`) and exposes it via `getVisionSubsystem()`. Before, nothing in the codebase ever created one, so the class — however complete — would never have run.

## What this gets you right now

- A camera-backed subsystem that's part of the robot and updates every loop.
- Real telemetry you can watch live in Elastic while testing.
- Accurate `hasTargets()` / `getBestTagID()` / `getEstimatedPose()` based on live detections, not hardcoded stubs.

## What's still missing / next steps

1. **Camera calibration** — replace the placeholder `FX/FY/CX/CY` with values from an actual calibration of your camera at 640x480.
2. **Nothing consumes it yet** — `RobotContainer` creates the subsystem, but no command reads from it (e.g., there's no "turn to face the tag" or "drive to tag" command bound to it). It's live, but not driving any robot behavior yet.
3. **No field-relative pose** — `getEstimatedPose()` is camera-relative only. Getting a field-relative robot pose would require a tag layout (known tag positions on the field) and combining that with the camera-to-tag transform.

If you want, I can build the "auto-align to tag" command next, similar in spirit to your standalone `AprilTagDriveAutoMode`, adapted to this command-based swerve template.

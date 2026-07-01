# Viewing NetworkTables Telemetry with OutlineViewer or Elastic

This project publishes Kiwi drive telemetry through NetworkTables. Use this guide when you want to verify that the robot is actually sending values to the Driver Station laptop.

This guide describes the macOS workflow tested with the WPILib 2027 alpha tools. The exact install folder may be different on another computer. In the examples below, replace:

```text
<WPILIB_INSTALL>
```

with the folder where WPILib is installed on your machine. For a default user-folder install, that may look like:

```text
~/wpilib/2027_alpha5
```

The Kiwi drive teleop mode to run is:

```text
Kiwi Drive + NT Telemetry
```

The Kiwi drive NetworkTables table to look for is:

```text
KiwiDriveExample
```

There is also a telemetry-only teleop mode:

```text
NT Telemetry Example
```

That mode publishes joystick numbers, booleans, and strings under:

```text
TelemetryExample
```

In OutlineViewer and Elastic, this appears as a tree/table item. You do not type the leading slash from paths like `/KiwiDriveExample/xInput`.

## If WPILib Start Tool Does Not Work

Sometimes VS Code's WPILib `Start Tool` command may say it cannot find tools, try to install tools through Gradle, restart, and then still fail. If that loop happens, skip VS Code's tool launcher for this check.

Open the dashboard tool directly from the WPILib install folder.

For OutlineViewer:

```text
<WPILIB_INSTALL>/tools/OutlineViewer.app
```

For Elastic:

```text
<WPILIB_INSTALL>/elastic/
```

In the working Elastic setup, the `elastic` folder contained:

```text
Elastic-WPILib-macOS.tar.gz
elastic_dashboard
```

If `elastic_dashboard` is not present, unpack `Elastic-WPILib-macOS.tar.gz`, then double-click `elastic_dashboard`.

If `OutlineViewer.app` is not present in `tools/`, check the artifacts folder:

```text
<WPILIB_INSTALL>/tools/artifacts/
```

There may be an archive named like:

```text
OutlineViewer-2027.0.0-alpha-6-osxuniversal.zip
```

Unpack that archive only if the app is missing from `tools/`. In the working setup, `tools/OutlineViewer.app` was already available and could be opened directly.

## Connecting OutlineViewer

1. Open `OutlineViewer.app`.
2. In the menu bar, choose:

```text
Options -> Settings
```

3. Use these settings:

```text
Mode: Client
Team/IP: your configured team number
Port: 5810 / Default
Network Identity: outlineviewer
Set Address from DS: checked
```

For the tested setup, `Team/IP` was set to the team number configured during robot setup. The example screenshot used:

```text
Team/IP: 666
```

Use your own configured team number if it is different.

4. Click:

```text
Apply
```

OutlineViewer should connect after applying the settings.

## Finding the Kiwi Telemetry

After the robot code is deployed and connected:

1. In Driver Station, select:

```text
Kiwi Drive + NT Telemetry
```

2. Enable TeleOp.
3. Move the gamepad joysticks.
4. In OutlineViewer, look in the NetworkTables tree for:

```text
Transitory Values
```

5. Expand the Kiwi drive table:

```text
KiwiDriveExample
```

6. You should see values like:

```text
xInput
yInput
rotationInput
frontLeftOutput
frontRightOutput
backOutput
```

The input values should change as the joysticks move. The output values should change after the Kiwi drive math runs.

## Finding the Telemetry-Only Example

After the robot code is deployed and connected:

1. In Driver Station, select:

```text
NT Telemetry Example
```

2. Enable TeleOp.
3. Move the gamepad joysticks and press the south face button or bumper buttons.
4. In OutlineViewer, look in the NetworkTables tree for:

```text
Transitory Values
```

5. Expand the telemetry table:

```text
TelemetryExample
```

6. You should see values like:

```text
leftX
leftY
rightX
leftTrigger
rightTrigger
leftStickMagnitude
southFaceButton
leftBumperButton
rightBumperButton
leftStickActive
driveDirectionStatus
buttonStatus
```

The number values show joystick axes and triggers. The boolean values show true/false button or stick states. The string values show readable status messages created by simple `if` statements in the teleop code.

## Connecting Elastic

1. Open the WPILib install folder:

```text
<WPILIB_INSTALL>
```

2. Open the `elastic` folder.
3. If needed, unpack:

```text
Elastic-WPILib-macOS.tar.gz
```

4. Double-click:

```text
elastic_dashboard
```

5. In Elastic, click:

```text
Settings
```

6. On the `Network` tab, use the robot/team settings. The tested setup used:

```text
Team Number: 666
IP Address Mode: Driver Station
Target Server: Robot Code
```

Use your own configured team number if it is different.

7. Confirm that Elastic shows a connected NetworkTables status at the bottom of the window. In the tested setup, Elastic showed:

```text
Network Tables: Connected (172.30.0.1)
Team 666
```

## Adding the Kiwi Telemetry Widget in Elastic

After Elastic is connected and either NetworkTables teleop mode is enabled:

1. Click:

```text
+ Add Widget
```

2. Stay on the `Network Tables` tab.
3. Find:

```text
KiwiDriveExample
```

or:

```text
TelemetryExample
```

4. Drag the table onto the main grid.
5. Elastic should create a widget that shows:

```text
backOutput
frontLeftOutput
frontRightOutput
rotationInput
xInput
yInput
```

For `TelemetryExample`, Elastic should show number, boolean, and string values such as:

```text
leftX
southFaceButton
driveDirectionStatus
buttonStatus
```

6. Move the joystick while TeleOp is enabled. The displayed values should update live.

## What These Values Mean

| Value | Meaning |
| --- | --- |
| `xInput` | Left stick X after deadband |
| `yInput` | Left stick Y after deadband, with forward made positive |
| `rotationInput` | Right stick X after deadband |
| `frontLeftOutput` | Computed throttle sent to motor 3 |
| `frontRightOutput` | Computed throttle sent to motor 1 |
| `backOutput` | Computed throttle sent to motor 2 |
| `leftStickMagnitude` | Distance of the left stick from center |
| `southFaceButton` | Whether the south face button is currently pressed |
| `leftStickActive` | Whether the left stick is far enough from center to count as active |
| `driveDirectionStatus` | String message describing the left stick direction |
| `buttonStatus` | String message describing selected button states |

## Troubleshooting

If `KiwiDriveExample` or `TelemetryExample` does not appear:

- Make sure the code has been deployed after the NetworkTables changes.
- Make sure Driver Station is running `Kiwi Drive + NT Telemetry` or `NT Telemetry Example`, not the default teleop mode.
- Make sure TeleOp is enabled. The values are updated from `periodic()`.
- Move the joysticks so the values change.
- Reopen `Options -> Settings`, confirm the team number, keep `Set Address from DS` checked, and click `Apply` again.
- In Elastic, reopen `Settings`, confirm the team number, use `IP Address Mode: Driver Station`, use `Target Server: Robot Code`, and check the bottom status bar for `Network Tables: Connected`.
- If you are testing a local simulation instead of the real robot/SystemCore connection, try `localhost` in `Team/IP`. For the tested robot setup, the configured team number worked.

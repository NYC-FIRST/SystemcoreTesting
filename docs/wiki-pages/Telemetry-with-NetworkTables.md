# Telemetry with NetworkTables

This page covers the code structure for telemetry. A separate wiki page can later
cover the full OutlineViewer and Elastic setup flow.

## FTC Pattern

```java
telemetry.addData("leftX", leftX);
telemetry.addData("button", pressed);
telemetry.update();
```

FTC telemetry usually appears as lines of text on the Driver Station screen.

## SystemCore Pattern

This project uses NetworkTables publishers:

```java
private final NetworkTable telemetryExampleTable =
    NetworkTableInstance.getDefault().getTable("TelemetryExample");

private final DoublePublisher leftXPublisher =
    telemetryExampleTable.getDoubleTopic("leftX").publish();

leftXPublisher.set(leftX);
```

## The Two-Step Structure

1. Publishers are created once as fields.
2. `.set(value)` is called every loop from `periodic()`.

Getting the table and topic happens when the OpMode object is constructed. Calling
`.set(...)` is the repeated update that most closely maps to FTC
`telemetry.addData(...)`.

## Publisher Types

| Publisher type | For | Example topic in this project |
| --- | --- | --- |
| `DoublePublisher` | numbers | `leftX`, `leftStickMagnitude` |
| `BooleanPublisher` | true/false | `leftBumperButton`, `leftStickActive` |
| `StringPublisher` | text | `driveDirectionStatus`, `buttonStatus` |

## FTC to NetworkTables Mapping

| FTC telemetry | NetworkTables here |
| --- | --- |
| `telemetry.addData("leftX", leftX)` | `leftXPublisher.set(leftX)` |
| `telemetry.update()` | values update when publishers call `.set(...)` |
| telemetry caption | topic name |
| telemetry screen | OutlineViewer or Elastic |

## Dashboard Tools

NetworkTables values can be viewed by connected dashboard tools:

- OutlineViewer shows the raw tree of tables and values.
- Elastic lets students arrange values as dashboard widgets.

The current repo guide
[`OUTLINEVIEWER_AND_ELASTIC_NETWORKTABLES_README.md`](https://github.com/NYC-FIRST/SystemcoreTesting/blob/main/testprojects/First%20Test%20June%2025%202026/OUTLINEVIEWER_AND_ELASTIC_NETWORKTABLES_README.md)
contains the detailed setup flow. That can become a separate wiki page later.

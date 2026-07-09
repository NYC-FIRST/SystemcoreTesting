# A301 Manual Firmware Update Via Terminal

## Context

This note documents a working terminal-based firmware update flow for an A301 connected through Motioncore and managed by REV Hardware Client 2 (RHC2) running directly on Systemcore.

The original problem was discovered while testing an A301 Java opmode with REVLib `2027.0.0-alpha-3`. The A301 was detected, but REVLib refused to run it because the firmware was too old:

```text
The firmware version of Bus #5 A301 #3 is too old and must be updated to 27.0.0-prerelease.11 or later.
```

For this test, the A301 was updated using:

```text
a301_27_0_0_prerelease_11.dfu
```

This was intentionally the `.11` firmware, not `.14`, because the immediate goal was to satisfy the minimum firmware required by REVLib `2027.0.0-alpha-3` and verify the A301 test opmode.

## First Install RHC2 on Systemcore

Before any of the terminal commands below will work, REV Hardware Client 2 must be installed on the Systemcore itself. The `curl` commands in this guide talk to the RHC2 backend service running on Systemcore at port `2714`; if RHC2 is not installed and running there, the terminal API will not exist.

For this test, RHC2 was installed directly on Systemcore by downloading the RHC2 IPK and installing it through the Systemcore dashboard:

1. Download the RHC2 IPK for Systemcore.
2. Open the Systemcore dashboard in a browser.
3. Use **Add Package**.
4. Select the downloaded RHC2 IPK.
5. Wait for the package to install and start.

The version used in the working test flow was RHC2 IPK `1.2.1`.

After installation, RHC2 should be reachable from the laptop at:

```text
http://<systemcore-ip>:2714
```

This is why the terminal workflow can access RHC2 with `curl`.

## Requirements

- Systemcore powered and reachable from the laptop.
- Motioncore connected and powered.
- A301 connected to Motioncore.
- REV Hardware Client 2 installed and running on Systemcore.
- A301 `.dfu` firmware file downloaded locally.
- Terminal access on the laptop.

## Network Notes

USB is recommended for firmware flashing because it is more stable.

This workflow was tested successfully over the Systemcore Wi-Fi access point. When connected over Wi-Fi, the Systemcore address was:

```bash
HOST=http://172.30.0.1:2714
```

For USB from macOS or Linux, the address is usually:

```bash
HOST=http://172.27.0.1:2714
```

For Windows over USB, the address is usually:

```bash
HOST=http://172.26.0.1:2714
```

## Set Variables

Set these first. Replace the `DFU` path with the local path to the downloaded A301 firmware file.

```bash
HOST=http://172.30.0.1:2714
BUS=Y2FuX2Qw
DFU="/path/to/a301_27_0_0_prerelease_11.dfu"
```

`BUS=Y2FuX2Qw` is the URL-safe base64 descriptor for `can_d0`, which corresponds to Motioncore D0.

## Claim Leader Access

RHC2 requires a leader session for firmware update actions.

Close any open RHC2 browser tabs before claiming leader from the terminal. If a browser tab already has leader, the terminal may only receive a `READER` token and update commands will return `401 Unauthorized`.

Claim leader:

```bash
curl -i -c rhc2.cookies -X POST \
  "$HOST/v1/login/claim?claimLeader=true"
```

Good response:

```text
HTTP/1.1 200 OK
...
...-LEADER
```

If the token ends in `-READER`, close RHC2 browser tabs or restart RHC2/Systemcore, then try again.

Verify leader:

```bash
curl -i -b rhc2.cookies "$HOST/v1/login/is-leader"
```

Expected:

```text
HTTP/1.1 200 OK

true
```

## Find the A301 UUID

List REV devices on the Motioncore bus:

```bash
curl -i -b rhc2.cookies "$HOST/v1/bus/$BUS/rev/devices"
```

Find the A301 entry in the response. It should include fields such as CAN ID, type, and UUID.

Copy the A301 UUID and set it:

```bash
UUID="PASTE_A301_UUID_HERE"
```

In the test case that produced this note, the A301 was using default CAN ID `3`.

## Create a Firmware Update Session

```bash
curl -i -b rhc2.cookies -X POST "$HOST/v1/bus/$BUS/revup/update/can/" \
  -H "Content-Type: application/json" \
  -d "{\"uuids\":[\"$UUID\"],\"isUpdatingApp\":true}"
```

Good response:

```text
HTTP/1.1 200 OK
content-length: 1

2
```

The number in the response body is the session ID. Use whatever number RHC2 returns:

```bash
SESSION=2
```

For example, if the response body is `0`, use `SESSION=0`. If the response body is `1`, use `SESSION=1`.

## Upload the Firmware File

```bash
curl -i -b rhc2.cookies -X POST "$HOST/v1/bus/$BUS/revup/update/can/$SESSION/upload" \
  -F "file=@$DFU"
```

Good response:

```text
HTTP/1.1 204 No Content
```

`204 No Content` is okay. It means the upload succeeded and there is intentionally no response body.

## Start Flashing

```bash
curl -i -b rhc2.cookies -X POST "$HOST/v1/bus/$BUS/revup/update/can/$SESSION/start"
```

Good responses may include:

```text
HTTP/1.1 200 OK
```

or:

```text
HTTP/1.1 202 Accepted
```

or:

```text
HTTP/1.1 204 No Content
```

After this point, do not power off the robot, Systemcore, Motioncore, or A301.

## Check Progress

```bash
curl -i -b rhc2.cookies "$HOST/v1/bus/$BUS/revup/update/can/$SESSION"
```

Repeat until the status reports completed or the percentage reaches `100`.

Good statuses may include:

```text
READY_TO_FLASH
FLASHING
COMPLETED
```

After completion, wait a few seconds, then power cycle Systemcore, Motioncore, and the A301.

## Common HTTP Responses

`200 OK`
: Success. For session creation, the response body may be just the session number.

`204 No Content`
: Success with no response body. This was seen after uploading the DFU file.

`401 Unauthorized`
: The terminal is not authenticated as leader. Reclaim leader and make sure every update command includes `-b rhc2.cookies`.

`404 Not Found`
: Wrong bus descriptor, wrong UUID, wrong session ID, or wrong endpoint.

`422`
: RHC2 sees the request, but the session or device state is wrong. For example, this may happen if starting before uploading.

`500`
: RHC2 backend error.

## Notes

- The `%` shown by zsh after some `curl` outputs is not an error. It means the response did not end with a newline.
- Keep the robot powered throughout flashing.
- USB is preferred for reliability, but this workflow worked over Systemcore Wi-Fi.
- This was tested with REVLib `2027.0.0-alpha-3` and A301 firmware `27.0.0-prerelease.11`.

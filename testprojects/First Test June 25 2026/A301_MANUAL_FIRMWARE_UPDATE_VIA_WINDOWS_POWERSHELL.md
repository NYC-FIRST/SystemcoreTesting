# A301 Manual Firmware Update Via Windows PowerShell

This guide documents a Windows PowerShell path for updating A301 firmware through REV Hardware Client 2 (RHC2) running directly on Systemcore.

It is adapted from a working terminal-based troubleshooting flow where an A301 was visible on Motioncore but could not be driven from REVLib until its firmware was updated.

## At a Glance

| Item | Value used in the original test |
| --- | --- |
| Device | REV A301 |
| Controller path | Systemcore + Motioncore |
| Motioncore port | `can_d0` / Motioncore D0 |
| A301 CAN ID | Default CAN ID `3` |
| REVLib version | `2027.0.0-alpha-3` |
| RHC2 install | RHC2 IPK `1.2.1` installed on Systemcore |
| Firmware file | `a301_27_0_0_prerelease_11.dfu` |
| Original connection used | Systemcore Wi-Fi AP |
| Recommended flashing connection | USB, when practical |

> [!IMPORTANT]
> RHC2 must be installed on the Systemcore itself before any of these PowerShell commands will work. The commands talk to the RHC2 backend service on Systemcore port `2714`; without the IPK-installed Systemcore RHC2 service, there is nothing for `curl.exe` to reach.

## Why This Was Needed

The A301 test opmode reached the device, but REVLib refused to run it because the firmware was below the required minimum:

```text
The firmware version of Bus #5 A301 #3 is too old and must be updated to 27.0.0-prerelease.11 or later.
```

For the original test, the A301 was updated to:

```text
27.0.0-prerelease.11
```

The original flow intentionally used `.11`, not `.14`, because the immediate goal was to satisfy the minimum firmware required by REVLib `2027.0.0-alpha-3` and validate the A301 test opmode.

## Install RHC2 on Systemcore First

Install REV Hardware Client 2 directly on Systemcore before using this guide.

1. Download the RHC2 IPK for Systemcore.
2. Open the Systemcore dashboard in a browser.
3. Select **Add Package**.
4. Choose the downloaded RHC2 IPK.
5. Wait for the package to install and the RHC2 service to start.

The working test flow used RHC2 IPK `1.2.1`.

After installation, RHC2 should be reachable at:

```text
http://<systemcore-ip>:2714
```

## Safety Notes

> [!WARNING]
> Keep Systemcore, Motioncore, and the A301 powered during the firmware update. Do not power cycle or disconnect CAN/power while flashing is in progress.

> [!NOTE]
> USB is preferred for firmware flashing because it is more stable. The original workflow was tested successfully over the Systemcore Wi-Fi access point, but USB is the safer default if available.

## Network Addresses

Choose the `$RhcHost` value for your connection method.

| Connection | Typical RHC2 host |
| --- | --- |
| USB from Windows | `http://172.26.0.1:2714` |
| Systemcore Wi-Fi AP | `http://172.30.0.1:2714` |
| USB from macOS/Linux | `http://172.27.0.1:2714` |

The commands below default to USB from Windows. If you are using the Systemcore Wi-Fi AP instead, set `$RhcHost` to `http://172.30.0.1:2714`.

## PowerShell Setup

Open Windows PowerShell or PowerShell 7.

Use `curl.exe`, not `curl`, in these commands. On some Windows systems, `curl` by itself can resolve to a PowerShell alias instead of the real curl executable.

Replace `$Dfu` with the local path to your downloaded firmware file.

```powershell
$RhcHost = "http://172.26.0.1:2714"
$Bus = "Y2FuX2Qw"
$Dfu = "C:\Users\YOUR_USER\Downloads\a301_27_0_0_prerelease_11.dfu"
```

`$Bus = "Y2FuX2Qw"` is the URL-safe base64 descriptor for `can_d0`, which corresponds to Motioncore D0.

> [!NOTE]
> Do not use `$Host` as the variable name in PowerShell. `$Host` is a built-in PowerShell variable, so this guide uses `$RhcHost`.

## Optional: Confirm RHC2 Is Reachable

```powershell
curl.exe -i "$RhcHost/v1/login/is-leader"
```

If RHC2 is reachable, the command should return an HTTP response. It may return `401 Unauthorized` before login; that still means the service is responding.

If the command cannot connect, recheck the USB/Wi-Fi connection and the `$RhcHost` address.

## Step 1: Claim RHC2 Leader Access

RHC2 protects write operations with a leader session. Firmware update commands will return `401 Unauthorized` unless PowerShell has a leader cookie.

Close open RHC2 browser tabs before claiming leader from PowerShell. If a browser tab already holds leader, PowerShell may receive a `READER` token instead.

```powershell
curl.exe -i -c rhc2.cookies -X POST "$RhcHost/v1/login/claim?claimLeader=true"
```

Expected leader response:

```text
HTTP/1.1 200 OK
Set-Cookie: REVUI-Auth=<token>-LEADER;...

<token>-LEADER
```

If the token ends in `-READER`, close RHC2 browser tabs or restart RHC2/Systemcore, then claim leader again.

Verify leader access:

```powershell
curl.exe -i -b rhc2.cookies "$RhcHost/v1/login/is-leader"
```

Expected response:

```text
HTTP/1.1 200 OK

true
```

## Step 2: Find the A301 UUID

List REV devices on Motioncore D0:

```powershell
curl.exe -i -b rhc2.cookies "$RhcHost/v1/bus/$Bus/rev/devices"
```

Find the A301 entry in the response and copy its `uuid`.

Then set:

```powershell
$Uuid = "PASTE_A301_UUID_HERE"
```

In the original test, the A301 was on default CAN ID `3`.

> [!TIP]
> The UUID is not the CAN ID. The CAN ID may be `3`, but the update API needs the full UUID returned by the RHC2 device list.

## Step 3: Create a Firmware Update Session

Build the JSON request body:

```powershell
$Body = @{
  uuids = @($Uuid)
  isUpdatingApp = $true
} | ConvertTo-Json -Compress
```

Create the update session:

```powershell
curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/" -H "Content-Type: application/json" -d $Body
```

Expected response:

```text
HTTP/1.1 200 OK
content-length: 1

2
```

The response body is the session number. Save it:

```powershell
$Session = "2"
```

If your response body is `0`, use `$Session = "0"`. If it is `1`, use `$Session = "1"`, and so on.

## Step 4: Upload the DFU File

```powershell
curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/$Session/upload" -F "file=@$Dfu"
```

Expected response:

```text
HTTP/1.1 204 No Content
```

`204 No Content` is success here. It means the file uploaded and the server intentionally returned no response body.

## Step 5: Start Flashing

```powershell
curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/$Session/start"
```

Good responses may include:

```text
HTTP/1.1 200 OK
```

```text
HTTP/1.1 202 Accepted
```

```text
HTTP/1.1 204 No Content
```

> [!CAUTION]
> After this command succeeds, assume flashing is active. Keep everything powered and connected until the status reports completion.

## Step 6: Check Progress

```powershell
curl.exe -i -b rhc2.cookies "$RhcHost/v1/bus/$Bus/revup/update/can/$Session"
```

Repeat the progress command until the update reports completion or reaches `100`.

Common good statuses include:

```text
READY_TO_FLASH
FLASHING
COMPLETED
```

When complete:

1. Wait a few seconds.
2. Power cycle Systemcore, Motioncore, and the A301.
3. Rerun the A301 test opmode.
4. Confirm the old firmware error is gone.

## Expected Response Reference

| Response | Meaning | Action |
| --- | --- | --- |
| `200 OK` | Success. Session creation may return only the session number in the body. | Continue. |
| `202 Accepted` | Request accepted and queued/started. | Continue monitoring progress. |
| `204 No Content` | Success with no response body. Seen after DFU upload. | Continue. |
| `401 Unauthorized` | PowerShell is not authenticated as leader. | Reclaim leader and ensure commands include `-b rhc2.cookies`. |
| `404 Not Found` | Wrong bus descriptor, UUID, session ID, or endpoint. | Recheck `$Bus`, `$Uuid`, `$Session`, and URL. |
| `422` | Request reached RHC2, but session/device state is invalid. | Check command order: create session, upload, then start. |
| `500` | RHC2 backend error. | Restart RHC2/Systemcore and retry; collect logs for an issue report. |

## Troubleshooting

### The Login Token Ends in `-READER`

Another RHC2 client is probably leader. Close browser tabs that have RHC2 open, or restart the RHC2 service/Systemcore, then run the leader claim command again.

### Session Creation Returns a Number Other Than `0`

That is normal. The returned number is the session ID. Use the number RHC2 returned:

```powershell
$Session = "<returned-number>"
```

### Upload Returns `204 No Content`

That is normal and was observed in the successful update flow.

### PowerShell Says `curl` Is Not Behaving Like curl

Use `curl.exe` exactly as shown in this guide. `curl` without `.exe` may be treated as a PowerShell alias on some Windows systems.

### The DFU Path Has Spaces

Keep the `-F` argument quoted:

```powershell
curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/$Session/upload" -F "file=@$Dfu"
```

For example:

```powershell
$Dfu = "C:\Users\YOUR_USER\Downloads\firmware files\a301_27_0_0_prerelease_11.dfu"
```

### RHC2 Downloads UI Crashes

This PowerShell flow avoids the Downloads UI. It uses a local `.dfu` file and the RHC2 backend API directly.

## Complete PowerShell Command Template

```powershell
$RhcHost = "http://172.26.0.1:2714"
$Bus = "Y2FuX2Qw"
$Dfu = "C:\Users\YOUR_USER\Downloads\a301_27_0_0_prerelease_11.dfu"

curl.exe -i -c rhc2.cookies -X POST "$RhcHost/v1/login/claim?claimLeader=true"

curl.exe -i -b rhc2.cookies "$RhcHost/v1/login/is-leader"

curl.exe -i -b rhc2.cookies "$RhcHost/v1/bus/$Bus/rev/devices"

$Uuid = "PASTE_A301_UUID_HERE"

$Body = @{
  uuids = @($Uuid)
  isUpdatingApp = $true
} | ConvertTo-Json -Compress

curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/" -H "Content-Type: application/json" -d $Body

$Session = "PASTE_RETURNED_SESSION_NUMBER_HERE"

curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/$Session/upload" -F "file=@$Dfu"

curl.exe -i -b rhc2.cookies -X POST "$RhcHost/v1/bus/$Bus/revup/update/can/$Session/start"

curl.exe -i -b rhc2.cookies "$RhcHost/v1/bus/$Bus/revup/update/can/$Session"
```

## Test Notes

- Original flow tested with REVLib `2027.0.0-alpha-3`.
- Original flow tested with RHC2 IPK `1.2.1` installed directly on Systemcore.
- Original flow tested with A301 firmware `27.0.0-prerelease.11`.
- Original flow tested over Systemcore Wi-Fi AP.
- This Windows version defaults to USB from Windows at `http://172.26.0.1:2714`.
- USB is recommended for firmware flashing when available.

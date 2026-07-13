# RHC2 on Systemcore Dashboard Crashes While Updating A301 Firmware

## Summary

We were trying to update firmware on an A301 so it could be used with REVLib `2027.0.0-alpha-3`.

REVLib could detect the A301, but it refused to run it because the firmware was too old and required at least:

```text
27.0.0-prerelease.11
```

We installed REV Hardware Client 2 on Systemcore using the IPK through the Systemcore dashboard. RHC2 opened in the dashboard, but the UI crashed before we could complete the firmware update through the interface.

We were eventually able to update the A301 by bypassing the RHC2 frontend and using the RHC2 backend API from the terminal.

## Environment

| Item | Value |
| --- | --- |
| Hardware | Systemcore + Motioncore + A301 |
| A301 connection | Motioncore D0 / `can_d0` |
| A301 CAN ID | Default CAN ID `3` |
| REVLib | `2027.0.0-alpha-3` |
| Target A301 firmware | `27.0.0-prerelease.11` |
| Firmware file used | `a301_27_0_0_prerelease_11.dfu` |
| RHC2 install method | IPK installed through the Systemcore dashboard |
| RHC2 IPK version used | `1.2.1` |
| Systemcore connection used | Wi-Fi AP |
| RHC2 address used | `http://172.30.0.1:2714` |

## What We Tried

1. We wrote a small A301 test opmode using REVLib `2027.0.0-alpha-3`.
2. The A301 was detected on `Bus #5` with CAN ID `3`.
3. REVLib reported that the A301 firmware was too old and required `27.0.0-prerelease.11` or later.
4. We first tried using REV Hardware Client 2 on a desktop computer.
5. We then realized that the desktop update path requires a supported bridge device, so we moved to the Systemcore path.
6. We installed the RHC2 IPK through the Systemcore dashboard.
7. RHC2 opened from the dashboard, but the UI crashed while loading.

## What Went Wrong

The RHC2 frontend appeared to claim leader successfully, but then several download-related backend calls failed with HTTP `500`. After that, the frontend threw:

```text
Uncaught TypeError: o.map is not a function
```

From the console log, it looked like the UI expected one of the download/channel endpoints to return an array, but the backend returned an error or non-array response instead.

Because the frontend crashed, we could not use the RHC2 dashboard UI to update the A301 firmware.

## Browser Console Log

```text
layout-d7d59cb576692695.js:1 Was leader? false
layout-d7d59cb576692695.js:1 Login token: afe476de-2d1b-412d-bea8-7fbad9f2c24b-LEADER
layout-d7d59cb576692695.js:1 Is leader? true
layout-d7d59cb576692695.js:1 Selected device: null
:2714/v1/download/sync:1  Failed to load resource: the server responded with a status of 500 (Internal Server Error)
:2714/v1/download/channel:1  Failed to load resource: the server responded with a status of 500 (Internal Server Error)
layout-d7d59cb576692695.js:1 Loaded theme from settings: default
layout-d7d59cb576692695.js:1 Uncaught TypeError: o.map is not a function
    at eO (layout-d7d59cb576692695.js:1:24762)
    at l9 (4bd1b696-da2e47cdb2de7720.js:1:51128)
    at o_ (4bd1b696-da2e47cdb2de7720.js:1:70988)
    at oq (4bd1b696-da2e47cdb2de7720.js:1:82018)
    at ik (4bd1b696-da2e47cdb2de7720.js:1:114680)
    at 4bd1b696-da2e47cdb2de7720.js:1:114525
    at ib (4bd1b696-da2e47cdb2de7720.js:1:114533)
    at iu (4bd1b696-da2e47cdb2de7720.js:1:111616)
    at iX (4bd1b696-da2e47cdb2de7720.js:1:132932)
    at MessagePort.w (261-92ab24d9fd8e2db1.js:1:145824)
:2714/v1/download/:1  Failed to load resource: the server responded with a status of 500 (Internal Server Error)
:2714/favicon.ico:1  Failed to load resource: the server responded with a status of 404 (Not Found)
```

## Workaround That Worked

Even though the RHC2 frontend crashed, the RHC2 backend API was still reachable on Systemcore port `2714`.

We updated the A301 manually from the terminal by:

1. Claiming RHC2 leader access with the backend login endpoint.
2. Querying the REV device list endpoint to find the A301 UUID.
3. Creating a firmware update session.
4. Uploading `a301_27_0_0_prerelease_11.dfu`.
5. Starting the update session.
6. Polling the session status.

This successfully updated the A301 firmware to the minimum version needed for REVLib `2027.0.0-alpha-3`.

We documented the terminal workaround in our fork as:

```text
A301_MANUAL_FIRMWARE_UPDATE_VIA_TERMINAL.md
```

## Question

Are we doing something wrong with the RHC2-on-Systemcore setup, or is there something else required for the RHC2 dashboard UI to work correctly when updating A301 firmware?

The backend API worked, so RHC2 was installed and running on Systemcore. The problem we hit was specifically that the dashboard UI crashed after the download-related endpoints returned HTTP `500`.

We would like to know whether this is expected behavior in the current alpha setup, whether there is an installation/configuration step we missed, or whether there is another recommended way to update A301 firmware through RHC2 on Systemcore.

# Beta 12 Update Issue and Resolution

## Summary

I experienced repeated failures while trying to update my SystemCore to **Beta 12**. After trying several different approaches, the update eventually succeeded, but I'm not sure which specific step resolved the issue.

## What Happened

- Initially, Beta 12 would not install successfully.
- I attempted updating through both the **web interface** and the **Hardware Manager**, but neither method consistently worked.
- I repeatedly installed and reinstalled the Beta 12 image.
- I downgraded to **Beta 11** and then upgraded back to **Beta 12**.
- I also ran some terminal commands (I don't remember the exact commands).
- After these steps, Beta 12 finally installed successfully and the SystemCore worked normally.

## Possible Causes

Since multiple changes were made before the update succeeded, it's difficult to determine the exact root cause. These are the most likely explanations:

### 1. The previous firmware/image wasn't fully replaced

The updater reported success while the image wasn't completely written. Reinstalling the firmware multiple times may have eventually overwritten a corrupted or partially written installation. As well as running commands in the terminal delating past instances of similar files.

### 2. The terminal commands reset or cleaned 

The terminal commands may have reset cached data, restarted services, fixed permissions, or otherwise placed the SystemCore into a clean state that allowed the installation to complete successfully.

### 3. Hardware Manager recognized the board correctly after a reboot

Disconnecting and reconnecting the device, or rebooting after running terminal commands, may have allowed the updater to properly detect and communicate with the SystemCore.

### 4. Beta 11 updated an intermediate component

Installing Beta 11 before upgrading back to Beta 12 may have updated an intermediate component (such as the bootloader or another firmware component), allowing the Beta 12 installation to complete successfully.

## Additional Notes

Because several troubleshooting steps were performed before the update finally worked, I can't confidently identify which one actually resolved the problem. The successful installation may have been the result of a combination of these steps rather than a single fix.

Hopefully this information helps narrow down the cause if others encounter the same issue.
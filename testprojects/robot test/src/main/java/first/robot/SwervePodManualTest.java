// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package first.robot;

import org.wpilib.driverstation.DefaultUserControls;
import org.wpilib.driverstation.Gamepad;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;
import org.wpilib.system.Timer;

@Teleop(
    name = "Swerve Pod Method Test",
    group = "Test",
    description = "Hold-to-run A301 D10 steering method test"
)
public class SwervePodManualTest extends PeriodicOpMode {

    // ============================================================
    // TEST VALUES
    // ============================================================

    private static final double THROTTLE_POWER = 0.20;
    private static final double VELOCITY_RPM = 500.0;

    private static final double DEADBAND = 0.08;

    private final Robot robot;
    private final DefaultUserControls userControls;
    private final Timer statusTimer = new Timer();

    public SwervePodManualTest(
            Robot robot,
            DefaultUserControls userControls) {

        this.robot = robot;
        this.userControls = userControls;
    }

    @Override
    public void start() {

        robot.disableA301s();
        robot.swerveDrive.disable();
        robot.swerveTheta.disable();

        statusTimer.restart();

        System.out.println("==============================");
        System.out.println(" A301 D10 METHOD TEST");
        System.out.println("==============================");
        System.out.println("RIGHT STICK X = THROTTLE");
        System.out.println("LEFT STICK Y  = VELOCITY");
        System.out.println("LEFT STICK X  = CLOSED LOOP PLACEHOLDER");
        System.out.println("START         = DISABLE");
        System.out.println("==============================");
    }

    @Override
    public void periodic() {

        Gamepad gamepad =
            userControls.getGamepad(0);

        // ========================================================
        // READ CONTROLS
        // ========================================================

        double rightX =
            gamepad.getRightX();

        double leftY =
            -gamepad.getLeftY();

        double leftX =
            gamepad.getLeftX();

        boolean throttleActive =
            Math.abs(rightX) > DEADBAND;

        boolean velocityActive =
            Math.abs(leftY) > DEADBAND;

        boolean closedLoopTestActive =
            Math.abs(leftX) > DEADBAND;

        // ========================================================
        // PRIORITY
        //
        // Only ONE method controls the motor at a time.
        //
        // Right stick -> throttle
        // Left stick  -> velocity
        // Left X      -> closed-loop placeholder
        // Otherwise   -> disable
        // ========================================================

        if (throttleActive) {

            // ====================================================
            // OPEN LOOP THROTTLE
            // ====================================================

            double throttle =
                rightX * THROTTLE_POWER;

            robot.swerveTheta.setThrottle(
                throttle
            );

            if (statusTimer.advanceIfElapsed(0.25)) {

                System.out.println(
                    "ACTIVE METHOD: setThrottle()"
                    + " | value=" + throttle
                );
            }

        } else if (velocityActive) {

            // ====================================================
            // VELOCITY CONTROL
            // ====================================================

            double velocity =
                leftY * VELOCITY_RPM;

            robot.swerveTheta.setVelocity(
                velocity
            );

            if (statusTimer.advanceIfElapsed(0.25)) {

                System.out.println(
                    "ACTIVE METHOD: setVelocity()"
                    + " | value=" + velocity
                );
            }

        } else if (closedLoopTestActive) {

            // ====================================================
            // CLOSED LOOP / SERVO TEST
            //
            // DO NOT PUT setServo() HERE.
            //
            // Your A301 class does not have setServo().
            // We will replace this with the actual A301
            // closed-loop method once identified.
            // ====================================================

            if (statusTimer.advanceIfElapsed(0.25)) {

                System.out.println(
                    "CLOSED LOOP TEST SELECTED"
                );
            }

        } else {

            // ====================================================
            // NOTHING HELD
            //
            // ALWAYS STOP THE MOTOR.
            // ====================================================

            robot.swerveTheta.disable();
        }

        // ========================================================
        // START = EMERGENCY STOP
        // ========================================================

        if (gamepad.getStartButtonPressed()) {

            System.out.println(
                "EMERGENCY STOP: disable()"
            );

            robot.swerveTheta.disable();
        }
    }

    @Override
    public void end() {

        System.out.println(
            "Swerve Pod Method Test ended"
        );

        robot.swerveTheta.disable();
        robot.swerveDrive.disable();
    }
}
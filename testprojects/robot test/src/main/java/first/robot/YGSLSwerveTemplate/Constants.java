package first.robot.YGSLSwerveTemplate;

import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.SwerveDriveKinematics;

public final class Constants {

    private Constants() {}

    public static final class Drive {

        // ==========================
        // Robot Dimensions (meters)
        // ==========================
        public static final double TRACK_WIDTH = 0.6096;   // 24 in
        public static final double WHEEL_BASE = 0.6096;    // 24 in

        public static final double WHEEL_DIAMETER = 0.1016; // 4 in
        public static final double WHEEL_CIRCUMFERENCE =
                WHEEL_DIAMETER * Math.PI;

        // ==========================
        // Robot Performance
        // ==========================
        public static final double MAX_SPEED = 4.5; // m/s
        public static final double MAX_ANGULAR_SPEED = 2.0 * Math.PI; // rad/s

        public static final double MAX_OPEN_LOOP_THROTTLE = 1.0;

        // 20ms robot loop
        public static final double LOOP_PERIOD = 0.020;

        // ==========================
        // Module Locations
        // ==========================
        public static final Translation2d FRONT_LEFT_LOCATION =
                new Translation2d(
                        WHEEL_BASE / 2.0,
                        TRACK_WIDTH / 2.0);

        public static final Translation2d FRONT_RIGHT_LOCATION =
                new Translation2d(
                        WHEEL_BASE / 2.0,
                        -TRACK_WIDTH / 2.0);

        public static final Translation2d BACK_LEFT_LOCATION =
                new Translation2d(
                        -WHEEL_BASE / 2.0,
                        TRACK_WIDTH / 2.0);

        public static final Translation2d BACK_RIGHT_LOCATION =
                new Translation2d(
                        -WHEEL_BASE / 2.0,
                        -TRACK_WIDTH / 2.0);

        public static final SwerveDriveKinematics KINEMATICS =
                new SwerveDriveKinematics(
                        FRONT_LEFT_LOCATION,
                        FRONT_RIGHT_LOCATION,
                        BACK_LEFT_LOCATION,
                        BACK_RIGHT_LOCATION);
    }

    public static final class CAN {

        /*
         * MotionCore CAN Bus
         * (Keep this unless your mentor tells you otherwise.)
         */
        public static final int MOTIONCORE_CAN_D0_BUS_ID = 5;

        // ==========================
        // Temporary Single-Pod Testing
        // ==========================
        public static final int DRIVE_TEST_CAN_ID = 3;
        public static final int STEER_TEST_CAN_ID = 3;

        // ==========================
        // Full Swerve CAN IDs
        // ==========================

        // Drive Motors
        public static final int FRONT_LEFT_DRIVE = 20;
        public static final int FRONT_RIGHT_DRIVE = 21;
        public static final int BACK_LEFT_DRIVE = 22;
        public static final int BACK_RIGHT_DRIVE = 23;

        // Steering Motors
        public static final int FRONT_LEFT_STEER = 30;
        public static final int FRONT_RIGHT_STEER = 31;
        public static final int BACK_LEFT_STEER = 32;
        public static final int BACK_RIGHT_STEER = 33;
    }

    public static final class GearRatios {

        // TODO: Replace with your actual module ratios
        public static final double DRIVE = 6.75;
        public static final double STEER = 1.0;
    }

    public static final class PID {

        // ==========================
        // Steering PID
        // ==========================
        public static final double STEER_kP = 2.0;
        public static final double STEER_kI = 0.0;
        public static final double STEER_kD = 0.08;

        public static final double STEER_TOLERANCE_DEGREES = 2.0;

        // ==========================
        // Drive PID
        // ==========================
        public static final double DRIVE_kP = 0.5;
        public static final double DRIVE_kI = 0.0;
        public static final double DRIVE_kD = 0.0;
    }

    public static final class Operator {

        public static final int DRIVER_CONTROLLER = 0;

        public static final double DEADBAND = 0.05;
    }

    /*
     * Keep these while we're converting the project from
     * a single module to a complete four-module drivetrain.
     * We'll remove them once everything is working.
     */
    public static final class SinglePod {

        public static final double FORWARD_ABSOLUTE_POSITION_ROTATIONS = -0.240;

        public static final double STICK_DIRECTION_DEADBAND = 0.12;

        public static final double STEERING_DIRECTION = 1.0;

        public static final double DRIVE_DIRECTION = 1.0;

        public static final double MAX_DRIVE_THROTTLE = 0.75;
    }
}
package first.robot.YGSLSwerveTemplate;

import org.wpilib.hardware.hal.CANBusMap;
import org.wpilib.math.geometry.Translation2d;
import org.wpilib.math.kinematics.SwerveDriveKinematics;

public final class Constants {

    private Constants() {}

    public static final class Drive {

        // Robot Dimensions (meters)
        public static final double TRACK_WIDTH = 0.6096;      // 24 in
        public static final double WHEEL_BASE = 0.6096;       // 24 in

        public static final double WHEEL_DIAMETER = 0.1016;   // 4 in
        public static final double WHEEL_CIRCUMFERENCE =
                WHEEL_DIAMETER * Math.PI;

        // Maximum Speeds
        public static final double MAX_SPEED = 4.5;
        public static final double MAX_ANGULAR_SPEED =
                Math.PI * 2.0;

        public static final double MAX_OPEN_LOOP_THROTTLE = 1.0;

        // Swerve Module Locations
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
     * REV MotionCore Alpha
     * MotionCore CAN_D0 is raw bus ID 5.
     */
    public static final int MOTIONCORE_CAN_D0_BUS_ID = 5;

    // -------------------------
    // Single Motor Test Setup
    // -------------------------

    // Drive motor (plugged into MotionCore D8)
    public static final int DRIVE_TEST_CAN_ID = 3;

    // Steering motor (plugged into MotionCore D9)
    public static final int STEER_TEST_CAN_ID = 3;

    // -------------------------
    // Full Swerve CAN IDs
    // -------------------------

    // Drive motors
    public static final int FRONT_LEFT_DRIVE = 1;
    public static final int FRONT_RIGHT_DRIVE = 2;
    public static final int BACK_LEFT_DRIVE = 3;
    public static final int BACK_RIGHT_DRIVE = 4;

    // Steering motors
    public static final int FRONT_LEFT_STEER = 5;
    public static final int FRONT_RIGHT_STEER = 6;
    public static final int BACK_LEFT_STEER = 7;
    public static final int BACK_RIGHT_STEER = 8;
}

    public static final class GearRatios {

        public static final double DRIVE = 6.75;
        public static final double STEER = 1.0;
    }

    public static final class PID {

        // Steering PID (temporary)
        public static final double STEER_kP = 2.0;
        public static final double STEER_kI = 0.0;
        public static final double STEER_kD = 0.0;

        // Drive PID
        public static final double DRIVE_kP = 0.5;
        public static final double DRIVE_kI = 0.0;
        public static final double DRIVE_kD = 0.0;
    }

    public static final class Operator {

        public static final int DRIVER_CONTROLLER = 0;

        public static final double DEADBAND = 0.05;
    }

    public static final class SinglePod {

        // Absolute encoder reading when the pod points robot-forward.
        public static final double FORWARD_ABSOLUTE_POSITION_ROTATIONS = -0.074;
        public static final double STICK_DIRECTION_DEADBAND = 0.12;
        public static final double DRIVE_DIRECTION = -1.0;
        public static final double MAX_DRIVE_THROTTLE = 0.25;
    }
}

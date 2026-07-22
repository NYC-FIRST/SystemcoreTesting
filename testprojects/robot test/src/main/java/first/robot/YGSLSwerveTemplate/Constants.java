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

        // MotionCore A301 bus IDs
        public static final int CAN_D0 = CANBusMap.CAN_D0;
        public static final int CAN_D1 = CANBusMap.CAN_D1;
        public static final int CAN_D2 = CANBusMap.CAN_D2;
        public static final int CAN_D3 = CANBusMap.CAN_D3;

        // Drive motor CAN IDs
        public static final int FRONT_LEFT_DRIVE = 1;
        public static final int FRONT_RIGHT_DRIVE = 2;
        public static final int BACK_LEFT_DRIVE = 3;
        public static final int BACK_RIGHT_DRIVE = 4;

        // Steering motor CAN IDs
        public static final int FRONT_LEFT_STEER = 5;
        public static final int FRONT_RIGHT_STEER = 6;
        public static final int BACK_LEFT_STEER = 7;
        public static final int BACK_RIGHT_STEER = 8;
    }

    public static final class GearRatios {

        // Update these when the final module ratios are known
        public static final double DRIVE = 6.75;
        public static final double STEER = 12.8;
    }

    public static final class PID {

        // Steering PID (temporary)
        public static final double STEER_kP = 1.0;
        public static final double STEER_kI = 0.0;
        public static final double STEER_kD = 0.05;

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

        public static final double FORWARD_ABSOLUTE_POSITION_ROTATIONS = 0.00;
        public static final double STICK_DIRECTION_DEADBAND = 0.12;
    }
}

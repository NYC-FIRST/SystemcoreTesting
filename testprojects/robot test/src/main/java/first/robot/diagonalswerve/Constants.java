package first.robot.diagonalswerve;

public final class Constants {

    private Constants() {}

    // Dimentions
    public static final double TRACK_WIDTH = 0.60;
    public static final double WHEEL_BASE = 0.60;

    // speed
    public static final double MAX_SPEED = 4.5;

    // front right pod
    public static final int FRONT_RIGHT_DRIVE_ID = 1;
    public static final int FRONT_RIGHT_STEER_ID = 2;

    // bacl left pod
    public static final int BACK_LEFT_DRIVE_ID = 3;
    public static final int BACK_LEFT_STEER_ID = 4;

    // PID (placeholder values)
    public static final double STEER_kP = 0.01;
    public static final double STEER_kI = 0.0;
    public static final double STEER_kD = 0.0;
}
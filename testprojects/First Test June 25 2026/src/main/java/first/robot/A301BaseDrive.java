package first.robot;

import com.revrobotics.spark.A301;
import com.revrobotics.util.Signal;

import org.wpilib.networktables.BooleanPublisher;
import org.wpilib.networktables.DoublePublisher;
import org.wpilib.networktables.NetworkTable;
import org.wpilib.networktables.NetworkTableInstance;
import org.wpilib.networktables.StringPublisher;
import org.wpilib.opmode.PeriodicOpMode;

public abstract class A301BaseDrive extends PeriodicOpMode {

    /* =========================
     * Hardware Configuration
     * ========================= */

    protected static final int FRONT_LEFT_BUS = 5;
    protected static final int FRONT_RIGHT_BUS = 6;
    protected static final int BACK_RIGHT_BUS = 7;
    protected static final int BACK_LEFT_BUS = 8;

    protected static final int A301_CAN_ID = 3;

    protected static final double DEADBAND = 0.05;

    /* =========================
     * Motors
     * ========================= */

    protected A301 frontLeft;
    protected A301 frontRight;
    protected A301 backRight;
    protected A301 backLeft;

    /* =========================
     * NetworkTables
     * ========================= */

    protected final NetworkTable table;

    protected final DoublePublisher loopCountPublisher;
    protected final DoublePublisher startCountPublisher;
    protected final DoublePublisher endCountPublisher;

    protected final StringPublisher lifecyclePublisher;
    protected final StringPublisher statusPublisher;

    /* =========================
     * Motor Telemetry
     * ========================= */

    protected final MotorTelemetry frontLeftTelemetry;
    protected final MotorTelemetry frontRightTelemetry;
    protected final MotorTelemetry backRightTelemetry;
    protected final MotorTelemetry backLeftTelemetry;

    /* =========================
     * Counters
     * ========================= */

    protected int loopCount;
    protected int startCount;
    protected int endCount;

    /* =========================
     * Constructor
     * ========================= */

    protected A301BaseDrive(String tableName) {

        table = NetworkTableInstance.getDefault().getTable(tableName);

        loopCountPublisher = table.getDoubleTopic("loopCount").publish();
        startCountPublisher = table.getDoubleTopic("startCount").publish();
        endCountPublisher = table.getDoubleTopic("endCount").publish();

        lifecyclePublisher = table.getStringTopic("lifecycle").publish();
        statusPublisher = table.getStringTopic("status").publish();

        frontLeftTelemetry = new MotorTelemetry("frontLeft");
        frontRightTelemetry = new MotorTelemetry("frontRight");
        backRightTelemetry = new MotorTelemetry("backRight");
        backLeftTelemetry = new MotorTelemetry("backLeft");
    }

    /* =========================
     * Lifecycle
     * ========================= */

    @Override
    public void start() {

        System.out.println(getClass().getSimpleName() + " START");

        loopCount = 0;

        startCount++;

        startCountPublisher.set(startCount);

        lifecyclePublisher.set("start() called; creating motor handles");

        closeAllMotors();
        createAllMotors();
        stopAllMotors();

        publishAllMotorTelemetry();
    }

    @Override
    public void end() {

        endCount++;

        endCountPublisher.set(endCount);

        lifecyclePublisher.set("end() called; stopping and closing motor handles");

        stopAllMotors();
        closeAllMotors();
    }

    @Override
    public void close() {

        lifecyclePublisher.set("close() called; stopping and closing motor handles");

        stopAllMotors();
        closeAllMotors();
    }

    /* =========================
     * Utility
     * ========================= */

    protected void incrementLoopCounter() {

        loopCount++;

        loopCountPublisher.set(loopCount);
    }

    protected boolean anyMotorMissing() {

        return frontLeft == null
                || frontRight == null
                || backRight == null
                || backLeft == null;
    }

    /* =========================
     * Motor Creation
     * ========================= */

    // Paste createMotor() here

    // Paste createAllMotors() here

    /* =========================
     * Motor Commands
     * ========================= */

    // Paste setMotor() here

    // Paste stopAllMotors() here

    /* =========================
     * Closing
     * ========================= */

    // Paste closeMotor() here

    // Paste closeAllMotors() here

    /* =========================
     * Error Helpers
     * ========================= */

    // Paste getThrowableText() here

    /* =========================
     * Telemetry (Part 3)
     * ========================= */

    // publishAllMotorTelemetry()

    // publishMotorTelemetry()

    // readDoubleSignal()

    // statusText()

    // DoubleSignalSupplier

    // MotorTelemetry
}
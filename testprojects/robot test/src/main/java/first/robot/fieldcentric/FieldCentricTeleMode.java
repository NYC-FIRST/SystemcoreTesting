package first.robot.fieldcentric;

import org.wpilib.driverstation.Joystick;
import org.wpilib.opmode.PeriodicOpMode;
import org.wpilib.opmode.Teleop;

import first.robot.Robot;

@Teleop(name = "Field Centric TeleOp")
public class FieldCentricTeleMode extends PeriodicOpMode {

    private Robot robot;
    private Joystick leftStick;
    private Joystick rightStick;
    private FieldCentricDrive drive;

    public FieldCentricTeleMode() {
        robot = Robot.getInstance();
        leftStick = new Joystick(0);
        rightStick = new Joystick(1);
        drive = new FieldCentricDrive(robot);
    }

    @Override
    public void start() {

        // Optional: zero the gyro when teleop starts
        // drive.zeroHeading(); // removed: FieldCentricDrive does not define this method

    }

    @Override
    public void periodic() {

        drive.drive(
                -leftStick.getY(),
                leftStick.getX(),
                rightStick.getX()
        );

    }

}
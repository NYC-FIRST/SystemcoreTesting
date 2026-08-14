package first.robot.fieldcentric;

import first.robot.Robot;

public class FieldCentricDrive {

    private final Gyro gyro;
    private final Odometry odometry;

    public FieldCentricDrive(Robot robot) {

        gyro = new Gyro();
        odometry = new Odometry();

    }


    public void drive(
            double xSpeed,
            double ySpeed,
            double rotation) {

        odometry.update();

        // TODO
        // Convert field-relative
        // joystick into robot-relative
        // chassis speeds

        // Send speeds to mecanum drivetrain

    }

}
package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.util.Range;

@SuppressWarnings("All")
public class Collector {
	private Brickbot robot = Brickbot.getInstance();

	private static final double ROTATION_SPEED = 0.2d;
	private static final double EXTENSION_SPEED = 1.0d;

	/* Constants */
	private static final double COUNTS_PER_MOTOR_REV    = 1680.0d;
	private static final double ROTATION_GEAR_REDUCTION = 3.0d;

	public void rotateArm(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			robot.motorRotation.setPower(ROTATION_SPEED);
		else if(btnClose)
			robot.motorRotation.setPower(-ROTATION_SPEED);
		else
			robot.motorRotation.setPower(0);

		double servoPosition = Range.scale(robot.motorRotation.getCurrentPosition(), 0, 2400, 0, 1);
		robot.servoBox.setPosition(servoPosition);

		robot.telemetry.addData("Rotation", Integer.toString(robot.motorRotation.getCurrentPosition()));
		robot.telemetry.update();
	}

	public void extendArm(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			robot.motorExtension.setPower(EXTENSION_SPEED);
		else if(btnClose)
			robot.motorExtension.setPower(-EXTENSION_SPEED);
		else
			robot.motorExtension.setPower(0);

		robot.telemetry.addData("Extension", Integer.toString(robot.motorExtension.getCurrentPosition()));
		robot.telemetry.update();
	}

	public void rotateCollector(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			robot.crServo.setPower(1);
		else if(btnClose)
			robot.crServo.setPower(-1);
		else
			robot.crServo.setPower(0);
	}

	public void rotateBox(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			robot.servoBox.setPosition(1);
		else if(btnClose)
			robot.servoBox.setPosition(0);
	}
}

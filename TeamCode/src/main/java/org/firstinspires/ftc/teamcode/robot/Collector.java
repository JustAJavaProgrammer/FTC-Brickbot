package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@SuppressWarnings("All")
public class Collector {
	private Telemetry telemetry;

	private static final double ROTATION_SPEED = 0.4d;
	private static final double EXTENSION_SPEED = 1.0d;

	/* Motors */
	private DcMotor motorRotation;
	private DcMotor motorExtension;

	/* Servos */
	private CRServo crServo;
	private Servo servo;

	/* Constants */
	private static final double COUNTS_PER_MOTOR_REV    = 1680.0d;
	private static final double ROTATION_GEAR_REDUCTION = 3.0d;

	public void init(Telemetry telemetry, HardwareMap hwMap) {
		this.telemetry = telemetry;

		motorRotation = hwMap.get(DcMotor.class, "motorRotation");
		motorExtension = hwMap.get(DcMotor.class, "motorExtension");

		servo = hwMap.servo.get("servobox");
		crServo = hwMap.crservo.get("crservo");

		motorRotation.setDirection(DcMotorSimple.Direction.REVERSE);
		motorExtension.setDirection(DcMotorSimple.Direction.FORWARD);

		crServo.setDirection(DcMotorSimple.Direction.REVERSE);

		motorRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		motorRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public void rotateArm(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			motorRotation.setPower(ROTATION_SPEED);
		else if(btnClose)
			motorRotation.setPower(-ROTATION_SPEED);
		else
			motorRotation.setPower(0);

		telemetry.addData("Rotation", Integer.toString(motorRotation.getCurrentPosition()));
		telemetry.update();
	}

	public void extendArm(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			motorExtension.setPower(EXTENSION_SPEED);
		else if(btnClose)
			motorExtension.setPower(-EXTENSION_SPEED);
		else
			motorExtension.setPower(0);

		telemetry.addData("Rotation", Integer.toString(motorExtension.getCurrentPosition()));
		telemetry.update();
	}

	public void rotateCollector(boolean btnOpen, boolean btnClose) {
		if(btnOpen)
			crServo.setPower(1);
		else if(btnClose)
			crServo.setPower(-1);
		else
			crServo.setPower(0);
	}

	public void rotateBox(boolean btnOpen, boolean btnClose) {

	}
}

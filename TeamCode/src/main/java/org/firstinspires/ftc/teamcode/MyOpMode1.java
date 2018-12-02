package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@SuppressWarnings({"ConstantConditions", "unused", "WeakerAccess"})

@TeleOp(name="Brickbot: OpMode (v0.1)", group="Linear Opmode")

public class MyOpMode1 extends OpMode {
	
	private ElapsedTime runtime = new ElapsedTime();
	DcMotor motorFrontLeft;
	DcMotor motorFrontRight;
	DcMotor motorBackLeft;
	DcMotor motorBackRight;
	double drive = 0,
		turn = 0,
		leftPower = 0,
		rightPower = 0;

	@Override
	public void init() {
		motorFrontLeft = hardwareMap.get(DcMotor.class, "motorfl");
		motorFrontRight = hardwareMap.get(DcMotor.class, "motorfr");
		motorBackLeft = hardwareMap.get(DcMotor.class, "motorbl");
		motorBackRight = hardwareMap.get(DcMotor.class, "motorbr");

		motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);
	}

	@Override
	public void start() {
		runtime.reset();
	}

	@Override
	public void loop() {
		leftPower = 0;
		rightPower = 0;

		if(gamepad1.left_stick_y != 0) {

			drive = -gamepad1.left_stick_y;
			if(gamepad1.left_stick_y < 0)
				turn = gamepad1.right_stick_x;
			else turn = -gamepad1.right_stick_x;
			leftPower = Range.clip(drive + turn, -1.0, 1.0);
			rightPower = Range.clip(drive - turn, -1.0, 1.0);

		} else if(gamepad1.left_stick_x != 0) {

			drive = -gamepad1.left_stick_x;
			leftPower = Range.clip(-drive, -1.0, 1.0);
			rightPower = Range.clip(drive, -1.0, 1.0);

		}

		motorFrontLeft.setPower(leftPower);
		motorFrontRight.setPower(rightPower);
		motorBackLeft.setPower(leftPower);
		motorBackRight.setPower(rightPower);
	}
}

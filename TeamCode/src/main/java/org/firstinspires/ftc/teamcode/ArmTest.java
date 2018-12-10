package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@SuppressWarnings("All")
@TeleOp(name="Brickbot: ArmTest (v0.1)", group="Linear Opmode")
public class ArmTest extends OpMode {

	CRServo servo;

	@Override
	public void init() {
		servo = hardwareMap.crservo.get("collector");
		servo.setDirection(DcMotorSimple.Direction.REVERSE);
	}

	@Override
	public void loop() {
		if (gamepad1.a) {
			servo.setPower(1);
		}
		if (!gamepad1.a) {
			servo.setPower(0);
		}
	}
}

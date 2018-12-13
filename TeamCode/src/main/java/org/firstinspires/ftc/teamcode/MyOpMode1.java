package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.robot.Brickbot;
import org.firstinspires.ftc.teamcode.robot.Collector;
import org.firstinspires.ftc.teamcode.robot.Drive;

@SuppressWarnings("All")
@TeleOp(name="Brickbot: OpMode (v0.1)", group="Linear Opmode")

public class MyOpMode1 extends OpMode {

	private Brickbot robot = Brickbot.getInstance();
	private Drive drive = new Drive();
	private Collector collector = new Collector();
	
	private ElapsedTime runtime = new ElapsedTime();

	@Override
	public void init() {
		telemetry.setAutoClear(false);
		robot.init(telemetry, hardwareMap);
	}

	@Override
	public void start() {
		runtime.reset();
	}

	@Override
	public void loop() {
		drive.teleDrive(gamepad1);

		collector.extendArm(gamepad2.left_stick_y < 0, gamepad2.left_stick_y > 0);
		collector.rotateArm(gamepad2.right_stick_y > 0, gamepad2.right_stick_y < 0);
		collector.rotateCollector(gamepad2.a, gamepad2.b);
		collector.rotateBox(gamepad2.right_bumper, gamepad2.right_trigger > 0);

//		if (gamepad1.a)
//			robot.test();
//
//		if (gamepad1.b) {
//			robot.motorRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//			robot.motorRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//		}
	}
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("All")

public class Autonomous_Base extends LinearOpMode {

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
	public void runOpMode(){

		motorFrontLeft = hardwareMap.get(DcMotor.class, "motorfl");
		motorFrontRight = hardwareMap.get(DcMotor.class, "motorfr");
		motorBackLeft = hardwareMap.get(DcMotor.class, "motorbl");
		motorBackRight = hardwareMap.get(DcMotor.class, "motorbr");

		motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);

		waitForStart();
		runtime.reset();

		while(opModeIsActive())
		{

		}

	}

	public void move_by_time(long miliseconds)
	{
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {

			}
		};

		timer.schedule(timerTask, miliseconds);
	}

}

package org.firstinspires.ftc.teamcode;

import android.util.Rational;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("All")

@TeleOp(name="Brickbot: AutoOpMode (v0.1)", group="Linear Opmode")
@Disabled
public class AutonomousOpMode extends LinearOpMode {
	//TODO: Set constants
	private static final double WHEEL_DIAMETER = 10;     //CENTIMETERS
	private static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;

	private static final Rational MOTOR_TO_WHEEL_RATIO = new Rational(0, 0); //TEETH

	private static final double MOTOR_MAX_SPEED = 0;    //ROTATIONS PER SECOND
	private static final double WHEEL_MAX_SPEED = MOTOR_MAX_SPEED / MOTOR_TO_WHEEL_RATIO.doubleValue(); //ROTATIONS PER SECOND


	protected ElapsedTime runtime = new ElapsedTime();
	protected DcMotor motorFrontLeft;
	protected DcMotor motorFrontRight;
	protected DcMotor motorBackLeft;
	protected DcMotor motorBackRight;

	@Override
	public void runOpMode() {

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
	}

	private void moveForward(final long time) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				motorFrontLeft.setPower(1);
				motorFrontRight.setPower(1);
				motorBackLeft.setPower(1);
				motorBackRight.setPower(1);

				try {
					wait(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				motorFrontLeft.setPower(0);
				motorFrontRight.setPower(0);
				motorBackLeft.setPower(0);
				motorBackRight.setPower(0);
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void moveBackward(final long miliseconds) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				motorFrontLeft.setPower(-1);
				motorFrontRight.setPower(-1);
				motorBackLeft.setPower(-1);
				motorBackRight.setPower(-1);

				try {
					wait(miliseconds);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				motorFrontLeft.setPower(0);
				motorFrontRight.setPower(0);
				motorBackLeft.setPower(0);
				motorBackRight.setPower(0);
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Moves the robot a certain amount of centimeters
	 * @param distance can be negative to move backwards
	 */
	protected void move(int distance) {
		double robo_speed = WHEEL_MAX_SPEED * WHEEL_CIRCUMFERENCE;
		long miliseconds = (long) Math.floor(Math.abs(distance) / robo_speed * 1000);

		if (distance < 0)
			moveBackward(miliseconds);
		else
			moveForward(miliseconds);
	}

	protected void rotate(final int degrees) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				motorFrontLeft.setPower(-Math.signum(degrees));
				motorFrontRight.setPower(Math.signum(degrees));
				motorBackLeft.setPower(-Math.signum(degrees));
				motorBackRight.setPower(Math.signum(degrees));

				try {
					wait(0); //TODO: Calculate rotation time
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				motorFrontLeft.setPower(0);
				motorFrontRight.setPower(0);
				motorBackLeft.setPower(0);
				motorBackRight.setPower(0);
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected void makeSquare(int size) {
		for (int i = 0; i < 4; i++) {
			move(size);
			rotate(90);
		}
	}
}

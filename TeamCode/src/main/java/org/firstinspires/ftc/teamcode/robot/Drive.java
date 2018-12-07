package org.firstinspires.ftc.teamcode.robot;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@SuppressWarnings("All")

/**
 * Class responsible with controlling the drive train
 */
public class Drive {
	private Telemetry telemetry;
	private Brickbot robot = Brickbot.getInstance();

	/* Motors */
	private DcMotor motorFrontLeft;
	private DcMotor motorFrontRight;
	private DcMotor motorBackLeft;
	private DcMotor motorBackRight;

	/* Constants */
	//TODO: Verify constants
	private static final double ROBOT_WIDTH = 13.97637795d;
	private static final double ROBOT_LENGTH = 14.17322835d;

	private static final double COUNTS_PER_MOTOR_REV    = 280.0d;       // Modify according to the drive motors
	private static final double WHEEL_DIAMETER_INCHES   = 4.0d;
	private static final double DRIVE_GEAR_REDUCTION    = 2.0d;         // Speed reduction
	private static final double COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

	private static final double DRIVE_SPEED  = 0.8d;
	private static final double TURN_SPEED   = 0.8d;

	/* Constructor */
	public Drive() {
		if (DRIVE_SPEED > 1.0d || TURN_SPEED > 1.0d)
			throw new ArithmeticException();
	}

	/* Initialize */
	public void init(Telemetry telemetry, HardwareMap hwMap) {
		this.telemetry = telemetry;

		motorFrontLeft = hwMap.get(DcMotor.class, "motorfl");
		motorFrontRight = hwMap.get(DcMotor.class, "motorfr");
		motorBackLeft = hwMap.get(DcMotor.class, "motorbl");
		motorBackRight = hwMap.get(DcMotor.class, "motorbr");

		motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);

		motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	public enum Direction {
		FORWARD,
		BACKWARD,
		LEFT,
		RIGHT
	}

	public enum Rotation {
		CLOCKWISE,
		COUNTERCLOCKWISE
	}

	/**
	 * Sets the power of the drive train motors to max
	 */
	private void startMotors() {
		motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		motorFrontLeft.setPower(DRIVE_SPEED);
		motorFrontRight.setPower(DRIVE_SPEED);
		motorBackLeft.setPower(DRIVE_SPEED);
		motorBackRight.setPower(DRIVE_SPEED);
	}

	/**
	 * Stops the drive train motors
	 */
	private void stopMotors() {
		motorFrontLeft.setPower(0);
		motorFrontRight.setPower(0);
		motorBackLeft.setPower(0);
		motorBackRight.setPower(0);

		motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	/**
	 * Modifies the target positions to the drive train motors depending on the current position
	 * @param args 4 ints corresponding to the positions of:
	 * 		       <p>1. Front Left Motor</p>
	 * 		       <p>2. Front Right Motor</p>
	 * 		       <p>3. Back Left Motor</p>
	 * 		       <p>4. Back Right Motor</p>
	 */
	private void modifyTargetPosition(@NonNull int... args) {
		if (args.length == 4) {
			motorFrontLeft.setTargetPosition(motorFrontLeft.getCurrentPosition() + args[0]);
			motorFrontRight.setTargetPosition(motorFrontRight.getCurrentPosition() + args[1]);
			motorBackLeft.setTargetPosition(motorBackLeft.getCurrentPosition() + args[2]);
			motorBackRight.setTargetPosition(motorBackRight.getCurrentPosition() + args[3]);
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	private void resetTargetPosition() {
		motorFrontLeft.setTargetPosition(motorFrontLeft.getCurrentPosition());
		motorFrontRight.setTargetPosition(motorFrontRight.getCurrentPosition());
		motorBackLeft.setTargetPosition(motorBackLeft.getCurrentPosition());
		motorBackRight.setTargetPosition(motorBackRight.getCurrentPosition());
	}

	private boolean isBusy() {
		return motorFrontLeft.isBusy() &&
				motorFrontRight.isBusy() &&
				motorBackLeft.isBusy() &&
				motorBackRight.isBusy();
	}

	/**
	 * The robot starts moving
	 * @param direction One of 4 cardinal directions in which the robot starts moving
	 * @param unit The unit of measure for the specified distance
	 * @param distance The distance the robot will move
	 */
	public void move(Direction direction, DistanceUnit unit, double distance) {
		distance = Math.abs(distance);
		int modifier = (int)(unit.toInches(distance) * COUNTS_PER_INCH);

		switch (direction) {
			default:
			case FORWARD:
				modifyTargetPosition(modifier, modifier, modifier, modifier);
				break;
			case BACKWARD:
				modifyTargetPosition(-modifier, -modifier, -modifier, -modifier);
				break;
			case LEFT:
				modifyTargetPosition(modifier, -modifier, -modifier, modifier);
				break;
			case RIGHT:
				modifyTargetPosition(-modifier, modifier, modifier, -modifier);
				break;
		}

		startMotors();
		while (isBusy()) {}
		stopMotors();
		resetTargetPosition();
	}

	/**
	 * The robot starts rotating
	 * @param rotation The sense of rotation in which the robot starts moving
	 * @param unit The unit of measure for the specified angle
	 * @param angle The amount the robot will rotate
	 */
	public void rotate(Rotation rotation, AngleUnit unit, double angle) {
		angle = unit.normalize(angle);
		angle = unit.toDegrees(angle);

		int modifier = (int) (unit.toRadians(angle)
						* Math.sqrt(ROBOT_LENGTH * ROBOT_LENGTH + ROBOT_WIDTH * ROBOT_WIDTH) * COUNTS_PER_INCH / 2);

		switch (rotation) {
			default:
			case CLOCKWISE:
				modifyTargetPosition(modifier, -modifier, modifier, -modifier);
				break;
			case COUNTERCLOCKWISE:
				modifyTargetPosition(-modifier, modifier, -modifier, modifier);
				break;
		}

		startMotors();
		while (isBusy()) {}
		stopMotors();
		resetTargetPosition();
	}

	/**
	 * Stops the drive train
	 */
	public void stop() {
		stopMotors();
		resetTargetPosition();
	}
}

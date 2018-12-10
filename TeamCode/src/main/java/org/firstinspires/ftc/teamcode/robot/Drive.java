package org.firstinspires.ftc.teamcode.robot;

import android.support.annotation.NonNull;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@SuppressWarnings("All")

/**
 * Class responsible with controlling the drive train
 */
public class Drive {
	private Telemetry telemetry;

	/* Gyro */
	private ModernRoboticsI2cGyro gyro;

	/* Motors */
	private DcMotor motorFrontLeft;
	private DcMotor motorFrontRight;
	private DcMotor motorBackLeft;
	private DcMotor motorBackRight;

	/* Constants */
	private static final double ROBOT_WIDTH   = 13.97637795d;
	private static final double ROBOT_LENGTH  = 14.17322835d;

	private static final double COUNTS_PER_MOTOR_REV    = 280.0d;       // Modify according to the drive motors
	private static final double WHEEL_DIAMETER_INCHES   = 4.0d;
	private static final double DRIVE_GEAR_REDUCTION    = 2.0d;         // Speed reduction
	private static final double COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);

	private static final double DRIVE_SPEED  = 0.8d;
	private static final double TURN_SPEED   = 0.8d;

	static final double HEADING_THRESHOLD   = 1 ;       // Ignore error if the angle is less than the threshold
	static final double P_TURN_COEFF        = 0.1;
	static final double P_DRIVE_COEFF       = 0.15;

	/* Constructor */
	public Drive() {
		if (DRIVE_SPEED > 1.0d || TURN_SPEED > 1.0d)
			throw new ArithmeticException();
	}

	/* Initialize */
	public void init(Telemetry telemetry, HardwareMap hwMap) {
		this.telemetry = telemetry;

		gyro = (ModernRoboticsI2cGyro) hwMap.gyroSensor.get("gyro");

		motorFrontLeft = hwMap.get(DcMotor.class, "motorfl");
		motorFrontRight = hwMap.get(DcMotor.class, "motorfr");
		motorBackLeft = hwMap.get(DcMotor.class, "motorbl");
		motorBackRight = hwMap.get(DcMotor.class, "motorbr");

		telemetry.addData(">", "Calibrating Gyro");
		telemetry.update();

		gyro.calibrate();

		motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
		motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
		motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);

		motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		while (gyro.isCalibrating()) {}

		motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

		gyro.resetZAxisIntegrator();

		telemetry.addData(">", "Robot Ready.");
		telemetry.update();
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
	 * Sets the power to the drive train motors.
	 * @param args 4 doubles corresponding to the powers of:
	 * 		       <p>1. Front Left Motor</p>
	 * 		       <p>2. Front Right Motor</p>
	 * 		       <p>3. Back Left Motor</p>
	 * 		       <p>4. Back Right Motor</p>
	 * @param args
	 */
	private void setPower(@NonNull double... args) {
		if (args.length == 4) {
			motorFrontLeft.setPower(args[0]);
			motorFrontRight.setPower(args[1]);
			motorBackLeft.setPower(args[2]);
			motorBackRight.setPower(args[3]);
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
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
	 * Sets the power of the drive train motors to max
	 */
	private void startMotors() {
		motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
		motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

		setPower(DRIVE_SPEED, DRIVE_SPEED, DRIVE_SPEED, DRIVE_SPEED);
	}

	/**
	 * Stops the drive train motors
	 */
	private void stopMotors() {
		setPower(0, 0, 0, 0);

		motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}

	/**
	 * Determines the error between the target angle and the robot's current heading.
	 * @param   targetAngle  Desired angle (relative to global reference established at last Gyro Reset).
	 * @return  Degrees in the range +/- 180. Centered on the robot's frame of reference.
	 */
	private double getError(double targetAngle) {
		double error;
		error = targetAngle - gyro.getIntegratedZValue();
		return AngleUnit.normalizeDegrees(error);
	}

	/**
	 * Returns desired steering force.  +/- 1 range.
	 * @param error   Error angle in robot relative degrees
	 * @param PCoeff  Proportional Gain Coefficient
	 */
	private double getSteer(double error, double PCoeff) {
		return Range.clip(error * PCoeff, -1, 1);
	}

	/**
	 *  Method to drive on a fixed compass bearing (angle), based on encoder counts.
	 *  Move will stop if the move gets to the desired position
	 *
	 * @param speed      Target speed for forward motion.  Should allow for _/- variance for adjusting heading
	 * @param distance   Distance (in inches) to move from current position.  Negative distance means move backwards.
	 * @param angle      Absolute Angle (in Degrees) relative to last gyro reset.
	 *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
	 *                   If a relative angle is required, add/subtract from current heading.
	 */
	public void gyroDrive (Direction direction, DistanceUnit unit, double distance) {
		double  max;
		double  error;
		double  steer;
		double  leftSpeed;
		double  rightSpeed;

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
/*
		while (isBusy()) {

			// adjust relative speed based on heading error.
			error = getError(angle);
			steer = getSteer(error, P_DRIVE_COEFF);

			// if driving in reverse, the motor correction also needs to be reversed
			if (distance < 0)
				steer *= -1.0;

			leftSpeed = speed - steer;
			rightSpeed = speed + steer;

			// Normalize speeds if either one exceeds +/- 1.0;
			max = Math.max(Math.abs(leftSpeed), Math.abs(rightSpeed));
			if (max > 1.0)
			{
				leftSpeed /= max;
				rightSpeed /= max;
			}

			robot.leftDrive.setPower(leftSpeed);
			robot.rightDrive.setPower(rightSpeed);

			// Display drive status for the driver.
			telemetry.addData("Err/St",  "%5.1f/%5.1f",  error, steer);
			telemetry.addData("Target",  "%7d:%7d",      newLeftTarget,  newRightTarget);
			telemetry.addData("Actual",  "%7d:%7d",      robot.leftDrive.getCurrentPosition(),
					robot.rightDrive.getCurrentPosition());
			telemetry.addData("Speed",   "%5.2f:%5.2f",  leftSpeed, rightSpeed);
			telemetry.update();
		}

		// Stop all motion;
		robot.leftDrive.setPower(0);
		robot.rightDrive.setPower(0);

		// Turn off RUN_TO_POSITION
		robot.leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		robot.rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
*/
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

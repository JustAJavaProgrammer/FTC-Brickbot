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

	/* Motors */
	private DcMotor motorRotation;
	private DcMotor motorExtension;

	/* Servos */
	private CRServo crServo;
	private Servo servo;

	/* Constants */
	private static final double COUNTS_PER_MOTOR_REV    = 420.0d;
	private static final double ROTATION_GEAR_REDUCTION = 3.0d;

	public void init(Telemetry telemetry, HardwareMap hwMap) {
		this.telemetry = telemetry;

		motorRotation = hwMap.get(DcMotor.class, "motorRotation");
		motorExtension = hwMap.get(DcMotor.class, "motorExtension");

		motorRotation.setDirection(DcMotorSimple.Direction.REVERSE);
		motorExtension.setDirection(DcMotorSimple.Direction.FORWARD);

		motorRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motorExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

		motorRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		motorExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
	}
}

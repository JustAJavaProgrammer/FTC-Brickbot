package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@SuppressWarnings("All")
public class Brickbot {         //TODO: Implement threads
	/* Make Singleton */
	private static Brickbot robot = new Brickbot();

	private Brickbot() {}

	public static Brickbot getInstance() {
		return robot;
	}

	/* Members */
	//TODO: Verify ore coords
	public      OpenGLMatrix            LEFT_ORE_COORDS     = OpenGLMatrix.translation((float) DistanceUnit.INCH.toMm(24.5d), (float) DistanceUnit.INCH.toMm(46.0d), 0);
	public      OpenGLMatrix            RIGHT_ORE_COORDS    = OpenGLMatrix.translation((float) DistanceUnit.INCH.toMm(46), (float) DistanceUnit.INCH.toMm(24.5d), 0);
	public      OpenGLMatrix            CENTER_ORE_COORDS   = OpenGLMatrix.translation((float) DistanceUnit.INCH.toMm(35.25d), (float) DistanceUnit.INCH.toMm(35.25d), 0);

	public      OpenGLMatrix            coords              = null;

	public      Drive                   drive               = new Drive();
	protected   Drive.Direction         Direction           = null;
	protected   Drive.Rotation          Rotation            = null;

	protected   DistanceUnit            distanceUnit        = DistanceUnit.CM;
	protected   AngleUnit               angleUnit           = AngleUnit.DEGREES;

	public      TFOreIdentification     tfID                = new TFOreIdentification();

	/* Initialize */
	public void init(Telemetry telemetry, HardwareMap hwMap) {
		drive.init(telemetry, hwMap);
		tfID.init(telemetry, hwMap);

		coords = OpenGLMatrix.translation(0, 0, 0)      //TODO: Find robot start position
				.multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, 0, 0, 45));
	}

	public void sampleGoldOre() {
		tfID.activate();

		String position = tfID.getGoldOrePosition();



		switch (position) {
			case "CENTER":

				break;
			case "RIGHT":

				break;
			case "LEFT":

				break;
		}

		tfID.shutdown();
	}
}

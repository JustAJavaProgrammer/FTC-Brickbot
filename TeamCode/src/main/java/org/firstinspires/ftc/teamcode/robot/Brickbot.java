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

	private     Drive                   drive               = new Drive();
	private     Drive.Direction         Direction           = null;

	private     Collector               collector           = new Collector();

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
		tfID.shutdown();

		tfID.dropWall();

		drive.move(Direction.RIGHT, distanceUnit, 40);

		switch (position) {
			case "RIGHT":
				drive.move(Direction.BACKWARD, distanceUnit, 50);
				break;
			case "LEFT":
				drive.move(Direction.FORWARD, distanceUnit, 50);
				break;
		}

		drive.move(Direction.RIGHT, distanceUnit, 55);

		tfID.raiseWall();
	}

	public void test() {

	}
}

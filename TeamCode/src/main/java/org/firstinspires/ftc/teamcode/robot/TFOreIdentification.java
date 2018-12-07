package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@SuppressWarnings("All")
public class TFOreIdentification {
	private Telemetry telemetry;

	private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
	private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
	private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

	private static final String VUFORIA_KEY = ""; //TODO: Get vuforia license

	private VuforiaLocalizer vuforia;
	private TFObjectDetector tfod;

	/* Initialize */
	public void init(Telemetry telemetry, HardwareMap hwMap) {
		this.telemetry = telemetry;

		initVuforia();

		if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
			initTfod(hwMap);
		} else {
			telemetry.addData("Sorry!", "This device is not compatible with TFOD");
			telemetry.update();

			throw new NullPointerException();
		}
	}

	/**
	 * Initialize the Vuforia localization engine.
	 */
	private void initVuforia() {
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

		parameters.vuforiaLicenseKey = VUFORIA_KEY;
		parameters.cameraDirection = CameraDirection.BACK;

		vuforia = ClassFactory.getInstance().createVuforia(parameters);
	}

	/**
	 * Initialize the Tensor Flow Object Detection engine.
	 */
	private void initTfod(HardwareMap hwMap) {
		int tfodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
				"tfodMonitorViewId", "id", hwMap.appContext.getPackageName());

		TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);

		tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
		tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
	}

	public void activate() {
		tfod.activate();
	}

	public void shutdown() {
		tfod.shutdown();
	}

	public String getGoldOrePosition() {
		List<Recognition> recognitions = tfod.getUpdatedRecognitions();

		int goldX = -1;
		int silver1X = -1;
		int silver2X = -1;

		for (Recognition recognition : recognitions) {
			if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
				goldX = (int) recognition.getLeft();
			} else if (silver1X == -1) {
				silver1X = (int) recognition.getLeft();
			} else {
				silver2X = (int) recognition.getLeft();
			}
		}

		DistanceUnit unitINCH = DistanceUnit.INCH;

		//LEFT
		if (goldX < silver1X && goldX < silver2X) {
			telemetry.addData("Gold Ore Position", "Left");
			telemetry.update();
			return "LEFT";
		}

		//RIGHT
		if (goldX > silver1X && goldX > silver2X) {
			telemetry.addData("Gold Ore Position", "Right");
			telemetry.update();
			return "RIGHT";
		}

		//CENTER
		telemetry.addData("Gold Ore Position", "Center");
		telemetry.update();
		return "CENTER";
	}
}

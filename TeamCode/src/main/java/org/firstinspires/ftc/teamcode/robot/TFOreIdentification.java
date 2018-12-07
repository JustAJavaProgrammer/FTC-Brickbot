package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

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

	private static final String VUFORIA_KEY = "AT2NRwv/////AAAAGU5D4weL2U/CiqT7iKeygcA+FgHmuCe13qmg32QO4cxxZcznZ9ydCb/xUCn8Znx98oaFHp/HxW9ixs9m6HMCvSSLzWB+7ITI8SPzwRifDQxOyZORxgo3sQbCJAOH3oM6VerGlv7CWJAP1ZUPCoSn06rEwZdYCGQu96Q7v68xWm4rz/e1SfBDgKyCWrCiEyLgI4XRcG/x6Vte7jHPysUBzwlXD+SGKALxVpKMWDCLyO8m0ImJj7+OWZLEznMnzhyesLcoMpFnzwUnnvnNiTVknlOlTaa1bDJOpN/E8K97DEFGv7joxdF5BfWz5LYrnfEFfrila20i5P76Np2bLa+TX/BG2/J+IoSeSkvz3NTkrpI5";

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
		parameters.cameraDirection = CameraDirection.FRONT;

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
		//tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
		tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_SILVER_MINERAL, LABEL_GOLD_MINERAL);
	}

	public void activate() {
		tfod.activate();
	}

	public void shutdown() {
		tfod.shutdown();
	}

	public String getGoldOrePosition() {
		List<Recognition> recognitions = null;

		int goldX = -1;
		int silver1X = -1;
		int silver2X = -1;


		while (goldX == -1) {
			recognitions = tfod.getUpdatedRecognitions();

			if (recognitions != null) {
				if (recognitions.size() == 3) {

					for (Recognition recognition : recognitions) {
						if (goldX == -1 ) {    //recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
							goldX = (int) recognition.getLeft();
						} else if (silver1X == -1) {
							silver1X = (int) recognition.getLeft();
						} else {
							silver2X = (int) recognition.getLeft();
						}
					}
				}
			}
		}

		DistanceUnit unitINCH = DistanceUnit.INCH;

		telemetry.addData("gold", Integer.toString(goldX));
		telemetry.addData("silver1", Integer.toString(silver1X));
		telemetry.addData("silver2", Integer.toString(silver2X));
		telemetry.update();

		ElapsedTime time = new ElapsedTime();
		time.reset();
		while (time.seconds() < 3) {}

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

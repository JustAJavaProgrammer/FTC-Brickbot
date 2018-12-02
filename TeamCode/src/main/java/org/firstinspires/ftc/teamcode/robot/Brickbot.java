package org.firstinspires.ftc.teamcode.robot;

import com.qualcomm.robotcore.hardware.HardwareMap;

@SuppressWarnings("All")
public class Brickbot {         //TODO: Implement threads
	/* Hardware */
	public Drive drive;

	/* Initialize */
	public void init(HardwareMap hwMap) {
		drive.init(hwMap);
	}
}

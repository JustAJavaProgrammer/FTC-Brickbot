package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@SuppressWarnings("all")

@TeleOp(name="Brickbot: BaseAutoOpMode (v0.1)", group="Linear Opmode")

public class Base_AutoOpMode extends AutonomousOpMode {
	@Override
	public void runOpMode() {
		super.runOpMode();

		makeSquare(40);
	}
}

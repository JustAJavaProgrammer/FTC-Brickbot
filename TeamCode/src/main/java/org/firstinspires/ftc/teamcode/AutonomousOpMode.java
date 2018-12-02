package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.robot.Brickbot;
import org.firstinspires.ftc.teamcode.robot.Drive;

import java.util.concurrent.TimeUnit;

@SuppressWarnings("All")

@TeleOp(name="Brickbot: AutoOpMode (v0.1)", group="Linear Opmode")
@Disabled
public class AutonomousOpMode extends LinearOpMode {

	protected Brickbot          robot           = new Brickbot();
	protected Drive             Drive           = robot.drive;
	protected Drive.Direction   Direction       = null;
	protected Drive.Rotation    Rotation        = null;

	protected DistanceUnit      distanceUnit    = DistanceUnit.CM;
	protected AngleUnit         angleUnit       = AngleUnit.DEGREES;

	@Override
	public void runOpMode() {
		robot.init(hardwareMap);

		waitForStart();

		//Code goes here
	}

	protected void makeSquare(int size) {
		for (int i = 0; i < 4; i++) {
			Drive.move(Direction.FORWARD, distanceUnit, size);
			Drive.rotate(Rotation.CLOCKWISE, angleUnit, 90);
		}
	}
}
//Brickbot:     set power to drive, constants
//AutoSuperclass:       move, terrain mapping
//AutoSubclass:         actual coords
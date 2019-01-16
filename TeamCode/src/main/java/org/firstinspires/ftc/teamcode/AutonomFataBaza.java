package org.firstinspires.ftc.teamcode;

import android.content.res.Resources;
//import android.drm.DrmInfoRequest;
import android.support.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.internal.vuforia.VuforiaException;
import org.firstinspires.ftc.teamcode.robot.Collector;

import java.util.List;

@SuppressWarnings("All")

@Autonomous(name="Brickbot: FATABAZA", group="CNU")
//@Disabled
public class AutonomFataBaza extends LinearOpMode {

    static final double HEADING_THRESHOLD   = 1 ;       // Ignore error if the angle is less than the threshold
    static final double P_TURN_COEFF        = 0.1;
    static final double P_DRIVE_COEFF       = 0.15;
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final String VUFORIA_KEY = "AW+QDt3/////AAABmUOonI8rb0LTkFhL3nkIkY+ORnThf7+Fzmt4rOxdKkealCvQLhEIRrrk3YmbsfoMzTIzo1sHEcMOdubdxhR7QJxAivwT80q3G0w6zH5uYSDN1obUzkJbAcmgrD19wnSbpJIS1q152Vc1Ao/FdnSEqGBux9dM5Ng2N8ADBWtO0HzMk2iIx6z3MFGa5xV49nlxl0PhFUvcElWABhz4ToI4c3RTJyge0FcNiUfz/yqkjmM6dgqhC7gbtEoLyUncltUsWzLC4r51ebsBj+oSNE4K8nIzFiaWS+AZa3zz/ORjd8RVevLV261raejwbBQGwmkCHBIRuyZvdJdVU3y3YF6iaAx5j45V04amDLer3FeXzJGd";
    private static final double ROBOT_WIDTH   = 13.97637795d;
    private static final double ROBOT_LENGTH  = 14.17322835d;
    private static final double COUNTS_PER_MOTOR_REV    = 1120.0d;       // Modify according to the drive motors
    private static final double WHEEL_DIAMETER_INCHES   = 4.0d;
    private static final double DRIVE_GEAR_REDUCTION    = 2.0d;         // Speed reduction
    private static final double COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * Math.PI);
    private static final double DRIVE_SPEED  = 0.8d;
    private static final double TURN_SPEED   = 0.8d;


    public Collector collector = new Collector();

    double FrontPower;
    double BackPower;
    double LeftPower;
    double RightPower;

    /* Declare OpMode members. */
    HardwareRO036 robot = new HardwareRO036();   // Use a Pushbot's hardware
    VuforiaLocalizer vuforia;
    TFObjectDetector tfod;
    private ElapsedTime runtime = new ElapsedTime();
    public enum Direction {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT
    }


    @Override
    public void runOpMode() {

        /*
         * Initializeaza variabilele de sistem.
         * Este apelata metoda init() din clasa hardware
         */
        robot.init(hardwareMap);


        // Trimite mesaj pentru a semnala actiunea;
        telemetry.addData("Status", "Resetting Encoders");    //
        telemetry.update();
        robot.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorExtension.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Calibrare Gyro");    //
        telemetry.update();
        robot.gyro.calibrate();
        while (robot.gyro.isCalibrating()) {
        }
        robot.gyro.resetZAxisIntegrator();
        telemetry.addData("Status", "Calibrare Gyro Ok");    //
        telemetry.update();

        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        if (opModeIsActive()) {

            String positionSave = null;

            if (tfod != null) {
               tfod.activate();
            }

            boolean complete = false;

            while (opModeIsActive() && !complete)  {

                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.

                    //TODO: LIFT GOES HERE

                    String position = getGoldOrePosition();

                    positionSave = position;

                    mergeDrept(Direction.RIGHT, 50, 1);

                    switch (positionSave) {
                        case "LEFT":
                            mergeDrept(Direction.FORWARD, 36,1);
                            dropWall();
                            mergeDrept(Direction.RIGHT, 40,1);
                            rotate(-135, 1);
                            mergeDrept(Direction.LEFT, 40,1);
                            mergeDrept(Direction.FORWARD, 40, 1);

                            raiseWall();
                            deployBrick(1500,Direction.FORWARD);
                            robot.servoBox.setPosition(0.1);
                            //sleep(500);
                            deployBrick(1500, Direction.BACKWARD);

                            //mergeDrept(Direction.BACKWARD, 200, 1);
                            complete = true;
                            break;
                        case "CENTER":
                            dropWall();
                            //mergeDrept(Direction.BACKWARD, 5, 1);
                            mergeDrept(Direction.RIGHT, 80, 1);
                            //mergeDrept(Direction.LEFT, 20, 1);
                            mergeDrept(Direction.FORWARD, 45, 1);

                            rotate(-135,1);
                            raiseWall();
                            deployBrick(1500, Direction.FORWARD);
                            robot.servoBox.setPosition(0.1);
                            //sleep(500);
                            deployBrick(1500, Direction.BACKWARD);

                            //mergeDrept(Direction.BACKWARD, 200, 1);
                            complete = true;
                            break;
                        case "RIGHT":
                            dropWall();
                            mergeDrept(Direction.BACKWARD, 40, 1);
                            //mergeDrept(Direction.FORWARD, 40, 1);
                            mergeDrept(Direction.RIGHT, 70,1);
                            raiseWall();
                            rotate(-45, 1);
                            mergeDrept(Direction.FORWARD, 20,1);

                            //mergeDrept(Direction.FORWARD, 80, 1);
                            //rotate(-105,1);
                            //mergeDrept(Direction.FORWARD,60,1);
                            deployBrick(1500, Direction.FORWARD);
                            robot.servoBox.setPosition(0.1);
                            //sleep(500);
                            deployBrick(1500, Direction.BACKWARD);

                            //mergeDrept(Direction.BACKWARD, 200, 1);
                            complete = true;
                            break;
                    }


                }

            }

            if (tfod != null) {
                tfod.shutdown();
            }
        }

    }


    private void initVuforia() throws VuforiaException {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraName = hardwareMap.get(WebcamName.class, "Webcam1");


        //parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() throws NullPointerException     {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minimumConfidence=0.4d;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public String getGoldOrePosition() throws Resources.NotFoundException {
        List<Recognition> recognitions = null;

        int goldX = -1;
        int silver1X = -1;
        int silver2X = -1;

        //CameraDevice.getInstance().setFlashTorchMode(true);

        while (silver1X == -1) {
            recognitions = tfod.getUpdatedRecognitions();

            if (recognitions != null) {/*
                if (recognitions.size() == 2) {
                    for (Recognition recognition : recognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldX = (int) recognition.getRight();
                        } else if (silver1X == -1) {
                            silver1X = (int) recognition.getRight();
                        } else {
                            silver2X = (int) recognition.getRight();
                        }
                    }
                }
                */
                if (recognitions.size() == 3) {
                    for (Recognition recognition : recognitions) {
                        if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                            goldX = (int) recognition.getLeft();
                        } else if (silver1X == -1) {
                            silver1X = (int) recognition.getLeft();
                        } else {
                            silver2X = (int) recognition.getLeft();
                        }
                    }
                    if (goldX != -1 && silver1X != -1 && silver2X != -1) {
                        if (goldX < silver1X && goldX < silver2X) {
                            telemetry.addData("Gold Mineral Position", "Left");
                            telemetry.update();
                            return "LEFT";

                        } else if (goldX > silver1X && goldX > silver2X) {
                            telemetry.addData("Gold Mineral Position", "Right");
                            telemetry.update();
                            return "RIGHT";

                        } else {
                            telemetry.addData("Gold Mineral Position", "Center");
                            telemetry.update();
                            return "CENTER";
                        }
                    }
                }
            }
        }

        return "UNKNOWN";


        //CameraDevice.getInstance().setFlashTorchMode(false);
/*
        telemetry.addData("G: ", Integer.toString(goldX));
        telemetry.addData("S1: ", Integer.toString(silver1X));
        telemetry.addData("S2: ", Integer.toString(silver2X));
        telemetry.update();

        //LEFT
        if (goldX == -1) {
            telemetry.addData("Gold Ore Position", "Left");
            telemetry.update();
            return "LEFT";
        }

        //RIGHT
        if (goldX > silver1X) {
            telemetry.addData("Gold Ore Position", "Right");
            telemetry.update();
            return "RIGHT";
        }

        //CENTER
        telemetry.addData("Gold Ore Position", "Center");
        telemetry.update();
        return "CENTER";*/
    }

    public void dropWall() throws NullPointerException {
        robot.servoWall.setPosition(0.5);
    }

    public void raiseWall() throws NullPointerException {
        robot.servoWall.setPosition(0);
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
            robot.motorFrontLeft.setPower(args[0]);
            robot.motorFrontRight.setPower(args[1]);
            robot.motorBackLeft.setPower(args[2]);
            robot.motorBackRight.setPower(args[3]);
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
            robot.motorFrontLeft.setTargetPosition(robot.motorFrontLeft.getCurrentPosition() + args[0]);
            robot.motorFrontRight.setTargetPosition(robot.motorFrontRight.getCurrentPosition() + args[1]);
            robot.motorBackLeft.setTargetPosition(robot.motorBackLeft.getCurrentPosition() + args[2]);
            robot.motorBackRight.setTargetPosition(robot.motorBackRight.getCurrentPosition() + args[3]);
        }
        else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    private void resetTargetPosition() {
        robot.motorFrontLeft.setTargetPosition(robot.motorFrontLeft.getCurrentPosition());
        robot.motorFrontRight.setTargetPosition(robot.motorFrontRight.getCurrentPosition());
        robot.motorBackLeft.setTargetPosition(robot.motorBackLeft.getCurrentPosition());
        robot.motorBackRight.setTargetPosition(robot.motorBackRight.getCurrentPosition());
    }

    private boolean isBusy() {
        return robot.motorFrontLeft.isBusy() &&
                robot.motorFrontRight.isBusy() &&
                robot.motorBackLeft.isBusy() &&
                robot.motorBackRight.isBusy();
    }

    /**
     * Sets the power of the drive train motors to max
     */
    private void startMotors() {
        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPower(DRIVE_SPEED, DRIVE_SPEED, DRIVE_SPEED, DRIVE_SPEED);
    }

    /**
     * Stops the drive train motors
     */
    private void stopMotors() {
        setPower(0, 0, 0, 0);

        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /**
     * Determines the error between the target angle and the robot's current heading.
     * @param   targetAngle  Desired angle (relative to global reference established at last Gyro Reset).
     * @return  Degrees in the range +/- 180. Centered on the robot's frame of reference.
     */
    private double getError(double targetAngle) {
        double error;
        error = targetAngle - robot.gyro.getIntegratedZValue();
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
     * The robot starts moving
     * @param direction One of 4 cardinal directions in which the robot starts moving
     * @param unit The unit of measure for the specified distance
     * @param distance The distance the robot will move
     */
    public void move (Direction direction, DistanceUnit unit, double distance) {
        double angle = robot.gyro.getIntegratedZValue();
        double error;
        double steer;
        double speed1;
        double speed2;

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
                modifyTargetPosition(-modifier, modifier, modifier, -modifier);
                break;
            case RIGHT:
                modifyTargetPosition(modifier, -modifier, -modifier, modifier);
                break;
        }

        startMotors();

        while (isBusy()) {
            error = getError(angle);

            if (error < HEADING_THRESHOLD)
                error = 0;

            steer = getSteer(error, P_DRIVE_COEFF);

            speed1 = DRIVE_SPEED - steer;
            speed2 = DRIVE_SPEED + steer;

            double max = Math.max(Math.abs(speed1), Math.abs(speed2));
            if (max > 1.0)
            {
                speed1 /= max;
                speed2 /= max;
            }

            switch (direction) {
                default:
                case FORWARD:
                    setPower(speed1, speed2, speed1, speed2);
                    break;
                case BACKWARD:
                    setPower(speed2, speed1, speed2, speed1);
                    break;
                case LEFT:
                    setPower(speed2, speed2, speed1, speed1);
                    break;
                case RIGHT:
                    setPower(speed1, speed1, speed2, speed2);
                    break;
            }
        }

        stopMotors();
    }

    public void mergeDrept(Direction direction, double distance, double power){
        robot.gyro.resetZAxisIntegrator();

        robot.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.waitForTick(40);


        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        distance = Math.abs(distance);
        int modifier = (int)(robot.distanceUnit.toInches(distance) * COUNTS_PER_INCH);


        double steer;
        double error;
        double max;
        double speed1;
        double speed2;
        double encoderValue1 = 0;
        double encoderValue2 = 0;

        int target=robot.gyro.getHeading();
        target=(target<=180)?target:(360-target);


        telemetry.addData("Direction:", direction);
        telemetry.update();


        while (opModeIsActive() && ((encoderValue1 + encoderValue2) / 2 < modifier)){
            int currentHeading = robot.gyro.getHeading();

            error = getError(target);
            steer = getSteer(error, P_DRIVE_COEFF);
            if (distance < 0)
                steer *= -1.0;

            switch (direction) {
                default:
                case FORWARD:
                    speed1 = power - steer;
                    speed2 = power + steer;
                    max = Math.max(Math.abs(speed1), Math.abs(speed2));
                    if (max > 1.0)
                    {
                        speed1 /= max;
                        speed2 /= max;
                    }
                    encoderValue1=(Math.abs(robot.motorFrontLeft.getCurrentPosition())+Math.abs(robot.motorBackLeft.getCurrentPosition()))/2;
                    encoderValue2=(Math.abs(robot.motorFrontRight.getCurrentPosition())+Math.abs(robot.motorBackRight.getCurrentPosition()))/2;
                    setPower(speed1, speed2, speed1, speed2);
                    break;
                case BACKWARD:
                    speed1 = power + steer;
                    speed2 = power - steer;
                    max = Math.max(Math.abs(speed1), Math.abs(speed2));
                    if (max > 1.0)
                    {
                        speed1 /= max;
                        speed2 /= max;
                    }
                    encoderValue1=(Math.abs(robot.motorFrontLeft.getCurrentPosition())+Math.abs(robot.motorBackLeft.getCurrentPosition()))/2;
                    encoderValue2=(Math.abs(robot.motorFrontRight.getCurrentPosition())+Math.abs(robot.motorBackRight.getCurrentPosition()))/2;
                    setPower(-speed1, -speed2, -speed1, -speed2);
                    break;
                case LEFT:
                    speed1 = power + steer;
                    speed2 = power - steer;
                    max = Math.max(Math.abs(speed1), Math.abs(speed2));
                    if (max > 1.0)
                    {
                        speed1 /= max;
                        speed2 /= max;
                    }
                    encoderValue1=(Math.abs(robot.motorFrontLeft.getCurrentPosition())+Math.abs(robot.motorBackLeft.getCurrentPosition()))/2;
                    encoderValue2=(Math.abs(robot.motorFrontRight.getCurrentPosition())+Math.abs(robot.motorBackRight.getCurrentPosition()))/2;
                    setPower(-speed1, speed1, speed2, -speed2);
                    break;
                case RIGHT:
                    speed1 = power - steer;
                    speed2 = power + steer;
                    max = Math.max(Math.abs(speed1), Math.abs(speed2));
                    if (max > 1.0)
                    {
                        speed1 /= max;
                        speed2 /= max;
                    }
                    encoderValue1=(Math.abs(robot.motorFrontLeft.getCurrentPosition())+Math.abs(robot.motorBackLeft.getCurrentPosition()))/2;
                    encoderValue2=(Math.abs(robot.motorFrontRight.getCurrentPosition())+Math.abs(robot.motorBackRight.getCurrentPosition()))/2;
                    setPower(speed1, -speed1, -speed2, speed2);
                    break;
            }

            telemetry.addData("Gyro Target:", target);
            telemetry.addData("Gyro Heading:", currentHeading);
            telemetry.addData("Modifier:",modifier);
            telemetry.addData("Enc1:", encoderValue1);
            telemetry.addData("Enc2:", encoderValue2);
            telemetry.update();
        }
        // Stop all motion;
        stopMotors();

    }

    /**
     * The robot starts rotating
     * @param rotation The sense of rotation in which the robot starts moving
     * @param unit The unit of measure for the specified angle
     * @param angle The amount the robot will rotate
     */
    private void rotate(int degrees, double power)
    {

        robot.gyro.resetZAxisIntegrator();

        robot.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.waitForTick(40);


        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double  leftPower, rightPower;
        int     targetAngle;

        // resetare gyro la zero
        robot.gyro.resetZAxisIntegrator();
        robot.waitForTick(40);

        // Gyro returneaza 0->359 cand se roteste la stanga si 359->0 la dreapta

        if (degrees < 0)
        {   // rotire dreapta.
            leftPower = power;
            rightPower = -power;
            targetAngle = 360 + degrees;    // gradele sunt – pt rot dreapta
        }
        else if (degrees > 0)
        {   // rotire stanga.
            leftPower = -power;
            rightPower = power;
            targetAngle = degrees;
        }
        else return;

        // setare putere motoare.
        robot.motorFrontLeft.setPower(leftPower);
        robot.motorBackLeft.setPower(leftPower);
        robot.motorFrontRight.setPower(rightPower);
        robot.motorBackRight.setPower(rightPower);

        // rotire pana la unghiul dorit.
        if (degrees < 0)
        {
            // La dreapta tb depasita valoarea 0.
            while (opModeIsActive() && robot.gyro.getHeading() == 0) {
                telemetry.addData("target","%d grade",targetAngle);
                telemetry.addData("heading1","%d grade", robot.gyro.getHeading() );
                telemetry.update();
                idle();

            }

            while (opModeIsActive() && robot.gyro.getHeading() > targetAngle) {
                telemetry.addData("target","%d grade",targetAngle);
                telemetry.addData("heading-","%d grade", robot.gyro.getHeading() );
                telemetry.update();
                idle();
            }

        }
        else
            while (opModeIsActive() && robot.gyro.getHeading() < targetAngle) {
                telemetry.addData("target","%d grade",targetAngle);
                telemetry.addData("heading+","%d grade", robot.gyro.getHeading() );
                telemetry.update();
                idle();
            }


        // opreste motoarele.
        /*
        robot.motorFrontLeft.setPower(0);
        robot.motorBackLeft.setPower(0);
        robot.motorFrontRight.setPower(0);
        robot.motorBackRight.setPower(0);
        */
        stopMotors();

    }

    public void deployBrick(int counts, Direction direction) throws NullPointerException {
        int currentCounts = 0;
        final double ROTATION_SPEED = 0.3d;

        robot.motorRotation.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorRotation.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        switch (direction) {
            case FORWARD:
                robot.motorRotation.setPower(ROTATION_SPEED);
                break;
            case BACKWARD:
                robot.motorRotation.setPower(-ROTATION_SPEED);
                break;
            default:
                robot.motorRotation.setPower(0);
                break;
        }

        while(currentCounts < counts) {
            double servoPosition = Range.scale(robot.motorRotation.getCurrentPosition(), 0, 2100, 0, 0.7);
            robot.servoBox.setPosition(servoPosition);

            currentCounts = Math.abs(robot.motorRotation.getCurrentPosition());
            robot.waitForTick(40);
        }

        robot.motorRotation.setPower(0);

    }

    public void arcRotate(double R, double L, double alpha, double D, double power, Direction direction) {
        double enc1 = Math.abs((COUNTS_PER_MOTOR_REV * R * alpha) / (180 * D));
        double enc2 = Math.abs((COUNTS_PER_MOTOR_REV * (R + L) * alpha) / (180 * D));

        boolean rotationComplete = false;

        double powerLeft;
        double powerRight;


        double leftEnc = 0;
        double rightEnc = 0;

        robot.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.waitForTick(40);

        robot.motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //setPower(power, power, power, power);



        switch (direction) {
            case LEFT:
                powerLeft = (L+R)/(R*power);
                powerRight = (2*L+R)/(R*power);

                setPower(powerLeft, powerRight, powerLeft, powerRight);

                while(opModeIsActive() && (leftEnc + rightEnc / 2  ) < (enc1 + enc2)) {
                    leftEnc = (robot.motorFrontLeft.getCurrentPosition() + robot.motorBackLeft.getCurrentPosition()) / 2;
                    rightEnc = (robot.motorFrontRight.getCurrentPosition() + robot.motorBackRight.getCurrentPosition()) / 2;

                    robot.motorFrontLeft.setTargetPosition((int) enc1);
                    robot.motorBackLeft.setTargetPosition((int) enc1);

                    robot.motorFrontRight.setTargetPosition((int) enc2);
                    robot.motorBackRight.setTargetPosition((int) enc2);
                }
                break;
            case RIGHT:
                powerLeft = (2*L+R)/(R*power);
                powerRight = (L+R)/(R*power);

                setPower(powerLeft, powerRight, powerLeft, powerRight);

                while(opModeIsActive() && (leftEnc + rightEnc) / 2  < (enc1 + enc2)) {
                    leftEnc = (robot.motorFrontLeft.getCurrentPosition() + robot.motorBackLeft.getCurrentPosition()) / 2;
                    rightEnc = (robot.motorFrontRight.getCurrentPosition() + robot.motorBackRight.getCurrentPosition()) / 2;
                    robot.motorFrontLeft.setTargetPosition((int) enc2);
                    robot.motorBackLeft.setTargetPosition((int) enc2);

                    robot.motorFrontRight.setTargetPosition((int) enc1);
                    robot.motorBackRight.setTargetPosition((int) enc1);
                }
                break;
        }

    }
}
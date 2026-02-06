package org.firstinspires.ftc.teamcode.teamcode.Active;

import android.annotation.SuppressLint;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;
import org.firstinspires.ftc.teamcode.teamcode.ToggleServo;

import java.util.Locale;

@TeleOp(name = "Decode2026 (AndroidStudio)", group = "Active")
public class Decode2026 extends LinearOpMode {
    Configuration.Decode2026 cfg = new Configuration.Decode2026();

    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    // Create time variables and set them to current time
    private double lastTimeDrive = lastElapsed;
    // private double lastTimeTurn = lastElapsed; // Currently unused, no smoothing on turning
    private double lastTimeStrafe = lastElapsed;

    // Convert a boolean to a 1 or 0, (1=true,0=false)
    public double boolToNumber(boolean x) {
        if (x) return 1;
        else return 0;
    }

    public double circleCurve(double x) { // Follows the curve of a quarter circle with radius 1
        return 1 - Math.sqrt(1 - Math.pow(x, 2));
    }

    // Quick control overview:
    // y / triangle = reverse mode toggle
    // right & left bumpers = sweeper control
    // a/cross = spool flywheels
    // x/square = toggle kickstand
    @Override
    public void runOpMode() {
        //Create a new base drivetrain3
        boolean reverse = false; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap, cfg, telemetry);

        int flywheelSpeed = 20;
        boolean readyPlayed = false; // Used to rumble controller when flywheel speed target hit

        @SuppressLint("DiscouragedApi") final int forwardModeID = hardwareMap.appContext.getResources().getIdentifier("forwardmode", "raw", hardwareMap.appContext.getPackageName());
        @SuppressLint("DiscouragedApi") final int reverseModeID = hardwareMap.appContext.getResources().getIdentifier("reversemode", "raw", hardwareMap.appContext.getPackageName());

        //Motors
        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        DcMotor sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        //Servo
        ToggleServo standLeft = new ToggleServo(hardwareMap.get(Servo.class, "servoL"));
        ToggleServo standRight = new ToggleServo(hardwareMap.get(Servo.class, "servoR"));
        Servo hoodLeft = hardwareMap.get(Servo.class, "hoodL");
        Servo hoodRight = hardwareMap.get(Servo.class, "hoodR");
        Servo intakeBlocker = hardwareMap.get(Servo.class, "intakeBlocker");
        // Sensors
        // -- Main Hub
        Rev2mDistanceSensor leftDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceL");
        Rev2mDistanceSensor rightDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceR");
        NormalizedColorSensor roofColorSensor = hardwareMap.get(RevColorSensorV3.class, "roof");
        // -- Expansion Hub
        NormalizedColorSensor rearColorSensor = hardwareMap.get(RevColorSensorV3.class, "rear");
        Rev2mDistanceSensor turretRangefinder = hardwareMap.get(Rev2mDistanceSensor.class, "rangefinder");

        boolean stepFlywheelspeed = false;
        int lastFly1 = 0;
        int lastFly2 = 0;

        turret.setDirection(DcMotor.Direction.REVERSE);
        flywheel2.setDirection(DcMotor.Direction.REVERSE);
        hoodLeft.setDirection(Servo.Direction.REVERSE);

        flywheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            // Drive time scaling
            double timeScaleDrive;
            if (Math.abs(gamepad1.left_stick_y) == 0) lastTimeDrive = runtime.milliseconds();

            timeScaleDrive = (runtime.milliseconds() - lastTimeDrive) / cfg.timeToMaxScale;
            timeScaleDrive = Range.clip(timeScaleDrive, 0, 1);

            //Turn time scaling
            double timeScaleTurn = 1;

            // Strafe time scaling
            double timeScaleStrafe;
            if (Math.abs(gamepad1.left_stick_x) == 0) {
                lastTimeStrafe = runtime.milliseconds();
            }

            //Turn runtime (ms) into percentage of goal time
            timeScaleStrafe = (runtime.milliseconds() - lastTimeStrafe) / cfg.timeToMaxScale;
            timeScaleStrafe = Range.clip(timeScaleStrafe, 0, 1);

            //Movement variables, all clamped
            double drive = Range.clip(-gamepad1.left_stick_y * cfg.scaleDrive, -cfg.maxDrive, cfg.maxDrive) * timeScaleDrive;
            double turn = Range.clip(gamepad1.right_stick_x * cfg.scaleTurn, -cfg.maxTurn, cfg.maxTurn) * timeScaleTurn;
            double strafe = Range.clip(gamepad1.left_stick_x * cfg.scaleStrafe, -cfg.maxStrafe, cfg.maxStrafe) * timeScaleStrafe;

            // Reverse driving mode logic
            if (!gamepad1.y) _reverse = false;
            if (reverse) {
                drive = -drive;
                strafe = -strafe;
            }

            //Send control values to the basic Mecanum Drivetrain
            mbs.setPower(drive, turn, -strafe);
            // Reverse controls with toggle button
            if (gamepad1.y) {
                if (!_reverse) {
                    _reverse = true;
                    reverse = !reverse;
                    SoundPlayer.getInstance().stopPlayingAll();
                    if (reverse) {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, reverseModeID);
                    } else {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, forwardModeID);
                    }
                }
            }

            // Feed/Sweep up system
            sweeper.setPower(gamepad1.right_bumper ? 1 : gamepad1.left_bumper ? -1 : 0);
            //Turret
            double leftPower = circleCurve(gamepad1.left_trigger);
            double rightPower = circleCurve(gamepad1.right_trigger);
            if (turret.getCurrentPosition() > 1000) {
                rightPower = 0;
            }
            if (turret.getCurrentPosition() < -1500) {
                leftPower = 0;
            }
            turret.setPower(rightPower - leftPower);

            intakeBlocker.setPosition(gamepad1.a ? 1 : -1);

            // Flywheel
            int delta1 = flywheel1.getCurrentPosition() - lastFly1;
            int delta2 = flywheel2.getCurrentPosition() - lastFly2;
            lastFly1 = flywheel1.getCurrentPosition();
            lastFly2 = flywheel2.getCurrentPosition();

            // Rumble when target speed achieved
            if (Math.abs(delta1) > flywheelSpeed && Math.abs(delta2) > flywheelSpeed) { // Play sound when flywheels are ready
                if (!readyPlayed) {
                    readyPlayed = true;
                    gamepad1.rumble(500);
                }
                flywheel1.setPower(0.35);
                flywheel2.setPower(0.35);
            } else {
                double power = ((flywheelSpeed - (Math.abs((double) delta1) + Math.abs((double) delta2)) / 2) / 10) + 0.3;
                flywheel1.setPower(gamepad1.a ? power : 0);
                flywheel2.setPower(gamepad1.a ? power : 0);
                readyPlayed = false;
            }

            // Toggle kickstand servos
            if (gamepad1.x) {
                if (!standLeft.getDebounce()) {
                    standLeft.setState(!standLeft.getState());
                    standLeft.setDebounce(true);
                    standLeft.setPosition(boolToNumber(standLeft.getState()));
                    standRight.setPosition(boolToNumber(!standLeft.getState()));
                }
            } else {
                standLeft.setDebounce(false);
            }

            if (gamepad1.dpad_up) {
                hoodLeft.setPosition(1);
                hoodRight.setPosition(1);
            }
            if (gamepad1.dpad_down) {
                hoodLeft.setPosition(-1);
                hoodRight.setPosition(-1);
            }

            if (gamepad1.dpadRightWasPressed()) {
                flywheelSpeed += 1;
            }
            if (gamepad1.dpadLeftWasPressed()) {
                flywheelSpeed -= 1;
            }

            telemetry.addLine();
            telemetry.addData("turret", turret.getCurrentPosition());
            telemetry.addData("hood", hoodLeft.getPosition());
            telemetry.addLine();
            telemetry.addData("target", flywheelSpeed);
            telemetry.addData("flywheel1", flywheel1.getPower());
            telemetry.addData("flywheel2", flywheel2.getPower());
            telemetry.addLine();
            telemetry.addData("delta1", delta1);
            telemetry.addData("delta2", delta2);
            telemetry.addLine();
            telemetry.addData("LD", leftDistanceSensor.getDistance(DistanceUnit.CM));
            telemetry.addData("RD", rightDistanceSensor.getDistance(DistanceUnit.CM));
            NormalizedRGBA rcs = roofColorSensor.getNormalizedColors();
            String something = String.format(Locale.ENGLISH,"%f,%f,%f",rcs.red,rcs.green,rcs.blue);
            telemetry.addData("RC",something);
            telemetry.addData("Rangefinder", turretRangefinder.getDistance(DistanceUnit.CM));
            telemetry.addData("BC", rearColorSensor.getNormalizedColors().toColor());
            telemetry.update();
        }
    }
}
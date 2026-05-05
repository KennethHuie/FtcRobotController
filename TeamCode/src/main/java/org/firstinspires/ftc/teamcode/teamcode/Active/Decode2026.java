package org.firstinspires.ftc.teamcode.teamcode.Active;

import android.annotation.SuppressLint;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.teamcode.Components.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.Components.Flywheel;
import org.firstinspires.ftc.teamcode.teamcode.Components.MecanumBase;
import org.firstinspires.ftc.teamcode.teamcode.Components.ToggleServo;
import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;

@TeleOp(name = "Decode2026 (AndroidStudio)", group = "Active")
public class Decode2026 extends LinearOpMode {
    Configuration.Decode2026 cfg = new Configuration.Decode2026();

    GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager(); // Bylazar panels

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
    // b/circle = auto-aim turret
    @Override
    public void runOpMode() {
        //Create a new base drivetrain3
        boolean reverse = false; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap, cfg, telemetry);

        double flywheelSpeed = 30;

        boolean readyPlayed = false; // Used to rumble controller when flywheel speed target hit

        @SuppressLint("DiscouragedApi") final int forwardModeID = hardwareMap.appContext.getResources().getIdentifier("forwardmode", "raw", hardwareMap.appContext.getPackageName());
        @SuppressLint("DiscouragedApi") final int reverseModeID = hardwareMap.appContext.getResources().getIdentifier("reversemode", "raw", hardwareMap.appContext.getPackageName());

        Turret turret = new Turret(hardwareMap);
        //Motors
        final Flywheel flywheels = new Flywheel(hardwareMap, runtime);
        flywheels.flywheel2.setDirection(DcMotor.Direction.REVERSE);
        DcMotor sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        //Servo
        ToggleServo standLeft = new ToggleServo(hardwareMap.get(Servo.class, "servoL"));
        ToggleServo standRight = new ToggleServo(hardwareMap.get(Servo.class, "servoR"));
        Servo hoodLeft = hardwareMap.get(Servo.class, "hoodL");
        Servo hoodRight = hardwareMap.get(Servo.class, "hoodR");
        Servo intakeBlocker = hardwareMap.get(Servo.class, "intakeBlocker");
        // Sensors
        // -- Main Hub
        Rev2mDistanceSensor leftDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceL");
        NormalizedColorSensor roofColorSensor = hardwareMap.get(RevColorSensorV3.class, "roof");
        // -- Expansion Hub
        NormalizedColorSensor rearColorSensor = hardwareMap.get(RevColorSensorV3.class, "rear");

        boolean stepFlywheelspeed = false;
        int lastFly1 = 0;
        int lastFly2 = 0;

        double lastPoll = runtime.milliseconds();
        double leftDistance = leftDistanceSensor.getDistance(DistanceUnit.CM);
        NormalizedRGBA roofColor = roofColorSensor.getNormalizedColors();
        NormalizedRGBA rearColor = rearColorSensor.getNormalizedColors();

        turret.motor.setDirection(DcMotor.Direction.REVERSE);
        hoodLeft.setDirection(Servo.Direction.REVERSE);
        turret.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            Gamepad Gamepad1 = gpm1.asCombinedFTCGamepad(gamepad1);
            // Drive time scaling
            double timeScaleDrive;
            if (Math.abs(Gamepad1.left_stick_y) == 0) lastTimeDrive = runtime.milliseconds();

            timeScaleDrive = (runtime.milliseconds() - lastTimeDrive) / cfg.timeToMaxScale;
            timeScaleDrive = Range.clip(timeScaleDrive, 0, 1);

            //Turn time scaling
            double timeScaleTurn = 1;

            // Strafe time scaling
            double timeScaleStrafe;
            if (Math.abs(Gamepad1.left_stick_x) == 0) {
                lastTimeStrafe = runtime.milliseconds();
            }

            //Turn runtime (ms) into percentage of goal time
            timeScaleStrafe = (runtime.milliseconds() - lastTimeStrafe) / cfg.timeToMaxScale;
            timeScaleStrafe = Range.clip(timeScaleStrafe, 0, 1);

            //Movement variables, all clamped
            double drive = Range.clip(-Gamepad1.left_stick_y * cfg.scaleDrive, -cfg.maxDrive, cfg.maxDrive) * timeScaleDrive;
            double turn = Range.clip(Gamepad1.right_stick_x * cfg.scaleTurn, -cfg.maxTurn, cfg.maxTurn) * timeScaleTurn;
            double strafe = Range.clip(Gamepad1.left_stick_x * cfg.scaleStrafe, -cfg.maxStrafe, cfg.maxStrafe) * timeScaleStrafe;

            // Reverse driving mode logic
            if (!Gamepad1.y) _reverse = false;
            if (reverse) {
                drive = -drive;
                strafe = -strafe;
            }

            //Send control values to the basic Mecanum Drivetrain
            mbs.setPower(drive, turn, -strafe);
            // Reverse controls with toggle button
            if (Gamepad1.y) {
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
            sweeper.setPower(Gamepad1.right_bumper ? 1 : Gamepad1.left_bumper ? -1 : 0);
            intakeBlocker.setPosition(Gamepad1.left_bumper ? -1 : Gamepad1.a ? 1 : -1);

            //Hood
            if (gamepad1.dpadLeftWasReleased()) {
                flywheelSpeed -= 2.5;
            }
            if (gamepad1.dpadRightWasReleased()) {
                flywheelSpeed += 2.5;
            }

            //Turret
            double leftPower = 0;
            double rightPower = 0;

            if (Gamepad1.left_trigger + Gamepad1.right_trigger != 0) { // Input override
                leftPower = Gamepad1.left_trigger > 0 ? circleCurve(Gamepad1.left_trigger) : leftPower;
                rightPower = Gamepad1.right_trigger > 0 ? circleCurve(Gamepad1.right_trigger) : rightPower;
                turret.setTargetVelocity(rightPower - leftPower);
            } else if (Gamepad1.a) { //Lock onto the closest tag
                turret.trackTag(20); // blu
                turret.trackTag(24); // reb
            } else {
                turret.setTargetVelocity(0); // Reset if neither condition is met
                turret.trackTag(20); // blu
                turret.trackTag(24); // reb
            }

            // Flywheel
            int delta1 = flywheels.flywheel1.getCurrentPosition() - lastFly1;
            int delta2 = flywheels.flywheel2.getCurrentPosition() - lastFly2;
            lastFly1 = flywheels.flywheel1.getCurrentPosition();
            lastFly2 = flywheels.flywheel2.getCurrentPosition();

            // Rumble when target speed achieved
            if (Math.abs(delta1) > flywheelSpeed && Math.abs(delta2) > flywheelSpeed) {
                if (!readyPlayed) {
                    readyPlayed = true;
                    Gamepad1.rumble(500);
                }
                flywheels.setPower(Gamepad1.a ? 0.35 : 0.3);
            } else {
                double power = ((flywheelSpeed - (Math.abs((double) delta1) + Math.abs((double) delta2)) / 2) / 10) + 0.3;
                flywheels.setPower(Gamepad1.a ? power : 0.3);
                readyPlayed = false;
            }

            if (lastPoll < runtime.milliseconds() && Gamepad1.a) {
                lastPoll = runtime.milliseconds() + 50;
                leftDistance = leftDistanceSensor.getDistance(DistanceUnit.CM);
                roofColor = roofColorSensor.getNormalizedColors();
                rearColor = rearColorSensor.getNormalizedColors();
            }

            // Toggle kickstand servos
            if (Gamepad1.x) {
                if (!standLeft.getDebounce()) {
                    standLeft.setState(!standLeft.getState());
                    standLeft.setDebounce(true);
                    standLeft.setPosition(boolToNumber(standLeft.getState()));
                    standRight.setPosition(boolToNumber(!standLeft.getState()));
                }
            } else {
                standLeft.setDebounce(false);
            }

            if (Gamepad1.dpad_up) {
                hoodLeft.setPosition(1);
                hoodRight.setPosition(1);
            }
            if (Gamepad1.dpad_down) {
                hoodLeft.setPosition(-1);
                hoodRight.setPosition(-1);
            }

            telemetry.addLine();
            telemetry.addData("turret", turret.motor.getCurrentPosition());
            telemetry.addData("hood", hoodLeft.getPosition());
            telemetry.addLine();
            telemetry.addData("target", flywheelSpeed);
            telemetry.addData("flywheel1", flywheels.flywheel1.getPower());
            telemetry.addData("flywheel2", flywheels.flywheel2.getPower());
            telemetry.addLine();
            telemetry.addData("delta1", delta1);
            telemetry.addData("delta2", delta2);
            telemetry.addLine();
            telemetry.addData("leftDistance", leftDistance);
            telemetry.addData("roofColor", roofColor);
            telemetry.addData("rearColor", rearColor);
            PanelsTelemetry.INSTANCE.getTelemetry().update(telemetry);
        }
    }
}
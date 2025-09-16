package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;
import org.firstinspires.ftc.teamcode.teamcode.ToggleServo;

// Flag FTC-Dashboard
@Config
// Set name of OpMode
@TeleOp(name = "MecanumV2 (AndroidStudio)", group = "Prototype")
public class MecanumV2 extends LinearOpMode {
    Configuration.MecanumV2 cfg = new Configuration.MecanumV2();

    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    private static final double bucketWristRange = 0.5;
    private static final double grabberRange = 0.2;
    // Create time variables and set them to current time
    private double lastTimeDrive = lastElapsed;
    // private double lastTimeTurn = lastElapsed; // Currently unused, no smoothing on turning
    private double lastTimeStrafe = lastElapsed;

    final int maxHeight = -10000;
    final int maxLength = 5500;
    private boolean heightLimit = false;
    private boolean lengthLimit = false;
    boolean dbl = false;
    boolean dbr = false;

    int step = 0;
    final int[] test = {
            0, // i0 - Initial height
            -250, // i1 - Wall Height
            -2000, // i2 - Low Bucket
            -5150, // i4 - Specimen Bar
            -7150 // i5 - High Bucket
    };

    // Convert a boolean to a 1 or 0, (1=true,0=false)
    public double boolToNumber(boolean x) {
        if (x) return 1;
        else return 0;
    }

    @Override
    public void runOpMode() {
        // Play hello moto
        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,hardwareMap.appContext.getResources().getIdentifier("bomb", "raw", hardwareMap.appContext.getPackageName()));
        //SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,hardwareMap.appContext.getResources().getIdentifier("moto", "raw", hardwareMap.appContext.getPackageName()));
        int forwardModeID = hardwareMap.appContext.getResources().getIdentifier("forwardmode", "raw", hardwareMap.appContext.getPackageName());
        int reverseModeID = hardwareMap.appContext.getResources().getIdentifier("reversemode", "raw", hardwareMap.appContext.getPackageName());
        int grabberOpenID = hardwareMap.appContext.getResources().getIdentifier("grabberopen", "raw", hardwareMap.appContext.getPackageName());
        int grabberClosedID = hardwareMap.appContext.getResources().getIdentifier("grabberclosed", "raw", hardwareMap.appContext.getPackageName());
        //Create a new base drivetrain3
        boolean reverse = false; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap,cfg,telemetry);

        // Find motor instances on initialization
        DcMotor vsLeftMotor = hardwareMap.get(DcMotor.class,"vsLeftMotor");
        DcMotor vsRightMotor = hardwareMap.get(DcMotor.class,"vsRightMotor");
        DcMotor hsMotor = hardwareMap.get(DcMotor.class,"hsMotor");

        // Define servos
        ToggleServo bucketServo = new ToggleServo(hardwareMap.get(Servo.class, "bucket"));
        ToggleServo wristServoRight = new ToggleServo(hardwareMap.get(Servo.class, "bucketWristRight"));
        ToggleServo wristServoLeft = new ToggleServo(hardwareMap.get(Servo.class, "bucketWristLeft"));
        ToggleServo grabberServo = new ToggleServo(hardwareMap.get(Servo.class, "grabber"));
        CRServo sweeper = hardwareMap.get(CRServo.class,"sweeper");

        // Set direction of motors
        vsRightMotor.setDirection(DcMotor.Direction.REVERSE);
        bucketServo.setDirection(Servo.Direction.REVERSE);
        bucketServo.setState(true);

        //Grab on init
        grabberServo.setPosition(0);

        // Vertical slider zeroing
        vsRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vsRightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        vsLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        vsLeftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {

            // Drive time scaling
            double timeScaleDrive;
            if (Math.abs(gamepad1.left_stick_y) == 0) lastTimeDrive = runtime.milliseconds();

            timeScaleDrive = (runtime.milliseconds()-lastTimeDrive)/cfg.timeToMaxScale;
            timeScaleDrive = Range.clip(timeScaleDrive,0,1);

            //Turn time scaling
            double timeScaleTurn = 1;

            // Strafe time scaling
            double timeScaleStrafe;
            if (Math.abs(gamepad1.left_stick_x) == 0) {
                lastTimeStrafe = runtime.milliseconds();
            }

            //Turn runtime (ms) into percentage of goal time
            timeScaleStrafe = (runtime.milliseconds()-lastTimeStrafe)/cfg.timeToMaxScale;
            timeScaleStrafe = Range.clip(timeScaleStrafe,0,1);

            //Movement variables, all clamped
            double drive = Range.clip(-gamepad1.left_stick_y * cfg.scaleDrive, -cfg.maxDrive, cfg.maxDrive) * timeScaleDrive;
            double turn = Range.clip(gamepad1.right_stick_x * cfg.scaleTurn, -cfg.maxTurn, cfg.maxTurn) * timeScaleTurn;
            double strafe = Range.clip(gamepad1.left_stick_x * cfg.scaleStrafe, -cfg.maxStrafe, cfg.maxStrafe) * timeScaleStrafe;

            double percentageDriveStrafe = Math.abs(drive / strafe);
            double percentageStrafeDrive = Math.abs(strafe / drive);

            //Round inputs if over a certain threshold, helps with maintaining straight lines
            if (percentageDriveStrafe > cfg.minLStickOverridePerc) {
                strafe = 0;
                telemetry.addData("Debug","Drive over Strafe active");
            } else if (percentageStrafeDrive > cfg.minLStickOverridePerc) {
                drive = 0;
                telemetry.addData("Debug","Strafe over Drive active");
            }
            if (gamepad1.y) {
                if (!_reverse) {
                    _reverse = true;
                    reverse = !reverse;
                    SoundPlayer.getInstance().stopPlayingAll();
                    if (reverse) {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,reverseModeID);
                    } else {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,forwardModeID);
                    }
                }
            }

            if (!gamepad1.y) {
                _reverse = false;
            }

            if (reverse) {
                drive = -drive;
                strafe = -strafe;
            }

            //Send control values to the basic Mecanum Drivetrain
            mbs.setPower(drive,turn,strafe);

            /* todo:
             - Create fixed height points that the sides automatically change between
             - Make variables 'not public'
             - Make the code more pleasing and add comments
             */
            //Determines the power for the vertical lift slide motors
            heightLimit = vsLeftMotor.getCurrentPosition() <= maxHeight;
            //double verticalSlidePower = (boolToNumber((gamepad1.left_bumper))-boolToNumber(gamepad1.right_bumper&&!heightLimit));
            double a = boolToNumber(vsLeftMotor.getCurrentPosition() < (test[step]-150)); // Lower
            double b = boolToNumber(vsLeftMotor.getCurrentPosition() > (test[step]+150)); // Higher
            double verticalSlidePower = (a-b);
            if (gamepad1.right_bumper) {
                if (!dbr) {
                    if (step < test.length-1) {
                        step += 1;
                    }
                }
                dbr = true;
            } else {
                dbr = false;
            }
            if (gamepad1.left_bumper) {
                if (!dbl) {
                    if (step > 0) {
                        step -= 1;
                    }
                }
                dbl = true;
            } else {
                dbl = false;
            }
            int value = test[step];

            /*
            val = false
            if gamepad {
                if !db {
                    val = true
                }
                db = true
            } else {
                db = false
            }
            */
            //Determines the power for the Intake slider motor
            lengthLimit = hsMotor.getCurrentPosition() >= maxLength;
            double horizontalSlidePower=0;
            if (!lengthLimit) {
                horizontalSlidePower = -(gamepad1.left_trigger - gamepad1.right_trigger);
            } else {
                horizontalSlidePower = -gamepad1.left_trigger;
            }

            if (gamepad1.a) { // Toggle Bucket flip state
                if (!bucketServo.getDebounce()) bucketServo.setState(!bucketServo.getState());
                bucketServo.setDebounce(true);
                bucketServo.setPosition(boolToNumber(bucketServo.getState()));
                bucketServo.setPosition(boolToNumber(bucketServo.getState()));
            }
            if (!gamepad1.a) {bucketServo.setDebounce(false);} // Reset when let go

            if (gamepad1.x) { // Toggle Intake flip state
                if (!wristServoRight.getDebounce()) wristServoRight.setState(!wristServoRight.getState());
                wristServoRight.setDebounce(true);

                if (wristServoRight.getState() && bucketServo.getState()) {
                    bucketServo.setPosition(0.725);
                }
                if (!wristServoRight.getState() && bucketServo.getState()) {
                    bucketServo.setPosition(1);
                }

                wristServoRight.setPosition(boolToNumber(wristServoRight.getState())*bucketWristRange);
                wristServoLeft.setPosition(1-boolToNumber(wristServoRight.getState())*bucketWristRange);
            }
            if (!gamepad1.x) {wristServoRight.setDebounce(false);} // Reset when let go

            if (gamepad1.b) { // Toggle Grabber state
                if (!grabberServo.getDebounce()) {
                    grabberServo.setState(!grabberServo.getState());
                    SoundPlayer.getInstance().stopPlayingAll();
                    if (grabberServo.getState()) {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,grabberOpenID);
                    } else {
                        SoundPlayer.getInstance().startPlaying(hardwareMap.appContext,grabberClosedID);

                    }
                }
                grabberServo.setDebounce(true);
                grabberServo.setPosition(boolToNumber(grabberServo.getState())*grabberRange);
            }
            if (!gamepad1.b) {grabberServo.setDebounce(false);} // Reset when let go

            // Sweeper logic
            if (gamepad1.dpad_up) {
                sweeper.setPower(1);
            } else if (gamepad1.dpad_down) {
                sweeper.setPower(-1);
            } else {
                sweeper.setPower(0);
            }

            // Set motor power
            vsLeftMotor.setPower(verticalSlidePower); // Synchronize Left & Right slide
            vsRightMotor.setPower(verticalSlidePower);
            // Some kind of attempt to equalize the slide positions - Kenneth, 4/24/2025
            if (verticalSlidePower == 0) {
                double balancing = (double)(vsLeftMotor.getCurrentPosition()-vsRightMotor.getCurrentPosition())/150;
                vsRightMotor.setPower(balancing);
                vsLeftMotor.setPower(-balancing);
            }
            hsMotor.setPower(horizontalSlidePower);
            // Set servo power
            telemetry.addData("A",a);
            telemetry.addData("B",b);
            telemetry.addData("S",step);
            telemetry.addData("V:",a-b);

            //Reverse Mode:
            if (!reverse) telemetry.addData("FORWARD","MODE");
            if (reverse) telemetry.addData("REVERSE","MODE");

            // Fast Flags
            if (cfg.controllerAxesDebug) {
                telemetry.addData("Left Stick", "X: " + gamepad1.left_stick_x);
                telemetry.addData("Left Stick", "Y: " + gamepad1.left_stick_y);
                telemetry.addData("Right Stick", "X: " + gamepad1.right_stick_x);
                telemetry.addData("Right Stick", "Y: " + gamepad1.right_stick_y);
            }
            if (cfg.appliedDriveValuesDebug) {
                telemetry.addData("drive", drive);
                telemetry.addData("turn", turn);
                telemetry.addData("strafe", strafe);
            }
            if (cfg.timeScalePerAxesDebug) {
                telemetry.addData("drive scale", timeScaleDrive);
                telemetry.addData("turn scale", timeScaleTurn);
                telemetry.addData("strafe scale", timeScaleStrafe);
            }
            if (cfg.sliderMotorPowerDebug) {
                telemetry.addData("vertical", verticalSlidePower);
                telemetry.addData("horizontal", horizontalSlidePower);
            }
            if (cfg.sliderEncoderDebug) {
                telemetry.addData("LeftSlide",vsLeftMotor.getCurrentPosition());
                telemetry.addData("RightSlide",vsRightMotor.getCurrentPosition());
            }
            if (cfg.runtimeDebug) telemetry.addData("Status","Run Time: " + runtime);
            telemetry.update();
        }
    }
}

package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.acmerobotics.dashboard.config.Config;
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

    // Convert a boolean to a 1 or 0, (1=true,0=false)
    public double boolToNumber(boolean x) {
        if (x) return 1;
        else return 0;
    }

    @Override
    public void runOpMode() {
        //Create a new base drivetrain
        boolean reverse = false; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap,cfg,telemetry);

        // Find motor instances on initialization
        DcMotor vsLeftMotor = hardwareMap.get(DcMotor.class,"vsLeftMotor");
        DcMotor vsRightMotor = hardwareMap.get(DcMotor.class,"vsRightMotor");
        DcMotor hsMotor = hardwareMap.get(DcMotor.class,"hsMotor");

        // Define servos
        ToggleServo bucketServo = new ToggleServo(hardwareMap.get(Servo.class, "bucket"));
        ToggleServo wristServo = new ToggleServo(hardwareMap.get(Servo.class, "bucketWrist"));
        ToggleServo grabberServo = new ToggleServo(hardwareMap.get(Servo.class, "grabber"));
        CRServo sweeper = hardwareMap.get(CRServo.class,"sweeper");

        // Set direction of motors
        vsRightMotor.setDirection(DcMotor.Direction.REVERSE);
        bucketServo.setDirection(Servo.Direction.REVERSE);
        bucketServo.setState(true);

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
                }
            }
            if (!gamepad1.y) {
                _reverse = false;
            }

            if (reverse) {
                drive=-drive;
                strafe=-strafe;
            }

            //Send control values to the basic Mecanum Drivetrain
            mbs.setPower(drive,turn,strafe);

            //Determines the power for the vertical lift slide motors
            double verticalSlidePower = (boolToNumber(gamepad1.left_bumper)-boolToNumber(gamepad1.right_bumper));
            //Determines the power for the Intake slider motor
            double horizontalSlidePower = -(gamepad1.left_trigger-gamepad1.right_trigger);

            if (gamepad1.a) { // Toggle Bucket flip state
                if (!bucketServo.getDebounce()) bucketServo.setState(!bucketServo.getState());
                bucketServo.setDebounce(true);
            }
            if (!gamepad1.a) {bucketServo.setDebounce(false);} // Reset when let go

            if (gamepad1.x) { // Toggle Intake flip state
                if (!wristServo.getDebounce()) wristServo.setState(!wristServo.getState());
                wristServo.setDebounce(true);
            }
            if (!gamepad1.x) {wristServo.setDebounce(false);} // Reset when let go

            if (gamepad1.b) { // Toggle Grabber state
                if (!grabberServo.getDebounce()) grabberServo.setState(!grabberServo.getState());
                grabberServo.setDebounce(true);
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
            hsMotor.setPower(horizontalSlidePower);
            // Set servo power
            bucketServo.setPosition(boolToNumber(bucketServo.getState()));
            wristServo.setPosition(boolToNumber(wristServo.getState())*bucketWristRange);
            grabberServo.setPosition(boolToNumber(grabberServo.getState())*grabberRange);

            //Reverse Mode:
            if (!reverse) telemetry.addData("FORWARD","MODE");
            if (reverse) telemetry.addData("REVERSE","MODE");
            //1/25/2025 Debug sweeper
            telemetry.addData("Sweeper", sweeper.getPower());

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

            if (cfg.runtimeDebug) telemetry.addData("Status","Run Time: " + runtime);
            telemetry.update();
        }
    }
}

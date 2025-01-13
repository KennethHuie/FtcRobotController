package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;

@TeleOp(name = "MecanumV2 (AndroidStudio)", group = "Prototype")
public class MecanumV2 extends LinearOpMode {
    Configuration.MecanumV2 cfg = new Configuration.MecanumV2();

    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    private double lastTimeDrive = lastElapsed;
    // private double lastTimeTurn = lastElapsed; // Currently unused, no smoothing on turning
    private double lastTimeStrafe = lastElapsed;

    public double boolToNumber(boolean x) {
        if (x) return 1;
        else return 0;
    }

    @Override
    public void runOpMode() {
        MecanumBase mbs = new MecanumBase(hardwareMap,cfg,telemetry);
        // Find motor instances on initialization
        DcMotor vsLeftMotor = hardwareMap.get(DcMotor.class,"vsLeftMotor");
        DcMotor vsRightMotor = hardwareMap.get(DcMotor.class,"vsRightMotor");
        vsRightMotor.setDirection(DcMotor.Direction.REVERSE);

        //Servo
        boolean state = false;
        boolean debounce = false;
        Servo bucketServo = hardwareMap.get(Servo.class, "bucket");

        //DcMotor hsMotor = hardwareMap.get(DcMotor.class,"hsMotor");

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {

            // Drive time scaling
            double timeScaleDrive;
            if (Math.abs(gamepad1.left_stick_y) == 0) lastTimeDrive = runtime.milliseconds();

            timeScaleDrive = (runtime.milliseconds()-lastTimeDrive)/cfg.timeToMaxScale;
            timeScaleDrive = Range.clip(timeScaleDrive,0,1);

            // Strafe time scaling
            double timeScaleStrafe;
            if (Math.abs(gamepad1.left_stick_x) == 0) {
                lastTimeStrafe = runtime.milliseconds();
            }
            timeScaleStrafe = (runtime.milliseconds()-lastTimeStrafe)/cfg.timeToMaxScale;
            timeScaleStrafe = Range.clip(timeScaleStrafe,0,1);

            double drive = Range.clip(-gamepad1.left_stick_y * cfg.scaleDrive, -cfg.maxDrive, cfg.maxDrive) * timeScaleDrive;
            double timeScaleTurn = 1;
            double turn = Range.clip(gamepad1.right_stick_x * cfg.scaleTurn, -cfg.maxTurn, cfg.maxTurn) * timeScaleTurn;
            double strafe = Range.clip(gamepad1.left_stick_x * cfg.scaleStrafe, -cfg.maxStrafe, cfg.maxStrafe) * timeScaleStrafe;

            double percentageDriveStrafe = Math.abs(drive / strafe);
            double percentageStrafeDrive = Math.abs(strafe / drive);

            if (percentageDriveStrafe > cfg.minLStickOverridePerc) {
                strafe = 0;
                telemetry.addData("Debug","Drive over Strafe active");
            } else if (percentageStrafeDrive > cfg.minLStickOverridePerc) {
                drive = 0;
                telemetry.addData("Debug","Strafe over Drive active");
            }

            mbs.setPower(drive,turn,strafe);

            double verticalSlidePower = (boolToNumber(gamepad1.left_bumper)-boolToNumber(gamepad1.right_bumper));
            double horizontalSlidePower = (gamepad1.left_trigger-gamepad1.right_trigger);

            if (!gamepad1.a) {
                debounce = false;
            }
            if (gamepad1.a) {
                if (!debounce) state = !state;
                debounce = true;
            }

            telemetry.addData("Servo",boolToNumber(state));

            //hsMotor.setPower(horizontalSlidePower);
            vsLeftMotor.setPower(verticalSlidePower);
            vsRightMotor.setPower(verticalSlidePower);
            bucketServo.setPosition(boolToNumber(state));

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

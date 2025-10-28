package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;

@TeleOp(name = "Decode2026 (AndroidStudio)", group = "Active")
public class Decode2026 extends LinearOpMode {
    Configuration.Decode2026 cfg = new Configuration.Decode2026();

    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    // Create time variables and set them to current time
    private double lastTimeDrive = lastElapsed;
    // private double lastTimeTurn = lastElapsed; // Currently unused, no smoothing on turning
    private double lastTimeStrafe = lastElapsed;

    @Override
    public void runOpMode() {
        //Create a new base drivetrain3
        boolean reverse = false; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap, cfg, telemetry);

        int forwardModeID = hardwareMap.appContext.getResources().getIdentifier("forwardmode", "raw", hardwareMap.appContext.getPackageName());
        int reverseModeID = hardwareMap.appContext.getResources().getIdentifier("reversemode", "raw", hardwareMap.appContext.getPackageName());

        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        flywheel2.setDirection(DcMotor.Direction.REVERSE);

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

            intake.setPower(gamepad1.x ? -1 : gamepad1.a ? 1 : 0);
            flywheel1.setPower(gamepad1.b ? 1 : 0);
            flywheel2.setPower(gamepad1.b ? 1 : 0);

            if (!gamepad1.y) _reverse = false;


            if (reverse) {
                drive = -drive;
                strafe = -strafe;
            }

            //Send control values to the basic Mecanum Drivetrain
            mbs.setPower(drive, turn, -strafe);

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

            telemetry.addData("intake", intake.getPower());
            telemetry.addData("flywheel1", flywheel1.getPower());
            telemetry.addData("flywheel2", flywheel2.getPower());
            telemetry.update();
        }
    }
}
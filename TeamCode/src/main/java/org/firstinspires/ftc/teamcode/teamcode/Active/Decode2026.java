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
        boolean reverse = true; // Reverse drive mode
        boolean _reverse = false;// Debounce
        MecanumBase mbs = new MecanumBase(hardwareMap, cfg, telemetry);

        int sweeperSpeed = 0;
        boolean _sweeper = false;

        int forwardModeID = hardwareMap.appContext.getResources().getIdentifier("forwardmode", "raw", hardwareMap.appContext.getPackageName());
        int reverseModeID = hardwareMap.appContext.getResources().getIdentifier("reversemode", "raw", hardwareMap.appContext.getPackageName());

        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        DcMotor sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        DcMotor feed = hardwareMap.get(DcMotor.class, "feed");

        int lastFly1 = 0;
        int lastFly2 = 0;

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

            // Reverse driving mode logic
            if (!gamepad1.y) _reverse = false;
            if (reverse) {
                drive = -drive;
                strafe = -strafe;
            }

            // Sweeper control logic
            if (!_sweeper) {
                if (gamepad1.dpad_up && sweeperSpeed < 1) {
                    _sweeper = true;
                    sweeperSpeed += 1;
                }
                if (gamepad1.dpad_down && sweeperSpeed > -1) {
                    _sweeper = true;
                    sweeperSpeed -= 1;
                }
            }
            if (!(gamepad1.dpad_up || gamepad1.dpad_down)) {
                _sweeper = false;
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

            sweeper.setPower(sweeperSpeed);
            double rt = gamepad1.right_trigger;
            feed.setPower(-rt);
            flywheel1.setPower(rt + (gamepad1.right_bumper ? 1 : 0)); // Force spooling to manage less buttons simultaneously
            flywheel2.setPower(rt + (gamepad1.right_bumper ? 1 : 0));

            int delta1 = flywheel1.getCurrentPosition() - lastFly1;
            int delta2 = flywheel2.getCurrentPosition() - lastFly2;
            int diff = delta1 - delta2;
            lastFly1 = flywheel1.getCurrentPosition();
            lastFly2 = flywheel2.getCurrentPosition();

            telemetry.addLine();
            telemetry.addData("flywheel1", flywheel1.getPower());
            telemetry.addData("flywheel2", flywheel2.getPower());
            telemetry.addData("sweeper", sweeperSpeed);
            telemetry.addData("feed", feed.getPower());
            telemetry.addLine();
            telemetry.addData("delta1", delta1);
            telemetry.addData("delta2", delta2);
            telemetry.addData("diff", diff);
            telemetry.update();
        }
    }
}
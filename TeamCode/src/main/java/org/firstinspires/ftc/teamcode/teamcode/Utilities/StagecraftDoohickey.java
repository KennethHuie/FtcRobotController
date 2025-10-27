package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "StagecraftDoohickey (AndroidStudio)", group = "Utilities")
public class StagecraftDoohickey extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    //private final double lastElapsed = runtime.milliseconds();

    @Override
    public void runOpMode() {
        DcMotor Slider = hardwareMap.get(DcMotor.class,"slider");

        boolean toggle = false;
        boolean debounce = false;

        boolean cycle = false; // false = bottom, true = top
        boolean switchCycle = true;
        double nextTime = runtime.milliseconds() + 15000;
        final double maxlimit = 10000;

        Slider.setDirection(DcMotor.Direction.FORWARD);

        Slider.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Slider.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            if (gamepad1.a) {
                if (!debounce) {
                    debounce = true;
                    toggle = !toggle;
                }
            } else {
                debounce = false;
            }

            if (toggle) {
                if (switchCycle) {
                    cycle = !cycle;
                    switchCycle = false;
                    nextTime = runtime.milliseconds() + 15000;
                }
            }

            if (runtime.milliseconds() - nextTime > 0) {
                switchCycle = true;
            }

            double current = Slider.getCurrentPosition();
            double power = cycle ? maxlimit-current : 100 - current;
            Slider.setPower(power*-Math.abs(power)/10);

            Slider.setPower(gamepad1.left_stick_y);

            telemetry.addData("Diff", (runtime.milliseconds()-nextTime)/1000);
            telemetry.addData("Cycle", cycle);
            telemetry.addData("Set to", current);
            telemetry.addData("Toggle", toggle);
            telemetry.addData("Encoder", current);
            telemetry.update();
        }
    }
}
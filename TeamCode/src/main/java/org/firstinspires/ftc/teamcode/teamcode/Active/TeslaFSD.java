package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;

@Autonomous(name = "TeslaFSD (AndroidStudio)", group = "Prototype")
public class TeslaFSD extends LinearOpMode {
    Configuration.MecanumV2 cfg = new Configuration.MecanumV2();
    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    @Override
    public void runOpMode() {
        MecanumBase mbs = new MecanumBase(hardwareMap,cfg,telemetry);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            if (runtime.milliseconds() < 1000) { // Set power for 1 second
                mbs.setPower(-0.3, 0, 0);
            }
            if (runtime.milliseconds() > 3000) { // Stop after 1 second
                mbs.setPower(0, 0, 0);
            }
            telemetry.addData("Runtime",runtime.milliseconds());
            telemetry.update();
        }
    }
}

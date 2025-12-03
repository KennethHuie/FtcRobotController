package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "FlywheelTest (AndroidStudio)", group = "Utilities")
public class FlywheelTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    //private final double lastElapsed = runtime.milliseconds();

    @Override
    public void runOpMode() {
        boolean toggle = false;
        boolean _toggle = false;
        boolean MANUALOVERRIDE = true;

        DcMotor FlyWheel1 = hardwareMap.get(DcMotor.class, "fly1");
        FlyWheel1.setDirection(DcMotor.Direction.FORWARD);
        FlyWheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FlyWheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            if (Math.abs(gamepad1.left_stick_y) > 0) {
                MANUALOVERRIDE = true;
            }
            if (gamepad1.a) {
                MANUALOVERRIDE = false;
            }

            if (gamepad1.a && !_toggle) {
                _toggle = true;
                toggle = !toggle;
            }
            if (!gamepad1.a) {
                _toggle = false;
            }

            double openPosition = 10000;
            if (!MANUALOVERRIDE) {
                if (toggle) {
                    // OPEN POSITION
                    double div = (openPosition - FlyWheel1.getCurrentPosition()) / 1000;
                    double rounded = Math.round(div);
                    FlyWheel1.setPower(Math.min(rounded * 1000,0.5));
                } else {
                    // CLOSED POSITION
                    double div = (double) -FlyWheel1.getCurrentPosition() / 1000;
                    double rounded = Math.round(div);
                    FlyWheel1.setPower(Math.max(rounded * 1000,-0.358));
                }
            } else {
                FlyWheel1.setPower(gamepad1.left_stick_y/2);
            }

            telemetry.addData("Fly1", FlyWheel1.getPower());
            telemetry.addData("Encoder", FlyWheel1.getCurrentPosition());
            telemetry.addData("Toggle", toggle);
            telemetry.addData("MANUAL OVERRIDE", MANUALOVERRIDE);
            telemetry.addData("Reminder ⚠", "Up = CLOSE, Down = OPEN");
            telemetry.update();
        }
    }
}
package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "FlywheelTest2 (AndroidStudio)", group = "Utilities")
public class FlywheelTest2 extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    //private final double lastElapsed = runtime.milliseconds();

    boolean flydir1 = false;
    boolean flydir2 = false;
    boolean _db1 = false;
    boolean _db2 = false;

    @Override
    public void runOpMode() {
        DcMotor FlyWheel1 = hardwareMap.get(DcMotor.class, "fly1");
        DcMotor FlyWheel2 = hardwareMap.get(DcMotor.class, "fly2");
        FlyWheel1.setDirection(DcMotor.Direction.FORWARD);
        FlyWheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FlyWheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            // fly1 dir
            if (gamepad1.x) {
                if (!_db1) {
                    _db1 = true;
                    flydir1 = !flydir1;
                }
            } else {
                _db1 = false;
            }
            if (flydir1) {
                FlyWheel1.setDirection(DcMotor.Direction.FORWARD);
            }else {
                FlyWheel1.setDirection(DcMotor.Direction.REVERSE);
            }
            // fly2 dir
            if (gamepad1.b) {
                if (!_db2) {
                    _db2 = true;
                    flydir2 = !flydir2;
                }
            } else {
                _db2 = false;
            }
            if (flydir2) {
                FlyWheel2.setDirection(DcMotor.Direction.FORWARD);
            }else {
                FlyWheel2.setDirection(DcMotor.Direction.REVERSE);
            }

            // set power
            FlyWheel1.setPower(gamepad1.right_trigger);
            FlyWheel2.setPower(gamepad1.left_trigger);

            // data
            telemetry.addData("FlyPower1",gamepad1.right_trigger);
            telemetry.addData("FlyPower2",gamepad1.left_trigger);
            telemetry.addData("FlyDir1",flydir1);
            telemetry.addData("FlyDir2",flydir2);
            telemetry.update();
        }
    }
}
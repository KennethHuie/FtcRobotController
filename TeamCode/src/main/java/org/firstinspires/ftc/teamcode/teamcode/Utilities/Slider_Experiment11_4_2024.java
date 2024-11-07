package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class Slider_Experiment11_4_2024 extends LinearOpMode {
    public void runOpMode() {
        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "drive");
        waitForStart();
        while (opModeIsActive()) {
            motor.setPower(gamepad1.right_trigger-gamepad1.left_trigger);
            telemetry.addData("Current Power",motor.getPower());
            telemetry.update();
        }
    }
}

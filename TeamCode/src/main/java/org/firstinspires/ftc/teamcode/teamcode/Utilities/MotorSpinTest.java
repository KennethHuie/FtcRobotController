package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "MotorSpinTest (AndroidStudio)", group = "Utilities")
public class MotorSpinTest extends LinearOpMode{
    @Override
    public void runOpMode() {
        DcMotor motor = hardwareMap.get(DcMotor.class, "motor");
        waitForStart();
        while (opModeIsActive()) {
            // control the motor with gamepad

            double power = gamepad1.left_stick_y;
            motor.setPower(power * 0.5);
        }
    }
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "JaydenV1", group = "Prototype")

public class JaydenV1 extends LinearOpMode {
    private DcMotor FR = null;
    private DcMotor FL = null;
    private DcMotor RR = null;
    private DcMotor RL = null;
    public void runOpMode() {
        waitForStart();
        while (opModeIsActive()) {
        FR = hardwareMap.get(DcMotor.class, "FR_motor");
        FL = hardwareMap.get(DcMotor.class, "FL_motor");
        RR = hardwareMap.get(DcMotor.class, "RR_motor");
        RL = hardwareMap.get(DcMotor.class, "RL_motor");
        FR.setPower(gamepad1.left_stick_y + 2 * gamepad1.right_stick_x);
        FL.setPower(gamepad1.left_stick_y - 2 * gamepad1.right_stick_x);
        RL.setPower(gamepad1.left_stick_y + 2 * gamepad1.right_stick_x);
        RR.setPower(gamepad1.left_stick_y - 2 *gamepad1.right_stick_x);
        }
    }
}
package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "WheelConfigDebug (AndroidStudio)",group = "Utilities")
public class MotorConfigurationDebug extends LinearOpMode {

    @Override
    public void runOpMode() {
        DcMotor FL_Motor = hardwareMap.get(DcMotor .class, "FL_Motor");
        DcMotor RL_Motor = hardwareMap.get(DcMotor.class, "RL_Motor");
        DcMotor FR_Motor = hardwareMap.get(DcMotor.class, "FR_Motor");
        DcMotor RR_Motor = hardwareMap.get(DcMotor.class, "RR_Motor");

        FR_Motor.setDirection(DcMotorSimple.Direction.REVERSE);
        RR_Motor.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        while (opModeIsActive()) {
            FL_Motor.setPower(0);
            RL_Motor.setPower(0);
            FR_Motor.setPower(0);
            RR_Motor.setPower(0);
            if (gamepad1.x) {
                FL_Motor.setPower(1);
                telemetry.addData("FL Active","");
            }
            if (gamepad1.a) {
                RL_Motor.setPower(1);
                telemetry.addData("RL Active","");
            }
            if (gamepad1.y) {
                FR_Motor.setPower(1);
                telemetry.addData("FR Active","");
            }
            if (gamepad1.b) {
                RR_Motor.setPower(1);
                telemetry.addData("RR Active","");
            }
        }
    }
}

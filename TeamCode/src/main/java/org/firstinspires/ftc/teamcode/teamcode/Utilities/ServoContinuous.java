package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@TeleOp(name = "servoContinuous",group = "Utilities")
public class ServoContinuous extends LinearOpMode {
    @Override
    public void runOpMode() {
        CRServo servo = hardwareMap.get(CRServo.class, "servo");
        waitForStart();
        while (opModeIsActive()){
            servo.setPower(1);
        }
    }
}
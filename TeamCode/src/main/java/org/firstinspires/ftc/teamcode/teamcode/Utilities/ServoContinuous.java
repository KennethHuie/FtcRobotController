package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "servoContinuous",group = "Testing")
public class ServoContinuous extends LinearOpMode {
    private CRServo servo = null;
    @Override
    public void runOpMode() {
        servo = hardwareMap.get(CRServo.class, "servo");
        waitForStart();
        while (opModeIsActive()){
            servo.setPower(1);
        }
    }
}
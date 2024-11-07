package org.firstinspires.ftc.teamcode.teamcode.WIP;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
@TeleOp(name = "Jayden", group = "Testing")
public class JaydenPlayground extends LinearOpMode {
    private DcMotor motor = null;
    private CRServo servo = null;
   public void runOpMode() {
       motor = hardwareMap.get(DcMotor.class,"motor");
       servo = hardwareMap.get(CRServo.class,"servo");
       waitForStart();
       while (opModeIsActive()) {
          motor.setPower(0);
          
          servo.setPower(gamepad1.right_stick_y);
       }
   }
}
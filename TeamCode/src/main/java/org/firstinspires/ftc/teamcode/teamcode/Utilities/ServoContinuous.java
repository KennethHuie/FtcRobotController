package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

@Config // Flag for FTC-Dashboard
@TeleOp(name = "servoContinuous",group = "Utilities")
public class ServoContinuous extends LinearOpMode {
    public static double servoPower = 0;
    public static String servoName = "servo";
    @Override
    public void runOpMode() {
        CRServo servo = hardwareMap.get(CRServo.class, servoName);
        waitForStart();
        while (opModeIsActive()){
            servo.setPower(servoPower);
        }
    }
}
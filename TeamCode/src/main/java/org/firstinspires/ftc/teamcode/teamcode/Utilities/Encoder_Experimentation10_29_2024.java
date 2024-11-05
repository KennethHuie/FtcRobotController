package org.firstinspires.ftc.teamcode.teamcode.Utilities;
import java.lang.Math;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "10/29/2024", group = "Testing")
public class Encoder_Experimentation10_29_2024 extends LinearOpMode {
    private DcMotorEx motor = null;
    
    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotorEx.class,"motor");
        
        motor.setTargetPosition(0);
        motor.setMode(com.qualcomm.robotcore.hardware.DcMotor.RunMode.RUN_TO_POSITION);
        motor.setVelocity(500);
        
        waitForStart();
        while (opModeIsActive()) {
            motor.setTargetPosition(90);
            
            telemetry.addData("current pos", motor.getCurrentPosition());
            telemetry.update();
        }
    }
}
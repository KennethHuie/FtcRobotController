package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;

@TeleOp(name = "ResetTurretEncoder", group = "Utilities")
public class ResetTurretEncoder extends LinearOpMode {
    @Override
    public void runOpMode() {
        Turret turret = new Turret(hardwareMap);
        telemetry.addData("current", turret.motor.getCurrentPosition());
        telemetry.update();
        turret.resetEncoder();
    }
}

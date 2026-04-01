package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "TurretCalibration", group = "Utilities")
public class TurretCalibration extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        PanelsTelemetry pt = PanelsTelemetry.INSTANCE;
        DcMotor turret = hardwareMap.get(DcMotor.class,"turret");
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setTargetPosition(0);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            telemetry.addData("turret",turret.getCurrentPosition());
            telemetry.update();
        }
    }
}
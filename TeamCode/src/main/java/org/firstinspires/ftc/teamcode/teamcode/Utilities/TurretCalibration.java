package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;


@TeleOp(name = "TurretCalibration", group = "Utilities")
public class TurretCalibration extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager();
        TelemetryManager pt = PanelsTelemetry.INSTANCE.getTelemetry();
        DcMotor turret = hardwareMap.get(DcMotor.class,"turret");
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            Gamepad Gamepad1 = gpm1.asCombinedFTCGamepad(gamepad1);
            turret.setPower(-Gamepad1.left_stick_x);
            pt.addData("turret",turret.getCurrentPosition());
            pt.update(telemetry);
        }
    }
}
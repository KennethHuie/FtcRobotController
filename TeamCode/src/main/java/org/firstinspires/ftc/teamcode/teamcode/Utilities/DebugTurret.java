package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;


@TeleOp(name = "DebugTurret", group = "Utilities")
public class DebugTurret extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();

    public double circleCurve(double x) { // Follows the curve of a quarter circle with radius 1
        return 1 - Math.sqrt(1 - Math.pow(x, 2));
    }

    @Override
    public void runOpMode() {
        TelemetryManager pt = PanelsTelemetry.INSTANCE.getTelemetry();
        GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager();
        Turret turret = new Turret(hardwareMap);
        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");

        camera.start();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            Gamepad Gamepad1 = gpm1.asCombinedFTCGamepad(gamepad1);
            //Turret
            double leftPower = Gamepad1.left_trigger > 0 ? circleCurve(Gamepad1.left_trigger) : 0;
            double rightPower = Gamepad1.right_trigger > 0 ? circleCurve(Gamepad1.right_trigger) : 0;
            turret.setTargetVelocity(rightPower - leftPower);
            if (Gamepad1.x) {
                pt.addData("tracking Blue", turret.trackTag(20).name());
            }
            if (Gamepad1.y) {
                pt.addData("tracking Red", turret.trackTag(24).name());
            }

            pt.addData("targetPower", rightPower - leftPower);
            pt.addData("turretPower", turret.motor.getPower());
            pt.addData("turretposition", turret.motor.getCurrentPosition());
            pt.update(telemetry);
        }
    }
}
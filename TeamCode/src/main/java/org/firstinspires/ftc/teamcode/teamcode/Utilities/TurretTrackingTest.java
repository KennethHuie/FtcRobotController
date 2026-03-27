package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.gamepad.GamepadManager;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "TurretTrackingTest", group = "Utilities")
public class TurretTrackingTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager();
    @Override
    public void runOpMode() {
        Limelight3A camera = hardwareMap.get(Limelight3A.class,"turretCam");
        camera.start();
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            Gamepad Gamepad1 = gpm1.asCombinedFTCGamepad(gamepad1);
            telemetry.update();
        }
    }
}
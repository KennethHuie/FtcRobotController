package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.gamepad.GamepadManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.bylazar.gamepad.PanelsGamepad;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Autonomous(name = "TurretTrackingTest", group = "Utilities")
public class TurretTrackingTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager();

    @Override
    public void runOpMode() {
        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");
        camera.start();
        waitForStart();
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        runtime.reset();
        while (opModeIsActive()) {
            Gamepad Gamepad1 = gpm1.asCombinedFTCGamepad(gamepad1);
            LLResult result = camera.getLatestResult();
            if (result != null && result.isValid()) {
                Pose3D botpose = result.getBotpose();
                turret.setPower(-result.getTx() / 10);
                if (turret.getCurrentPosition() > 1000) {
                    turret.setPower(0);
                }
                if (turret.getCurrentPosition() < -1500) {
                    turret.setPower(0);
                }
                telemetry.addData("X",result.getTx());
                telemetry.update();
            } else {
                turret.setPower(0);
            }
        }
    }
}
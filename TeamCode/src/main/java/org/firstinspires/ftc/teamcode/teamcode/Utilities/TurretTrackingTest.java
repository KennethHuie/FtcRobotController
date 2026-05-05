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
import org.firstinspires.ftc.teamcode.teamcode.Components.TrackStatus;
import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;

@Autonomous(name = "TurretTrackingTest", group = "Utilities")
public class TurretTrackingTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    GamepadManager gpm1 = PanelsGamepad.INSTANCE.getFirstManager();

    @Override
    public void runOpMode() {
        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");
        camera.start();
        waitForStart();
        Turret turret = new Turret(hardwareMap);
        runtime.reset();
        while (opModeIsActive()) {
            TrackStatus blue = turret.trackTag(20);
            TrackStatus red = turret.trackTag(24);
            telemetry.addData("status blue", blue.toString());
            telemetry.addData("status red", red.toString());
            telemetry.update();
        }
    }
}
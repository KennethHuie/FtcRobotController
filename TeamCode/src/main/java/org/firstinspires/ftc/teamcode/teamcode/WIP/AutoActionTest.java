package org.firstinspires.ftc.teamcode.teamcode.WIP;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.Components.Sweeper;
import org.firstinspires.ftc.teamcode.teamcode.Components.ToggleServo;
import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;

@Autonomous(name = "AutoActionTest", group = "Utilities")
public class AutoActionTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
        Pose2d initialPose = new Pose2d(36, 63, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        double flywheelSpeed = 30;

        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");
        //Motors
        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        Turret turret = new Turret(hardwareMap);
        //Servo
        ToggleServo standLeft = new ToggleServo(hardwareMap.get(Servo.class, "servoL"));
        ToggleServo standRight = new ToggleServo(hardwareMap.get(Servo.class, "servoR"));
        Servo hoodLeft = hardwareMap.get(Servo.class, "hoodL");
        Servo hoodRight = hardwareMap.get(Servo.class, "hoodR");
        Servo intakeBlocker = hardwareMap.get(Servo.class, "intakeBlocker");
        Sweeper sweeper = new Sweeper(hardwareMap);

        panelsTelemetry.getTelemetry().update(telemetry);
        waitForStart();
        Actions.runBlocking(
                drive.actionBuilder(initialPose)
                        //Decode Fast Preset
                        .stopAndAdd(sweeper.intake())
                        .waitSeconds(1)
                        .stopAndAdd(sweeper.stop())
                        .waitSeconds(1)
                        .stopAndAdd(turret.turnTo(45))
                        .build());

    }
}




package org.firstinspires.ftc.teamcode.teamcode.WIP;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.ToggleServo;

@Autonomous(name = "DecodeAutonomous", preselectTeleOp = "Decode2026")
public class DecodeAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(36, 63, Math.toRadians(-90));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        double flywheelSpeed = 30;

        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");
        //Motors
        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        DcMotor sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        DcMotor turret = hardwareMap.get(DcMotor.class, "turret");
        //Servo
        ToggleServo standLeft = new ToggleServo(hardwareMap.get(Servo.class, "servoL"));
        ToggleServo standRight = new ToggleServo(hardwareMap.get(Servo.class, "servoR"));
        Servo hoodLeft = hardwareMap.get(Servo.class, "hoodL");
        Servo hoodRight = hardwareMap.get(Servo.class, "hoodR");
        Servo intakeBlocker = hardwareMap.get(Servo.class, "intakeBlocker");

        class CollectStack implements Action {
            final Vector2d start;
            final Vector2d shootPos;
            final int dist;

            public CollectStack(Vector2d Start, Vector2d ShootPosition, int Distance) {
                start = Start;
                shootPos = ShootPosition;
                dist = Distance;
            }

            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                Actions.runBlocking(drive.actionBuilder(drive.pose)
                        .strafeTo(start) // ALIGN to 1st stack
                        .strafeTo(new Vector2d(start.x, start.y + dist)) // INTAKE 1st stack
                        .stopAndAdd(() -> {
                            sweeper.setPower(0);
                        })
                        .strafeTo(shootPos) // MOVE to the designated shooting area
                        .stopAndAdd(() -> {
                            sweeper.setPower(0);
                        })
                        .build());
                return true;
            }
        }

        waitForStart();
        Actions.runBlocking(
                drive.actionBuilder(initialPose)
                        //Decode Fast Preset
                        .strafeToLinearHeading(new Vector2d(0, 0), Math.toRadians(90)) // MOVE to standard shooting area
                        .waitSeconds(5) // SHOOT preloaded
                        .stopAndAdd(new CollectStack(new Vector2d(-12, 30), new Vector2d(-18, 24),15))
                        .waitSeconds(5) // SHOOT preloaded
                        .stopAndAdd(new CollectStack(new Vector2d(12, 30), new Vector2d(-18, 24),15))
                        .waitSeconds(5) // SHOOT 2nd stack
                        .stopAndAdd(new CollectStack(new Vector2d(34, 30), new Vector2d(-18, 24),15))
                        .waitSeconds(5) // SHOOT 3rd stack
                        .build());

    }
}




package org.firstinspires.ftc.teamcode.teamcode.WIP;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.Components.Sweeper;
import org.firstinspires.ftc.teamcode.teamcode.Components.Turret;

@Autonomous(name = "DecodeAutonomous", preselectTeleOp = "Decode2026")
public class DecodeAutonomous extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(60, -16, Math.toRadians(180));
        MecanumDrive drive = new MecanumDrive(hardwareMap, initialPose);

        double flywheelSpeed = 30;

        Limelight3A camera = hardwareMap.get(Limelight3A.class, "turretCam");
        //Motors
        DcMotor flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        DcMotor flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        Sweeper sweeper = new Sweeper(hardwareMap);
        Turret turret = new Turret(hardwareMap);
        //Servo
        Servo hoodLeft = hardwareMap.get(Servo.class, "hoodL");
        Servo hoodRight = hardwareMap.get(Servo.class, "hoodR");
        Servo intakeBlocker = hardwareMap.get(Servo.class, "intakeBlocker");

        class CollectStack implements Action {
            final Vector2d start;
            final int dist;

            public CollectStack(Vector2d Start, int Distance) {
                start = Start;
                dist = Distance;
            }

            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                Actions.runBlocking(
                        drive.actionBuilder(drive.localizer.getPose())
                                .strafeTo(start) // ALIGN to 1st stack
                                .stopAndAdd(new ParallelAction(
                                        sweeper.intake(),
                                        sweeper.startTrack()
                                ))
                                .strafeTo(new Vector2d(start.x, start.y + dist)) // INTAKE 1st stack
                                .stopAndAdd(new ParallelAction(
                                        sweeper.stop(),
                                        sweeper.stopTrack()
                                ))
                                .build()
                );
                return false;
            }
        }

        waitForStart();
        Actions.runBlocking(
                drive.actionBuilder(initialPose)
                        //Decode Fast Preset
                        .strafeToLinearHeading(new Vector2d(0, 0), Math.toRadians(90)) // MOVE to standard shooting area
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT preloaded
                        .stopAndAdd(new CollectStack(new Vector2d(-12, 30), 15))
                        .strafeTo(new Vector2d(-18, 24))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 1st stack
                        .stopAndAdd(new CollectStack(new Vector2d(12, 30), 15))
                        .strafeTo(new Vector2d(-18, 24))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 2nd stack
                        .stopAndAdd(new CollectStack(new Vector2d(34, 30), 15))
                        .strafeTo(new Vector2d(-18, 24))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 3rd stack
                        .build());

    }
}




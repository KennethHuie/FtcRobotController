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
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.teamcode.ToggleServo;
class Sweeper {
    DcMotor sweeper;

    public Sweeper(HardwareMap hardwareMap) {
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
    }

    class Intake implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            sweeper.setPower(1);
            return false;
        }
    }

    class Stop implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            sweeper.setPower(0);
            return false;
        }
    }

    public Action intake() {
        return new Intake();
    }

    public Action stop() {
        return new Stop();
    }
}

class Turret {
    int tolerance = 15;
    DcMotor turret;

    public Turret(HardwareMap hardwareMap) {
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setTargetPosition(turret.getCurrentPosition()); // Keep current encoder position
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    class TurnToAngle implements Action {
        int angle;

        public TurnToAngle(double a) {
            angle = (int) Math.round((50d / 3) * a);
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            telemetryPacket.put("target", angle);
            turret.setTargetPosition(angle);
            int current = turret.getCurrentPosition();
            telemetryPacket.put("current", current);
            return ((angle - 15) < current) && (angle < (angle + 15));
        }
    }

    public Action turnTo(double a) {
        return new TurnToAngle(a);
    }

    public void setTolerance(int x) {
        tolerance = x;
    }
}

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
                Actions.runBlocking(drive.actionBuilder(drive.localizer.getPose())
                        .strafeTo(start) // ALIGN to 1st stack
                        .stopAndAdd(sweeper.intake())
                        .strafeTo(new Vector2d(start.x, start.y + dist)) // INTAKE 1st stack
                        .stopAndAdd(sweeper.stop())
                        .strafeTo(shootPos) // MOVE to the designated shooting area
                        .build());
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
                        .stopAndAdd(new CollectStack(new Vector2d(-12, 30), new Vector2d(-18, 24),15))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 1st stack
                        .stopAndAdd(new CollectStack(new Vector2d(12, 30), new Vector2d(-18, 24),15))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 2nd stack
                        .stopAndAdd(new CollectStack(new Vector2d(34, 30), new Vector2d(-18, 24),15))
                        .stopAndAdd(turret.turnTo(45))
                        .waitSeconds(5) // SHOOT 3rd stack
                        .build());

    }
}




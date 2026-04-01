package org.firstinspires.ftc.teamcode.teamcode.WIP;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.bylazar.telemetry.PanelsTelemetry;
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
            sweeper.setPower(0.5);
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
        double angle;

        public TurnToAngle(double a) {
            angle = a;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            int rounded = (int) Math.round((50d / 3) * angle);
            turret.setTargetPosition(rounded);
            int current = turret.getCurrentPosition();
            telemetryPacket.put("target", current);
            return ((rounded - 15) < current) && (rounded < (rounded + 15));
        }
    }

    public Action turnTo(double a) {
        return new TurnToAngle(a);
    }

    public void setTolerance(int x) {
        tolerance = x;
    }
}

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

        telemetry.update();
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




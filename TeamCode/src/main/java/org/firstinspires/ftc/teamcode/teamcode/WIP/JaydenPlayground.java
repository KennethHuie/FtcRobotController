package org.firstinspires.ftc.teamcode.teamcode.WIP;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorREV2mDistance;

@TeleOp(name = "Jayden", group = "Testing")
public class JaydenPlayground extends LinearOpMode {
    private DcMotor RR = null;
    private DcMotor RL = null;
    private DcMotor FR = null;
    private DcMotor FL = null;
    private DcMotor feed = null;
    private DcMotor sweeper = null;
    private Servo servoL = null;
    private Servo servoR = null;
    private SensorREV2mDistance DistanceL = null;
    private SensorREV2mDistance DistanceR = null;
    private boolean pressing = false;
    private boolean toggle = false;
    private double boolToNum(boolean bool) {
        double correspNum = 0;
        if (bool) {
            correspNum++;
        }
        return correspNum;
    }
    public void runOpMode() {
        waitForStart();
        RR = hardwareMap.get(DcMotor.class, "RR_Motor");
        RL = hardwareMap.get(DcMotor.class, "RL_Motor");
        RL.setDirection(DcMotor.Direction.REVERSE);
        FR = hardwareMap.get(DcMotor.class, "FR_Motor");
        FR.setDirection(DcMotorSimple.Direction.REVERSE);
        FL = hardwareMap.get(DcMotor.class, "FL_Motor");
        feed = hardwareMap.get(DcMotor.class, "feed");
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        servoL = hardwareMap.get(Servo.class, "servoL");
        servoR = hardwareMap.get(Servo.class, "servoR");
        DistanceL = hardwareMap.get(SensorREV2mDistance.class, "distanceL");
        DistanceR = hardwareMap.get(SensorREV2mDistance.class, "distanceR");
        while (opModeIsActive()) {
            telemetry.addData("skibidi", DistanceL);
            if (gamepad1.y != pressing && gamepad1.y) {
                toggle = !toggle;
            }
            pressing = gamepad1.y;

            if (toggle) {
                servoL.setPosition(1);
                servoR.setPosition(-1);
            } else {
                servoL.setPosition(0.5);
                servoR.setPosition(0.5);
            }

            double lat = gamepad1.left_stick_x;
            double fbk = gamepad1.left_stick_y;
            double fd = boolToNum(gamepad1.x) - boolToNum(gamepad1.b);
            double sweepPower =  boolToNum(gamepad1.a);
            double rot = gamepad1.right_stick_x;

            FR.setPower((fbk - lat) + rot);
            FL.setPower((fbk + lat) - rot);
            RR.setPower((fbk + lat) + rot);
            RL.setPower((fbk - lat) - rot);
            feed.setPower(fd);
            sweeper.setPower(sweepPower);
        }
    }
}
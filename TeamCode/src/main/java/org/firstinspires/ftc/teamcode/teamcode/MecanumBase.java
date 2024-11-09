package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumBase {
    DcMotor FL_Motor;
    DcMotor RL_Motor;
    DcMotor FR_Motor;
    DcMotor RR_Motor;

    Configuration cfg;
    Telemetry telemetry;

    public MecanumBase(HardwareMap hardwareMap, Configuration c,Telemetry t) {
        FL_Motor = hardwareMap.get(DcMotor.class, "FL_Motor");
        RL_Motor = hardwareMap.get(DcMotor.class, "RL_Motor");
        FR_Motor = hardwareMap.get(DcMotor.class, "FR_Motor");
        RR_Motor = hardwareMap.get(DcMotor.class, "RR_Motor");

        cfg = c;
        telemetry = t;

        FL_Motor.setDirection(cfg.FL_Direction);
        RL_Motor.setDirection(cfg.RL_Direction);
        FR_Motor.setDirection(cfg.FR_Direction);
        RR_Motor.setDirection(cfg.RR_Direction);
    }

    public void setPower(double drive, double turn, double strafe) {
        // All set to positive drive
        // Left and Right side opposite turn
        // Diagonal pairs strafe
        double FL_Power = drive + turn + strafe;
        double RL_Power = drive + turn - strafe;
        double FR_Power = drive - turn - strafe;
        double RR_Power = drive - turn + strafe;
        FL_Motor.setPower(FL_Power);
        RL_Motor.setPower(RL_Power);
        FR_Motor.setPower(FR_Power);
        RR_Motor.setPower(RR_Power);

        if (cfg.motorPowerDebug) {
            telemetry.addData("FL Power",FL_Power);
            telemetry.addData("RL Power",RL_Power);
            telemetry.addData("FR Power",FR_Power);
            telemetry.addData("RR Power",RR_Power);
        }
        if (cfg.motorDirectionDebug) {
            telemetry.addData("FL Power",FL_Motor.getDirection());
            telemetry.addData("RL Power",RL_Motor.getDirection());
            telemetry.addData("FR Power",FR_Motor.getDirection());
            telemetry.addData("RR Power",RR_Motor.getDirection());
        }
    }
}

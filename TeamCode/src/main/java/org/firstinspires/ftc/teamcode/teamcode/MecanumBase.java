package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class MecanumBase {
    DcMotor FL_Motor;
    DcMotor RL_Motor;
    DcMotor FR_Motor;
    DcMotor RR_Motor;

    public MecanumBase(HardwareMap hardwareMap) {
        FL_Motor = hardwareMap.get(DcMotor.class, "FL_Motor");
        RL_Motor = hardwareMap.get(DcMotor.class, "RL_Motor");
        FR_Motor = hardwareMap.get(DcMotor.class, "FR_Motor");
        RR_Motor = hardwareMap.get(DcMotor.class, "RR_Motor");

        FL_Motor.setDirection(DcMotor.Direction.FORWARD);
        RL_Motor.setDirection(DcMotor.Direction.REVERSE);
        FR_Motor.setDirection(DcMotor.Direction.FORWARD);
        RR_Motor.setDirection(DcMotor.Direction.FORWARD);
    }

    public void setPower(double drive, double turn, double strafe) {
        FL_Motor.setPower(-drive + turn - strafe);
        RL_Motor.setPower(-drive + turn + strafe);
        FR_Motor.setPower(-drive - turn - strafe);
        RR_Motor.setPower(-drive - turn + strafe);
    }
}

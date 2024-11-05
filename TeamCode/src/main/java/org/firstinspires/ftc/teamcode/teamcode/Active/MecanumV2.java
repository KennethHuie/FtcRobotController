package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.qualcomm.robotcore.util.ElapsedTime;
import java.util.concurrent.TimeUnit;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "MecanumV2 (AndroidStudio)", group = "Prototype")
public class MecanumV2 extends LinearOpMode {
    final double scaleDrive = 1; // Scale drive stick axis.
    final double scaleTurn = 1; // Scale turn stick axis.
    final double scaleStrafe = 0.5; // Scale strafe stick axis.

    final double maxDrive = 1;// Maximum motor speed while driving.
    final double maxTurn = 1; // Maximum motor speed while turning.
    final double maxStrafe = 0.5; // Maximum motor speed while strafing.

    final double minLStickOverridePerc = 2; // How many times more the stick position has to be bigger than the other to override it

    final double timeToMaxScale = 150; // Time in ms to reach full speed

    private ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private double lastElapsed = runtime.milliseconds();
    
    private double lastTimeDrive = lastElapsed;
    private double lastTimeTurn = lastElapsed; // Currently unused, no smoothing on turning
    private double lastTimeStrafe = lastElapsed;

    @Override
    public void runOpMode() {
        
        // Find motor instances on initialization
        // Define motor instance variables
        DcMotor FL_Motor = hardwareMap.get(DcMotor.class, "FL_Motor");
        DcMotor RL_Motor = hardwareMap.get(DcMotor.class, "RL_Motor");
        DcMotor FR_Motor = hardwareMap.get(DcMotor.class, "FR_Motor");
        DcMotor RR_Motor = hardwareMap.get(DcMotor.class, "RR_Motor");
        
        FL_Motor.setDirection(DcMotor.Direction.FORWARD);
        RL_Motor.setDirection(DcMotor.Direction.REVERSE);
        FR_Motor.setDirection(DcMotor.Direction.REVERSE);
        RR_Motor.setDirection(DcMotor.Direction.REVERSE);
        
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            
            // Drive time scaling
            double timeScaleDrive;
            if (Math.abs(gamepad1.left_stick_y) == 0) {
                lastTimeDrive = runtime.milliseconds();
            }
            timeScaleDrive = (runtime.milliseconds()-lastTimeDrive)/timeToMaxScale;
            timeScaleDrive = Range.clip(timeScaleDrive,0,1);
            
            // Strafe time scaling
            double timeScaleStrafe;
            if (Math.abs(gamepad1.left_stick_x) == 0) {
                lastTimeStrafe = runtime.milliseconds();
            }
            timeScaleStrafe = (runtime.milliseconds()-lastTimeStrafe)/timeToMaxScale;
            timeScaleStrafe = Range.clip(timeScaleStrafe,0,1);

            double drive = Range.clip(gamepad1.left_stick_y * scaleDrive, -maxDrive, maxDrive) * timeScaleDrive;
            double timeScaleTurn = 1;
            double turn = Range.clip(gamepad1.right_stick_x * scaleTurn, -maxTurn, maxTurn) * timeScaleTurn;
            double strafe = Range.clip(gamepad1.left_stick_x * scaleStrafe, -maxStrafe, maxStrafe) * timeScaleStrafe;

            double percentageDriveStrafe = Math.abs(drive / strafe);
            double percentageStrafeDrive = Math.abs(strafe / drive);
            
            if (percentageDriveStrafe > minLStickOverridePerc) {
                strafe = 0;
                telemetry.addData("Debug","Drive over Strafe active");
            } else if (percentageStrafeDrive > minLStickOverridePerc) {
                drive = 0;
                telemetry.addData("Debug","Strafe over Drive active");
            }
            
            double FL_Power = (drive - turn - strafe);
            double RL_Power = (drive - turn + strafe);
            double FR_Power = (drive + turn + strafe);
            double RR_Power = (drive + turn - strafe);
            
            FL_Motor.setPower(FL_Power);
            RL_Motor.setPower(RL_Power);
            FR_Motor.setPower(FR_Power);
            RR_Motor.setPower(RR_Power);
            
            
            telemetry.addData("Left Stick","X: "+gamepad1.left_stick_x);
            telemetry.addData("Left Stick","Y: "+gamepad1.left_stick_y);
            telemetry.addData("Right Stick","X: "+gamepad1.right_stick_x);
            telemetry.addData("Right Stick","Y: "+gamepad1.right_stick_y);
            /*
            telemetry.addData("drive",drive);
            telemetry.addData("turn",turn);
            telemetry.addData("strafe",strafe);
            */
            
            telemetry.addData("A",runtime.milliseconds());
            telemetry.addData("B",lastTimeDrive);
            
            telemetry.addData("drive", timeScaleDrive);
            telemetry.addData("turn", timeScaleTurn);
            telemetry.addData("strafe", timeScaleStrafe);
            
            telemetry.addData("Status","Run Time: " + runtime.toString());
            telemetry.addData("FL_Motor","Power: " + FL_Power);
            telemetry.addData("RL_Motor","Power: " + RL_Power);
            telemetry.addData("FR_Motor","Power: " + FR_Power);
            telemetry.addData("RR_Motor","Power: " + RR_Power);
            telemetry.update();
        }
    }
}
// package org.firstinspires.ftc.teamcode;
// 
// import android.util.Size;
// import com.qualcomm.robotcore.eventloop.opmode.Disabled;
// import com.qualcomm.robotcore.util.ElapsedTime;
// import com.qualcomm.robotcore.util.Range;
// import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
// import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
// import org.firstinspires.ftc.vision.VisionPortal;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.hardware.DcMotor; //Get motor properties
// import com.qualcomm.robotcore.util.ElapsedTime;
// import com.qualcomm.robotcore.util.Range;
// 
// @TeleOp(name = "MecanumTest (OnBotJava)")
// 
// public class MecanumTest extends LinearOpMode {
//     //    Declare    OpMode    members.
//     private ElapsedTime runtime = new ElapsedTime(); //Time since startup
//     private double lastElapsed = runtime.milliseconds();
//     
//     // Drive motors
//     private DcMotor FL_Motor = null;
//     private DcMotor RL_Motor = null;
//     private DcMotor FR_Motor = null;
//     private DcMotor RR_Motor = null;
//     
//     private final int accelTime = 300; // In milliseconds, will take X amount of milliseconds to get up to full speed
//     public double scale = 0;
//     
//     public Boolean still = true;
//     
//     @Override
//     public void runOpMode() {
//         telemetry.addData("Status", "Initialized");
//         telemetry.update();
// 
//         // Drive motors
//         FL_Motor = hardwareMap.get(DcMotor.class, "fl");
//         RL_Motor = hardwareMap.get(DcMotor.class, "rl");
//         FR_Motor = hardwareMap.get(DcMotor.class, "fr");
//         RR_Motor = hardwareMap.get(DcMotor.class, "rr");
//         
//         // Set motor directions
//         FL_Motor.setDirection(DcMotor.Direction.FORWARD);
//         RL_Motor.setDirection(DcMotor.Direction.FORWARD);
//         FL_Motor.setDirection(DcMotor.Direction.REVERSE);
//         RL_Motor.setDirection(DcMotor.Direction.REVERSE);
// 
//         // Wait for driver to press play
//         waitForStart();
//         runtime.reset();
// 
//         while (opModeIsActive()) {
//             // Power level for telemetry
//             double FL_Power;
//             double RL_Power;
//             double FR_Power;
//             double RR_Power;
// 
//             // Controls
//             double strafe = -gamepad1.left_stick_x;
//             double drive = -gamepad1.left_stick_y;
//             double turn = gamepad1.right_stick_x;
//             
//             // Turn input into power values
//             FL_Power = Range.clip(drive+turn-strafe,-1,1);
//             RL_Power = Range.clip(drive+turn+strafe,-1,1);
//             FR_Power = Range.clip(drive-turn+strafe,-1,1);
//             RR_Power = Range.clip(drive-turn-strafe,-1,1);
//             
//             scale = Range.clip((runtime.milliseconds()-lastElapsed)/accelTime,0,1);
//             
//             if (strafe == 0 && drive == 0 && turn == 0) {
//                 still = true;
//             } else {
//                 if (still == true) {
//                     lastElapsed = runtime.milliseconds();
//                 }
//                 still = false;
//             }
//             
//             // Set power for the motors
//             FL_Motor.setPower(FL_Power * scale);
//             RL_Motor.setPower(RL_Power * scale);
//             FR_Motor.setPower(FR_Power * scale);
//             RR_Motor.setPower(RR_Power * scale);
// 
//             // Show the elapsed time and motor power
//             telemetry.addData("Status","Run Time: " + runtime.toString());
//             telemetry.addData("Motors","front_left (%.2f),rear_left (%.2f)",FL_Power, RL_Power);
//             telemetry.update();
//         }
//     }
// }
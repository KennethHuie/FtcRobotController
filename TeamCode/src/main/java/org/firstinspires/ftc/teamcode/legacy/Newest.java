// package org.firstinspires.ftc.teamcode;
// 
// import com.qualcomm.robotcore.eventloop.opmode.Disabled;
// import com.qualcomm.robotcore.util.ElapsedTime;
// import com.qualcomm.robotcore.util.Range;
// import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import com.qualcomm.robotcore.hardware.DcMotor; //Get motor properties
// import com.qualcomm.robotcore.hardware.Servo;
// import com.qualcomm.robotcore.util.ElapsedTime;
// import com.qualcomm.robotcore.util.Range;
// 
// @TeleOp(name = "Newest (OnBotJava)")
// 
// public class Newest extends LinearOpMode {
//     //    Declare    OpMode    members.
//     private ElapsedTime runtime = new ElapsedTime(); //Time since startup
//     
//     // Drive motors
//     private DcMotor leftDriveMotor = null;
//     private DcMotor rightDriveMotor = null;
//     // Arm controllers
//     private DcMotor armCoreHexLeft = null;
//     private DcMotor armCoreHexRight = null;
//     // Arm Servos
//     private Servo servoPivot = null;
//     private Servo servoGrab = null;
// 
//     @Override
//     public void runOpMode() {
//         telemetry.addData("Status", "Initialized");
//         telemetry.update();
// 
//         // Drive motors
//         leftDriveMotor = hardwareMap.get(DcMotor.class, "leftdrive");
//         rightDriveMotor = hardwareMap.get(DcMotor.class, "rightdrive");
//         // Find arm core hex motors
//         armCoreHexLeft = hardwareMap.get(DcMotor.class, "leftarmcore");
//         armCoreHexRight = hardwareMap.get(DcMotor.class, "rightarmcore");
//         // Smart Robot Servo
//         servoPivot =  hardwareMap.get(Servo.class,"armpivot");
//         servoGrab = hardwareMap.get(Servo.class,"armgrab");
// 
//         // Set motor directions
//         leftDriveMotor.setDirection(DcMotor.Direction.REVERSE);
//         rightDriveMotor.setDirection(DcMotor.Direction.FORWARD);
//         // Set servo direction
//         servoPivot.setDirection(Servo.Direction.FORWARD);
//         servoGrab.setDirection(Servo.Direction.FORWARD);
//         
//         double armSecondary = 0.5;
//         double grabPower = 0;
//         double stupid = 0.1; // How much to add per DPad press
//                     
//         boolean debounce = false;
// 
//         // Wait for driver to press play
//         waitForStart();
//         runtime.reset();
// 
//         while (opModeIsActive()) {
//             // Power level for telemetry
//             double leftPower;
//             double rightPower;
//             double armPrimary = 0;
// 
//             // Controls
//             double drive = gamepad1.left_stick_y; //Inverse of the Y Stick
//             double turn = -gamepad1.right_stick_x;
//             // Turn input into power values
//             leftPower = Range.clip(drive + turn, -1.0, 1.0);
//             rightPower = Range.clip(drive - turn, -1.0, 1.0);
//             armPrimary+= gamepad1.right_trigger;
//             armPrimary-= gamepad1.left_trigger;
//             
//             if (gamepad1.dpad_down && armSecondary > 0.1 && !debounce) {
//                 debounce = true;
//                 armSecondary+=stupid;
//             }
//             if (gamepad1.dpad_up && armSecondary < 0.9 && !debounce) {
//                 debounce = true;
//                 armSecondary-=stupid;
//             }
//             if (!gamepad1.dpad_up && !gamepad1.dpad_down) {
//                 debounce = false;
//             }
//             
//             if (gamepad1.dpad_left) {
//                 grabPower = 1;
//             }
//             if (gamepad1.dpad_right) {
//                 grabPower = 0;
//             }
// 
//             // Set power for the motors
//             leftDriveMotor.setPower(leftPower);
//             rightDriveMotor.setPower(rightPower);
//             // Set arm core motor power
//             armCoreHexLeft.setPower(armPrimary);
//             armCoreHexRight.setPower(-armPrimary);
//             // Set servo angle idk
//             servoPivot.setPosition(armSecondary);
//             servoGrab.setPosition(grabPower);
// 
//             // COmpiler said screw you so I have to do this now
//             String a = (new Double(armSecondary)).toString();
//             String b = (new Double(servoPivot.getPosition())).toString();
//             String c = (new Double(grabPower)).toString();
//             String d = (new Double(servoGrab.getPosition())).toString();
//             // Show the elapsed time and motor power
//             telemetry.addData("Status","Run Time: " + runtime.toString());
//             telemetry.addData("Motors","left (%.2f),right (%.2f)",leftPower, rightPower);
//             telemetry.addData("Pivot",a,b);
//             telemetry.addData("GrabPosition",c,d);
//             telemetry.update();
//         }
//     }
// }
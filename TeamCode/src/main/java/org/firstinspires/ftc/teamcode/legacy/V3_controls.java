// package org.firstinspires.ftc.teamcode;
// 
// import java.lang.Math;
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
// @TeleOp(name = "V3_controls (OnBotJava)")
// 
// public class V3_controls extends LinearOpMode {
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
//     private Servo servoThrow = null;
//     
//     // test
//     private double pOffset = 0.05;
// 
//     private int boolToInt(boolean b) {
//         if (b) {
//             return 1;
//         } else {
//             return 0;
//         }
//     }
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
//         servoThrow = hardwareMap.get(Servo.class,"dronethrow");
// 
//         // Set motor directions
//         leftDriveMotor.setDirection(DcMotor.Direction.REVERSE);
//         rightDriveMotor.setDirection(DcMotor.Direction.FORWARD);
//         // Set servo direction
//         servoPivot.setDirection(Servo.Direction.FORWARD);
//         servoGrab.setDirection(Servo.Direction.FORWARD);
//         servoThrow.setDirection(Servo.Direction.FORWARD);
//         
//         double armSecondary = 0.2;
//         boolean grab = false; // Grabs
//         double stupid = 0.05; // How much to add per DPad press
//                     
//         boolean debounce = false;
//         boolean grabDebounce = false;
//         
//         double drone = 0;
// 
//         // Wait for driver to press play
//         waitForStart();
//         runtime.reset();
// 
//         while (opModeIsActive()) {
//             // Power level for telemetry
//             double leftPower;
//             double rightPower;
//             double armPrimary = pOffset;
// 
//             // Controls
//             double drive = gamepad1.left_stick_y; //Inverse of the Y Stick
//             double turn = -gamepad1.right_stick_x;
//             // Turn input into power values
//             leftPower = Range.clip(drive + turn, -1.0, 1.0);
//             rightPower = Range.clip(drive - turn, -1.0, 1.0);
//             armPrimary+= (gamepad1.right_trigger * (1-pOffset));
//             armPrimary-= (gamepad1.left_trigger * (1-pOffset));
//             
//             // grabber pivot
//             if (gamepad1.left_bumper && armSecondary < 0.9 && !debounce) {
//                 debounce = true;
//                 armSecondary+=stupid;
//             } else if (gamepad1.right_bumper && armSecondary > 0.1 && !debounce) {
//                 debounce = true;
//                 armSecondary-=stupid;
//             }
//             if (!gamepad1.right_bumper && !gamepad1.left_bumper) {
//                 debounce = false;
//             }
//             
//             // grabber claw
//             if (gamepad1.a && !grabDebounce) {
//                 grabDebounce = true;
//                 grab = !grab;
//             } else if (!gamepad1.a) {
//                 grabDebounce = false;
//             }
//             if (gamepad1.b) {
//                 drone = 1;
//             } else {
//                 drone = 0;
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
//             servoGrab.setPosition(boolToInt(grab));
//             servoThrow.setPosition(drone);
// 
//             // COmpiler said screw you so I have to do this now
//             String a = (new Double(armSecondary)).toString();
//             String b = (new Double(servoPivot.getPosition())).toString();
//             String c = (new Integer(boolToInt(grab))).toString();
//             String d = (new Double(servoGrab.getPosition())).toString();
//             String e = (new Double(armPrimary).toString());
//             // Show the elapsed time and motor power
//             telemetry.addData("Status","Run Time: " + runtime.toString());
//             telemetry.addData("Motors","left (%.2f),right (%.2f)",leftPower, rightPower);
//             telemetry.addData("PrimaryPivot",e);
//             telemetry.addData("SecPivot",a,b);
//             telemetry.addData("GrabPosition",c,d);
//             telemetry.update();
//         }
//     }
// }
// package    org.firstinspires.ftc.teamcode;
// 
// import    com.qualcomm.robotcore.eventloop.opmode.Disabled;
// import    com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
// import    com.qualcomm.robotcore.eventloop.opmode.TeleOp;
// import    com.qualcomm.robotcore.hardware.DcMotor;    //    Get    motor    properties
// import    com.qualcomm.robotcore.util.ElapsedTime;
// import    com.qualcomm.robotcore.util.Range;
// 
// @TeleOp(name = "MotorGoSpeen (OnBotJava)")
// public class MotorGoSpeen extends LinearOpMode    {
//     private ElapsedTime runtime = new ElapsedTime();
//     public DcMotor motor = null;
//     
//     @Override
//     public void runOpMode() {
//         motor = hardwareMap.get(DcMotor.class,"motor");
//         motor.setDirection(DcMotor.Direction.REVERSE);
//         
//         double power;
//         
//         waitForStart();
//         runtime.reset();
//         
//         while (opModeIsActive())    {
//             double throt = -gamepad1.left_stick_y; //Inverse of the Y Stick
//             // Turn input into power values
//             power = Range.clip(throt, -1.0, 1.0);
//             
//             motor.setPower(power);
//             telemetry.addData("Status","Run Time: " + runtime.toString());
//             telemetry.addData("Motors","motor (%.2f)",power);
//             telemetry.addData("Power",motor.getPower());
//             telemetry.update();
//         }
//     }
// }
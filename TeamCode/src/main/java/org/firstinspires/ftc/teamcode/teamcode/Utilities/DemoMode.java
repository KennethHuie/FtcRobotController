package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.ftccommon.SoundPlayer;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.teamcode.Configuration;
import org.firstinspires.ftc.teamcode.teamcode.MecanumBase;
import org.firstinspires.ftc.teamcode.teamcode.ToggleServo;

// Flag FTC-Dashboard
@Config
// Set name of OpMode
@TeleOp(name = "DemonstrationMode (AndroidStudio)", group = "Utilities")
public class DemoMode extends LinearOpMode {
    Configuration.MecanumV2 cfg = new Configuration.MecanumV2();

    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private final double lastElapsed = runtime.milliseconds();

    private static final double bucketWristRange = 0.5;
    private static final double grabberRange = 0.2;

    // Convert a boolean to a 1 or 0, (1=true,0=false)
    public double boolToNumber(boolean x) {
        if (x) return 1;
        else return 0;
    }

    @Override
    public void runOpMode() {
        // Find motor instances on initialization
        DcMotor vsLeftMotor = hardwareMap.get(DcMotor.class,"vsLeftMotor");
        DcMotor vsRightMotor = hardwareMap.get(DcMotor.class,"vsRightMotor");
        DcMotor hsMotor = hardwareMap.get(DcMotor.class,"hsMotor");

        // Define servos
        ToggleServo bucketServo = new ToggleServo(hardwareMap.get(Servo.class, "bucket"));
        ToggleServo wristServo = new ToggleServo(hardwareMap.get(Servo.class, "bucketWrist"));
        ToggleServo grabberServo = new ToggleServo(hardwareMap.get(Servo.class, "grabber"));
        CRServo sweeper = hardwareMap.get(CRServo.class,"sweeper");

        // Set direction of motors
        vsRightMotor.setDirection(DcMotor.Direction.REVERSE);
        bucketServo.setDirection(Servo.Direction.REVERSE);
        bucketServo.setState(true);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            //Determines the power for the vertical lift slide motors
            double verticalSlidePower = (boolToNumber(gamepad1.left_bumper)-boolToNumber(gamepad1.right_bumper));
            //Determines the power for the Intake slider motor
            double horizontalSlidePower = -(gamepad1.left_trigger-gamepad1.right_trigger);

            if (gamepad1.a) { // Toggle Bucket flip state
                if (!bucketServo.getDebounce()) bucketServo.setState(!bucketServo.getState());
                bucketServo.setDebounce(true);
                bucketServo.setPosition(boolToNumber(bucketServo.getState()));
            }
            if (!gamepad1.a) {bucketServo.setDebounce(false);} // Reset when let go

            if (gamepad1.x) { // Toggle Intake flip state
                if (!wristServo.getDebounce()) wristServo.setState(!wristServo.getState());
                wristServo.setDebounce(true);

                if (wristServo.getState() && bucketServo.getState()) {
                    bucketServo.setPosition(0.65);
                }
                if (!wristServo.getState() && bucketServo.getState()) {
                    bucketServo.setPosition(1);
                }

                wristServo.setPosition(boolToNumber(wristServo.getState())*bucketWristRange);
            }
            if (!gamepad1.x) {wristServo.setDebounce(false);} // Reset when let go

            if (gamepad1.b) { // Toggle Grabber state
                if (!grabberServo.getDebounce()) {
                    grabberServo.setState(!grabberServo.getState());
                }
                grabberServo.setDebounce(true);
                grabberServo.setPosition(boolToNumber(grabberServo.getState())*grabberRange);
            }
            if (!gamepad1.b) {grabberServo.setDebounce(false);} // Reset when let go

            // Sweeper logic
            if (gamepad1.dpad_up) {
                sweeper.setPower(1);
            } else if (gamepad1.dpad_down) {
                sweeper.setPower(-1);
            } else {
                sweeper.setPower(0);
            }

            // Set motor power
            vsLeftMotor.setPower(verticalSlidePower); // Synchronize Left & Right slide
            vsRightMotor.setPower(verticalSlidePower);
            hsMotor.setPower(horizontalSlidePower);
            // Set servo power
            telemetry.addData("Strafe",cfg.scaleStrafe);

            // Fast Flags
            if (cfg.controllerAxesDebug) {
                telemetry.addData("Left Stick", "X: " + gamepad1.left_stick_x);
                telemetry.addData("Left Stick", "Y: " + gamepad1.left_stick_y);
                telemetry.addData("Right Stick", "X: " + gamepad1.right_stick_x);
                telemetry.addData("Right Stick", "Y: " + gamepad1.right_stick_y);
            }
            if (cfg.sliderMotorPowerDebug) {
                telemetry.addData("vertical", verticalSlidePower);
                telemetry.addData("horizontal", horizontalSlidePower);
            }
            if (cfg.sliderEncoderDebug) {
                telemetry.addData("LeftSlide",vsLeftMotor.getCurrentPosition());
                telemetry.addData("RightSlide",vsRightMotor.getCurrentPosition());
            }
            if (cfg.runtimeDebug) telemetry.addData("Status","Run Time: " + runtime);
            telemetry.update();
        }
    }
}

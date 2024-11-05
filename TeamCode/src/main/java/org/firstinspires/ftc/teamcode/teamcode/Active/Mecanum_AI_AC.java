package org.firstinspires.ftc.teamcode.teamcode.Active;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import java.util.concurrent.TimeUnit;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor; // Motor class
import com.qualcomm.robotcore.hardware.DcMotorEx; // Motor class with extra features
import com.qualcomm.robotcore.hardware.Servo; // Servo class
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Mecanum_AI_AC (AndroidStudio)", group = "Production")

public class Mecanum_AI_AC extends LinearOpMode {
    //    Declare    OpMode    members.
    private ElapsedTime runtime = new ElapsedTime(); //Time since startup
    private double lastElapsed = runtime.milliseconds();
    // Speed (Driver) to limit speed
    final double maxPowerX = 1; // how much power is sent to the motors on full throttle in the fwd direction
    final double maxPowerY = 1; // how much power is sent to the motors on full throttle in the side direction
    final double maxPowerTurn = 0.5; // how much power is sent to the motors when turning
    final double minPercent = 3; // how many times X has to be more than Y before it rounds the inputs
    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final double cameraOffset = 4.5;

    // Used by acceleration
    // - Config
    private final int accelTime = 150; // In milliseconds, will take X amount of milliseconds to get up to full speed
    private final double step = 0.015; // How much each bumper press adds/subtracts
    // - Used by code
    private boolean still = true;
    private double scale = 0;
    private double goalTimeArm = 0;
    private int DesiredID = 1;

    private int armPrimary = 0;
    private double armSecondary = 1; // Scale (0-1) [0 = fully flat, 1 = fully upright]
    private boolean leftGrab = false; // (false = grab, true = dont grab)
    private boolean rightGrab = true; // (true = grab, false = dont grab)

    private boolean _DB_X = false; // Debounce for button "X"
    private boolean _DB_B = false; // Debounce for button "B"
    // private boolean _DB_Y = false;

    private boolean _DB_RB = false; // Debounce for right bumper
    private boolean _DB_LB = false; // Debounce for left bumper

    private boolean _DB_DPL = false; // Debounce for D-Pad left
    private boolean _DB_DPR = false; // Debounce for D-Pad right

    // Vision stuff
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag

    // Drive motor variables
    private DcMotor FL_Motor = null; // Front left side drive motor
    private DcMotor RL_Motor = null; // Rear left side drive motor
    private DcMotor FR_Motor = null; // Front right side drive motor
    private DcMotor RR_Motor = null; // Rear right side drive motor

    private DcMotorEx L_Arm = null; // Left side Core Hex Motor
    private DcMotorEx R_Arm = null; // Right side Core Hex Motor

    // Servo variables
    private Servo servoArm = null; // Wrist
    private Servo L_Grab = null; // Left grabber
    private Servo R_Grab = null; // Right grabber
    // private Servo launcher = null; // Plane launcher

    // Misc functions

    public double boolToNum(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }

    public String boolToString(boolean b) {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    public String IDtoName(int ID) {
        if (ID == 1) {
            return "Blue Left";
        } else if (ID == 2) {
            return "Blue Center";
        } else if (ID == 3) {
            return "Blue Right";
        } else if (ID == 4) {
            return "Red Left";
        } else if (ID == 5) {
            return "Red Center";
        } else if (ID == 6) {
            return "Red Right";
        } else {
            return null;
        }
    }

    public void setLevel() {
        armPrimary = 35;
        armSecondary = 0.39;
    }

    public void setUp() {
        armPrimary = 60;
        armSecondary = 0.6;
    }

    @Override
    public void runOpMode() {
        boolean targetFound = false;    // Set to true when an AprilTag target is detected

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Define drive motor
        FL_Motor = hardwareMap.get(DcMotor.class, "fl");
        RL_Motor = hardwareMap.get(DcMotor.class, "rl");
        FR_Motor = hardwareMap.get(DcMotor.class, "fr");
        RR_Motor = hardwareMap.get(DcMotor.class, "rr");

        L_Arm = hardwareMap.get(DcMotorEx.class, "l_arm");
        R_Arm = hardwareMap.get(DcMotorEx.class, "r_arm");

        // Documentation told me to do this idk
        L_Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        R_Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Define servo
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        L_Grab = hardwareMap.get(Servo.class, "l_grab");
        R_Grab = hardwareMap.get(Servo.class, "r_grab");
        //launcher = hardwareMap.get(Servo.class,"launcher");

        // Set motor directions
        FL_Motor.setDirection(DcMotor.Direction.REVERSE);
        RL_Motor.setDirection(DcMotor.Direction.REVERSE);
        FR_Motor.setDirection(DcMotor.Direction.REVERSE);
        RR_Motor.setDirection(DcMotor.Direction.REVERSE);

        L_Arm.setDirection(DcMotor.Direction.FORWARD);
        R_Arm.setDirection(DcMotor.Direction.REVERSE);

        servoArm.setDirection(Servo.Direction.REVERSE);
        L_Grab.setDirection(Servo.Direction.FORWARD);
        R_Grab.setDirection(Servo.Direction.FORWARD);

        // Set arm motors to be Servo-like
        L_Arm.setTargetPosition(0);
        R_Arm.setTargetPosition(0);

        L_Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        R_Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        L_Arm.setVelocity(500);
        R_Arm.setVelocity(500);

        initAprilTag();

        // Wait for driver to press play
        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // Vision
            targetFound = false;
            desiredTag  = null;

            // Power level for telemetry
            double FL_Power;
            double RL_Power;
            double FR_Power;
            double RR_Power;

            // Controls
            // - Driving
            double strafe = Range.clip(-gamepad1.left_stick_x,-maxPowerY,maxPowerY);
            double drive = Range.clip(-gamepad1.left_stick_y,-maxPowerX,maxPowerX);
            double turn = Range.clip(gamepad1.right_stick_x,-maxPowerTurn,maxPowerTurn);

            // Round inputs to avoid drift
            double percDS = Math.abs(drive)/Math.abs(strafe); // Ratio of drive to strafe
            double percSD = Math.abs(strafe)/Math.abs(drive); // Ratio of strafe to drive

            if (percDS > minPercent) {
                strafe = Math.floor(strafe);
            } else if (percSD > minPercent) {
                drive = Math.floor(drive);
            }

            // Turn input into power values

            FL_Power = Range.clip(drive+turn-strafe,-1,1);
            RL_Power = Range.clip(drive+turn+strafe,-1,1);
            FR_Power = Range.clip(drive-turn+strafe,-1,1);
            RR_Power = Range.clip(drive-turn-strafe,-1,1);

            scale = Range.clip((runtime.milliseconds()-lastElapsed)/accelTime,0,1);

            if (strafe == 0 && drive == 0 && turn == 0) {
                still = true;
            } else {
                if (still == true) {
                    lastElapsed = runtime.milliseconds();
                }
                still = false;
            }

            // - Arm
            armPrimary-=gamepad1.left_trigger * 2;
            armPrimary+=gamepad1.right_trigger * 2;

            // -- Wrist (Two seperate blocks if you want to press both buttons simultaneously???)

            /*if (gamepad1.left_bumper && !_DB_LB) { // Old stepper version
                _DB_LB = true;
                armSecondary-=step;
            } else if (!gamepad1.left_bumper) {
                _DB_LB = false;
            }*/

            if (gamepad1.left_bumper && !_DB_LB) {
                _DB_LB = true;
                _DB_RB = false;
                goalTimeArm = runtime.milliseconds() + 15;
            }

            if (gamepad1.right_bumper && !_DB_RB) {
                _DB_RB = true;
                _DB_LB = false;
                goalTimeArm = runtime.milliseconds() + 15;
            }

            // Faux Async Function XD
            if (runtime.milliseconds() <= goalTimeArm) {
                if (_DB_LB) {
                    armSecondary-=step;
                } else if (_DB_RB) {
                    armSecondary+=step;
                }
            } else if (runtime.milliseconds() >= goalTimeArm) {
                _DB_RB = false;
                _DB_LB = false;
            }

            armSecondary = Range.clip(armSecondary,0,1);

            // -- Grabber L
            if (gamepad1.x && !_DB_X) {
                leftGrab = !leftGrab; // Invert boolean
                _DB_X = true;
            } else if (!gamepad1.x) {
                _DB_X = false;
            }
            // -- Grabber R
            if (gamepad1.b && !_DB_B) {
                rightGrab = !rightGrab; // Invert boolean
                _DB_B = true;
            } else if (!gamepad1.b) {
                _DB_B = false;
            }

            // -- Drone launcher

            /*if (gamepad1.y && !_DB_Y) {
                launcher.setPosition(1);
                _DB_Y = true;
            } else if (!gamepad1.y) {
               launcher.setPosition(0);
                _DB_Y = false;
            }*/

            if (gamepad1.a) {
                setLevel();
            }
            if (gamepad1.dpad_up) {
                setUp();
            }

            if (gamepad1.dpad_right && !_DB_DPR) {
                _DB_DPR = true;
                DesiredID+=1;
            }
            if (!gamepad1.dpad_right) {
                _DB_DPR = false;
            }

            if (gamepad1.dpad_left && !_DB_DPL) {
                _DB_DPL = true;
                DesiredID-=1;
            }
            if (!gamepad1.dpad_left) {
                _DB_DPL = false;
            }

            if (DesiredID < 1) {
                DesiredID = 6;
            } else if (DesiredID > 6) {
                DesiredID = 1;
            }

            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    if ((DesiredID < 0) || (detection.id == DesiredID)) {
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    }
                }
            }

            servoArm.setPosition(armSecondary); // Wrist
            L_Grab.setPosition(boolToNum(leftGrab)); // Left grabber
            R_Grab.setPosition(boolToNum(rightGrab)); // Right grabber

            // Set power for the motors
            FL_Motor.setPower(FL_Power * scale);
            RL_Motor.setPower(RL_Power * scale);
            FR_Motor.setPower(FR_Power * scale);
            RR_Motor.setPower(RR_Power * scale);

            L_Arm.setTargetPosition(armPrimary);
            R_Arm.setTargetPosition(armPrimary);

            // Show the elapsed time and motor power
            telemetry.addData("Status","Run Time: " + runtime.toString());
            telemetry.addData("Motors","front_left (%.2f),front_right (%.2f)",FL_Power, FR_Power);
            telemetry.addData("Motors","rear_left (%.2f),rear_right (%.2f)",RL_Power, RR_Power);
            telemetry.addData("Wrist",armSecondary);
            telemetry.addData("leftGrab",boolToString(!leftGrab)); // It's inverse
            telemetry.addData("rightGrab",boolToString(rightGrab));
            telemetry.addData("ap",armPrimary);
            telemetry.addData("Left encoder value", L_Arm.getCurrentPosition());
            telemetry.addData("Right encoder value", R_Arm.getCurrentPosition());
            telemetry.addData("Currently Tracking:",IDtoName(DesiredID));

            if (targetFound) {
                telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
                telemetry.addData("Range",  "%5.1f inches", desiredTag.ftcPose.range);
                telemetry.addData("Offset", desiredTag.ftcPose.x-cameraOffset);
                telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
            }

            telemetry.update();
        }
    }
    //Initialize the AprilTag processor.
    private void initAprilTag() {
        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder().build();

        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        // Create the vision portal by using a builder.
        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }

    //Manually set the camera gain and exposure.
    private void setManualExposure(int exposureMS, int gain) {
        // Wait for the camera to be open, then use the controls

        if (visionPortal == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        // Set camera controls unless we are stopping.
        if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }
    }
}

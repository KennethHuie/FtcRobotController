package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;


@Autonomous (name="AutoPrototype")

public class AutoPrototype extends LinearOpMode {
    private static final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private static final int DESIRED_TAG_ID = 1;    // Choose the tag you want to approach or set to -1 for ANY tag.
    private static final int camOffset = -5;
    private static final double distance = 4.0;
    
    private boolean leftGrab = false; // (false = grab, true = dont grab)
    private boolean rightGrab = true; // (true = grab, false = dont grab)
    
    private int armPrimary = 0;
    private double armSecondary = 1; // Scale (0-1) [0 = fully flat, 1 = fully upright] 
    
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag
    
    // Drive motor variables
    private DcMotorEx FL_Motor = null; // Front left side drive motor
    private DcMotorEx RL_Motor = null; // Rear left side drive motor
    private DcMotorEx FR_Motor = null; // Front right side drive motor
    private DcMotorEx RR_Motor = null; // Rear right side drive motor
    
    private DcMotorEx L_Arm = null; // Left side Core Hex Motor
    private DcMotorEx R_Arm = null; // Right side Core Hex Motor
    
    // Servo variables
    private Servo servoArm = null; // Wrist
    private Servo L_Grab = null; // Left grabber
    private Servo R_Grab = null; // Right grabber
    
    // Positions to set the motors to
    public int posFL = 0;
    public int posRL = 0;
    public int posFR = 0;
    public int posRR = 0;
    
    public double goalX = 0; // Forward/Back
    public double goalY = 0; // Side to side
    public double goalR = 0; // Rotation
    
    interface action {
        void move();
    }
    
    public void moveX(double dist) {
        double temp = dist * 525;
        double add = Math.round(temp);
        posFL+=add;
        posRL+=add;
        posFR+=add;
        posRR-=add;
    }
    
    public void moveY(double dist) {
        double temp = dist * 525;
        double add = Math.round(temp);
        posFL-=add;
        posRL+=add;
        posFR+=add;
        posRR+=add;
    }
    
    public void turn(double deg) {
        double temp = deg * (1000/90);
        double add = Math.round(temp);
        posFL+=add;
        posRL+=add;
        posFR-=add;
        posRR+=add;
    }
    
    public double boolToNum(boolean b) {
        if (b) {
            return 1;
        } else {
            return 0;
        }
    }
    
    public boolean closeEnough(int num, int goal,  int margin) { // Seeing if a goal is close enough
        if ((num < (goal+margin)) && ((num > goal-margin))) {
            return true;
        } else {
            return false;
        }
    }
    
    public String boolToString(boolean b) {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    public void setLevel() {
        // Lift
        armPrimary = 60;
        armSecondary = 0.55;
        updateMotors();
        sleep(2500);
        // Drop
        leftGrab = true;
        rightGrab = false;
        updateMotors();
        sleep(500);
        // Retreat
        armSecondary = 0.7;
        updateMotors();
    }
    
    private action[] actionQueue = new action[] {
        new action() { public void move() { moveY(1); } },
        new action() { public void move() { moveY(goalY/12); } },
        new action() { public void move() { moveX(goalX/12); } },
        //new action() { public void move() { turn(goalR); } },
        new action() { public void move() { setLevel(); } }
    };
    
    public boolean conditionMet() { // Check if goal is reached
    boolean a = false;
    boolean b = false;
    boolean c = false;
    boolean d = false;
    boolean e = false;
    boolean f = false;
    
    if (closeEnough(FL_Motor.getCurrentPosition(),posFL,2)) {
        sleep(20);
        a = true;
    }
    if (closeEnough(RL_Motor.getCurrentPosition(),posRL,2)) {
        sleep(20);
        b = true;
    }
    if (closeEnough(FR_Motor.getCurrentPosition(),posFR,2)) {
        sleep(20);
        c = true;
    }
    if (closeEnough(RR_Motor.getCurrentPosition(),posRR,2)) {
        sleep(20);
        d = true;
    }
    if (closeEnough(L_Arm.getCurrentPosition(),armPrimary,2)) {
        sleep(20);
        e = true;
    }
    if (closeEnough(L_Arm.getCurrentPosition(),armPrimary,2)) {
        sleep(20);
        f = true;
    }
        if (a && b && c && d && e && f) {
            return true;
        } else {
            return false;
        }
    }
    
    public void updateMotors() {
        // Set drive motors to position
            FL_Motor.setTargetPosition(posFL);
            RL_Motor.setTargetPosition(posRL);
            FR_Motor.setTargetPosition(posFR);
            RR_Motor.setTargetPosition(posRR);
            
            servoArm.setPosition(armSecondary); // Wrist
            L_Grab.setPosition(boolToNum(leftGrab)); // Left grabber
            R_Grab.setPosition(boolToNum(rightGrab)); // Right grabber
            
            L_Arm.setTargetPosition(armPrimary);
            R_Arm.setTargetPosition(armPrimary);
    }
    
    @Override
    public void runOpMode() {
        int currentCommand = 0;
        boolean targetFound = false;    // Set to true when an AprilTag target is detected
        
        // Define drive motor
        FL_Motor = hardwareMap.get(DcMotorEx.class, "fl");
        RL_Motor = hardwareMap.get(DcMotorEx.class, "rl");
        FR_Motor = hardwareMap.get(DcMotorEx.class, "fr");
        RR_Motor = hardwareMap.get(DcMotorEx.class, "rr");
        
        L_Arm = hardwareMap.get(DcMotorEx.class, "l_arm");
        R_Arm = hardwareMap.get(DcMotorEx.class, "r_arm");
        
        // Documentation told me to do this idk
        FL_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RL_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FR_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RR_Motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        L_Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        R_Arm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        
        // Define servo
        servoArm = hardwareMap.get(Servo.class, "servoArm");
        L_Grab = hardwareMap.get(Servo.class, "l_grab");
        R_Grab = hardwareMap.get(Servo.class, "r_grab");
        
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
        
        // Set drive motors to run precisely
        FL_Motor.setTargetPosition(0);
        RL_Motor.setTargetPosition(0);
        FR_Motor.setTargetPosition(0);
        RR_Motor.setTargetPosition(0);
        
        FL_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RL_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        FR_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        RR_Motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        FL_Motor.setVelocity(250);
        RL_Motor.setVelocity(250);
        FR_Motor.setVelocity(250);
        RR_Motor.setVelocity(250);
        
        // Set arm motors to be Servo-like
        L_Arm.setTargetPosition(0);
        R_Arm.setTargetPosition(0);
        
        L_Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        R_Arm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        
        L_Arm.setVelocity(500);
        R_Arm.setVelocity(500);
        
        // Initialize the Apriltag Detection process
        initAprilTag();

        if (USE_WEBCAM) {
            setManualExposure(3, 250);  // Use low exposure time to reduce motion blur
        }

        waitForStart();
        while (opModeIsActive()) {
            targetFound = false;
            desiredTag  = null;
            
            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
                        targetFound = true;
                        desiredTag = detection;
                        break;
                    }
                }
            }
            
            if (targetFound) {
                telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
                telemetry.addData("Range",desiredTag.ftcPose.range);
                telemetry.addData("Offset", desiredTag.ftcPose.x + camOffset);
                telemetry.addData("Yaw","%3.0f degrees", desiredTag.ftcPose.yaw);
                
                goalX = (desiredTag.ftcPose.range - distance);
                goalY = (desiredTag.ftcPose.x + camOffset);
                goalR = -desiredTag.ftcPose.yaw;
            }
            
            if (conditionMet()) {
                if (currentCommand <= 3) {
                actionQueue[currentCommand].move();
                sleep(200);
                currentCommand+=1;
                }
            }
                
            updateMotors();

            // Show motor power
            telemetry.addData("Wrist",armSecondary);
            telemetry.addData("leftGrab",boolToString(!leftGrab)); // It's inverse
            telemetry.addData("rightGrab",boolToString(rightGrab));
            telemetry.addData("ap",armPrimary);
            telemetry.addData("Left encoder value", L_Arm.getCurrentPosition());
            telemetry.addData("Right encoder value", R_Arm.getCurrentPosition());
            
            telemetry.addData("FL",posFL);
            telemetry.addData("RL",posRL);
            telemetry.addData("FR",posFR);
            telemetry.addData("RR",posRR);
            
            telemetry.addData("Current command in queue :",currentCommand);
        
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

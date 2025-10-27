package org.firstinspires.ftc.teamcode.teamcode.Utilities;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "FlywheelTest (AndroidStudio)", group = "Utilities")
public class FlywheelTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime(); //Time since startup
    //private final double lastElapsed = runtime.milliseconds();

    @Override
    public void runOpMode() {

        DcMotor FlyWheel1 = hardwareMap.get(DcMotor.class,"fly1");
        DcMotor FlyWheel2 = hardwareMap.get(DcMotor.class,"fly2");

        FlyWheel1.setDirection(DcMotor.Direction.FORWARD);
        FlyWheel2.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            if (gamepad1.b) {
                FlyWheel1.setPower(1);
            } else {
                FlyWheel1.setPower(0);
            }
            if (gamepad1.x) {
                FlyWheel2.setPower(1);
            } else {
                FlyWheel2.setPower(0);
            }
        }
        telemetry.addData("Fly1", FlyWheel1.getPower());
        telemetry.addData("Fly2", FlyWheel2.getPower());
    }
}
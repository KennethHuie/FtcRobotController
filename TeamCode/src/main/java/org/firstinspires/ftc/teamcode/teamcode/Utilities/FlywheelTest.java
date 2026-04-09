package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.teamcode.Components.Flywheel;

@Configurable
@TeleOp(name = "FlywheelTest", group = "Utilities")
public class FlywheelTest extends LinearOpMode {
    private final ElapsedTime runtime = new ElapsedTime();
    public static double Start = 0;
    public static double Power = 0;
    public static double RPM = 4000;

    @Override
    public void runOpMode() {
        final TelemetryManager pt = PanelsTelemetry.INSTANCE.getTelemetry();
        final Flywheel flywheels = new Flywheel(hardwareMap, runtime);
        flywheels.flywheel2.setDirection(DcMotor.Direction.REVERSE);
        flywheels.setTargetSpeed(1000);
        boolean CHECKING = true;
        double FINAL = 0;
        flywheels.setPower(Start);
        waitForStart();
        runtime.reset();
        while (opModeIsActive()) {
            flywheels.setPower(CHECKING ? Power : 0);
            //flywheels.update();
            if (flywheels.getCurrentSpeed() > (RPM / 60) * 28) {
                FINAL = runtime.milliseconds();
                CHECKING = false;
            }
            pt.addData("Speed", flywheels.getCurrentSpeed());
            pt.addData("Diff", flywheels.getDiff());
            pt.addData("TIME", FINAL);
            pt.update(telemetry);
        }
    }
}

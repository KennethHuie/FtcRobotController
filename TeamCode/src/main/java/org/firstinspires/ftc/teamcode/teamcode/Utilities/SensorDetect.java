package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.teamcode.teamcode.Components.ColorDetector;
import org.firstinspires.ftc.teamcode.teamcode.Components.Sweeper;

@Configurable
@TeleOp(name = "SensorDetect", group = "Utilities")
public class SensorDetect extends LinearOpMode {
    public static double speed = 0.5;

    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryManager pt = PanelsTelemetry.INSTANCE.getTelemetry();
        Rev2mDistanceSensor leftDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceL");
        NormalizedColorSensor roofColorSensor = hardwareMap.get(RevColorSensorV3.class, "roof");
        NormalizedColorSensor rearColorSensor = hardwareMap.get(RevColorSensorV3.class, "rear");
        Sweeper sweeper = new Sweeper(hardwareMap);
        sweeper.speed = speed;

        waitForStart();
        sweeper.intake().run(new TelemetryPacket());
        while (opModeIsActive()) {
            sweeper.startTrack().run(new TelemetryPacket());
            NormalizedRGBA rfcs = roofColorSensor.getNormalizedColors();
            NormalizedRGBA rrcs = rearColorSensor.getNormalizedColors();
            ColorDetector Roof = new ColorDetector(roofColorSensor);
            ColorDetector Rear = new ColorDetector(rearColorSensor);
            pt.addData("BC", sweeper.BallCount);
            pt.addData("FC", sweeper.FireCount);
            pt.addData("Front Sensor", sweeper.ballDetected() ? "Detected" : "No Ball");
            pt.addData("Roof", Roof.isGreen() ? "Detecting Green" : Roof.isPurple() ? "Detecting Purple" : "Unsure / None");
            pt.addData("Rear", Rear.isGreen() ? "Detecting Green" : Rear.isPurple() ? "Detecting Purple" : "Unsure / None");
            pt.addLine("");
            pt.addData("Polling", sweeper.polling);
            pt.addData("PollCount", sweeper.PollCount);
            pt.addData("Incoming", sweeper.incoming);
            pt.update(telemetry);
        }
    }
}

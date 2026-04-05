package org.firstinspires.ftc.teamcode.teamcode.Utilities;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.teamcode.Components.ColorDetector;

@TeleOp(name = "SensorDetect", group = "Utilities")
public class SensorDetect extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        TelemetryManager pt = PanelsTelemetry.INSTANCE.getTelemetry();
        Rev2mDistanceSensor leftDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceL");
        NormalizedColorSensor roofColorSensor = hardwareMap.get(RevColorSensorV3.class, "roof");
        NormalizedColorSensor rearColorSensor = hardwareMap.get(RevColorSensorV3.class, "rear");

        waitForStart();
        while (opModeIsActive()) {
            NormalizedRGBA rfcs = roofColorSensor.getNormalizedColors();
            NormalizedRGBA rrcs = rearColorSensor.getNormalizedColors();
            ColorDetector Roof = new ColorDetector(roofColorSensor);
            ColorDetector Rear = new ColorDetector(rearColorSensor);
            pt.addData("Roof", Roof.isGreen() ? "Detecting Green" : Roof.isPurple() ? "Detecting Purple" : "Unsure / None");
            pt.addData("Rear", Rear.isGreen() ? "Detecting Green" : Rear.isPurple() ? "Detecting Purple" : "Unsure / None");
            pt.addData("Front Sensor", leftDistanceSensor.getDistance(DistanceUnit.CM) < 20 ? "Detected" : "No Ball");
            pt.update(telemetry);
        }
    }
}

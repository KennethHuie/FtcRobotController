package org.firstinspires.ftc.teamcode.teamcode.Components;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

public class ColorDetector {
    NormalizedColorSensor sensor;

    public ColorDetector(NormalizedColorSensor Sensor) {
        sensor = Sensor;
    }

    public boolean isSensing() {
        // Threshold check that maybe works maybe doesn't
        return sensor.getNormalizedColors().alpha > 0.15;
    }

    public boolean isGreen() {
        NormalizedRGBA color = sensor.getNormalizedColors();
        RGB toRGB = new RGB(color);
        double hue = toRGB.toHSV()[0];

        return isSensing() && hue < 180;
    }

    public boolean isPurple() {
        NormalizedRGBA color = sensor.getNormalizedColors();
        RGB toRGB = new RGB(color);
        double hue = toRGB.toHSV()[0];

        return isSensing() && hue > 180;
    }
}

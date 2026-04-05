package org.firstinspires.ftc.teamcode.teamcode.Components;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.NormalizedRGBA;

public class RGB {
    public int red;
    public int green;
    public int blue;

    public RGB(NormalizedRGBA rgba) {
        double mag = Math.sqrt((rgba.red * rgba.red) + (rgba.green * rgba.green) + (rgba.blue * rgba.blue));
        red = (int) Math.round(rgba.red * 255 / mag);
        green = (int) Math.round(rgba.green * 255 / mag);
        blue = (int) Math.round(rgba.blue * 255 / mag);
    }

    public float[] toHSV() {
        float[] r = new float[3];
        Color.RGBToHSV(this.red, this.green, this.blue, r);
        return r;
    }
}

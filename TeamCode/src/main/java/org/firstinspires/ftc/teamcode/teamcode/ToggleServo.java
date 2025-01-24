package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

public class ToggleServo implements Servo {
    private final Servo fservo;
    private boolean state;
    private boolean debounce;
    public ToggleServo(Servo servo) {
        this.fservo = servo;
        this.state = false;
        this.debounce = false;
    }

    public boolean getState() {
        return this.state;
    }

    public void setState(boolean s) {
        this.state = s;
    }

    public boolean getDebounce() {
        return this.debounce;
    }

    public void setDebounce(boolean db) {
        this.debounce = db;
    }

    @Override
    public ServoController getController() {
        return null;
    }

    @Override
    public int getPortNumber() {
        return 0;
    }

    @Override
    public void setDirection(Direction direction) {

    }

    @Override
    public Direction getDirection() {
        return null;
    }

    @Override
    public void setPosition (double position) {
        fservo.setPosition(position);
    }
    @Override
    public double getPosition () {
        return fservo.getPosition();
    }
    @Override
    public void scaleRange(double min, double max) {
        fservo.scaleRange(min,max);
    }

    @Override
    public Manufacturer getManufacturer() {
        return null;
    }

    @Override
    public String getDeviceName() {
        return "";
    }

    @Override
    public String getConnectionInfo() {
        return "";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void resetDeviceConfigurationForOpMode() {

    }

    @Override
    public void close() {

    }
}
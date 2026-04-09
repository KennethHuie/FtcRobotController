package org.firstinspires.ftc.teamcode.teamcode.Components;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

public class Flywheel {
    @NonNull
    public final DcMotor flywheel1;
    @NonNull
    public final DcMotor flywheel2;
    @NonNull
    final ElapsedTime elapsedTime;

    double lastTime;
    int targetSpeed = 0;
    int last1 = 0; // Encoder position at last measurement
    int last2 = 0; // ^
    double diff = 0;

    public Flywheel(HardwareMap hardwareMap, @NonNull ElapsedTime et) {
        elapsedTime = et;
        flywheel1 = hardwareMap.get(DcMotor.class, "flywheel1");
        flywheel2 = hardwareMap.get(DcMotor.class, "flywheel2");
        lastTime = et.seconds();
        // May offer better precision, remove if unnecessary
        flywheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // returns flywheel speed in ticks/second
    public double getCurrentSpeed() {
        int current1 = flywheel1.getCurrentPosition();
        int current2 = flywheel2.getCurrentPosition();

        double deltaTime = elapsedTime.seconds() - lastTime;
        lastTime = elapsedTime.seconds();

        double delta1 = (current1 - last1) / deltaTime;
        double delta2 = (current2 - last2) / deltaTime;
        // Update last-tick values
        last1 = current1;
        last2 = current2;
        diff = delta2 - delta1; // How much faster the second flywheel is than the left
        return delta1;
    }

    public double getDiff() {
        getCurrentSpeed(); // probably not the most efficient thing in the world but its simpler
        return diff;
    }

    // Set the speed in ticks/second
    public void setTargetSpeed(int x) {
        targetSpeed = x;
    }

    public void setPower(double power) {
        flywheel1.setPower(power);
        flywheel2.setPower(power);
    }

    public void update() {
        double current = getCurrentSpeed();
        flywheel1.setPower((targetSpeed / current) - 1);
        flywheel2.setPower((targetSpeed / current) - 1);
    }
}

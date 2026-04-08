package org.firstinspires.ftc.teamcode.teamcode.Components;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Sweeper {
    @NonNull
    final DcMotor sweeper;
    @NonNull
    final Rev2mDistanceSensor leftDistanceSensor;
    @NonNull
    final ColorDetector roofColorSensor;
    @NonNull
    final ColorDetector rearColorSensor;
    public int PollCount = 0;
    public int BallCount = 0; // How many times the ball has been intaked
    public int FireCount = 0; // How many times the ball has been outputted
    public boolean polling = false; // True when ball count tracking is started,
    public boolean incoming = false; // Flags incoming ball detected by distance sensor
    public double speed = 1;

    public Sweeper(@NonNull HardwareMap hardwareMap) {
        sweeper = hardwareMap.get(DcMotor.class, "sweeper");
        leftDistanceSensor = hardwareMap.get(Rev2mDistanceSensor.class, "distanceL");
        roofColorSensor = new ColorDetector(hardwareMap.get(RevColorSensorV3.class, "roof"));
        rearColorSensor = new ColorDetector(hardwareMap.get(RevColorSensorV3.class, "rear"));
    }

    class Intake implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            sweeper.setPower(speed);
            return false;
        }
    }

    class Stop implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            sweeper.setPower(0);
            return false;
        }
    }

    public Action intake() {
        return new Intake();
    }

    public Action stop() {
        return new Stop();
    }

    // Runs until polling flag is false, only call this in Parallel or else everything stops forever
    class StartTrack implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            PollCount += 1;
            if (leftDistanceSensor.getDistance(DistanceUnit.CM) < 20) {
                incoming = true;
            }
            if (incoming && roofColorSensor.isSensing() && BallCount < 2) {
                BallCount += 1;
                incoming = false;
            }
            if (rearColorSensor.isSensing() && BallCount > 0) {
                BallCount -= 1;
                FireCount += 1;
            }
            return polling;
        }
    }

    public Action startTrack() {
        polling = true;
        return new StartTrack();
    }

    // Stops sensor polling with a flag
    class StopTrack implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            polling = false;
            return false;
        }
    }

    public Action stopTrack() {
        return new StopTrack();
    }
}

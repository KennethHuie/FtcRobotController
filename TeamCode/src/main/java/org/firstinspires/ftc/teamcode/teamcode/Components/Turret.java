package org.firstinspires.ftc.teamcode.teamcode.Components;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class Turret {
    private final double TICKS_TO_DEGREES = 0.06; // 0.06 IS FOR A TETRIX TORQUENADO MOTOR AND 1:0.24 RATIO
    //https://www.desmos.com/calculator/r7tltoapdl < use this to calculate encoder limits using angles
    // -1500 = 90°, 1000 = 60°
    int toleranceTicks = 50; // Acceptable +- to target
    double toleranceDegrees = toleranceTicks * TICKS_TO_DEGREES;
    int leftBound = -1500; // Maximum left position in encoder ticks
    int rightBound = 1000; // Maximum right position in encoder ticks
    int dropoff = 500; // Width of the dropoff range in encoder ticks
    int correctionReductionFactor = 15000; // Given in encoder ticks, higher = corrects less (currentPositionTicks/THIS = correctionPower)
    public final DcMotor motor;
    public final Limelight3A camera;

    public Turret(HardwareMap hardwareMap) {
        motor = hardwareMap.get(DcMotor.class, "turret");
        motor.setDirection(DcMotorSimple.Direction.REVERSE);
        camera = hardwareMap.get(Limelight3A.class, "turretCam");
        camera.start();
    }

    private double tickToDegrees(int ticks) {
        return ticks * 0.06;
    }

    private int degreesToTicks(double degrees) {
        return (int) Math.round(degrees / 0.06);
    }

    class TurnToAngle implements Action {
        int angle;

        public TurnToAngle(double a) {
            angle = (int) Math.round((50d / 3) * a);
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            telemetryPacket.put("target", angle);
            motor.setTargetPosition(angle);
            int current = motor.getCurrentPosition();
            telemetryPacket.put("current", current);
            return ((angle - toleranceTicks) < current) && (angle < (angle + toleranceTicks));
        }
    }

    class TrackTag implements Action {
        int id;

        public TrackTag(int ID) {
            id = ID;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            return trackTag(id) == TrackStatus.Aligned;
        }
    }

    public Action turnTo(double a) {
        return new TurnToAngle(a);
    }

    public Action awaitTrackTag(int id) {
        return new TrackTag(id);
    }

    public void setTolerance(int ticks) {
        toleranceTicks = ticks;
        toleranceDegrees = ticks * TICKS_TO_DEGREES;
    }

    public void setTolerance(double degrees) {
        toleranceDegrees = degrees;
        toleranceTicks = (int) Math.round(degrees / TICKS_TO_DEGREES);
    }

    public void setLeftBound(int x) {
        if (x > 0) {
            throw new RuntimeException("Cannot set Left Bound to be greater than 0");
        }
        leftBound = x;
    }

    public void setRightBound(int x) {
        if (x < 0) {
            throw new RuntimeException("Cannot set Right Bound to be less than 0");
        }
        rightBound = x;
    }

    public void setDropoff(int x) {
        dropoff = Math.abs(x);
    }

    //https://www.desmos.com/calculator/80zwducpyr < Visualization of what this math does
    public void setTargetVelocity(double power) {
        double currentAsDouble = motor.getCurrentPosition();
        double limiterCoeff = 1;
        double d = 0;
        if (currentAsDouble <= leftBound + dropoff & power < 0) {
            d = (currentAsDouble - leftBound - dropoff) / dropoff;
        } else if (currentAsDouble >= rightBound - dropoff && power > 0) {
            d = (currentAsDouble - rightBound + dropoff) / dropoff;
        }
        limiterCoeff -= 1 - Math.sqrt(Math.max(0, 1 - d * d));
        double CRF = -currentAsDouble / correctionReductionFactor;
        double correction = (currentAsDouble < leftBound) ? CRF : (currentAsDouble > rightBound ? CRF : 0);
        motor.setPower((power * limiterCoeff) + correction);
    }

    public TrackStatus trackTag(int id) {
        List<LLResultTypes.FiducialResult> trackedTags = camera.getLatestResult().getFiducialResults();
        for (LLResultTypes.FiducialResult tag : trackedTags) {
            if (tag.getFiducialId() != id) continue;
            double degX = tag.getTargetXDegrees();
            setTargetVelocity(degX / 20);
            if (degX < toleranceDegrees && degX > -toleranceDegrees) {
                return TrackStatus.Aligned;
            }
            return TrackStatus.Tracking;
        }
        return TrackStatus.Stopped;
    }
}

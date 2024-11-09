package org.firstinspires.ftc.teamcode.teamcode;

import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Configuration {
    public double scaleDrive = 1; // Scale drive stick axis.
    public double scaleTurn = 1; // Scale turn stick axis.
    public double scaleStrafe = 0.5; // Scale strafe stick axis.
    public double maxDrive = 1;// Maximum motor speed while driving.
    public double maxTurn = 1; // Maximum motor speed while turning.
    public double maxStrafe = 0.5; // Maximum motor speed while strafing.

    public double minLStickOverridePerc = 2; // How many times more the stick position has to be bigger than the other to override it

    public double timeToMaxScale = 150; // Time in ms to reach full speed

    public Direction FL_Direction = Direction.FORWARD;
    public Direction RL_Direction = Direction.FORWARD;
    public Direction FR_Direction = Direction.FORWARD;
    public Direction RR_Direction = Direction.FORWARD;

    public boolean runtimeDebug = true; // Show runtime
    public boolean motorPowerDebug = false; // Show individual motor power debug
    public boolean motorDirectionDebug = false; // Show individual motor directions
    public boolean appliedDriveValuesDebug = false; // Show drive, turn, strafe
    public boolean controllerAxesDebug = false; // Show left and right stick X and Y
    public boolean timeScalePerAxesDebug = false; // Show time scale for drive, turn, and strafe

    public static class MecanumV2 extends Configuration{
        public MecanumV2() {
            super();
            super.RL_Direction = Direction.REVERSE;
        }
        public boolean sliderMotorPowerDebug = true;
    }

    public static class Testrig extends Configuration {
        public Testrig() {
            super();
            super.FR_Direction = Direction.REVERSE;
            super.RR_Direction = Direction.REVERSE;
            super.motorPowerDebug = true; // Show individual motor power debug
            super.motorDirectionDebug = true; // Show individual motor directions
        }

        public boolean runtimeDebug = true; // Show runtime
        public boolean appliedDriveValuesDebug = true; // Show drive, turn, strafe
        public boolean controllerAxesDebug = true; // Show left and right stick X and Y
        public boolean timeScalePerAxesDebug = true; // Show time scale for drive, turn, and strafe
    }
}
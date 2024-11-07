package org.firstinspires.ftc.teamcode.teamcode;

public class Configuration {
    public static class MecanumV2 {
        public final double scaleDrive = 1; // Scale drive stick axis.
        public final double scaleTurn = 1; // Scale turn stick axis.
        public final double scaleStrafe = 0.5; // Scale strafe stick axis.

        public final double maxDrive = 1;// Maximum motor speed while driving.
        public final double maxTurn = 1; // Maximum motor speed while turning.
        public final double maxStrafe = 0.5; // Maximum motor speed while strafing.

        public final double minLStickOverridePerc = 2; // How many times more the stick position has to be bigger than the other to override it

        public final double timeToMaxScale = 150; // Time in ms to reach full speed

        public final boolean runtimeDebug = true; // Show runtime
        public final boolean appliedDriveValuesDebug = false; // Show drive, turn, strafe
        public final boolean sliderMotorPowerDebug = true; // Show vsPower, and hsPower
        public final boolean controllerAxesDebug = false; // Show left and right stick X and Y
        public final boolean timeScalePerAxesDebug = false; // Show time scale for drive, turn, and strafe
    }
}

package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class DecodeFast {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(1080);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(30, 25, Math.toRadians(180), Math.toRadians(180), 18)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(60, -16,Math.toRadians(180)))
                .strafeToLinearHeading(new Vector2d(0,0),Math.toRadians(90))
                .waitSeconds(5)
                .strafeTo(new Vector2d(-12,30))
                .strafeTo(new Vector2d(-12,45))
                .strafeTo(new Vector2d(-18,24))
                .waitSeconds(5)
                .strafeTo(new Vector2d(12,30))
                .strafeTo(new Vector2d(12,45))
                .strafeTo(new Vector2d(-18,24))
                .waitSeconds(5)
                .strafeTo(new Vector2d(34,30))
                .strafeTo(new Vector2d(34,45))
                .strafeTo(new Vector2d(-18,24))
                .waitSeconds(5)
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_DECODE_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}
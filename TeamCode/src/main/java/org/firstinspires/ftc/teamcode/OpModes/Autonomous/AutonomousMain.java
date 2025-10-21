package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class AutonomousMain extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("testing", "it works");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("testing", "look ma am running");
            telemetry.update();
        }
    }
}
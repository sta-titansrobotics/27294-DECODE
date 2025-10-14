package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class Main extends LinearOpMode {
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
package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class ControllerInputTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("status", "awaiting init");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Left Stick X: 1", this.gamepad1.left_stick_x);
            telemetry.addData("Left Stick Y: 1", this.gamepad1.left_stick_y);

            telemetry.addData("status", "running");
            telemetry.update();
        }

        telemetry.addData("status", "stopped");
        telemetry.update();
    }
}
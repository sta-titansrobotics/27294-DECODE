package org.firstinspires.ftc.teamcode.OpModes.Analysis;

import org.firstinspires.ftc.teamcode.Mechanisms.ManualLaunchControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(group = "Analysis")
public class LaunchAnalysis extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        final ManualLaunchControl<DcMotor> launchControl = new ManualLaunchControl<>(
            hardwareMap.get(DcMotor.class, "feederMotor"),
            hardwareMap.get(DcMotor.class, "launchMotor")
        );

        telemetry.addData("status", "ready");
        telemetry.update();

        waitForStart();

        double targetRPM = 0;

        while (opModeIsActive()) {
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Hardware Reported Max RPM", launchControl.launcher.getMotorType().getMaxRPM());
            telemetry.addData("---", "---");
            telemetry.addData("DPAD Left/Right", "-+ 1");
            telemetry.addData("Bumper Left/Right", "-+ 10");
            telemetry.addData("X/B", "-+ 100");
            telemetry.addData("Start", "Begin Launch");
            telemetry.update();

            if (this.gamepad1.dpadLeftWasPressed()) {
                targetRPM -= 1;
            } else if (this.gamepad1.dpadRightWasPressed()) {
                targetRPM += 1;
            } else if (this.gamepad1.leftBumperWasPressed()) {
                targetRPM -= 10;
            } else if (this.gamepad1.rightBumperWasPressed()) {
                targetRPM += 10;
            } else if (this.gamepad1.xWasPressed()) {
                targetRPM -= 100;
            } else if (this.gamepad1.bWasPressed()) {
                targetRPM += 100;
            }

            if (!this.gamepad1.startWasPressed()) continue;

            int lastPos = launchControl.launcher.getCurrentPosition();
            double lastTime = getRuntime();
            double ticksPerRev = launchControl.launcher.getMotorType().getTicksPerRev();
            launchControl.setEnableLauncher(true);
            launchControl.setFeederPower(0);

            double timeSinceAchieved = -1;

            while (opModeIsActive() && !this.gamepad1.back) {
                // rpm calculations
                double cTime = getRuntime();
                int cPos = launchControl.launcher.getCurrentPosition();
                double timeSinceLast = cTime - lastTime;
                double ticksPerMinute = ((double)(cPos - lastPos)) * (60 / timeSinceLast);
                double rpm = ticksPerMinute / ticksPerRev;

                if (rpm >= targetRPM) {
                    launchControl.setFeederPower(1.0);
                    if (timeSinceAchieved == -1) timeSinceAchieved = getRuntime();
                }

                telemetry.addData("Observed RPM", rpm);
                if (timeSinceAchieved != -1) telemetry.addData("RPM Achieved, shutting down in (s)", timeSinceAchieved + 10 - getRuntime());
                telemetry.addData("---", "---");
                telemetry.addData("Back", "Stop Launch");
                telemetry.update();

                if (timeSinceAchieved != -1 && timeSinceAchieved + 10 - getRuntime() <= 0) break; // breakout since 10s has passed

                lastTime = cTime;
                lastPos = cPos;
                sleep(100);
            }

            timeSinceAchieved = -1;
            launchControl.setFeederPower(0);
            launchControl.setEnableLauncher(false);
        }
    }
}

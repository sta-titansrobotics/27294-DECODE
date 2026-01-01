package org.firstinspires.ftc.teamcode.OpModes.Analysis;

import org.firstinspires.ftc.teamcode.Utilities.RPMTracker;
import org.firstinspires.ftc.teamcode.Utilities.Setup;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(group = "Analysis")
public class LaunchAnalysis extends LinearOpMode {
    public DcMotor feederMotor;
    public DcMotor launchMotor;

    public RPMTracker<DcMotor> launchRPM;

    @Override
    public void runOpMode() throws InterruptedException {
        this.feederMotor = hardwareMap.get(DcMotor.class, "feederMotor");
        this.launchMotor = hardwareMap.get(DcMotor.class, "launchMotor");
        Setup.launchMotors(this.launchMotor, this.feederMotor);

        telemetry.addData("status", "ready");
        telemetry.update();

        waitForStart();

        double targetRPM = 0;

        while (opModeIsActive()) {
            telemetry.addData("Target RPM", targetRPM);
            telemetry.addData("Hardware Reported Max RPM", this.launchMotor.getMotorType().getMaxRPM());
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

            this.launchMotor.setPower(1);
            this.feederMotor.setPower(0);

            double timeSinceAchieved = -1;

            this.launchRPM = new RPMTracker<>(this.launchMotor, getRuntime());

            while (opModeIsActive() && !this.gamepad1.back) {
                double rpm = this.launchRPM.getRPM(getRuntime());

                if (rpm >= targetRPM) {
                    this.feederMotor.setPower(1);
                    if (timeSinceAchieved == -1) timeSinceAchieved = getRuntime();
                }

                telemetry.addData("Observed RPM", rpm);
                if (timeSinceAchieved != -1) telemetry.addData("RPM Achieved, shutting down in (s)", timeSinceAchieved + 10 - getRuntime());
                telemetry.addData("---", "---");
                telemetry.addData("Back", "Stop Launch");
                telemetry.update();

                if (timeSinceAchieved != -1 && timeSinceAchieved + 10 - getRuntime() <= 0) break; // breakout since 10s has passed

                sleep(100);
            }

            timeSinceAchieved = -1;
            this.feederMotor.setPower(0);
            this.launchMotor.setPower(0);
        }
    }
}

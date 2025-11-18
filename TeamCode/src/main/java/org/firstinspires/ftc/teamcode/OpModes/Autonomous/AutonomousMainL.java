package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import org.firstinspires.ftc.teamcode.DrivechainMovement.Drivechain4WD;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class AutonomousMainL extends LinearOpMode {
    public Drivechain4WD<DcMotor> drivechain;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivechain = new Drivechain4WD<>(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        telemetry.addData("status", "ready");
        telemetry.update();

        waitForStart();

        double timeoutUntil = getRuntime() + AutonomousConfiguration.FORWARD_TIMEOUT; // 5s
        this.drivechain.setPower(0.5);

        while (opModeIsActive()) {
            telemetry.addData("status", "b-lining to the wall :)");
            telemetry.update();
            if (getRuntime() >= timeoutUntil) break;
        }

        timeoutUntil = getRuntime() + AutonomousConfiguration.SIDE_MANOUVER_TIMEOUT; // 3s
        this.drivechain.setSideManouverPower(-0.5f);

        while (opModeIsActive()) {
            telemetry.addData("status", "side manouvering");
            telemetry.update();
            if (getRuntime() >= timeoutUntil) break;
        }

        this.drivechain.setPower(0);

        while (opModeIsActive()) {
            telemetry.addData("status", "done");
            telemetry.update();
        }
    }
}
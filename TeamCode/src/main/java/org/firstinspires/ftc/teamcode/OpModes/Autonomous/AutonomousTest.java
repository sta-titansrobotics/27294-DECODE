package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AprilTag.AprilTagController;
import org.firstinspires.ftc.teamcode.DrivechainMovement.Drivechain4WD;
import org.firstinspires.ftc.teamcode.LaunchMechanism.ManualLaunchControl;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
public class AutonomousTest extends LinearOpMode {
    public AprilTagController aprilTagProcess;
    public Drivechain4WD drivechain;
    public ManualLaunchControl launchControl;

    public Procedures currentProcedure = Procedures.NAVIGATE_TO_PIT;
    public double timeoutUntil = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        this.aprilTagProcess = new AprilTagController(
            hardwareMap.get(WebcamName.class, "camera"),
            this
        );

        this.drivechain = new Drivechain4WD(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        this.launchControl = new ManualLaunchControl(
            hardwareMap.get(DcMotor.class, "feederMotor"),
            hardwareMap.get(DcMotor.class, "launchMotor")
        );

        telemetry.addData("status", "awaiting start");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            switch (this.currentProcedure) {
                case NAVIGATE_TO_PIT: // TODO
                    break;
                case WAIT: // always goes to NAVIGATE_TO_GOAL as this is a grace period for team to load artefacts TODO
                    if (getRuntime() <= this.timeoutUntil) continue;

                    this.currentProcedure = Procedures.NAVIGATE_TO_GOAL;
                    break;
                case NAVIGATE_TO_GOAL: // TODO
                    this.currentProcedure = Procedures.LAUNCH_ARTEFACTS;
                    break;
                case REV_LAUNCH_MOTOR:
                    this.launchControl.setEnableLauncher(true);
                    if (getRuntime() >= this.timeoutUntil) { // waited enough
                        this.currentProcedure = Procedures.LAUNCH_ARTEFACTS;
                        this.timeoutUntil = getRuntime() + 10000; // 10000 ms = 10s
                    }
                    break;
                case LAUNCH_ARTEFACTS:
                    this.launchControl.setFeederPower(1);
                    if (getRuntime() >= this.timeoutUntil) { // waited enough
                        this.currentProcedure = Procedures.NAVIGATE_TO_PIT;
                    }
                    
                    break;
            }
        }
    }

    public void setWaitTimeout(double ms) {
        this.timeoutUntil = getRuntime() + ms;
        this.currentProcedure = Procedures.WAIT;
    }

}

enum Procedures {
    NAVIGATE_TO_PIT,
    WAIT,
    NAVIGATE_TO_GOAL,
    REV_LAUNCH_MOTOR,
    LAUNCH_ARTEFACTS
}

class TagIDS {
    public static final int BLUE_GOAL = 20;
    public static final int RED_GOAL = 24;

    public static final int GPP_OBLISK = 21;
    public static final int PGP_OBLISK = 22;
    public static final int PPG_OBLISK = 23;
}
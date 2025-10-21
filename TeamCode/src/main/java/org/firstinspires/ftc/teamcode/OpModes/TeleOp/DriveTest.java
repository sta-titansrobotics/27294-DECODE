package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import org.firstinspires.ftc.teamcode.DrivechainMovement.Drivechain4WD;
import org.firstinspires.ftc.teamcode.LaunchMechanism.ManualLaunchControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class DriveTest extends LinearOpMode {
    public Drivechain4WD drivechain;
    public ManualLaunchControl launchControl;
    public CRServo agitator;

    @Override
    public void runOpMode() throws InterruptedException {
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

        this.agitator = hardwareMap.get(CRServo.class, "agitator");

        telemetry.addData("status", "awaiting init");
        telemetry.addData("Expected Drivechain Configuration", "Drivechain: [frontLeft, frontRight, backLeft, backRight]");
        telemetry.addData("Expected Launch Control Configuration", "Drivechain: [feederMotor, launchMotor]");
        telemetry.addData("Expected Servos", "[agitator]");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // note: gamepad values are from -1 to 1

            float rotationalOffset = this.gamepad1.left_stick_x; // left is -1 and right is 1
            float forwardPower = -this.gamepad1.left_stick_y;  // according to ftc docs, without negating the value; 1 is down -1 is up

            if (forwardPower == 0) { // stationary rotation
                this.drivechain.setPower(rotationalOffset, -rotationalOffset);
            } else { // forward movement with optional rotation (while driving)
                this.drivechain.setPower(
                    rotationalOffset < 0 ? forwardPower + rotationalOffset : forwardPower,
                    rotationalOffset > 0 ? forwardPower - rotationalOffset : forwardPower
                );
            }

            if (this.gamepad1.left_trigger > 0.2f) {
                this.launchControl.enableLauncher();
            } else {
                this.launchControl.disableLauncher();
            }

            if (this.gamepad1.right_trigger > 0.2f) {
                this.launchControl.setFeederPower(1);
            } else {
                this.launchControl.setFeederPower(0);
            }

            this.agitator.setPower(1);

            telemetry.addData("status", "running");
            telemetry.update();
        }

        telemetry.addData("status", "stopped");
        telemetry.update();
    }
}
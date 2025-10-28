package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import org.firstinspires.ftc.teamcode.DrivechainMovement.Drivechain4WD;
import org.firstinspires.ftc.teamcode.LaunchMechanism.ManualLaunchControl;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class DriveTest extends LinearOpMode {
    public Drivechain4WD drivechain;
    public ManualLaunchControl launchControls;
    public CRServo agitator;

    public double agitatorPower = 1;

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivechain = new Drivechain4WD(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        this.launchControls = new ManualLaunchControl(
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

        this.gamepad1.backWasPressed();

        while (opModeIsActive()) {
            // note: gamepad values are from -1 to 1
            
            float rotationalOffset = this.gamepad1.right_stick_x; // left is -1 and right is 1
            float forwardPower = -this.gamepad1.left_stick_y;  // according to ftc docs, without negating the value; 1 is down -1 is up

            if (forwardPower == 0) { // stationary rotation
                this.drivechain.setPower(rotationalOffset, -rotationalOffset);
            } else { // forward movement with optional rotation (while driving)
                if (forwardPower < 0) rotationalOffset = -rotationalOffset;

                this.drivechain.setPower(
                    forwardPower + rotationalOffset,
                    forwardPower - rotationalOffset 
                );
            }

            if (this.gamepad1.left_trigger > 0.2f) { // launch
                this.launchControls.enableLauncher();
            } else {
                this.launchControls.disableLauncher();
            }

            if (this.gamepad1.right_trigger > 0.2f) { // feeder
                this.launchControls.setFeederPower(1);
            } else {
                this.launchControls.setFeederPower(0);
            }

            if (this.gamepad1.backWasPressed()) { // disabling of agitator because artefact gets stuck
                this.agitatorPower = this.agitatorPower == 1 ? 0 : 1;
            }

            this.agitator.setPower(this.agitatorPower);

            telemetry.addData("status", "running");
            telemetry.addData("Back Button", "enable/disable of agitator");
            telemetry.addData("Left Trigger", "Launch Motor");
            telemetry.addData("Right Trigger", "Feeder Motor");
            telemetry.addData("Left Joystick", "forward and back");
            telemetry.addData("Right Joystick", "rotation left and right respectively");
            telemetry.update();
        }

        telemetry.addData("status", "stopped");
        telemetry.update();
    }
}
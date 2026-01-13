package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Drivechain4Motors;
import org.firstinspires.ftc.teamcode.Utilities.Setup;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class TeleopMain extends LinearOpMode {
    public Drivechain4Motors<DcMotor> drivechain;
    public DcMotor feederMotor;
    public DcMotor launchMotor;
    public DcMotor intakeMotor;
    public CRServo gateServo;
    public CRServo internalAgitator;
    public Servo hoodServo;
    public Servo liftServo;
    
    // incase one cycle last less then 0.1 or more
    public long timeToCatch = 1; // 1 = 100ms

    @Override
    public void runOpMode() throws InterruptedException {
        this.drivechain = new Drivechain4Motors<>(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        this.hoodServo = hardwareMap.get(Servo.class, "hoodServo");

        this.feederMotor = hardwareMap.get(DcMotor.class, "feederMotor");
        this.launchMotor = hardwareMap.get(DcMotor.class, "launchMotor");
        Setup.launchMotors(this.launchMotor, this.feederMotor);
        this.intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        this.gateServo = hardwareMap.get(CRServo.class, "gateServo");
        this.gateServo.setDirection(CRServo.Direction.REVERSE);
        this.liftServo = hardwareMap.get(Servo.class, "liftServo");

        telemetry.addData("status", "awaiting init");
        telemetry.update();

        double hoodPos = 0;
        double liftPos = 0;

        waitForStart();

        while (opModeIsActive()) {
            // note: gamepad values are from -1 to 1
            float rotationalOffset = this.gamepad1.right_stick_x; // left is -1 and right is 1
            float forwardPower = -this.gamepad1.left_stick_y;  // according to ftc docs, without negating the value; 1 is down -1 is up

            if (this.gamepad1.dpad_up) { // dpad movement fwd and bk
                this.drivechain.frontLeft.setPower(this.gamepad1.dpad_left ? 0 : 1);
                this.drivechain.frontRight.setPower(this.gamepad1.dpad_right ? 0 : 1);
                this.drivechain.backLeft.setPower(this.gamepad1.dpad_right ? 0 : 1);
                this.drivechain.backRight.setPower(this.gamepad1.dpad_left ? 0 : 1);
            } else if (this.gamepad1.dpad_down) {
                this.drivechain.frontLeft.setPower(this.gamepad1.dpad_right ? 0 : -1);
                this.drivechain.frontRight.setPower(this.gamepad1.dpad_left ? 0 : -1);
                this.drivechain.backLeft.setPower(this.gamepad1.dpad_left ? 0 : -1);
                this.drivechain.backRight.setPower(this.gamepad1.dpad_right ? 0 : -1);
            } else if (this.gamepad1.dpad_left || this.gamepad1.dpad_right) { // dpad movement side to side
                this.drivechain.setSideManouverPower(this.gamepad1.dpad_right ? 1 : -1);
            } else if (forwardPower == 0) { // stationary rotation
                this.drivechain.setPower(rotationalOffset, -rotationalOffset);
            } else { // forward movement with optional rotation (while driving)
                if (forwardPower < 0) rotationalOffset = -rotationalOffset;

                this.drivechain.setPower(
                    Range.clip(forwardPower + rotationalOffset * 1.5f, -1, 1),
                    Range.clip(forwardPower - rotationalOffset * 1.5f, -1, 1)
                );
            }

            this.feederMotor.setPower(this.gamepad1.left_trigger > 0.1f ? 1 : 0);
            this.launchMotor.setPower(this.gamepad1.right_trigger > 0.1f ? 1 : 0);
            if (this.gamepad1.left_bumper ^ this.gamepad1.right_bumper) {
                hoodPos = Range.clip(
                    hoodPos + (this.gamepad1.left_bumper ? -0.05 : 0.05),
                    0, 1
                );
            }
            this.hoodServo.setPosition(hoodPos);


            this.gateServo.setPower(this.gamepad2.left_trigger > 0.1f ? 1 : 0);
            this.intakeMotor.setPower(this.gamepad2.left_bumper ? 1 : 0);
            if (this.gamepad2.dpad_up ^ this.gamepad2.dpad_down) {
                liftPos = Range.clip(
                    liftPos + (this.gamepad2.dpad_up ? 0.1 : -0.1),
                    0, 1
                );
            }
            this.liftServo.setPosition(liftPos);


            telemetry.addData("status", "running");
            telemetry.addData("Left Trigger", "Launch Motor");
            telemetry.addData("Right Trigger", "Feeder Motor");
            telemetry.addData("Left Joystick", "forward and back");
            telemetry.addData("Right Joystick", "rotation left and right respectively");
            telemetry.addData("DPad", "stationary manouvers, joysticks will be disabled when using dpad actively");
            telemetry.update();

            long val = (long)getRuntime() * 10;
            if (val < timeToCatch) {
                sleep((timeToCatch - val) * 100);
            }

            timeToCatch += 1;
        }

        telemetry.addData("status", "stopped");
        telemetry.update();
    }
}
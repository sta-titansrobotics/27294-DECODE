package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import org.firstinspires.ftc.teamcode.Mechanisms.Drivechain4Motors;
import org.firstinspires.ftc.teamcode.Mechanisms.HoodControl;
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
    public HoodControl hood;
    public DcMotor feederMotor;
    public DcMotor launchMotor;
    public DcMotor intakeMotor;
    public CRServo agitator;
    
    @Override
    public void runOpMode() throws InterruptedException {
        this.drivechain = new Drivechain4Motors<>(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        this.hood = new HoodControl(
            hardwareMap.get(Servo.class, "hoodServo"),
            0 // TODO: get values stat.
        );

        this.feederMotor = hardwareMap.get(DcMotor.class, "feederMotor");
        this.launchMotor = hardwareMap.get(DcMotor.class, "launchMotor");
        Setup.launchMotors(this.launchMotor, this.feederMotor);

        this.agitator = hardwareMap.get(CRServo.class, "agitator");
        this.intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");

        telemetry.addData("status", "awaiting init");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // note: gamepad values are from -1 to 1
            float rotationalOffset = this.gamepad1.right_stick_x; // left is -1 and right is 1
            float forwardPower = -this.gamepad1.left_stick_y;  // according to ftc docs, without negating the value; 1 is down -1 is up

            if (this.gamepad1.dpad_up || this.gamepad1.dpad_down) { // dpad movement fwd and bk
                boolean fwdSDir = this.gamepad1.dpad_up ? this.gamepad1.dpad_right : this.gamepad1.dpad_left;
                boolean bwdSDir = this.gamepad1.dpad_up ? this.gamepad1.dpad_left : this.gamepad1.dpad_right;
                int power = this.gamepad1.dpad_up ? 1 : -1;

                this.drivechain.frontLeft.setPower(bwdSDir ? 0 : power);
                this.drivechain.frontRight.setPower(fwdSDir ? 0 : power);
                this.drivechain.backLeft.setPower(fwdSDir ? 0 : power);
                this.drivechain.backRight.setPower(bwdSDir ? 0 : power);
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

            // a / y: agitator control
            if (this.gamepad2.x || this.gamepad2.y) {
                this.agitator.setPower(this.gamepad2.x ? -1 : 1); // y will be forward
            } else {
                this.agitator.setPower(0);
            }

            this.intakeMotor.setPower(this.gamepad2.left_bumper ? 1 : 0);
            this.feederMotor.setPower(this.gamepad2.right_trigger > 0.1f ? 1 : 0);
            this.launchMotor.setPower(this.gamepad2.left_trigger > 0.1f ? 1 : 0);

            telemetry.addData("status", "running");
            telemetry.addData("Left Trigger", "Launch Motor");
            telemetry.addData("Right Trigger", "Feeder Motor");
            telemetry.addData("Left Joystick", "forward and back");
            telemetry.addData("Right Joystick", "rotation left and right respectively");
            telemetry.addData("DPad", "stationary manouvers, joysticks will be disabled when using dpad actively");
            telemetry.update();

            sleep(100);
        }

        telemetry.addData("status", "stopped");
        telemetry.update();
    }
}
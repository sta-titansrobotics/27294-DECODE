package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import java.util.List;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp
public class MotorAnalysis extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        final List<DcMotor> motors = hardwareMap.getAll(DcMotor.class);

        telemetry.addData("Detectable Motors", motors.size());
        telemetry.update();

        waitForStart();

        int pointer = 0;

        while (opModeIsActive()) {
            DcMotor motor = motors.get(pointer++);
            if (motor == null) {
                motor = motors.get(0);
                pointer = 0;
            }

            float motorPower = 0;

            while (!this.gamepad1.rightBumperWasPressed() || opModeIsActive()) {
                if (this.gamepad1.dpadRightWasPressed()) {
                    motorPower = motorPower <= 1f ? motorPower + 0.1f : 1f;
                } else if (this.gamepad1.dpadLeftWasPressed()) {
                    motorPower = motorPower >= -1f ? motorPower - 0.1f : -1f;
                }

                motor.setPower(motorPower);

                telemetry.addData("Currently Inspecting", motor.getMotorType().getName());
                telemetry.addData("Detectable Motors", motors.size());
                telemetry.addData("List Index", pointer);
                telemetry.addData("Name", motor.getMotorType().getDescription());
                telemetry.addData("Tracked Power", motorPower);
                telemetry.addData("Reported Power", motor.getPower());
                telemetry.addData("Max RPM", motor.getMotorType().getMaxRPM());
                telemetry.addData("---", "---");
                telemetry.addData("Right Bumper", "Next Motor");
                telemetry.addData("DPad Left and Right", "decrease/increase power respectivly by 0.1");
                telemetry.update();
            }

            motor.setPower(0);
            if (pointer >= motors.size()) pointer = 0;
        }
    }
}

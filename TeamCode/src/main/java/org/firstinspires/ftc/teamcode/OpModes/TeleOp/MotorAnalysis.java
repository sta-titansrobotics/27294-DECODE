package org.firstinspires.ftc.teamcode.OpModes.TeleOp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;

@TeleOp
public class MotorAnalysis extends LinearOpMode {
    @SuppressWarnings("unchecked") // in try/catch; unless they changed the code this is *probably* not an issue
    @Override
    public void runOpMode() throws InterruptedException {
        Field declared;
        try {
            declared = HardwareMap.class.getDeclaredField("allDevicesMap");
            declared.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new InterruptedException("allDeviceMap does not exist on hardwareMap, reinspect the code pls");
        }

        Map<String, List<HardwareDevice>> deviceMapping;
        try {
            // @SuppressWarnings("unchecked") 
            deviceMapping = (Map<String, List<HardwareDevice>>) declared.get(hardwareMap);
        } catch (IllegalAccessException e) {
            throw new InterruptedException("so somehow reflection api does not want me to access allDeviceMap, go debug man");
        }

        final HashMap<DcMotor, String> motors = new HashMap<>();
        deviceMapping.forEach((name, list) -> {
            for (HardwareDevice device : list) {
                if (device instanceof DcMotor) motors.put((DcMotor) device, name);
            }
        });

        telemetry.addData("Detectable Motors", motors.size());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            float motorPower = 0;

            for (DcMotor motor : motors.keySet()) {
                int lastPos = 0;
                double lastTime = getRuntime();
                double ticksPerRev = motor.getMotorType().getTicksPerRev();

                while (opModeIsActive()) {
                    if (this.gamepad1.dpadRightWasPressed()) {
                        motorPower = motorPower <= 1f ? motorPower + 0.1f : 1f;
                    } else if (this.gamepad1.dpadLeftWasPressed()) {
                        motorPower = motorPower >= -1f ? motorPower - 0.1f : -1f;
                    }

                    if (this.gamepad1.rightBumperWasPressed()) break;
    
                    motor.setPower(motorPower);

                    double timeSinceLast = getRuntime() - lastTime;
                    double ticksPerMinute = ((double)(motor.getCurrentPosition() - lastPos)) * (60 / timeSinceLast);

                    telemetry.addData("Currently Inspecting", motor.getMotorType().getName());
                    telemetry.addData("Name", motors.get(motor));
                    telemetry.addData("Tracked Power", motorPower);
                    telemetry.addData("Reported Power", motor.getPower());
                    telemetry.addData("Max RPM", motor.getMotorType().getMaxRPM());
                    telemetry.addData("RPM Calculations", (
                        ticksPerMinute / ticksPerRev
                    ));
                    telemetry.addData("Detectable Motors", motors.size());
                    telemetry.addData("---", "---");
                    telemetry.addData("Right Bumper", "Next Motor");
                    telemetry.addData("DPad Left and Right", "decrease/increase power respectivly by 0.1");
                    telemetry.update();

                    lastTime = getRuntime();
                    lastPos = motor.getCurrentPosition();
                }

                motor.setPower(0);

                if (!opModeIsActive()) break;
            }
        }
    }
}

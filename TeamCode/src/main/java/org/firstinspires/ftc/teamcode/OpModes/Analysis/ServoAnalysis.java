package org.firstinspires.ftc.teamcode.OpModes.Analysis;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(group = "Analysis")
public class ServoAnalysis extends LinearOpMode {
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

        final HashMap<Servo, String> servos = new HashMap<>();
        deviceMapping.forEach((name, list) -> {
            for (HardwareDevice device : list) {
                if (device instanceof Servo) servos.put((Servo) device, name);
            }
        });

        telemetry.addData("Detectable Motors", servos.size());
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            float setPos = 0;

            for (Servo servo : servos.keySet()) {
                while (opModeIsActive()) {
                    if (this.gamepad1.dpadLeftWasPressed()) {
                        setPos -= 0.001;
                    } else if (this.gamepad1.dpadRightWasPressed()) {
                        setPos += 0.001;
                    } else if (this.gamepad1.leftBumperWasPressed()) {
                        setPos -= 0.01;
                    } else if (this.gamepad1.rightBumperWasPressed()) {
                        setPos += 0.01;
                    } else if (this.gamepad1.xWasPressed()) {
                        setPos -= 0.1;
                    } else if (this.gamepad1.bWasPressed()) {
                        setPos += 0.1;
                    }

                    setPos = Range.clip(setPos, 0, 1);

                    if (this.gamepad1.backWasPressed()) break;
                    servo.setPosition(setPos);

                    telemetry.addData("Currently Inspecting", servos.get(servo));
                    telemetry.addData("Code Set Position", setPos);
                    telemetry.addData("Reported Position", servo.getPosition());
                    telemetry.addData("Number of Detectable Servos", servos.size());
                    telemetry.addData("---", "---");
                    telemetry.addData("Back", "Next Servo");
                    telemetry.addData("DPAD Left/Right", "-+ 0.001");
                    telemetry.addData("Bumper Left/Right", "-+ 0.01");
                    telemetry.addData("X/B", "-+ 0.1");
                    telemetry.update();
                }

                servo.setPosition(0);
                if (!opModeIsActive()) break;
            }
        }
    }
}

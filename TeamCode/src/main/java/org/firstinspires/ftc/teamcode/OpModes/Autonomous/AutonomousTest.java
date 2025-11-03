package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AprilTag.AprilTagController;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous
public class AutonomousTest extends LinearOpMode {
    public AprilTagController aprilTagProcess = new AprilTagController(
        hardwareMap.get(WebcamName.class, "camera")
    );

    @Override
    public void runOpMode() throws InterruptedException {

    }
}

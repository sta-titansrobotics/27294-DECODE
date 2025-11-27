package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AprilTag.AprilTagController;
import org.firstinspires.ftc.teamcode.DrivechainMovement.Drivechain4WD;
import org.firstinspires.ftc.teamcode.LaunchMechanism.ManualLaunchControl;
import org.firstinspires.ftc.vision.VisionPortal.StreamFormat;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import android.util.Size;

@Autonomous
public class AutonomousTest extends LinearOpMode {
    public AprilTagController aprilTagProcess;
    public Drivechain4WD<DcMotor> drivechain;
    public ManualLaunchControl launchControl;
    public ColorSensor color;

    public Procedures currentProcedure = Procedures.NAVIGATE_TO_LAUNCHZONE; // assuming that at the start of auto we already have artefacts loaded, so we can navigate to a launchzone
    public double timeoutUntil = 0;

    public int targetTagID_Team = TagID.BLUE_GOAL;
    public int targetTagID_Motif = TagID.GPP_OBLISK;

    public boolean inLaunchZone = false;

    @Override
    public void runOpMode() throws InterruptedException {
        this.aprilTagProcess = new AprilTagController(
            hardwareMap.get(WebcamName.class, "camera"),
            builder -> builder.setCameraResolution(new Size(1280, 720))
                        .setStreamFormat(StreamFormat.MJPEG),
            this,
            new AprilTagProcessor.Builder()
                .build()
        );

        this.color = hardwareMap.get(ColorSensor.class, "color");

        this.drivechain = new Drivechain4WD<>(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        // this.launchControl = new ManualLaunchControl(
        //     hardwareMap.get(DcMotor.class, "feederMotor"),
        //     hardwareMap.get(DcMotor.class, "launchMotor")
        // );

        while (opModeInInit()) {
            telemetry.addData("Configure Team Side", "X for Blue, B for Red");
            telemetry.update();

            if (gamepad1.x || gamepad1.b) {
                targetTagID_Team = gamepad1.x ? TagID.BLUE_GOAL : TagID.RED_GOAL;
                while (gamepad1.x || gamepad1.b || !opModeInInit()) {} // do nothing since we dont want the gamepads to be pressed while we are setting up the next configuration
                break;
            }
        }

        while (opModeInInit()) {
            telemetry.addData("Configure In Launch Zone (Color Sensors must be in the launch zone)", "X for Yes, B for No");
            telemetry.update();

            if (gamepad1.x || gamepad1.b) {
                inLaunchZone = gamepad1.x ? true : false;
                break;
            }
        }

        // TODO: configure camera angle for motif detection (init stage)

        while (opModeInInit()) { // we gonna camp the motif banner to see what it is since they randomize it before starting auto
            telemetry.addData("Selected Team", targetTagID_Team);
            telemetry.addData("Detected Motif (GPP = 21, PGP = 22, PPG = 23)", targetTagID_Motif);
            telemetry.update();
            
            for (AprilTagDetection detection : this.aprilTagProcess.getDetections()) {
                if (detection.id >= 21 && detection.id <= 23) { // possible motif tags are 21-23
                    targetTagID_Motif = detection.id;
                }
            }
        }
        

        // by default when the driver has not properly setup the configuration, the program will automatically assume blue team and GPP motif
        while (opModeIsActive()) {
            // TODO check if in launchzone using color sensors

            this.currentProcedure = Procedures.NAVIGATE_TO_LAUNCHZONE; // TODO: remove this line after testing

            switch (this.currentProcedure) {
                case WAIT: // always goes to NAVIGATE_TO_LAUNCHZONE as this is a grace period for team to load artefacts TODO
                    if (getRuntime() <= this.timeoutUntil) continue;

                    this.currentProcedure = Procedures.NAVIGATE_TO_LAUNCHZONE;
                    break;
                case NAVIGATE_TO_LAUNCHZONE: // TODO
                    // if (this.inLaunchZone) {
                    //     this.currentProcedure = Procedures.LAUNCH_ARTEFACTS;
                    //     break;
                    // }

                    // TODO: navigation to launchzone implementation

                    AprilTagDetection detection = this.aprilTagProcess.getDetectionByID(targetTagID_Team);
                    if (detection != null) {
                        AprilTagPoseFtc pose = detection.ftcPose;

                        if (Math.abs(pose.bearing) > 2) {
                            drivechain.setSideManouverPower(pose.bearing > 0 ? -0.4f : 0.4f);
                        } else drivechain.setPower(0);

                        telemetry.addData("bearing", detection.ftcPose.bearing);
                        telemetry.addData("distance", detection.ftcPose.range);
                        telemetry.update();
                    } else { // TODO: ask the builders if we can make the camera rotate lol
                        
                    }

                    break;
                case REV_LAUNCH_MOTOR: // TODO: TBD because we dont have a launch system yet
                    break;
                case LAUNCH_ARTEFACTS: // TODO: TBD because we dont have a launch system yet
                    break;
                case FIND_ARTEFACTS_TO_INTAKE: // TODO: shall we do a bit of machine learning to do some object detection :D
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
    // NAVIGATE_TO_PIT, // NAVIGATE_TO_PIT is deprecated (we dont do that anymore since we gonna have intake system)
    WAIT,
    NAVIGATE_TO_LAUNCHZONE,
    REV_LAUNCH_MOTOR,
    LAUNCH_ARTEFACTS,
    FIND_ARTEFACTS_TO_INTAKE
}

class TagID {
    public static final int BLUE_GOAL = 20;
    public static final int RED_GOAL = 24;

    public static final int GPP_OBLISK = 21;
    public static final int PGP_OBLISK = 22;
    public static final int PPG_OBLISK = 23;
}

class Configuration {
}
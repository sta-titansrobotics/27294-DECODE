package org.firstinspires.ftc.teamcode.OpModes.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.AprilTag.AprilTagController;
import org.firstinspires.ftc.teamcode.Mechanisms.Drivechain4Motors;
import org.firstinspires.ftc.teamcode.Utilities.RPMTracker;
import org.firstinspires.ftc.teamcode.Utilities.Setup;
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
    public Drivechain4Motors<DcMotor> drivechain;
    public DcMotor launchMotor;
    public DcMotor feederMotor;
    public RPMTracker<DcMotor> launchMotorRPM;
    public ColorSensor color;

    public Procedures currentProcedure = Procedures.NAVIGATE_TO_LAUNCHZONE; // assuming that at the start of auto we already have artefacts loaded, so we can navigate to a launchzone

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

        this.drivechain = new Drivechain4Motors<>(
            hardwareMap.get(DcMotor.class, "frontLeft"),
            hardwareMap.get(DcMotor.class, "frontRight"),
            hardwareMap.get(DcMotor.class, "backLeft"),
            hardwareMap.get(DcMotor.class, "backRight")
        );

        this.feederMotor = hardwareMap.get(DcMotor.class, "feederMotor");
        this.launchMotor = hardwareMap.get(DcMotor.class, "launchMotor");
        Setup.launchMotors(this.launchMotor, this.feederMotor);

        this.launchMotorRPM = new RPMTracker<>(this.launchMotor, getRuntime());

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
        
        boolean hasEnabledFeeder = false;
        double timeWhenFeederEnabled = 0;
        int tagNotFoundCount = 0;

        // by default when the driver has not properly setup the configuration, the program will automatically assume blue team and GPP motif
        while (opModeIsActive()) {
            telemetry.addData("Current Procedure", this.currentProcedure);

            final AprilTagDetection detection;
            final AprilTagPoseFtc pose;
            switch (this.currentProcedure) {
                case NAVIGATE_TO_LAUNCHZONE:
                    detection = this.aprilTagProcess.getDetectionByID(TagID.BLUE_GOAL, TagID.RED_GOAL);
                    if (detection == null) {
                        drivechain.setRotatePower(0.3d); // TODO: rotate bot to find tag
                        continue;
                    }

                    pose = detection.ftcPose;

                    if (Math.abs(pose.x) > 10) {
                        drivechain.setSideManouverPower(pose.x > 0 ? 0.4f : -0.4f);
                    } else {
                        drivechain.setPower(0.5); // move forward as it may have not entered the launch zone
                    }

                    // launchzone entry detection
                    if (
                        color.red() > Configuration.LaunchLine_Trigger_Threshold &&
                        color.green() > Configuration.LaunchLine_Trigger_Threshold &&
                        color.blue() > Configuration.LaunchLine_Trigger_Threshold
                    ) {
                        this.currentProcedure = Procedures.PREPARE_LAUNCH_ALIGNMENT;
                        tagNotFoundCount = 0;
                        if (detection.id == targetTagID_Team) {
                            this.drivechain.setPower(0); // stop if we are facing the correct tag
                        } else {
                            this.drivechain.setRotatePower(detection.id == TagID.BLUE_GOAL ? 0.5f : -0.5f); // rotate if facing different tag
                        }
                        break;
                    }

                    break;
                case PREPARE_LAUNCH_ALIGNMENT:
                    // check again if we are in alienment with apriltag
                    detection = this.aprilTagProcess.getDetectionByID(targetTagID_Team);
                    if (detection != null) {
                        pose = detection.ftcPose;

                        if (Math.abs(pose.yaw) > 10) {
                            drivechain.setRotatePower(pose.yaw > 0 ? 0.4f : -0.4f);
                        } else {
                            drivechain.setPower(0);
                            this.currentProcedure = Procedures.COMMIT_LAUNCH;
                            this.launchMotorRPM.getRPM(getRuntime()); // reset
                        }
                    } else {
                        if (tagNotFoundCount++ > Configuration.Preperation_Count_Timeout) this.currentProcedure = Procedures.HALT; // proceed to launch anyway
                        sleep(1000);
                    }
                    break;
                case COMMIT_LAUNCH:
                    this.launchMotor.setPower(1); // TODO: maybe change this to set power as enabling feeder motor may not be acurate as rpm can go down

                    if (launchMotorRPM.getRPM(getRuntime()) >= 200) { // TODO: targetRPM Calculations (determine this on demand) (matybe substituite later for powerCalculations instead)
                        this.feederMotor.setPower(1);
                        if (!hasEnabledFeeder) {
                            timeWhenFeederEnabled = getRuntime();
                            hasEnabledFeeder = true;
                        }
                    }
                    
                    if (hasEnabledFeeder && getRuntime() >= timeWhenFeederEnabled + 10) {
                        // TODO: after 10s go collect more artefacts (feature to detect when nor more artefacts are present) (for now suspend robot functions)
                        this.feederMotor.setPower(0);
                        this.launchMotor.setPower(0);
                        hasEnabledFeeder = false;

                        this.currentProcedure = Procedures.HALT;
                    }

                    break;
                case FIND_ARTEFACTS_TO_INTAKE: // TODO: shall we do a bit of machine learning to do some object detection :D
                    break;
                case HALT:
                    break;
            }

            telemetry.update();
            sleep(50); // prevents rpm tracker from fluctuating
        }
    }

}

enum Procedures {
    // NAVIGATE_TO_PIT, // NAVIGATE_TO_PIT is deprecated (we dont do that anymore since we gonna have intake system)
    NAVIGATE_TO_LAUNCHZONE,
    PREPARE_LAUNCH_ALIGNMENT,
    COMMIT_LAUNCH,
    FIND_ARTEFACTS_TO_INTAKE,

    HALT
}

class TagID {
    public static final int BLUE_GOAL = 20;
    public static final int RED_GOAL = 24;

    public static final int GPP_OBLISK = 21;
    public static final int PGP_OBLISK = 22;
    public static final int PPG_OBLISK = 23;
}

class Configuration {
    public static final int LaunchLine_Trigger_Threshold = 200;
    public static final int Preperation_Count_Timeout = 1;
}
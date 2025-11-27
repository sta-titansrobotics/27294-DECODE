package org.firstinspires.ftc.teamcode.AprilTag;

import java.util.concurrent.TimeUnit;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.teamcode.interfaces.CameraConfigurationFunction;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import androidx.annotation.Nullable;

public class AprilTagController {
    public final AprilTagProcessor tagProcessor;
    public final VisionPortal videoFeed;

    public final LinearOpMode mainClass;

    private AprilTagController(VisionPortal.Builder builder, LinearOpMode mainClass, AprilTagProcessor apriltagProcessor) {
        this.tagProcessor = apriltagProcessor;
        this.videoFeed = builder.addProcessor(this.tagProcessor).build();
        this.mainClass = mainClass;
    }

    public AprilTagController(WebcamName externalCamera, CameraConfigurationFunction configure, LinearOpMode mainClass, AprilTagProcessor apriltagProcessor) {
        this(configure.apply(new VisionPortal.Builder().setCamera(externalCamera)), mainClass, apriltagProcessor);
    }

    public AprilTagController(BuiltinCameraDirection phoneCamera, CameraConfigurationFunction configure, LinearOpMode mainClass, AprilTagProcessor apriltagProcessor) {
        this(configure.apply(new VisionPortal.Builder().setCamera(phoneCamera)), mainClass, apriltagProcessor);
    }

    // RobotAutoDriveToAprilTagOmni.java
    public void setManualExposure(int exposureMS, int gain) {
        if (this.videoFeed == null) {
            return;
        }

        // Make sure camera is streaming before we try to set the exposure controls
        if (this.videoFeed.getCameraState() != VisionPortal.CameraState.STREAMING) {
            while (!this.mainClass.isStopRequested() && (this.videoFeed.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                this.mainClass.sleep(20);
            }
        }

        // Set camera controls unless we are stopping.
        if (!this.mainClass.isStopRequested())
        {
            ExposureControl exposureControl = this.videoFeed.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                this.mainClass.sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            this.mainClass.sleep(20);
            GainControl gainControl = this.videoFeed.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            this.mainClass.sleep(20);
        }
    }

    public Iterable<AprilTagDetection> getDetections() {
        return this.tagProcessor.getDetections();
    }

    public @Nullable AprilTagDetection getDetectionByID(int targetID) {
        for (AprilTagDetection detection : this.tagProcessor.getDetections()) {
            if (detection.id == targetID) return detection;
        }

        return null;
    }
}

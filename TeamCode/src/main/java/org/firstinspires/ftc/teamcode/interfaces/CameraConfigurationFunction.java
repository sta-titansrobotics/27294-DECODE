package org.firstinspires.ftc.teamcode.interfaces;

import org.firstinspires.ftc.vision.VisionPortal;

@FunctionalInterface
public interface CameraConfigurationFunction {
    VisionPortal.Builder apply(VisionPortal.Builder builder);
}

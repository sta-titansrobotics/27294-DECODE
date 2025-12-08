package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.TouchSensor;

public class ScissorLift {
    public final CRServo scissorServo;
    public final TouchSensor topDetection;
    public final TouchSensor bottomDetection;

    private boolean touchingDetectors = false;

    public ScissorLift(CRServo scissorServo, TouchSensor topDetection, TouchSensor bottomDetection) {
        this.scissorServo = scissorServo;
        this.topDetection = topDetection;
        this.bottomDetection = bottomDetection;
    }

    public boolean extend() {
        if (topDetection.isPressed() && touchingDetectors == false) return false;

        scissorServo.setPower(1.0);
        touchingDetectors = true;
        return true;
    }

    public boolean retract() {
        if (bottomDetection.isPressed() && touchingDetectors == false) return false;

        scissorServo.setPower(-1.0);
        touchingDetectors = true;
        return true;
    }

    public void update() {
        if (!(topDetection.isPressed() || bottomDetection.isPressed())) return;

        if (touchingDetectors) {
            touchingDetectors = false;
        } else {
            scissorServo.setPower(0.0);
        }
    }
}

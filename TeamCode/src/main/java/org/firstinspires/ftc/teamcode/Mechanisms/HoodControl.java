package org.firstinspires.ftc.teamcode.Mechanisms;

import com.qualcomm.robotcore.hardware.Servo;

public class HoodControl {
    public final Servo hoodServo;
    public final double maxPosition;

    public HoodControl(Servo hoodServo, double maxPosition) {
        this.hoodServo = hoodServo;
        this.maxPosition = maxPosition;
    }

    public void update(boolean shouldMoveUp, boolean shouldMoveDown) {
        if (!(shouldMoveUp ^ shouldMoveDown)) {
            // both pressed in conflict do not move
            // if none is pressed safe to assume that it is the position that the operator wants, therefore set the position to current to override previous commands

            // xor gate (^) satisfies both conditions
            this.hoodServo.setPosition(this.hoodServo.getPosition());
        } else if (shouldMoveUp) {  // move to max position
            this.hoodServo.setPosition(maxPosition);
        } else if (shouldMoveDown) { // return down
            this.hoodServo.setPosition(0);
        }
    }

    public void shutdown() throws InterruptedException {
        hoodServo.setPosition(0);

        while (hoodServo.getPosition() != 0) {
            wait(100);
        }
    }
}

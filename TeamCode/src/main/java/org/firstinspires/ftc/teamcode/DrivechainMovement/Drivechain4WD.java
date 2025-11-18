package org.firstinspires.ftc.teamcode.DrivechainMovement;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Drivechain4WD<T extends DcMotorSimple> {
    public final T frontLeft;
    public final T frontRight;
    public final T backLeft;
    public final T backRight;

    public Drivechain4WD(T fl, T fr, T bl, T br) {
        this.frontLeft = fl;
        this.frontRight = fr;
        this.backLeft = bl;
        this.backRight = br;

        fr.setDirection(Direction.REVERSE);
        br.setDirection(Direction.REVERSE);
        fl.setDirection(Direction.FORWARD);
        bl.setDirection(Direction.FORWARD);
    }

    public void setLeftPower(double pow) {
        this.frontLeft.setPower(pow);
        this.backLeft.setPower(pow);
    }

    public void setRightPower(double pow) {
        this.frontRight.setPower(pow);
        this.backRight.setPower(pow);
    }

    /**
     * Set the power to all the motors at once
     */
    public void setPower(double pow) {
        this.setLeftPower(pow);
        this.setRightPower(pow);
    }

    /**
     * Set the power to the left side and right side individually
     * This allows for the robot to rotate (stationary or on the go)
     */
    public void setPower(double leftSide, double rightSide) {
        this.setLeftPower(leftSide);
        this.setRightPower(rightSide);
    }

    /**
     * moves the bot horizontally without rotating
     * ie. if it is looking forward, it can move left to right without rotating its angle
     * @param power - negative power moves it to the left and positive to the right
     */
    public void setSideManouverPower(float power) {
        this.frontLeft.setPower(power);     // needs to move forward to go right
        this.frontRight.setPower(-power);   // needs to move back to go right
        this.backLeft.setPower(-power);     // needs to move back to go right
        this.backRight.setPower(power);     // needs to move forward to go right
    }
}

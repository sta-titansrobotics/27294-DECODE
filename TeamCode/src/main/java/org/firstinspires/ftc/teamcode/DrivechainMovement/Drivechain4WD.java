package org.firstinspires.ftc.teamcode.DrivechainMovement;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Drivechain4WD {
    public final DcMotorSimple frontLeft;
    public final DcMotorSimple frontRight;
    public final DcMotorSimple backLeft;
    public final DcMotorSimple backRight;

    public Drivechain4WD(DcMotorSimple fl, DcMotorSimple fr, DcMotorSimple bl, DcMotorSimple br) {
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
}

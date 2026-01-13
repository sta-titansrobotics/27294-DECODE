package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class Setup {
    public static void launchMotors(DcMotor launchMotor, DcMotor feederMotor) {
        launchMotor.setDirection(Direction.REVERSE);
        feederMotor.setDirection(Direction.FORWARD);
    }
}

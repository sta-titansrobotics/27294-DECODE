package org.firstinspires.ftc.teamcode.LaunchMechanism;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class ManualLaunchControl {
    public final DcMotorSimple feeder;
    public final DcMotorSimple launcher;

    public ManualLaunchControl(DcMotorSimple feeder, DcMotorSimple launcher) {
        this.feeder = feeder;
        this.launcher = launcher;

        feeder.setDirection(Direction.REVERSE);
        launcher.setDirection(Direction.REVERSE);
    }

    public void setEnableLauncher(boolean should) {
        this.launcher.setPower(should ? 1 : 0);
    }

    public void setFeederPower(double pow) {
        this.feeder.setPower(pow);
    }
}

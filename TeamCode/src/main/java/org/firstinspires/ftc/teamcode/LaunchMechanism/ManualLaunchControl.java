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
        launcher.setDirection(Direction.FORWARD);
    }

    public void enableLauncher() {
        this.launcher.setPower(1);
    }

    public void disableLauncher() {
        this.launcher.setPower(0);
    }

    public void setFeederPower(double pow) {
        this.feeder.setPower(pow);
    }
}

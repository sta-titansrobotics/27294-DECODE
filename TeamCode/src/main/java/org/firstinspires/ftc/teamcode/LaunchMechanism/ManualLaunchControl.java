package org.firstinspires.ftc.teamcode.LaunchMechanism;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

public class ManualLaunchControl<T extends DcMotorSimple> {
    public final T feeder;
    public final T launcher;

    public ManualLaunchControl(T feeder, T launcher) {
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

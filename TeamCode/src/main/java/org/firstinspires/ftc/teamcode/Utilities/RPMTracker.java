package org.firstinspires.ftc.teamcode.Utilities;

import com.qualcomm.robotcore.hardware.DcMotor;

public class RPMTracker<T extends DcMotor> {
    public final T motor;
    public final double ticksPerRev;

    public double lastTime;
    public double lastPosition;

    public RPMTracker(T motor, double currentTime) {
        this.motor = motor;

        this.lastPosition = motor.getCurrentPosition();
        this.lastTime = currentTime;

        this.ticksPerRev = motor.getMotorType().getTicksPerRev();
    }

    /**
     * Calculates the RPM of the motor since the last time this method was called
     * 
     * @param cTime - call getRuntime() and pass it into this method
     * @return
     */
    public double getRPM(double cTime) {
        int cPos = this.motor.getCurrentPosition();

        double timeSinceLast = cTime - this.lastTime;
        double ticksPerMinute = ((double)(cPos - this.lastPosition)) * (60d / timeSinceLast);

        return ticksPerMinute / this.ticksPerRev;
    }
}

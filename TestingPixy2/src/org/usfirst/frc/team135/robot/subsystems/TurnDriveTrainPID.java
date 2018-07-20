package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.PIDSubsystem;
import org.usfirst.frc.team135.robot.subsystems.PixyCam;
import org.usfirst.frc.team135.robot.Robot;

/**
 *
 */

public class TurnDriveTrainPID extends PIDSubsystem {
	private static double P_VALUE = .1;
	private static double I_VALUE = .1;
	private static double D_VALUE = .1;
	
	//PixyCam pixy2Cam = Robot.pixyCam;
	
    // Initialize your subsystem here
    public TurnDriveTrainPID() {
    	super("TurnDriveTrainPID", P_VALUE, I_VALUE, D_VALUE);
    	
        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        // enable() - Enables the PID controller.
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;
        return 0.0;
    }

    protected void usePIDOutput(double output) {
        // Use output to drive your system, like a motor
        // e.g. yourMotor.set(output);
    }
}

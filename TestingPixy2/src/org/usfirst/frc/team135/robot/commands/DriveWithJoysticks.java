package org.usfirst.frc.team135.robot.commands;

import org.usfirst.frc.team135.robot.OI;
import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveWithJoysticks extends Command {

	private double rightJoystickValue;
	private double leftJoystickValue;
	
    public DriveWithJoysticks() 
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drivetrain);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	leftJoystickValue = Robot.oi.GetJoystickYValue(OI.LEFT_JOYSTICK);
    	rightJoystickValue = Robot.oi.GetJoystickYValue(OI.RIGHT_JOYSTICK);
    	
    	Robot.drivetrain.TankDrive(leftJoystickValue,  rightJoystickValue);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() 
    {
    	Robot.drivetrain.TankDrive(0.0,  0.0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	this.end();
    }
}

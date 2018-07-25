package org.usfirst.frc.team135.robot.commands;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.Limelight;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class GetLimelightData extends Command {

	double[] limelightData = new double[Limelight.NUMBER_OF_LIMELIGHT_CHARACTERISTICS];
	
    public GetLimelightData()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.limelight);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	Robot.limelight.TurnLEDOnOff(Limelight.LED_OFF);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	limelightData = Robot.limelight.GetLimelightData();
    	
    	SmartDashboard.putNumber("Valid Target", limelightData[Limelight.VALID_TARGET]);
    	SmartDashboard.putNumber("Horizontal Offset", limelightData[Limelight.HORIZONTAL_OFFSET]);
    	SmartDashboard.putNumber("Vertical Offset", limelightData[Limelight.VERTICAL_OFFSET]);
    	SmartDashboard.putNumber("Target Area", limelightData[Limelight.TARGET_AREA]);
    	SmartDashboard.putNumber("Target Skew", limelightData[Limelight.TARGET_SKEW]);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	
    }
}

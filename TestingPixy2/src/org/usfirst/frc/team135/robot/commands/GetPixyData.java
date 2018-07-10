package org.usfirst.frc.team135.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team135.robot.Robot;

/**
 *
 */
public class GetPixyData extends Command {
	
	private static Timer timer = new Timer();
	
	private final int INITIAL_NUM_BYTES_TO_READ = 4;
	private int numberOfBytesToRead = 0;
	
	private byte[] dataRead = new byte[Robot.pixyCam.MAX_NUM_BYTES_TO_READ];

    public GetPixyData() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.pixyCam);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	
    	Robot.pixyCam.InitializeArrays();
    	
    	for (int i = 0; i < Robot.pixyCam.MAX_NUM_BYTES_TO_READ; i++)
    	{
    		dataRead[i] = 0;
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.pixyCam.WriteToPixy2();
    	timer.delay(.001);
    	numberOfBytesToRead = (int)Robot.pixyCam.GetNumberOfBytesToRead();
    	
    	System.out.println(numberOfBytesToRead);
    	
    	dataRead = Robot.pixyCam.ReadFromPixy2(numberOfBytesToRead);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}

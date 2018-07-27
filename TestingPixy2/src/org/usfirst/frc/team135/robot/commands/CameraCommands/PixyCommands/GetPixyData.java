package org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.PixyCam;
import org.usfirst.frc.team135.robot.subsystems.EstablishI2CPixyConnection;

/**
 *
 */
public class GetPixyData extends Command {
	
	private final int INITIALIZING_PIXY = 0;
	private final int RECEIVING_GENERAL_DATA = 1;
	private int phaseNumber;
	
	private int numberOfObjectsDetected;
	
	private int[][] generalDataBytesRead = new int[PixyCam.MAX_OBJECTS_TO_STORE][PixyCam.NUMBER_OF_GENERAL_DATA_BYTES];

    public GetPixyData()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.pixyCam);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	phaseNumber = INITIALIZING_PIXY;
    	numberOfObjectsDetected = 0;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	switch (phaseNumber)
    	{
	    	case INITIALIZING_PIXY:
	    		phaseNumber = Robot.pixyCam.isReadyToReadDataFromPixy() ? 1 : 0;
	    	case RECEIVING_GENERAL_DATA:
	    		numberOfObjectsDetected = Robot.pixyCam.GetNumberOfObjectsDetected(EstablishI2CPixyConnection.SIGNATURE_1);
	    		
	    		if (numberOfObjectsDetected > 0)
	    		{
	    			generalDataBytesRead = Robot.pixyCam.GetGeneralPixyData(numberOfObjectsDetected);
	    		}
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() 
    {
    	phaseNumber = INITIALIZING_PIXY;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	this.end();
    }
}

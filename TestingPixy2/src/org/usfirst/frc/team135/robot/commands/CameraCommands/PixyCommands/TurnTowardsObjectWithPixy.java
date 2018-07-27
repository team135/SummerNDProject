package org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team135.robot.subsystems.EstablishI2CPixyConnection;
import org.usfirst.frc.team135.robot.subsystems.PixyCam;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.PIDController;

/**
 *
 */
public class TurnTowardsObjectWithPixy extends Command {
	
	private final int INITIALIZING_PIXY = 0;
	private final int RECEIVING_GENERAL_DATA = 1;
	private int phaseNumber;
	
	private int numberOfObjectsDetected;
	
	private int[][] generalDataBytesRead = new int[PixyCam.MAX_OBJECTS_TO_STORE][PixyCam.NUMBER_OF_GENERAL_DATA_BYTES];
	
	boolean doneTurning;
	
	private final double TURNING_MOTOR_POWER = .35;
	

    public TurnTowardsObjectWithPixy()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	requires(Robot.pixyCam);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	doneTurning = false;
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
	    			Robot.driveTrain.TurnDriveTrain(.2, DriveTrain.DirectionToTurn.Left);
	    			
	    			if (Math.abs(generalDataBytesRead[0][PixyCam.X_COORDINATE_INDEX]) <= 120)
	    			{
	    				doneTurning = true;
	    			}
	    		}
	    		else if (numberOfObjectsDetected == 0)
	    		{
	    			Robot.driveTrain.TurnDriveTrain(TURNING_MOTOR_POWER, DriveTrain.DirectionToTurn.Left);
	    		}
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return doneTurning;
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	Robot.driveTrain.TankDrive(0.0, 0.0);
    	doneTurning = false;
    	phaseNumber = INITIALIZING_PIXY;
    	numberOfObjectsDetected = 0;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	this.end();
    }
}

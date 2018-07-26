package org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.EstablishI2CPixyConnection;
import org.usfirst.frc.team135.robot.subsystems.PixyCam;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class DriveStraightWithPixy extends Command {
	
	private final int INITIALIZING_PIXY = 0;
	private final int RECEIVING_GENERAL_DATA = 1;
	private int phaseNumber;
	
	private int numberOfObjectsDetected;
	
	private int[][] generalDataBytesRead = new int[PixyCam.MAX_OBJECTS_TO_STORE][PixyCam.NUMBER_OF_GENERAL_DATA_BYTES];
	
	private final double DRIVE_TRAIN_MOTOR_POWER = .35;
	
	private boolean stopDriving;
	private final double PIXY_DRIVE_STRAIGHT_P_VALUE = .015;
	private final int OBJECT_WIDTH_THRESHOLD = 150;

    public DriveStraightWithPixy() 
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.driveTrain);
    	requires(Robot.pixyCam);
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	Robot.driveTrain.InitializeCurvatureDrive();
    	stopDriving = false;
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
	    		break;
	    	case RECEIVING_GENERAL_DATA:
	    		numberOfObjectsDetected = Robot.pixyCam.GetNumberOfObjectsDetected(EstablishI2CPixyConnection.SIGNATURE_1);
	    		
	    		if (numberOfObjectsDetected > 0)
	    		{
	    			generalDataBytesRead = Robot.pixyCam.GetGeneralPixyData(numberOfObjectsDetected);
	    			Robot.driveTrain.DriveStraightTowardsBlockWithPixy(DRIVE_TRAIN_MOTOR_POWER, (double)generalDataBytesRead[0][PixyCam.X_COORDINATE_INDEX], PIXY_DRIVE_STRAIGHT_P_VALUE);
	    			stopDriving = generalDataBytesRead[0][PixyCam.WIDTH_INDEX] >= OBJECT_WIDTH_THRESHOLD ? true : false;
	    		}
	    		else 
	    		{
	    			Robot.driveTrain.TankDrive(0.0, 0.0);
	    		}
    	}
    	
		SmartDashboard.putNumber("Number of Objects Detected", numberOfObjectsDetected);
		SmartDashboard.putNumber("Object 1 Signature", generalDataBytesRead[0][PixyCam.SIGNATURE_INDEX]);
		SmartDashboard.putNumber("Object 1 X-Coordinate", generalDataBytesRead[0][PixyCam.X_COORDINATE_INDEX]);
		SmartDashboard.putNumber("Object 1 Y-Coordinate", generalDataBytesRead[0][PixyCam.Y_COORDINATE_INDEX]);
		SmartDashboard.putNumber("Object 1 Width", generalDataBytesRead[0][PixyCam.WIDTH_INDEX]);
		SmartDashboard.putNumber("Object 1 Height", generalDataBytesRead[0][PixyCam.HEIGHT_INDEX]);
		SmartDashboard.putNumber("Object 1 Index", generalDataBytesRead[0][PixyCam.INDEX_INDEX]);
		SmartDashboard.putNumber("Object 1 Age", generalDataBytesRead[0][PixyCam.AGE_INDEX]);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() 
    {
        return stopDriving;
    }

    // Called once after isFinished returns true
    protected void end() 
    {
    	Robot.driveTrain.TankDrive(0.0,  0.0);
    	stopDriving = false;
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

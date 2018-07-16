package org.usfirst.frc.team135.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.PixyCam;

/**
 *
 */
public class GetPixyData extends Command {
	
	private final int INITIALIZE_PIXY = 0;
	private final int GET_RESOLUTION = 1;
	private final int REQUEST_INFORMATION_FROM_PIXY = 2;	
	private int phaseNumber = INITIALIZE_PIXY;
	
	private int[] pixyResolution = new int[2];
	
	private int numberOfObjectsDetected = 0;
	private int[][] importantObjectInformation = new int [PixyCam.MAX_OBJECTS_TO_STORE][PixyCam.NUMBER_OF_IMPORTANT_CHARACTERISTICS];

    public GetPixyData()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.pixyCam);
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() 
    {
    	switch (phaseNumber)
    	{
	    	case INITIALIZE_PIXY:
	    		if (Robot.pixyCam.InitializePixy())
	    		{
	    			System.out.println("Pixy Initialized");
	    			phaseNumber++;
	    		}
	    		else
	    		{
	    			break;
	    		}
	    		
	    		Robot.pixyCam.ClearBuffer();
	    		
	    	case GET_RESOLUTION:
	    		//  Timer.delay(.0002);
	    		Robot.pixyCam.RequestDataFromPixy(PixyCam.RequestedDataType.Resolution);
	    		Timer.delay(.0002);
	    		
	    		if (Robot.pixyCam.IsCorrectResponseAddress(PixyCam.RequestedDataType.Resolution))
	    		{
	    			pixyResolution = Robot.pixyCam.GetResolution();
	    			System.out.println("RoboRIO received Pixy Resolution");
	    			System.out.print("Pixy Resolution Width: ");
	    			System.out.println(pixyResolution[0]);
	    			System.out.print("Pixy Resolution Height: ");
	    			System.out.println(pixyResolution[1]);
	    			phaseNumber++;
	    		}
	    		else
	    		{
	    			Robot.pixyCam.ClearBuffer();
	    			break;
	    		}
	    		
	    	case REQUEST_INFORMATION_FROM_PIXY:
	    		//  Timer.delay(.0002);
	    		//  System.out.println("Requesting Object from Pixy");
	    		Robot.pixyCam.RequestDataFromPixy(PixyCam.RequestedDataType.General, PixyCam.SIGNATURE_1);
	    		Timer.delay(.0002);  //  100 microseconds is MAX Latency Time
	    		
	    		if (Robot.pixyCam.IsCorrectResponseAddress(PixyCam.RequestedDataType.General))
	    		{
	    			//  System.out.println("Receiving Data from Pixy");
	    			numberOfObjectsDetected = Robot.pixyCam.GetNumberOfObjectsDetectedAndOrganizeGeneralData();
	    			
	    			for (int i = 0; i < numberOfObjectsDetected; i++)
	    			{
	    				importantObjectInformation[i] = Robot.pixyCam.GetImportantObjectInformation(i);
	    			}
	    		
	    			SmartDashboard.putNumber("Number of Objects Detected", numberOfObjectsDetected);
	    			SmartDashboard.putNumber("Object 1 Signature", importantObjectInformation[0][PixyCam.OBJECT_SIGNATURE]);
	    			SmartDashboard.putNumber("Object 1 X-Coordinate", importantObjectInformation[0][PixyCam.X_ONLY]);
	    			SmartDashboard.putNumber("Object 1 Y-Coordinate", importantObjectInformation[0][PixyCam.Y_ONLY]);
	    			SmartDashboard.putNumber("Object 1 Width", importantObjectInformation[0][PixyCam.WIDTH_ONLY]);
	    			SmartDashboard.putNumber("Object 1 Height", importantObjectInformation[0][PixyCam.HEIGHT_ONLY]);
	    		}
	    		else
	    		{
	    			Robot.pixyCam.ClearBuffer();
	    		}
	    		break;
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
    	phaseNumber = INITIALIZE_PIXY;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() 
    {
    	this.end();
    }
}

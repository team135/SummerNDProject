package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.networktables.NetworkTableInstance;

import org.usfirst.frc.team135.robot.commands.CameraCommands.LimelightCommands.GetLimelightData;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

/**
 *
 */
public class Limelight extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	
	//  Instance of the Subsystem that is used in Robot.java
	private static Limelight instance;
	
	//  Default Instance of NetworkTable that is created when the program is started
	NetworkTableInstance networkTableInstance = NetworkTableInstance.getDefault();
	
//  Reading and Writing from the "limelight" Table in NetworkTables
	NetworkTable limelightTable = networkTableInstance.getTable("limelight");
	
	//  NetworkTableEntries for Reading Data from Limelight
	NetworkTableEntry validTargetEntry = limelightTable.getEntry("tv");
	NetworkTableEntry horizontalOffsetEntry = limelightTable.getEntry("tx");
	NetworkTableEntry verticalOffsetEntry = limelightTable.getEntry("ty");
	NetworkTableEntry targetAreaEntry = limelightTable.getEntry("ta");
	NetworkTableEntry targetSkewEntry = limelightTable.getEntry("tl");
	
	//  NetworkTableEntries for Writing Data to Limelight
	NetworkTableEntry ledModeEntry = limelightTable.getEntry("ledMode");
	NetworkTableEntry cameraModeEntry = limelightTable.getEntry("camMode");
	NetworkTableEntry limelightPipelineEntry = limelightTable.getEntry("pipeline");
	
	public static final int NUMBER_OF_LIMELIGHT_CHARACTERISTICS = 5;
	
	//  Elements for limelightData Array
	public static final int VALID_TARGET = 0;
	public static final int HORIZONTAL_OFFSET = 1;
	public static final int VERTICAL_OFFSET = 2;
	public static final int TARGET_AREA = 3;
	public static final int TARGET_SKEW = 4;
	
	//  Stores the 5 Main Characteristics of the Target that the Limelight returns
	double[] limelightData = new double[NUMBER_OF_LIMELIGHT_CHARACTERISTICS];
	
	//  LED Modes
	public static int LED_ON = 0;
	public static int LED_OFF = 1;
	public static int LED_BLINKING = 2;
	
	//  Camera Modes
	public static int VISION_PROCESSOR = 0;
	public static int DRIVER_CAMERA = 1; 
	
	//  Pipeline Options
	public static int YELLOW_BLOCK_PIPELINE = 0;

	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
    	setDefaultCommand(new GetLimelightData());
    }
    
    //  Method used in Robot.java to Initialize the Subsystem to be used in the Commands
    public static Limelight InitializeSubystem()
    {
    	if (instance == null)
    	{
    		instance = new Limelight();
    	}
    	return instance;
    }
    
    //  Gets the Target Data and returns it in a Double Array
    public double[] GetLimelightData()
    {
    	limelightData[VALID_TARGET] = validTargetEntry.getDouble(0.0);
    	limelightData[HORIZONTAL_OFFSET] = horizontalOffsetEntry.getDouble(0.0);
    	limelightData[VERTICAL_OFFSET] = verticalOffsetEntry.getDouble(0.0);
    	limelightData[TARGET_AREA] = targetAreaEntry.getDouble(0.0);
    	limelightData[TARGET_SKEW] = targetSkewEntry.getDouble(0.0);
    	
    	return limelightData;
    }
    
    //  Returns True if a Valid Target exists with the currently set Vision Pipeline
    //  Returns False if there are no Valid Targets
    public boolean isTargetsExist()
    {
    	double numberOfValidTargets;
    	boolean targetsExist;
    	
    	numberOfValidTargets = validTargetEntry.getDouble(0.0);
    	
    	if (numberOfValidTargets > 0.0)
    	{
    		targetsExist = true;
    	}
    	else 
    	{
    		targetsExist = false;
    	}
    	
    	return targetsExist;
    }
    
    //  Sets the LED on the Limelight to be On, Off, or Blinking
    public void SetLEDMode(int onOrOff)
    {
    	ledModeEntry.setNumber(onOrOff);
    	return;
    }
    
    //  Sets the Limelight to either be an Vision Processor or just a Driver Camera (No Vision Processing)
    public void SetCameraMode(int cameraMode)
    {
    	cameraModeEntry.setNumber(cameraMode);
    	return;
    }
    
    //  Sets the Vision Pipeline to retrieve data from
    public void SetCameraPipeline(int pipeline)
    {
    	limelightPipelineEntry.setNumber(pipeline);
    	return;
    }
}


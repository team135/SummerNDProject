package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import org.usfirst.frc.team135.robot.commands.GetLimelightData;

/**
 *
 */
public class Limelight extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	private static Limelight instance;
	
	NetworkTableInstance networkTableInstance = NetworkTableInstance.getDefault();
	NetworkTable limelightTable = networkTableInstance.getTable("limelight");
	
	NetworkTableEntry validTargetEntry = limelightTable.getEntry("tv");
	NetworkTableEntry horizontalOffsetEntry = limelightTable.getEntry("tx");
	NetworkTableEntry verticalOffsetEntry = limelightTable.getEntry("ty");
	NetworkTableEntry targetAreaEntry = limelightTable.getEntry("ta");
	NetworkTableEntry targetSkewEntry = limelightTable.getEntry("tl");
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
	
	double[] limelightData = new double[NUMBER_OF_LIMELIGHT_CHARACTERISTICS];
	
	public static boolean LED_ON = true;
	public static boolean LED_OFF = false;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
    	setDefaultCommand(new GetLimelightData());
    }
    
    public static Limelight InitializeSubystem()
    {
    	if (instance == null)
    	{
    		instance = new Limelight();
    	}
    	return instance;
    }
    
    public double[] GetLimelightData()
    {
    	limelightData[VALID_TARGET] = validTargetEntry.getDouble(0.0);
    	limelightData[HORIZONTAL_OFFSET] = horizontalOffsetEntry.getDouble(0.0);
    	limelightData[VERTICAL_OFFSET] = verticalOffsetEntry.getDouble(0.0);
    	limelightData[TARGET_AREA] = targetAreaEntry.getDouble(0.0);
    	limelightData[TARGET_SKEW] = targetSkewEntry.getDouble(0.0);
    	
    	return limelightData;
    }
    
    public boolean isTargetsExist()
    {
    	double value;
    	boolean returnValue;
    	
    	value = (validTargetEntry.getDouble(0.0) + horizontalOffsetEntry.getDouble(0.0) 
    			+ verticalOffsetEntry.getDouble(0.0) + targetAreaEntry.getDouble(0.0) 
    			+ targetSkewEntry.getDouble(0.0));
    	
    	if (value != 0.0)
    	{
    		returnValue = true;
    	}
    	else
    	{
    		returnValue = false;
    	}
    	
    	return returnValue;
    }
    
    public void TurnLEDOnOff(boolean onOrOff)
    {
    	if (onOrOff = LED_ON)
    	{
    		ledModeEntry.setDouble(0.0);
    	}
    	else if (onOrOff == LED_OFF)
    	{
    		ledModeEntry.setDouble(1.0);
    	}
    }
}


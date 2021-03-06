package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

import org.usfirst.frc.team135.robot.RobotMap;
import org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands.GetPixyData;
import org.usfirst.frc.team135.robot.subsystems.EstablishI2CPixyConnection;

public class PixyCam extends Subsystem {
	
//  I2C Device Address of the Pixy2
private final int PIXY_CAM_DEVICE_ADDRESS = 0x54;

//  Instance of the I2C WPI Lib Class
//  Using I2C Port On Board, NOT through MXP
private I2C pixy2 = new I2C(I2C.Port.kOnboard, PIXY_CAM_DEVICE_ADDRESS);

private EstablishI2CPixyConnection initializingPixy2 = new EstablishI2CPixyConnection(pixy2);

//  InitializeSubsystem()
private static PixyCam instance;

//  ReadPixyResolution()
private final int RESOLUTION_WIDTH = 0;
private final int RESOLUTION_HEIGHT = 1;
int[] resolution = new int[2];
private final int NUMBER_OF_RESOLUTION_BYTES_TO_READ = 4;

//  GetNumberOfBytesToRead()
int numberOfBytesToRead = 0;

//  isReadyToReadDataFromPixy()
//  Constants
private final int INITIALIZING_PIXY = 0;
private final int GETTING_PIXY_RESOLUTION = 1;

int phaseNumber = INITIALIZING_PIXY;
boolean readyToReadData = false;

//  GetGeneralPixyData()
//  Constants
public static final int MAX_OBJECTS_TO_STORE = 10;
public static final int NUMBER_OF_GENERAL_DATA_BYTES = 14;
public final int MAX_NUM_BYTES_TO_READ = MAX_OBJECTS_TO_STORE * NUMBER_OF_GENERAL_DATA_BYTES;
private final int NUMBER_OF_CHARACTERISTICS_TO_STORE = 7;

//  Arrays
byte[] dataBytesRead = new byte[MAX_NUM_BYTES_TO_READ];
int[][] objectGeneralData = new int[MAX_OBJECTS_TO_STORE][NUMBER_OF_CHARACTERISTICS_TO_STORE];  //  Maximum of 10 Objects, each with 7 Characteristics

//  Array Indices of objectGeneralData
public static final int SIGNATURE_INDEX = 0;
public static final int X_COORDINATE_INDEX = 1;
public static final int Y_COORDINATE_INDEX = 2;
public static final int WIDTH_INDEX = 3;
public static final int HEIGHT_INDEX = 4;
public static final int INDEX_INDEX = 5;
public static final int AGE_INDEX = 6;


    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
    	if (RobotMap.cameraToUse == RobotMap.DesignatedCamera.PixyCamera)
    	{
    		setDefaultCommand(new GetPixyData());
    	}
    }
    
    //  Used to create and instance of the PixyCam Subsystem in Robot.java
    public static PixyCam InitializeSubsystem()
    {
    	if (instance == null)
    	{
    		instance = new PixyCam();
    	}
    	return instance;
    }
    
    private void ReadPixyResolution()
    {	
    	//  Local Declarations
    	byte[] bytesRead = new byte[NUMBER_OF_RESOLUTION_BYTES_TO_READ];
    	
    	//  Executable Statements
    	pixy2.readOnly(bytesRead, NUMBER_OF_RESOLUTION_BYTES_TO_READ);  //  Reading 4 Bytes
    	
    	resolution[RESOLUTION_WIDTH] = (initializingPixy2.ModifyDataByte(bytesRead[1]) << 8) + initializingPixy2.ModifyDataByte(bytesRead[0]);
    	resolution[RESOLUTION_HEIGHT] = (initializingPixy2.ModifyDataByte(bytesRead[3]) << 8) + initializingPixy2.ModifyDataByte(bytesRead[2]);
    	
    	//  SmartDashboard.putNumber("Pixy Resolution Width", resolution[RESOLUTION_WIDTH]);
    	//  SmartDashboard.putNumber("Pixy Resolutiuon Height", resolution[RESOLUTION_HEIGHT]);
    	
    	return;
    }
    
    private void GetNumberOfBytesToRead()
    {
    	//  Local Declarations
    	byte[] bytesRead = new byte[2];
    	
    	//  Executable Statements
    	pixy2.readOnly(bytesRead, 1);
    	numberOfBytesToRead = initializingPixy2.ModifyDataByte(bytesRead[0]);
    	
    	pixy2.readOnly(bytesRead, 2);  //  Reading the Next Two Bytes because we ignore them
    	
    	return;
    }
    
    //  Used to Modify Coordinates for a 4-Quadrant Plane, not just a single Quadrant
    private int ModifyData(int data, int index)
    {
    	if (index == X_COORDINATE_INDEX)
    	{
    		data = data - (int)(.5 * resolution[RESOLUTION_WIDTH]);
    	}
    	else if (index == Y_COORDINATE_INDEX)
    	{
    		data = (int)(.5 * resolution[RESOLUTION_HEIGHT]) - data;
    	}
    	
    	return data;
    }
    
	public boolean isReadyToReadDataFromPixy()
	{
		switch (phaseNumber)
		{
			case INITIALIZING_PIXY:
				readyToReadData = false;
				
				if (initializingPixy2.InitializePixy())
				{
					phaseNumber++;
				}
				else
				{
					break;
				}
			case GETTING_PIXY_RESOLUTION:
	    		initializingPixy2.RequestDataFromPixy(EstablishI2CPixyConnection.RequestedDataType.Resolution);
	    		Timer.delay(.002);
	    		
	    		if (initializingPixy2.IsCorrectResponseAddress(EstablishI2CPixyConnection.RequestedDataType.Resolution))
	    		{
	    			this.GetNumberOfBytesToRead();
	    			this.ReadPixyResolution();
	    			readyToReadData = true;
	    		}
		}
		
		return readyToReadData;
	}
	
    public int GetNumberOfObjectsDetected(int signatureOfObject)
    {
    	//  Local Declarations
    	int numberOfObjectsDetected = 0;
    	
    	//  Executable Statements
    	initializingPixy2.RequestDataFromPixy(EstablishI2CPixyConnection.RequestedDataType.General, signatureOfObject);
    	Timer.delay(.002);
    	
    	if (initializingPixy2.IsCorrectResponseAddress(EstablishI2CPixyConnection.RequestedDataType.General))
    	{
    		this.GetNumberOfBytesToRead();
    		numberOfObjectsDetected = (numberOfBytesToRead / NUMBER_OF_GENERAL_DATA_BYTES);
    	}
    	else 
    	{
    		System.out.println("Error Receiving General Data From Pixy");
    		numberOfObjectsDetected = -1;
    	}
    	
    	SmartDashboard.putNumber("Number of Objects Detected by Pixy", numberOfObjectsDetected);
    	
    	return numberOfObjectsDetected;
    }
    
    public int[][] GetGeneralPixyData(int numberOfObjectsDetected)
    {	
    	//  Local Declarations
    	int leastSignificantByte = 0;
    	int mostSignificantByte = 0;
    	int initialDataValue = 0;
    	
    	//  Executable Statements
    	pixy2.readOnly(dataBytesRead, numberOfBytesToRead);
    	
    	for (int i = 0; i < numberOfObjectsDetected; i++)
    	{
    		for (int j = 0; j < 7; j++)
    		{
    			switch (j)
    			{
	    			case SIGNATURE_INDEX:  //  Signature Number
	    				objectGeneralData[i][j] = initializingPixy2.ModifyDataByte(dataBytesRead[(7 * i)]);
	    				break;
	    			case X_COORDINATE_INDEX:  //  X Coordinate
	    			case Y_COORDINATE_INDEX:  //  Y Coordinate
	    			case WIDTH_INDEX:  //  Width
	    			case HEIGHT_INDEX:  //  Height
	    				leastSignificantByte = initializingPixy2.ModifyDataByte(dataBytesRead[(7 * i) + (2 * j)]);
	    				mostSignificantByte = initializingPixy2.ModifyDataByte(dataBytesRead[(7 * i) + (2 * j) + 1]);
	    				initialDataValue = (mostSignificantByte << 8) + leastSignificantByte;
	    				objectGeneralData[i][j] = ModifyData(initialDataValue, j);
	    				break;
	    			case INDEX_INDEX:  //  Index
	    				objectGeneralData[i][j] = initializingPixy2.ModifyDataByte(dataBytesRead[(7 * i) + 12]);
	    				break;
	    			case AGE_INDEX:  //  Age
	    				objectGeneralData[i][j] = initializingPixy2.ModifyDataByte(dataBytesRead[(7 * i) + 13]);
	    				break;
    			}
    		}
    	}
    	
		//  SmartDashboard.putNumber("Pixy Object 0 Signature", objectGeneralData[0][SIGNATURE_INDEX]);
		SmartDashboard.putNumber("Pixy Object 0 X-Coordinate", objectGeneralData[0][X_COORDINATE_INDEX]);
		SmartDashboard.putNumber("Pixy Object 0 Y-Coordinate", objectGeneralData[0][Y_COORDINATE_INDEX]);
		SmartDashboard.putNumber("Pixy Object 0 Width", objectGeneralData[0][WIDTH_INDEX]);
		//  SmartDashboard.putNumber("Pixy Object 0 Height", objectGeneralData[0][HEIGHT_INDEX]);
		//  SmartDashboard.putNumber("Pixy Object 0 Index", objectGeneralData[0][INDEX_INDEX]);
		//  SmartDashboard.putNumber("Pixy Object 0 Age", objectGeneralData[0][AGE_INDEX]);
    	
    	return objectGeneralData;
    }
    
}


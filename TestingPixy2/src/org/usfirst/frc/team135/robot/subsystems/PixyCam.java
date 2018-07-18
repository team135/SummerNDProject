package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

//  import org.usfirst.frc.team135.robot.commands.GetPixyData;

public class PixyCam extends Subsystem {
	
//  I2C Device Address of the Pixy2
private final int PIXY_CAM_DEVICE_ADDRESS = 0x54;

//  Instance of the I2C WPI Lib Class
//  Using I2C Port On Board, NOT through MXP
private I2C pixy2 = new I2C(I2C.Port.kOnboard, PIXY_CAM_DEVICE_ADDRESS);

//  Initiate a Transaction with Pixy2 by sending these two bytes first
//  All communications with Pixy2 are Litte-Endian, meaning the LSB comes first
private final int NO_CHECKSUM_SYNC_LSB = 0xAE;  //  LSB - Least Significant Byte
private final int NO_CHECKSUM_SYNC_MSB = 0xC1;  //  MSB - Most Significant Byte

//  Packet Type - 3rd Byte to Send
////////////////////////////////////////////////////////////////////////////////////////////
private final int PIXY_CAM_RESOLUTION_REQUEST_ADDRESS = 0x0C;
private final int PIXY_CAM_RESOLUTION_RESPONSE_ADDRESS = 0x0D;

private final int PIXY_CAM_VERSION_REQUEST_ADDRESS = 0x0E;
private final int PIXY_CAM_VERSION_RESPONSE_ADDRESS = 0x0F;

private final int PIXY_CAM_REQUEST_BLOCKS_ADDRESS = 0x20;
private final int PIXY_CAM_RESPONSE_BLOCKS_ADDRESS = 0x21;
////////////////////////////////////////////////////////////////////////////////////////////

//Creating Arrays to Store Bytes to be Sent or Received
////////////////////////////////////////////////////////////////////////////////////////////
private final int MAX_NUM_BYTES_TO_SEND = 6;
public final int MAX_NUM_BYTES_TO_READ = 142;  //  10 Detected Objects + 2 Checksum Bytes

private byte[] dataToSend = new byte[MAX_NUM_BYTES_TO_SEND];
private byte[] dataBytesRead = new byte[MAX_NUM_BYTES_TO_READ];
////////////////////////////////////////////////////////////////////////////////////////////

//  Used for Objects of a specific Signature
////////////////////////////////////////////////////////////////////////////////////////////
public static final int SIGNATURE_1 = 0b00000001;
public static final int SIGNATURE_2 = 0b00000010;
public static final int SIGNATURE_3 = 0b00000100;
public static final int SIGNATURE_4 = 0b00001000;
public static final int SIGNATURE_5 = 0b00010000;
public static final int SIGNATURE_6 = 0b00100000;
public static final int SIGNATURE_7 = 0b01000000;
////////////////////////////////////////////////////////////////////////////////////////////

public static enum RequestedDataType
{
	PixyVersion, Resolution, General
}

//  InitializeSubsystem()
private static PixyCam instance;

//  RequestDataFromPixy()
private int numberOfBytesToWrite;
private final int DEFAULT_SIGNATURE = SIGNATURE_1;
private final int MAX_BLOCKS_TO_DETECT = 0xFF;

//  ModifyDataByte()
private int modifiedDataByte;

//  IsCorrectResponseAddress()
private int responseAddress;
private int correctResponseAddress = 0x00;  //  Giving Variable Initial Value

//  GetNumberOfBytesToRead()
private int bytesToRead;

//  GetResolution()
private int[] resolution = new int[2];

//  GetNumberOfObjectsDetectedAndOrganizeGeneralData()
private int numberOfObjectsDetected;
private int leastSignificantByte;
private int mostSignificantByte;
private int initialData;
public static final int MAX_OBJECTS_TO_STORE = 10;
private final int NUMBER_OF_CHARACTERISTICS_TO_STORE = 7;
public static final int NUMBER_OF_IMPORTANT_CHARACTERISTICS = 5;
int[][] objectGeneralData = new int[MAX_OBJECTS_TO_STORE][NUMBER_OF_CHARACTERISTICS_TO_STORE];  //  Maximum of 10 Objects, each with 7 Characteristics

//  GetSignatureOfObject()
public static final int OBJECT_SIGNATURE = 0;

//  GetXAndYCoordinatesOfObject()
int[] coordinates = new int[2];
public static final int X_ONLY = 1;
public static final int Y_ONLY = 2;

//  ModifyCoordinates()
private int modifiedData;

//  GetWidthAndHeightOfObject()
int[] widthAndHeight = new int[2];
public static final int WIDTH_ONLY = 3;
public static final int HEIGHT_ONLY = 4;

//  GetImportantObjectInformation()
private int[] objectImportantData = new int[5];




    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
    	//  setDefaultCommand(new GetPixyData());
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
    
    private void ZeroDataBytesReadArray()
    {
    	for (int i = 0; i < MAX_NUM_BYTES_TO_READ; i++)
    	{
    		dataBytesRead[i] = 0;
    	}
    	return;
    }
    
    public boolean InitializePixy()
    {	
    	this.RequestDataFromPixy(RequestedDataType.PixyVersion);
    	Timer.delay(.0002);
    	
    	return IsCorrectResponseAddress(RequestedDataType.PixyVersion);
    }
    
    public void RequestDataFromPixy(RequestedDataType requestedDataType, int signatureCode, int maxBlocksToDetect)
    {
    	dataToSend[0] = (byte)NO_CHECKSUM_SYNC_LSB;
    	dataToSend[1] = (byte)NO_CHECKSUM_SYNC_MSB;
    	
    	switch(requestedDataType)
    	{
	    	case PixyVersion:
	    		dataToSend[2] = PIXY_CAM_VERSION_REQUEST_ADDRESS;
	    		dataToSend[3] = 0x00;
	    		numberOfBytesToWrite = 4;
	    		break;
	    	case Resolution:
	    		dataToSend[2] = PIXY_CAM_RESOLUTION_REQUEST_ADDRESS;
	    		dataToSend[3] = 0x01;
	    		dataToSend[4] = 0x00;
	    		numberOfBytesToWrite = 5;
	    		break;
	    	case General:
	        	dataToSend[2] = (byte)PIXY_CAM_REQUEST_BLOCKS_ADDRESS;
	        	dataToSend[3] = (byte)0x02;
	        	dataToSend[4] = (byte)signatureCode;
	        	dataToSend[5] = (byte)maxBlocksToDetect;
	        	numberOfBytesToWrite = 6;
	        	break;
    	}
    	
    	pixy2.writeBulk(dataToSend, numberOfBytesToWrite);
    	
    	return;
    }
    
    public void RequestDataFromPixy(RequestedDataType requestedDataType, int signatureCode)
    {
    	this.RequestDataFromPixy(requestedDataType, signatureCode, MAX_BLOCKS_TO_DETECT);
    	return;
    }
    
    public void RequestDataFromPixy(RequestedDataType requestedDataType)
    {
    	this.RequestDataFromPixy(requestedDataType, DEFAULT_SIGNATURE);
    	return;
    }
    
    private int ModifyDataByte(byte dataByte)
    {	
    	if (dataByte < 0)
    	{
    		modifiedDataByte = (256 + ((int)dataByte));
    	}
    	else
    	{
    		modifiedDataByte = (int)dataByte;
    	}
    	return modifiedDataByte;
    }
    
    public boolean IsCorrectResponseAddress(RequestedDataType requestedDataType)
    {
    	pixy2.readOnly(dataBytesRead, 3);
    	responseAddress = ModifyDataByte(dataBytesRead[2]);
    	
    	switch (requestedDataType)
    	{
	    	case PixyVersion:
	    		correctResponseAddress = PIXY_CAM_VERSION_RESPONSE_ADDRESS;
	    		break;
	    	case Resolution:
	    		correctResponseAddress = PIXY_CAM_RESOLUTION_RESPONSE_ADDRESS;
	    		break;
	    	case General:
	    		correctResponseAddress = PIXY_CAM_RESPONSE_BLOCKS_ADDRESS;
	    		break;
    	}
    	
    	return (responseAddress == correctResponseAddress);
    }
    
    public int GetNumberOfBytesToRead()
    {
    	pixy2.readOnly(dataBytesRead, 1);
    	bytesToRead = ModifyDataByte(dataBytesRead[0]);
    	
    	pixy2.readOnly(dataBytesRead, 2);  //  Reading the Next Two Bytes because we ignore them
    	return bytesToRead;
    }
    
    public void ClearBuffer(int numberOfBytesToRead)
    {
    	pixy2.readOnly(dataBytesRead, numberOfBytesToRead);
    	return;
    }
    
    public int[] GetResolution(int numberOfBytesToRead)
    {	
    	pixy2.readOnly(dataBytesRead, numberOfBytesToRead);  //  Reading 4 Bytes
    	
    	resolution[0] = (ModifyDataByte(dataBytesRead[1]) << 8) + ModifyDataByte(dataBytesRead[0]);
    	resolution[1] = (ModifyDataByte(dataBytesRead[3]) << 8) + ModifyDataByte(dataBytesRead[2]);
    	
    	return resolution;
    }
    
    public int GetNumberOfObjectsDetectedAndOrganizeGeneralData(int numberOfBytesToRead)
    {	
    	numberOfObjectsDetected = numberOfBytesToRead / 14;
    	this.ZeroDataBytesReadArray();
    	pixy2.readOnly(dataBytesRead, numberOfBytesToRead);
    	
    	for (int i = 0; i < numberOfObjectsDetected; i++)
    	{
    		for (int j = 0; j < 7; j++)
    		{
    			switch (j)
    			{
	    			case 0:  //  Signature Number
	    				objectGeneralData[i][j] = ModifyDataByte(dataBytesRead[(7 * i)]);
	    				break;
	    			case 1:  //  X Coordinate
	    			case 2:  //  Y Coordinate
	    			case 3:  //  Width
	    			case 4:  //  Height
	    				leastSignificantByte = ModifyDataByte(dataBytesRead[(7 * i) + (2 * j)]);
	    				mostSignificantByte = ModifyDataByte(dataBytesRead[(7 * i) + (2 * j) + 1]);
	    				initialData = (mostSignificantByte << 8) + leastSignificantByte;
	    				objectGeneralData[i][j] = ModifyData(initialData, j);
	    				break;
	    			case 5:  //  Index
	    				objectGeneralData[i][j] = ModifyDataByte(dataBytesRead[(7 * i) + 12]);
	    				break;
	    			case 6:  //  Age
	    				objectGeneralData[i][j] = ModifyDataByte(dataBytesRead[(7 * i) + 13]);
	    				break;
    			}
    		}
    	}
    	
    	return numberOfObjectsDetected;
    }
    
    public int GetSignatureOfObject(int objectNumber)
    {
    	return objectGeneralData[objectNumber][0];
    }
    
    public int[] GetXAndYCoordinatesOfObject(int objectNumber)
    {
    	coordinates[0] = objectGeneralData[objectNumber][1];
    	coordinates[1] = objectGeneralData[objectNumber][2];
    	return coordinates;
    }
    
    private int GetXAndYCoordinatesOfObject(int objectNumber, int xOrYOnly)
    {
    	return objectGeneralData[objectNumber][xOrYOnly];
    }
    
    //  Used to Modify Coordinates for a 4-Quadrant Plane, not just a single Quadrant
    public int ModifyData(int data, int xOrYOnly)
    {
    	modifiedData = data;
    	
    	if (xOrYOnly == X_ONLY)
    	{
    		modifiedData = data - (int)(.5 * resolution[0]);
    	}
    	else if (xOrYOnly == Y_ONLY)
    	{
    		modifiedData = (int)(.5 * resolution[1]) - data;
    	}
    	return modifiedData;
    }
    
    
    public int[] GetWidthAndHeightOfObject(int objectNumber)
    {
    	widthAndHeight[0] = objectGeneralData[objectNumber][3];
    	widthAndHeight[1] = objectGeneralData[objectNumber][4];
    	return widthAndHeight;
    }
    
    private int GetWidthAndHeightOfObject(int objectNumber, int widthOrHeightOnly)
    {
    	return objectGeneralData[objectNumber][widthOrHeightOnly];
    }
    
    public int[] GetImportantObjectInformation(int objectNumber)
    {
    	objectImportantData[0] = this.GetSignatureOfObject(objectNumber);
    	objectImportantData[1] = this.GetXAndYCoordinatesOfObject(objectNumber, X_ONLY);
    	objectImportantData[2] = this.GetXAndYCoordinatesOfObject(objectNumber, Y_ONLY);
    	objectImportantData[3] = this.GetWidthAndHeightOfObject(objectNumber,  WIDTH_ONLY);
    	objectImportantData[4] = this.GetWidthAndHeightOfObject(objectNumber,  HEIGHT_ONLY);
    	return objectImportantData;
    }
    
    public int GetIndexOfObject(int objectNumber)
    {
    	return objectGeneralData[objectNumber][5];
    }
    
    public int GetAgeOfObject(int objectNumber)
    {
    	return objectGeneralData[objectNumber][6];
    }
    
}


/*  This class was created to condense the PixyCam Subsystem
    The methods in EstablishI2CPixyConnection are used for establishing a connection with the PixyCam
    while the methods in PixyCam are used to read and interpret the data sent from the Pixy.
    All the methods in this class are only used in the PixyCam Subsystem, however, the public enum
    RequestedDataType is used in the Subsystem, along with some of the Pixy Commands. */

package org.usfirst.frc.team135.robot.subsystems;

//  Importing I2C and Timer Classes from WPILib
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;


public class EstablishI2CPixyConnection {
	
	//  Create an Instance of the I2C WPILib Class for the Pixy2
	private I2C pixy2;
	
	//  Pixy Communication with Other Devices is explained here: https://docs.pixycam.com/wiki/doku.php?id=wiki:v2:porting_guide
	
	//  To Initiate a Transaction with Pixy2, send these two bytes first
	//  All communications with Pixy2 are Litte-Endian, meaning the LSB is written and read first
	private final int NO_CHECKSUM_SYNC_LSB = 0xAE;  //  LSB - Least Significant Byte
	private final int NO_CHECKSUM_SYNC_MSB = 0xC1;  //  MSB - Most Significant Byte
	
	//  Request and Response Addresses to receive the specified data
	//  RoboRIO sends the Request Address to Pixy
	//  Pixy will send back the respective Response Address if it correctly interpreted the Request Address sent
	////////////////////////////////////////////////////////////////////////////////////////////
	private final int PIXY_CAM_RESOLUTION_REQUEST_ADDRESS = 0x0C;
	private final int PIXY_CAM_RESOLUTION_RESPONSE_ADDRESS = 0x0D;

	private final int PIXY_CAM_VERSION_REQUEST_ADDRESS = 0x0E;
	private final int PIXY_CAM_VERSION_RESPONSE_ADDRESS = 0x0F;

	private final int PIXY_CAM_REQUEST_BLOCKS_ADDRESS = 0x20;
	private final int PIXY_CAM_RESPONSE_BLOCKS_ADDRESS = 0x21;
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//  Used for only requesting object information of a specific signature
	////////////////////////////////////////////////////////////////////////////////////////////
	public static final int SIGNATURE_1 = 0b00000001;
	public static final int SIGNATURE_2 = 0b00000010;
	public static final int SIGNATURE_3 = 0b00000100;
	public static final int SIGNATURE_4 = 0b00001000;
	public static final int SIGNATURE_5 = 0b00010000;
	public static final int SIGNATURE_6 = 0b00100000;
	public static final int SIGNATURE_7 = 0b01000000;
	////////////////////////////////////////////////////////////////////////////////////////////
	
	//  Two Bytes to Send when Requesting "RequestedDataType.General" Data
	private final int DEFAULT_SIGNATURE = SIGNATURE_1;  //  Variable to set the Default Signature to request Object Information from
	private final int MAX_BLOCKS_TO_DETECT = 0xFF;  //  0xFF = 255 Max Blocks to Receive Data From
	
	//  Enum used to better organize what data to request from Pixy
	public static enum RequestedDataType
	{
		PixyVersion,
		Resolution,
		General
	}
	
	//  Constructor of EstablishI2CPixyConnection Class
	//  Uses the same instance of I2C that is created in the PixyCam Subsystem
	public EstablishI2CPixyConnection(I2C i2CInstance)
	{
		pixy2 = i2CInstance;
	}
	
	//  Requesting the Pixy Version Number
	//  When the Pixy sends back the correct Response Address, we deem the Pixy "Initialized" and start requesting more important information
	public boolean InitializePixy()
    {	
    	this.RequestDataFromPixy(RequestedDataType.PixyVersion);
    	Timer.delay(.0002);  //  200 microsecond delay
    	
    	return IsCorrectResponseAddress(RequestedDataType.PixyVersion);
    }
    
	//  Requests Information of a specific type: PixyVersion, Resolution, or General Information
	/*  Pixy Byte Request Layout
	    1. NO_CHECKSUM_SYNC_LSB
	    2. NO_CHECKSUM_SYNC_MSB
	    3. Request Address
	    4. Number of Bytes to Send to Pixy
	    5-End. Bytes to Send to Pixy      */
    public void RequestDataFromPixy(RequestedDataType requestedDataType, int signatureCode, int maxBlocksToDetect)
    {
    	//  Local Variable Declarations
    	final int MAX_NUM_BYTES_TO_SEND = 6;  //  Maximum Elements of the Array
    	int numberOfBytesToWrite = 0;
    	byte[] dataToSend = new byte[MAX_NUM_BYTES_TO_SEND];
    	
    	//  Executable Statements
    	
    	//  Store the Data Bytes to Send in a Byte Array
    	dataToSend[0] = (byte)NO_CHECKSUM_SYNC_LSB;
    	dataToSend[1] = (byte)NO_CHECKSUM_SYNC_MSB;
    	
    	switch(requestedDataType)
    	{
	    	case PixyVersion:
	    		dataToSend[2] = (byte)PIXY_CAM_VERSION_REQUEST_ADDRESS;
	    		dataToSend[3] = (byte)0x00;
	    		numberOfBytesToWrite = 4;
	    		break;
	    	case Resolution:
	    		dataToSend[2] = (byte)PIXY_CAM_RESOLUTION_REQUEST_ADDRESS;
	    		dataToSend[3] = (byte)0x01;
	    		dataToSend[4] = (byte)0x00;
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
    
    //  Same Function as Above, but with the Default MAX_BLOCK_TO_DETECT
    public void RequestDataFromPixy(RequestedDataType requestedDataType, int signatureCode)
    {
    	this.RequestDataFromPixy(requestedDataType, signatureCode, MAX_BLOCKS_TO_DETECT);
    	return;
    }
    
    //  Same Function as above, but with the Default DEFAULT_SIGNATURE
    public void RequestDataFromPixy(RequestedDataType requestedDataType)
    {
    	this.RequestDataFromPixy(requestedDataType, DEFAULT_SIGNATURE);
    	return;
    }
    
    //  Returns true if the respective response address is sent back from Pixy to RoboRIO
    //  Returns false if this is not the case; Typically an error
    /*  Pixy Byte Response Layout:
        1. LSB of Checksum Sync
        2. MSB of Checksum Sync
        3. Response Address
        4. Number of Data Bytes Sent from Pixy
        5. 5-End. Data Bytes Sent from Pixy    */
    //  Only Reading first 3 Bytes to Determine if the Response Address is Correct
    public boolean IsCorrectResponseAddress(RequestedDataType requestedDataType)
    {
    	//  Local Declarations
    	byte[] dataBytesRead = new byte[3];
    	int responseAddress = 0x00;
    	int correctResponseAddress = 0x00;
    	
    	//  Executable Statements
    	pixy2.readOnly(dataBytesRead, 3);
    	responseAddress = ModifyDataByte(dataBytesRead[2]);  //  Modify the Data Bytes in case they are greater than 127
    	
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
    
    //  Method to Modify the Data Bytes Received
    //  This is needed for some bytes because the Java Data Type "byte" only goes from -128 to 127 (both inclusive)
    //  If the number is negative, we convert it into a positive number
    //  Return Data Type is an int so it can be greater than 127
    public int ModifyDataByte(byte dataByte)
    {	
    	//  Local Declarations
    	int modifiedDataByte = 0;
    	
    	//  Executable Statements
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
}

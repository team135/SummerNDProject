package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;

public class EstablishI2CPixyConnection {
	
	
	private I2C pixy2;
	
	//  Initiate a Transaction with Pixy2 by sending these two bytes first
	//  All communications with Pixy2 are Litte-Endian, meaning the LSB comes first
	private final int NO_CHECKSUM_SYNC_LSB = 0xAE;  //  LSB - Least Significant Byte
	private final int NO_CHECKSUM_SYNC_MSB = 0xC1;  //  MSB - Most Significant Byte
	
	private final int PIXY_CAM_RESOLUTION_REQUEST_ADDRESS = 0x0C;
	private final int PIXY_CAM_RESOLUTION_RESPONSE_ADDRESS = 0x0D;

	private final int PIXY_CAM_VERSION_REQUEST_ADDRESS = 0x0E;
	private final int PIXY_CAM_VERSION_RESPONSE_ADDRESS = 0x0F;

	private final int PIXY_CAM_REQUEST_BLOCKS_ADDRESS = 0x20;
	private final int PIXY_CAM_RESPONSE_BLOCKS_ADDRESS = 0x21;
	
	private final int MAX_NUM_BYTES_TO_SEND = 6;
	
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
	
	private final int DEFAULT_SIGNATURE = SIGNATURE_1;
	private final int MAX_BLOCKS_TO_DETECT = 0xFF;
	
	public static enum RequestedDataType
	{
		PixyVersion, Resolution, General
	}
	
	public boolean InitializePixy(I2C pixy2)
    {	
		this.pixy2 = pixy2;
    	this.RequestDataFromPixy(RequestedDataType.PixyVersion);
    	Timer.delay(.0002);
    	
    	return IsCorrectResponseAddress(RequestedDataType.PixyVersion);
    }
    
    public void RequestDataFromPixy(RequestedDataType requestedDataType, int signatureCode, int maxBlocksToDetect)
    {
    	//  Local Variable Declarations
    	int numberOfBytesToWrite = 0;
    	byte[] dataToSend = new byte[MAX_NUM_BYTES_TO_SEND];
    	
    	//  Executable Statements
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
    	
    	this.pixy2.writeBulk(dataToSend, numberOfBytesToWrite);
    	
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
    
    public boolean IsCorrectResponseAddress(RequestedDataType requestedDataType)
    {
    	//  Local Declarations
    	byte[] dataBytesRead = new byte[3];
    	int responseAddress = 0x00;
    	int correctResponseAddress = 0x00;
    	
    	//  Executable Statements
    	this.pixy2.readOnly(dataBytesRead, 3);
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

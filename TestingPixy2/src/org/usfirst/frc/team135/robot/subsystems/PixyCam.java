package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import edu.wpi.first.wpilibj.I2C;
import org.usfirst.frc.team135.robot.commands.GetPixyData;


/**
 *
 */
public class PixyCam extends Subsystem {
	
private static PixyCam instance;
	
private final int PIXY_CAM_DEVICE_ADDRESS = 0x54;

//  Initiate Transaction with Pixy2 by sending these two bytes first
private final int NO_CHECKSUM_SYNC_LSB = 0xAE;
private final int NO_CHECKSUM_SYNC_MSB = 0xC1;

//  Packet Type - 3rd Byte to Send
private final int PIXY_CAM_REQUEST_BLOCKS_ADDRESS = 0x20;
private final int PIXY_CAM_VERSION_REQUEST_ADDRESS = 0x0e;

private I2C pixy2 = new I2C(I2C.Port.kOnboard, PIXY_CAM_DEVICE_ADDRESS);

private final int NUM_BYTES_TO_SEND = 4;
public final int MAX_NUM_BYTES_TO_READ = 32;
private final int INITIAL_NUMBER_OF_BYTES_TO_READ = 4;
private final int NUMBER_OF_CHECKSUM_BYTES_RECEIVED = 2;

private byte[] dataToSend = new byte[NUM_BYTES_TO_SEND];
private byte[] dataBytesRead = new byte[MAX_NUM_BYTES_TO_READ];

private final int SIGNATURE_1 = 0b00000001;
private final int SIGNATURE_2 = 0b00000010;
private final int SIGNATURE_3 = 0b00000100;
private final int SIGNATURE_4 = 0b00001000;
private final int SIGNATURE_5 = 0b00010000;
private final int SIGNATURE_6 = 0b00100000;
private final int SIGNATURE_7 = 0b01000000;

private int signatureCode;

    //  Methods

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	
    	setDefaultCommand(new GetPixyData());
    }
    
    public static PixyCam InitializeSubsystem()
    {
    	if (instance == null)
    	{
    		instance = new PixyCam();
    	}
    	
    	return instance;
    }
    
    public void InitializeArrays()
    {
    	dataToSend[0] = (byte)NO_CHECKSUM_SYNC_LSB;
    	dataToSend[1] = (byte)NO_CHECKSUM_SYNC_MSB;
    	dataToSend[2] = (byte)PIXY_CAM_REQUEST_BLOCKS_ADDRESS;
    	dataToSend[3] = (byte)0;
    	
    	this.ZeroDataReadArray();
    	
    	return;
    }
    
    public void ZeroDataReadArray()
    {
    	for (int i = 0; i < MAX_NUM_BYTES_TO_READ; i++)
    	{
    		dataBytesRead[i] = (byte)0;
    	}
    	return;
    }
    
    public void WriteToPixy2()
    {	
    	pixy2.writeBulk(dataToSend, NUM_BYTES_TO_SEND);
    	return;
    }
    
    public int GetNumberOfBytesToRead()
    {
    	pixy2.readOnly(dataBytesRead, INITIAL_NUMBER_OF_BYTES_TO_READ);
    	return ((int)dataBytesRead[INITIAL_NUMBER_OF_BYTES_TO_READ - 1] + NUMBER_OF_CHECKSUM_BYTES_RECEIVED);
    }
    
    public byte[] ReadFromPixy2(int numberOfBytesToRead)
    {
    	pixy2.readOnly(dataBytesRead, numberOfBytesToRead);
    	return dataBytesRead;
    }
    
    public void ChooseSignatures(int signatureCode)
    {
    	this.signatureCode = signatureCode;
    	return;
    }
}


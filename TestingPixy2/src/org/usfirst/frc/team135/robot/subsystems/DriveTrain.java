package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.usfirst.frc.team135.robot.RobotMap;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import org.usfirst.frc.team135.robot.commands.DriveWithJoysticks;

/**
 *
 */
public class DriveTrain extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	static DriveTrain instance;

	WPI_TalonSRX frontLeftMotor = new WPI_TalonSRX(RobotMap.DRIVE_TRAIN_FRONT_LEFT_TALON_ID);
	WPI_TalonSRX rearLeftMotor = new WPI_TalonSRX(RobotMap.DRIVE_TRAIN_REAR_LEFT_TALON_ID);
	SpeedControllerGroup leftSide = new SpeedControllerGroup(frontLeftMotor, rearLeftMotor);
	
	WPI_TalonSRX frontRightMotor = new WPI_TalonSRX(RobotMap.DRIVE_TRAIN_FRONT_RIGHT_TALON_ID);
	WPI_TalonSRX rearRightMotor = new WPI_TalonSRX(RobotMap.DRIVE_TRAIN_REAR_RIGHT_TALON_ID);
	SpeedControllerGroup rightSide = new SpeedControllerGroup(frontRightMotor, rearRightMotor);
	
	DifferentialDrive chassis = new DifferentialDrive(leftSide, rightSide);

	
	//  TankDrive()
	private final boolean SQUARED_INPUTS = false;
	

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new DriveWithJoysticks());
    }
    
    public static DriveTrain InitializeDriveTrain()
    {
    	if (instance == null)
    	{
    		instance = new DriveTrain();
    	}
    	return instance;
    }
    
    public void InvertMotors()
    {
    	frontLeftMotor.setInverted(false);
    	frontRightMotor.setInverted(false);
    	rearLeftMotor.setInverted(false);
    	rearRightMotor.setInverted(false);
    	return;
    }
    
    public void TankDrive(double leftMotorPower, double rightMotorPower)
    {
    	chassis.tankDrive(leftMotorPower, rightMotorPower, SQUARED_INPUTS);
    	return;
    }
    
    public void DriveIndividualMotor(int talonID)
    {
    	if (talonID == RobotMap.DRIVE_TRAIN_FRONT_LEFT_TALON_ID)
    	{
    		frontLeftMotor.set(.2);
    	}
    	else if (talonID == RobotMap.DRIVE_TRAIN_FRONT_RIGHT_TALON_ID)
    	{
    		frontRightMotor.set(.2);
    	}
    	else if (talonID == RobotMap.DRIVE_TRAIN_REAR_LEFT_TALON_ID)
    	{
    		rearLeftMotor.set(.2);
    	}
    	else if (talonID == RobotMap.DRIVE_TRAIN_REAR_RIGHT_TALON_ID)
    	{
    		rearRightMotor.set(.2);
    	}
    	return;
    }
    
}


package org.usfirst.frc.team135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.usfirst.frc.team135.robot.RobotMap;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import org.usfirst.frc.team135.robot.commands.DriveWithJoysticks;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 *
 */
public class DriveTrain extends Subsystem implements PIDOutput{

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
	
	public static enum DirectionToTurn
	{
		Left, Right
	}
	
	//  TankDrive()
	private final boolean SQUARED_INPUTS = false;
	
	//  InitializeCurvatureDrive()
	private final double INITIAL_QUICK_STOP_ALPHA_VALUE = 1.0;
	
	//  DriveStraightTowardsBlockWithPixy()
	private final boolean QUICK_STOP_DISABLED = false;
	private double zRotationPower = 0;
	

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
    
    public void TankDrive(double leftMotorPower, double rightMotorPower)
    {
    	chassis.tankDrive(leftMotorPower, rightMotorPower, SQUARED_INPUTS);
    	return;
    }
    
    public void TurnDriveTrain(double driveTrainMotorPower, DirectionToTurn directionToTurn)
    {	
    	if (directionToTurn == DirectionToTurn.Left)
    	{
    		this.TankDrive(-driveTrainMotorPower, driveTrainMotorPower);
    	}
    	else if (directionToTurn == DirectionToTurn.Right)
    	{
    		this.TankDrive(driveTrainMotorPower, -driveTrainMotorPower);
    	}
    	
    	return;
    }
    
    //  Positive driveTrainMotorPower -> Robot Turns Right
    //  Negative driveTrainMotorPower -> Robot Turns Left
    public void TurnDriveTrain(double driveTrainMotorPower)
    {
    	this.TurnDriveTrain(driveTrainMotorPower, DirectionToTurn.Right);
    }
    
    public void DriveIndividualMotor(int talonID, double motorPower)
    {
    	switch (talonID)
    	{
	    	case RobotMap.DRIVE_TRAIN_FRONT_LEFT_TALON_ID:
	    		frontLeftMotor.set(motorPower);
	    	case RobotMap.DRIVE_TRAIN_FRONT_RIGHT_TALON_ID:
	    		frontRightMotor.set(motorPower);
	    	case RobotMap.DRIVE_TRAIN_REAR_LEFT_TALON_ID:
	    		rearLeftMotor.set(motorPower);
	    	case RobotMap.DRIVE_TRAIN_REAR_RIGHT_TALON_ID:
	    		rearRightMotor.set(motorPower);
    	}
    	return;
    }
    
    public void InitializeCurvatureDrive()
    {
    	chassis.setQuickStopAlpha(INITIAL_QUICK_STOP_ALPHA_VALUE);
    	chassis.curvatureDrive(0.0, 0.0, true);
    	return;
    }
    
    public void DriveStraightTowardsBlockWithVision(double motorPower, double xValue, double pValue)
    {
    	zRotationPower = xValue * pValue;
    	chassis.curvatureDrive(motorPower, zRotationPower, QUICK_STOP_DISABLED);
    	return;
    }

	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub
		this.TurnDriveTrain(output);
	}
    
}


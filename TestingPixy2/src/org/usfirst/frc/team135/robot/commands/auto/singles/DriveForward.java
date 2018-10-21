package org.usfirst.frc.team135.robot.commands.auto.singles;

import java.util.Arrays;
import java.util.Collections;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.subsystems.DriveTrain;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.InstantCommand;

public class DriveForward extends InstantCommand 
{
	double 
	distancetravelled,
	distancetotravel,
	currentvoltage,
	estimatedvelocity,
	error,
	time;
	public DriveForward(double distance)
	{
		distancetravelled = 0;
		distancetotravel = distance;
		currentvoltage = 0;
		estimatedvelocity = 0;
		error = 0;
		time = 0;
	}
	protected void execute()
	{
		Timer finaltimer = new Timer();
		finaltimer.start();
		Timer timer = new Timer();
		timer.start();
		Robot.drivetrain.TankDrive(-1.0, -1.0);
		time = timer.get();
		while (distancetravelled < distancetotravel && finaltimer.get() < 2)
		{
			while (timer.get() - time > AUTONOMOUS.TIME_PERIOD)
			{
				currentvoltage = DriveTrain.frontRightMotor.getMotorOutputVoltage();
				estimatedvelocity = (currentvoltage + (currentvoltage < 0 ? 1.25 : -1.25)) * 1.25;
				distancetravelled += estimatedvelocity * AUTONOMOUS.TIME_PERIOD;
				error = (distancetotravel - distancetravelled) / distancetotravel;
				Robot.drivetrain.TankDrive(-1.0 * error, -1.0 * error);
				System.out.println("Voltage: " + currentvoltage +
						" Estimated Velocity: " + estimatedvelocity + 
						" Distance Travelled: " + distancetravelled + "\n");
				time = timer.get();
			}
		}
	}
	protected void end()
	{
		Robot.drivetrain.TankDrive(0.0, 0.0);
	}
	/*
	public static final int FORWARD = 1;
	public static final int BACKWARD = -1; 
	public static final double DRIVE_POWER = .7;
	
	private FunctionalDoubleManager _rangedSensor, _encoder;
	private Mode _driveMode;
	private double _targetDisplacement;
	
	private double _timeout;
	
	private enum Mode
	{
		RANGED_SENSOR,
		ENCODER,
		FUSED
	}
	
	public DriveForward(double targetDistance, FunctionalDoubleManager encoder, FunctionalDoubleManager sonarSensor, boolean isFacingBackwards, double timeout) 
	{
		super();
		requires(Robot.drivetrain);
		
		Robot.drivetrain.ResetEncoders();
		 this._targetDisplacement = targetDistance;
		    this._rangedSensor = sonarSensor;
		    
		    this._driveMode = Mode.RANGED_SENSOR;
		    
		    this._timeout = timeout;
	}
	
	public DriveForward(double targetDisplacement, boolean isFacingBackwards, double timeout) {
        super();
	    requires(Robot.drivetrain);

    	Robot.drivetrain.ResetEncoders();
	    this._targetDisplacement = targetDisplacement;
	    
	    this._encoder = () -> AUTONOMOUS.CONVERSIONS.TICKS2INCHES * Robot.drivetrain.getEncoderSpeed(DRIVETRAIN.BACK_LEFT_MOTOR);
	    this._driveMode = Mode.ENCODER;
	    
	    this._timeout = timeout;
	    
    }
    
    // Called once when the command executes
    protected void initialize() 
    {
    	Timer timer = new Timer();
    	
    	int direction = 0;
    	if (this._targetDisplacement != 0)
    	{
    		direction = (this._targetDisplacement > 0) ? 1 : -1;
    	}
    	else
    	{
    		System.out.println("Done driving straight... 0?");
    		return;
    	}
    	
    	timer.start();
    	if (this._driveMode == Mode.RANGED_SENSOR)
    	{

    		if (direction == DriveForward.FORWARD)
    		{
				while (this._rangedSensor.get() < this._targetDisplacement && timer.get() < this._timeout && DriverStation.getInstance().isAutonomous()) {

					Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);

				}
    		}
    		else if (direction == DriveForward.BACKWARD)
    		{
				while (this._rangedSensor.get() > this._targetDisplacement && timer.get() < this._timeout && DriverStation.getInstance().isAutonomous()) {

					Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);

				}
    		}
    	   
    	}
    	else if (this._driveMode == Mode.ENCODER)
    	{
    		if (direction == DriveForward.FORWARD)
    		{
    			System.out.println("Encoder got: " + this._encoder.get());
        	    while(this._encoder.get() < this._targetDisplacement && timer.get() < this._timeout && DriverStation.getInstance().isAutonomous()) {
        	    	Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);
        	    	
        	    }
    		}
    		else if (direction == DriveForward.BACKWARD)
    		{

        	    while(this._encoder.get() > this._targetDisplacement && timer.get() < this._timeout && DriverStation.getInstance().isAutonomous()) {	
        			System.out.println("Encoder got: " + this._encoder.get());
        			System.out.println("Target Displacement: " + this._targetDisplacement);
        	    	Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);
        	    	
        	    }
    		}

    	}
    	else if (this._driveMode == Mode.FUSED)
    	{

			if (direction == DriveForward.FORWARD) 
			{
				while(true)
				{
		    		double fusedSensorVal = Collections.min(Arrays.asList(new Double[] {this._encoder.get(), this._rangedSensor.get()}));

		    		if (fusedSensorVal > this._targetDisplacement || timer.get() > this._timeout || !DriverStation.getInstance().isAutonomous())
		    		{
		    			break;
		    		}
		    		
		    		Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);
		    		
				}
				
			} 
			else if (direction == DriveForward.BACKWARD) 
			{
				while(true)
				{
		    		double fusedSensorVal = Collections.min(Arrays.asList(new Double[] {this._encoder.get(), this._rangedSensor.get()}));

		    		if (fusedSensorVal < this._targetDisplacement || timer.get() > this._timeout || !DriverStation.getInstance().isAutonomous())
		    		{
		    			break;
		    		}
		    		
		    		Robot.drivetrain.TankDrive(DriveForward.DRIVE_POWER * direction, DriveForward.DRIVE_POWER * direction);
				}
			}

    		
    	}
    	
    
    	Robot.drivetrain.stopMotors();
    }
	*/


	
}

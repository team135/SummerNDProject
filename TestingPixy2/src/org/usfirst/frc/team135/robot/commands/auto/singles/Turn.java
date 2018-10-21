package org.usfirst.frc.team135.robot.commands.auto.singles;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.subsystems.DriveTrain;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.command.TimedCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Turn extends InstantCommand
{
	PIDController turnController;
	double angletoturn;
	double absoluteangle;
	double 
	distancetravelled,
	distancetotravel,
	currentvoltage,
	estimatedvelocity,
	error,
	time;
	int isleftturn;
    public Turn(double angle) {
    	requires(Robot.drivetrain);
    	absoluteangle =  (angle < 0 ? 180 + angle: angle); 
    	angletoturn = angle;
    	distancetravelled = 0;
    	distancetotravel = 0;
		currentvoltage = 0;
		estimatedvelocity = 0;
		error = 0;
		time = 0;
		isleftturn = 1;
    }
    
    protected void execute()
    {
    	distancetotravel = 21.63 * Math.toRadians(Math.abs(angletoturn));
    	System.out.println("Distancetotravel: " + distancetotravel + "\n");
    	isleftturn = (absoluteangle != angletoturn ? 1 : -1);
    	Timer finaltimer = new Timer();
    	finaltimer.start();
    	Timer timer = new Timer();
		timer.start();
		Robot.drivetrain.TankDrive(1.0 * isleftturn, -1.0 * isleftturn);
		time = timer.get();
		while (distancetravelled < (distancetotravel) && finaltimer.get() < 2)
		{
			while ( timer.get() - time > AUTONOMOUS.TIME_PERIOD)
			{
				currentvoltage = DriveTrain.frontRightMotor.getMotorOutputVoltage();
				estimatedvelocity = (currentvoltage + (currentvoltage < 0 ? 1.25 : -1.25)) * 1.25;
				distancetravelled += estimatedvelocity * AUTONOMOUS.TIME_PERIOD;
				error = (distancetotravel - distancetravelled) / distancetotravel;
				Robot.drivetrain.TankDrive(1.0 * isleftturn * error, -1.0 * isleftturn * error);
				System.out.println("Voltage: " + currentvoltage +
						" Estimated Velocity: " + estimatedvelocity + 
						" Distance Travelled: " + distancetravelled +"\n");
				time = timer.get();
			}
		}	
    	/*
    	turnController = new PIDController(AUTONOMOUS.kP, AUTONOMOUS.kI, AUTONOMOUS.kD, AUTONOMOUS.kF, Robot.navx.ahrs, buffer);
        turnController.setInputRange(-180.0f,  180.0f);
        turnController.setOutputRange(-1.0, 1.0);
        turnController.setAbsoluteTolerance(AUTONOMOUS.kToleranceDegrees);
        turnController.setContinuous(true);
    	turnController.enable();
    	SmartDashboard.putNumber("NavX: ", Robot.navx.ahrs.getAngle());
    	System.out.println(Robot.navx.ahrs.getAngle());
    	Robot.drivetrain.TankDrive(1.0 * buffer.output, 1.0 * buffer.output);
    	DriveTrain.frontLeftMotor.pidWrite(buffer.output);
    	*/
    }
    
    protected boolean isFinished()
    {
    	return false;
    }
    protected void interupted()
    {
    	end();
    }
    protected void end()
    {
    	Robot.drivetrain.TankDrive(0.0, 0.0);
    }
}
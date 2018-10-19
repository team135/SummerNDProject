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
	  
    public Turn(double angle) {
    	absoluteangle =  (angle < 0 ? 180 + angle: angle); 
    	angletoturn = angle;
    	requires(Robot.drivetrain);
    }
    
    protected void initialize() {
    	Timer finaltimer = new Timer();
    	finaltimer.start();
    	double distancetotravel = Math.sqrt(2 * 21.63 * 21.63 * (1 - Math.cos(Math.toRadians(absoluteangle))));
    	System.out.println("Distancetotravel: " + distancetotravel + "\n");
    	double distancetravelled = 0;
    	Timer timer = new Timer();
		timer.start();
		Robot.drivetrain.TankDrive(1.0 * (absoluteangle != angletoturn ? 1 : -1), 1.0 * (absoluteangle != angletoturn ? -1 : 1));
		double time = timer.get();
		while (finaltimer.get() < 2)
		{
			while (distancetravelled < (distancetotravel) && timer.get() - time > AUTONOMOUS.TIME_PERIOD)
			{
				double currentvoltage = DriveTrain.frontRightMotor.getMotorOutputVoltage();
				double estimatedvelocity = (currentvoltage - 1.25) * 1.25;
				distancetravelled += estimatedvelocity * AUTONOMOUS.TIME_PERIOD;
				double error = (distancetotravel - distancetravelled) / distancetotravel;
				Robot.drivetrain.TankDrive(1.0 * (absoluteangle != angletoturn ? 1 : -1) * error
						, 1.0 * (absoluteangle != angletoturn ? -1 : 1) * error);
				System.out.println("Voltage: " + currentvoltage +
						" Estimated Velocity: " + estimatedvelocity + 
						" Distance Travelled: " + distancetravelled +"\n");
				time = timer.get();
			}
		}
    }
    
    protected void execute()
    {
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
package org.usfirst.frc.team135.robot.commands.CameraCommands.LimelightCommands;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.Limelight;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DriveStraightWithLimelight extends Command {

	//  Creates an Array to Store the Data from the Limelight
	private double[] limelightData = new double[Limelight.NUMBER_OF_LIMELIGHT_CHARACTERISTICS];
	
	private final double DRIVE_TRAIN_MOTOR_POWER = .35;
	private final double LIMELIGHT_DRIVE_STRAIGHT_P_VALUE = .06;
	private final double TARGET_AREA_THRESHOLD = .5;
	
    public DriveStraightWithLimelight()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.limelight);
    	requires(Robot.driveTrain);
    	
    }

    // Called just before this Command runs the first time
    protected void initialize()
    {
    	//  Initializing Drive Train
    	Robot.driveTrain.InitializeCurvatureDrive();
    	
    	//  Initializing Limelight
    	Robot.limelight.SetCameraPipeline(Limelight.YELLOW_BLOCK_PIPELINE);
    	Robot.limelight.SetCameraMode(Limelight.VISION_PROCESSOR);
    	Robot.limelight.SetLEDMode(Limelight.LED_OFF);  //  Turns off LED to Track the Yellow Block
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	limelightData = Robot.limelight.GetLimelightData();
    	Robot.driveTrain.DriveStraightTowardsBlockWithPixy(DRIVE_TRAIN_MOTOR_POWER, limelightData[Limelight.HORIZONTAL_OFFSET], LIMELIGHT_DRIVE_STRAIGHT_P_VALUE);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return (limelightData[Limelight.TARGET_AREA] >= TARGET_AREA_THRESHOLD);
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	Robot.driveTrain.TankDrive(0.0, 0.0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	this.end();
    }
}

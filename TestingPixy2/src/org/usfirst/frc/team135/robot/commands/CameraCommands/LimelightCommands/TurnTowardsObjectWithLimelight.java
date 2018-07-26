package org.usfirst.frc.team135.robot.commands.CameraCommands.LimelightCommands;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team135.robot.subsystems.Limelight;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class TurnTowardsObjectWithLimelight extends Command {

	private double[] limelightData = new double[Limelight.NUMBER_OF_LIMELIGHT_CHARACTERISTICS];
	
	private boolean targetExists = false;
	private final double TURNING_MOTOR_POWER = .35;
	private final double X_THRESHOLD_TO_STOP_TURNING = 20.0;
	
    public TurnTowardsObjectWithLimelight()
    {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.limelight);
    	requires(Robot.driveTrain);
    }

    // Called just before this Command runs the first time
    protected void initialize() 
    {
    	//  Initializing Limelight
    	Robot.limelight.SetCameraPipeline(Limelight.YELLOW_BLOCK_PIPELINE);
    	Robot.limelight.SetCameraMode(Limelight.VISION_PROCESSOR);
    	Robot.limelight.SetLEDMode(Limelight.LED_OFF);  //  Turns off LED to Track the Yellow Block
    	
    	targetExists = false;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute()
    {
    	targetExists = Robot.limelight.isTargetsExist();
    	limelightData = Robot.limelight.GetLimelightData();
    	
    	if (targetExists == false)  //  If no target is detected, Turn Left
    	{
    		Robot.driveTrain.TurnDriveTrain(TURNING_MOTOR_POWER, DriveTrain.DirectionToTurn.Left);
    	}
    	else if (targetExists && limelightData[Limelight.HORIZONTAL_OFFSET] > 0.0)  // If Target is to the Right, turn to the Right
    	{
    		Robot.driveTrain.TurnDriveTrain(TURNING_MOTOR_POWER, DriveTrain.DirectionToTurn.Right);
    	}
    	else if (targetExists && limelightData[Limelight.HORIZONTAL_OFFSET] < 0.0)  //  If Target is to the Left, turn to the Left
    	{
    		Robot.driveTrain.TurnDriveTrain(TURNING_MOTOR_POWER, DriveTrain.DirectionToTurn.Left);
    	}
    	else  //  If the Horizontal Offset is equal to 0.0, Don't Turn
    	{
    		Robot.driveTrain.TankDrive(0.0, 0.0);
    	}
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished()
    {
        return (limelightData[Limelight.HORIZONTAL_OFFSET] <= X_THRESHOLD_TO_STOP_TURNING 
        		&& limelightData[Limelight.HORIZONTAL_OFFSET] >= -X_THRESHOLD_TO_STOP_TURNING);
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	Robot.driveTrain.TankDrive(0.0, 0.0);
    	targetExists = false;
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted()
    {
    	this.end();
    }
}

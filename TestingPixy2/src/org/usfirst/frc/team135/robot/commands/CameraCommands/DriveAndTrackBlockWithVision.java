package org.usfirst.frc.team135.robot.commands.CameraCommands;

import edu.wpi.first.wpilibj.command.CommandGroup;

import org.usfirst.frc.team135.robot.RobotMap;
import org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands.DriveStraightWithPixy;
import org.usfirst.frc.team135.robot.commands.CameraCommands.PixyCommands.TurnTowardsObjectWithPixy;
import org.usfirst.frc.team135.robot.commands.CameraCommands.LimelightCommands.DriveStraightWithLimelight;
import org.usfirst.frc.team135.robot.commands.CameraCommands.LimelightCommands.TurnTowardsObjectWithLimelight;

/**
 *
 */
public class DriveAndTrackBlockWithVision extends CommandGroup {

    public DriveAndTrackBlockWithVision() {
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    	
    	if (RobotMap.CAMERA_TO_USE == RobotMap.DesignatedCamera.PixyCamera)
    	{
        	addSequential(new TurnTowardsObjectWithPixy());
        	addSequential(new DriveStraightWithPixy());
    	}
    	else if (RobotMap.CAMERA_TO_USE == RobotMap.DesignatedCamera.Limelight)
    	{
        	addSequential(new TurnTowardsObjectWithLimelight());
        	addSequential(new DriveStraightWithLimelight());
    	}
    }
}

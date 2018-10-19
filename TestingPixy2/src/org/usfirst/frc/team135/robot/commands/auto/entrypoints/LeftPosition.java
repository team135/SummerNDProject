package org.usfirst.frc.team135.robot.commands.auto.entrypoints;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.commands.auto.groups.SideToLine;
import org.usfirst.frc.team135.robot.commands.auto.groups.SideToNearSwitch;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class LeftPosition extends CommandGroup {
	

	public LeftPosition() 
    {
    	int switchPosition = getSwitchPosition(Robot.gameMessage);
    	
    	//Robot.drivetrain.ResetEncoders();
    	//*******Robot.navx.reset();
    	switch (switchPosition)
    	{
    	case AUTONOMOUS.CLOSE:
			addSequential(new SideToNearSwitch(AUTONOMOUS.IS_LEFT));
			break;
    	case AUTONOMOUS.FAR:
			addSequential(new SideToLine(AUTONOMOUS.IS_LEFT));
			break;
		}

    }
	private int getSwitchPosition(String msg)
    {
    	return msg.charAt(0)  == 'L' ? AUTONOMOUS.CLOSE : AUTONOMOUS.FAR;
    }
}
package org.usfirst.frc.team135.robot.commands.auto.entrypoints;

import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.commands.auto.groups.SideToLine;
import org.usfirst.frc.team135.robot.commands.auto.groups.SideToNearSwitch;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class RightPosition extends CommandGroup {
	

	public RightPosition() 
    {
    	int switchPosition = getSwitchPosition(Robot.gameMessage);
    	
    	//Robot.drivetrain.ResetEncoders();
    	//Robot.navx.reset();
    	switch (switchPosition)
    	{
    	case AUTONOMOUS.CLOSE:
			addSequential(new SideToNearSwitch(AUTONOMOUS.IS_RIGHT));
			break;
    	case AUTONOMOUS.FAR:
			addSequential(new SideToLine(AUTONOMOUS.IS_RIGHT));
			break;
		}

    }
	private int getSwitchPosition(String msg)
    {
    	return msg.charAt(0)  == 'R' ? AUTONOMOUS.CLOSE : AUTONOMOUS.FAR;
    }
}
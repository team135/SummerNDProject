package org.usfirst.frc.team135.robot.commands.auto.entrypoints;
import org.usfirst.frc.team135.robot.Robot;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.commands.auto.groups.MidToSwitch;

import edu.wpi.first.wpilibj.command.CommandGroup;
public class MiddlePosition extends CommandGroup {
	

	public MiddlePosition() 
    {    	
    	//Robot.drivetrain.ResetEncoders();
    	//Robot.navx.reset();
    	addSequential(new MidToSwitch(getSwitchPosition(Robot.gameMessage)));
    }
    private boolean getSwitchPosition(String msg)
    {
    	return msg.charAt(0)  == 'L' ? AUTONOMOUS.IS_LEFT : AUTONOMOUS.IS_RIGHT;
    }
}
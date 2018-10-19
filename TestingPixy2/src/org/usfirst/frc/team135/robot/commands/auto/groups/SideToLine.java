package org.usfirst.frc.team135.robot.commands.auto.groups;

import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS;
import org.usfirst.frc.team135.robot.commands.auto.singles.DriveForward;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SideToLine extends CommandGroup
{
	public SideToLine(boolean isbackward)
	{
		System.out.println("SideToLine");
		addSequential(new DriveForward(AUTONOMOUS.FIELD.AUTO_LINE));
	}
}

package org.usfirst.frc.team135.robot.commands.auto.groups;

import org.usfirst.frc.team135.robot.RobotMap;
import org.usfirst.frc.team135.robot.RobotMap.AUTONOMOUS.FIELD;
import org.usfirst.frc.team135.robot.commands.auto.singles.DriveForward;
import org.usfirst.frc.team135.robot.commands.auto.singles.SetArmPosition;
import org.usfirst.frc.team135.robot.commands.auto.singles.Turn;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class MidToSwitch extends CommandGroup
{	
	public MidToSwitch(boolean isLeft)
	{
		addSequential(new DriveForward(FIELD.SCALE_X / 2));
		System.out.println("MidToSwitch");
		if (isLeft)
		{
			addSequential(new Turn(-90));
		}
		else
		{
			addSequential(new Turn(90));
		}
		addSequential(new DriveForward(FIELD.SCALE_BOX_Y + FIELD.SCALE_Y / 2));
		if (isLeft)
		{
			addSequential(new Turn(90));
		}
		else
		{
			addSequential(new Turn(-90));
		}
		addSequential(new DriveForward(FIELD.SCALE_X / 2));
	}
	}
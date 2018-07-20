/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team135.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.usfirst.frc.team135.robot.commands.DriveTowardsObjectWithPixy;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	
	public static final int LEFT_JOYSTICK = 0;
	public static final int RIGHT_JOYSTICK = 1;
	
	private static Joystick leftJoystick = new Joystick(LEFT_JOYSTICK);
	private static Joystick rightJoystick = new Joystick(RIGHT_JOYSTICK);
	
	private static final int TRIGGER_BUTTON = 1;
	
	private static JoystickButton rightTrigger = new JoystickButton(rightJoystick, TRIGGER_BUTTON);
	
	//  DeadbandJoystickValue()
	private double DRIVE_TRAIN_JOYSTICK_DEADBAND = .15;
	private double returnJoystickValue;
	
	//  GetJoystickYValue()
	private double joystickYValue;
	
	public static OI instance;
	
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	
	public static OI InitializeOperatorInterface()
	{
		if (instance == null)
		{
			instance = new OI();
		}
		return instance;
	}
	
	private double DeadbandJoystickValue(double joystickValue)
	{
		if (Math.abs(joystickValue) >= DRIVE_TRAIN_JOYSTICK_DEADBAND)
		{
			returnJoystickValue = joystickValue;
		}
		else 
		{
			returnJoystickValue = 0.0;
		}
		return returnJoystickValue;
	}
	
	public double GetJoystickYValue(int joystickNumber)
	{
		if (joystickNumber == LEFT_JOYSTICK)
		{
			joystickYValue = this.DeadbandJoystickValue(-leftJoystick.getY());
		}
		else if (joystickNumber == RIGHT_JOYSTICK)
		{
			joystickYValue = this.DeadbandJoystickValue(-rightJoystick.getY());
		}
		
		return joystickYValue;
	}
	
	public static void InitializeButtonsWithCommands()
	{
		rightTrigger.toggleWhenPressed(new DriveTowardsObjectWithPixy());
		return;
	}
}

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team135.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
//  import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team135.robot.subsystems.PixyCam;
import org.usfirst.frc.team135.robot.commands.auto.entrypoints.LeftPosition;
import org.usfirst.frc.team135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team135.robot.subsystems.Limelight;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	//  public static ExampleSubsystem m_subsystem = new ExampleSubsystem();
	
	//  Declaring Instances of the Subsystems
	public static OI oi;
	public static PixyCam pixyCam;
	public static DriveTrain drivetrain;
	public static Limelight limelight;
	
	boolean chooseLimelightCamera;
	public Command autonomousCommand;

	public static String gameMessage;
	SendableChooser<String> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		oi = OI.InitializeOperatorInterface();
		pixyCam = PixyCam.InitializeSubsystem();
		drivetrain = DriveTrain.InitializeDriveTrain();
		limelight = Limelight.InitializeSubystem();
		
		
		chooseLimelightCamera = Preferences.getInstance().getBoolean("Limelight?", true);
		RobotMap.cameraToUse  = chooseLimelightCamera ? RobotMap.DesignatedCamera.Limelight : RobotMap.DesignatedCamera.PixyCamera;
		
		if (RobotMap.cameraToUse == RobotMap.DesignatedCamera.Limelight)
		{
			SmartDashboard.putString("Camera Using", "Limelight");
		}
		else if (RobotMap.cameraToUse == RobotMap.DesignatedCamera.PixyCamera)
		{
			SmartDashboard.putString("Camera Using", "Pixy2");
		}
		
		OI.InitializeButtonsWithCommands();
		
		//  m_chooser.addDefault("Default Auto", new ExampleCommand());
		//  chooser.addObject("My Auto", new MyAutoCommand());
		//  SmartDashboard.putData("Auto mode", m_chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		
		gameMessage = DriverStation.getInstance().getGameSpecificMessage();
		
		String position = chooser.getSelected();
		
		System.out.println("Left Position");
		autonomousCommand = new LeftPosition();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}

/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.components.Climber;
import org.usfirst.frc.team5431.robot.components.DriveBase;

import edu.wpi.first.wpilibj.IterativeRobot;

public class Robot extends IterativeRobot {

	private final DriveBase driveBase = new DriveBase();
	private final Climber climber = new Climber();
	private Titan.Xbox controller;
	
	@Override
	public void robotInit() {
		controller = new Titan.Xbox(0);
		controller.setDeadzone(0.1);
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopPeriodic() {
		driveBase.drive(controller.getRawAxis(Titan.Xbox.Axis.LEFT_Y), controller.getRawAxis(Titan.Xbox.Axis.RIGHT_Y));
	
		if(controller.getRawButton(Titan.Xbox.Button.BUMPER_R)) {
			climber.climb(1.0);
		}else{
			climber.climb(0.0);
		}
	}

}

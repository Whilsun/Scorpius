/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5431.robot;

import java.util.LinkedList;
import java.util.Queue;

import org.usfirst.frc.team5431.robot.auton.DriveStep;
import org.usfirst.frc.team5431.robot.auton.Step;
import org.usfirst.frc.team5431.robot.auton.Step.StepResult;
import org.usfirst.frc.team5431.robot.auton.TurnStep;
import org.usfirst.frc.team5431.robot.components.Catapult;
import org.usfirst.frc.team5431.robot.components.Climber;
import org.usfirst.frc.team5431.robot.components.DriveBase;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private final DriveBase driveBase = new DriveBase();
	private final Climber climber = new Climber();
	private final Catapult catapult = new Catapult();
	private final Titan.NavX navx = new Titan.NavX();
	private Titan.FSi6S driver;
	private Titan.Xbox operator;
	

	private final Queue<Step> autonomousSteps = new LinkedList<>();

	@Override
	public void robotInit() {
		driver = new Titan.FSi6S(0);
		driver.setDeadzone(0.1);

		operator = new Titan.Xbox(1);
		operator.setDeadzone(0.1);
	}

	@Override
	public void autonomousInit() {
		navx.reset();
		navx.resetDisplacement();
		navx.resetYaw();
		
		autonomousSteps.add(new DriveStep(20.0));
		autonomousSteps.add(new TurnStep(90.0));
		
		autonomousSteps.element().init(this);
	}

	@Override
	public void autonomousPeriodic() {
		if (!autonomousSteps.isEmpty()) {
			final Step step = autonomousSteps.peek();

			final StepResult result = step.periodic(this);
			if (result == StepResult.COMPLETE) {
				step.done(this);
				autonomousSteps.remove();
				final Step nextStep = autonomousSteps.peek();
				if (nextStep != null) {
					nextStep.init(this);
				}
			}
		}
		
		SmartDashboard.putNumber("navx", navx.getAngle());
	}
	
	@Override
	public void teleopInit() {
		navx.reset();
		navx.resetDisplacement();
		navx.resetYaw();
	}

	@Override
	public void teleopPeriodic() {
		driveBase.drive(driver.getRawAxis(Titan.FSi6S.Axis.LEFT_Y), driver.getRawAxis(Titan.FSi6S.Axis.RIGHT_Y));

		climber.setClimbing(operator.getRawButton(Titan.Xbox.Button.BUMPER_R));
		catapult.setLowering(operator.getRawButton(Titan.Xbox.Button.B));
	
		SmartDashboard.putNumber("navx", navx.getAngle());
	}

	public Titan.NavX getNavx() {
		return navx;
	}

	public DriveBase getDriveBase() {
		return driveBase;
	}

	public Climber getClimber() {
		return climber;
	}

	public Catapult getCatapult() {
		return catapult;
	}
}

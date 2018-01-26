package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class TurnStep extends Step{
	private final double degrees;

	public TurnStep(final double deg) {
		degrees = deg;
	}
	
	@Override
	public void init(final Robot robot) {
		robot.getDriveBase().reset();
		robot.getDriveBase().enablePID();
	}

	@Override
	public StepResult periodic(final Robot robot) {
		final double currentAngle = robot.getDriveBase().getNavx().getAngle();
		if(Titan.approxEquals(currentAngle, degrees, 0.1)){
			return StepResult.COMPLETE;
		}
		
		if(degrees > 0){
			robot.getDriveBase().turnPID(0.5, degrees);
		}else if(degrees < 0){
			robot.getDriveBase().turnPID(-0.5, degrees);
		}
		
		return StepResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().disablePID();
		robot.getDriveBase().drive(0.0, 0.0);
	}

}

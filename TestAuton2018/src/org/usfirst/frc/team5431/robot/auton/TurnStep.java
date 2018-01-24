package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;

public class TurnStep extends Step{
	private final double degrees;

	public TurnStep(final double deg) {
		degrees = deg;
	}
	
	@Override
	public void init(final Robot robot) {
		robot.getNavx().reset();
	}

	@Override
	public StepResult periodic(final Robot robot) {
		final double currentAngle = robot.getNavx().getAngle();
		if((degrees < 0 && currentAngle < degrees) || currentAngle > degrees){
			return StepResult.COMPLETE;
		}
		
		if(degrees > 0){
			robot.getDriveBase().drive(0.5, -0.5);
		}else if(degrees < 0){
			robot.getDriveBase().drive(-0.5, 0.5);
		}
		
		return StepResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().drive(0.0, 0.0);
	}

}

package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;

public abstract class Step {
	public static enum StepResult{
		IN_PROGRESS, COMPLETE
	};
	
	public abstract StepResult periodic(final Robot robot);
}

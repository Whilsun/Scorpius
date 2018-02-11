package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;

public abstract class Step {
	public String name = "Step";
	public String properties = "None";
	public long startTime = 0;
	
	public static enum StepResult{
		IN_PROGRESS, COMPLETE
	};
	
	public abstract void init(final Robot robot);
	
	public abstract StepResult periodic(final Robot robot);
	
	public abstract void done(final Robot robot);

	public String getName() {
		return name;
	}
	
	public String getProperties() {
		return properties;
	}
	
	public void startTimer() {
		startTime = System.currentTimeMillis();
	}
	
	public double getSecondsElapsed() {
		return (System.currentTimeMillis() - startTime) / 1000.0;
	}
}

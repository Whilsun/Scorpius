package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;

public class WaitStep extends Step {

	private final long durationMS;
	private long startTime;
	
	public WaitStep(final long ms) {
		durationMS = ms;
	}

	@Override
	public void init(final Robot robot) {
		startTime = System.currentTimeMillis();
	}

	@Override
	public StepResult periodic(final Robot robot) {
		if (System.currentTimeMillis() >= startTime + durationMS) {
			return StepResult.COMPLETE;
		}

		return StepResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
	}

}

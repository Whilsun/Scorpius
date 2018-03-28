package org.usfirst.frc.team5431.robot.commands;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class OuttakeCommand extends Titan.Command<Robot>{

	private final long durationMS;
	
	public OuttakeCommand(final long duration) {
		name = "OuttakeCommand";
		properties = String.format("Duration: %d (ms)", duration);
		durationMS = duration;
	}
	
	@Override
	public void init(final Robot robot) {
		robot.getIntake().outtake();
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(getElapsed() > durationMS) {
			return CommandResult.COMPLETE;
		}
		
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getIntake().stopIntake();
	}

}

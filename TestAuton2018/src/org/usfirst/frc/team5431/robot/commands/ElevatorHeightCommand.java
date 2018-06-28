package org.usfirst.frc.team5431.robot.commands;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class ElevatorHeightCommand extends Titan.Command<Robot>{
	private final double height;
	
	public ElevatorHeightCommand(final double height) {
		name = "ElevatorHeightCommand";
		properties = String.format("Height: %.2f (in)", height);
		this.height = height;
	}
	
	
	@Override
	public void init(final Robot robot) {
		robot.getElevator().setWantedHeight(height);
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(robot.getElevator().isAtWantedHeight()) {
			return CommandResult.COMPLETE;
		}
		
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getElevator().stopUp(robot);
		robot.getElevator().resetWantedHeight();
	}

}

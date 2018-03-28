package org.usfirst.frc.team5431.robot.commands;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class CalibrateCommand extends Titan.Command<Robot> {
	public CalibrateCommand() {
		name = "CalibrateCommand";
		properties = "Intake and Gamedata calibration";
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(getElapsed() > 200) {
			return CommandResult.COMPLETE; //Timeout
		}
		
		if(robot.getGameData().hasData()) return CommandResult.COMPLETE;
		Titan.l("Calibrating... %d (Current game data: %s)", getElapsed(), robot.getGameData().getString());
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void init(final Robot robot) {
		robot.getDriveBase().drive(0.0, 0.0);
		robot.getElevator().stopUp();
		robot.getIntake().stopIntake();
		robot.getIntake().setHomeUp();
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().setHome();
		robot.getElevator().stopUp();
		robot.getIntake().stopIntake();
	}
}

package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class CalibrateCommand extends Titan.Command<Robot> {
	public CalibrateCommand() {
		name = "CalibrateStep";
		properties = "Intake and Gamedata calibration";
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(getElapsed() > 200) {
			return CommandResult.COMPLETE; //Timeout
		}
		
		if(robot.getGameData().hasData()) return CommandResult.COMPLETE;
		robot.getDriveBase().drive(0.0, 0.0);
		Titan.l("Calibrating... %d (Current game data: %s)", getElapsed(), robot.getGameData().getString());
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void init(final Robot robot) {
		robot.getIntake().stopUp();
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().setHome();
		robot.getIntake().stopUp();
	}
}

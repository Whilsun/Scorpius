package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

public class CalibrateStep extends Step {
	public CalibrateStep() {
		name = "CalibrateStep";
		properties = "Intake and Gamedata calibration";
	}

	@Override
	public StepResult periodic(final Robot robot) {
		if(getElapsed() > 200) {
			return StepResult.COMPLETE; //Timeout
		}
		
		if(robot.getGameData().hasData()) return StepResult.COMPLETE;
		robot.getIntake().pinchSoft();
		robot.getDriveBase().drive(0.0, 0.0);
		Titan.l("Calibrating... %d (Current game data: %s)", getElapsed(), robot.getGameData().getString());
		return StepResult.IN_PROGRESS;
	}

	@Override
	public void init(final Robot robot) {
		robot.getIntake().intakeStop();
		robot.getIntake().stopUp();
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().setHome();
		robot.getIntake().intakeStop();
		robot.getIntake().stopUp();
	}
}

package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.components.Intake.IntakeState;

public class SwitchCubeStep extends Step{
	@Override
	public void init(final Robot robot) {
		name = "SwitchCubeStep";
		properties = "Shooting cube into switch";
		
		//Start the intake shoot state machine
		robot.getIntake().shootCube();
	}

	@Override
	public StepResult periodic(final Robot robot) {
		//Check to see if the state machine is done
		if(robot.getIntake().getState() == IntakeState.STAY_UP) {
			return StepResult.COMPLETE;
		}
		return StepResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getIntake().intakeStop();
		robot.getIntake().pinchSoft();
	}

}

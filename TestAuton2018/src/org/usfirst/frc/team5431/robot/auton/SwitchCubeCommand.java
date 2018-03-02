package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.components.Intake.IntakeState;

public class SwitchCubeCommand extends Titan.Command<Robot>{
	private boolean shootingCube = false;
	
	@Override
	public void init(final Robot robot) {
		name = "SwitchCubeStep";
		properties = "Shooting cube into switch";
		shootingCube = false;
		
		//Start the intake shoot state machine
		robot.getDriveBase().drive(-0.15, -0.15);
	}

	@Override
	public CommandResult periodic(final Robot robot) {
		//Check to see if the state machine is done
		if(getElapsed() > 800) {
			if(!shootingCube) {
				robot.getIntake().shootCube();
				shootingCube = true;
			}
			if(robot.getIntake().getState() == IntakeState.STAY_UP) {
				return CommandResult.COMPLETE;
			}
		}
		
		robot.getDriveBase().drive(-0.15, -0.15);
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getIntake().intakeStop();
		robot.getIntake().pinchSoft();
	}

}

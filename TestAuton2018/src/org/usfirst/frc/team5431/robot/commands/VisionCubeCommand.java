package org.usfirst.frc.team5431.robot.commands;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.components.DriveBase.TitanPIDSource;
import org.usfirst.frc.team5431.robot.vision.Vision.TargetMode;

public class VisionCubeCommand extends Titan.Command<Robot>{
	@Override
	public void init(final Robot robot) {
		name = "VisionCubeCommand";
		properties = "Using vision to capture cube";
		
		robot.getElevator().goToBottom();
		robot.getIntake().intake();
		robot.getDriveBase().driveAtAnglePID(0.15, 0, TitanPIDSource.VISION, TargetMode.Cube);
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(robot.getElevator().isAtWantedHeight()) {
			robot.getElevator().stopUp();
		}
		
		if(robot.getIntake().hasCube()) {
			return CommandResult.COMPLETE;
		} else {
			robot.getIntake().intake();
		}
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().disableAllPID();
		robot.getElevator().stopUp();
		robot.getIntake().stopIntake();
	}

}

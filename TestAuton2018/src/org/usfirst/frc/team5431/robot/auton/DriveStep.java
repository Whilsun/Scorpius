package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Robot;

import edu.wpi.first.wpilibj.Encoder;

public class DriveStep extends Step{
	private final double distance;
	
	public DriveStep(final double dis) {
		distance = dis;
	}
	
	public double getDistance() {
		return distance;
	}

	@Override
	public StepResult periodic(final Robot robot) {
		robot.getDriveBase().drivePID(0.3, 5);
		
		final Encoder encoder = robot.getDriveBase().getLeftEncoder();
		if((distance < 0 && encoder.getDistance() < distance) || (encoder.getDistance() > distance)) {
			return StepResult.COMPLETE;
		}
		
		return StepResult.IN_PROGRESS;
	}

	@Override
	public void init(final Robot robot) {
		robot.getDriveBase().reset();
		robot.getDriveBase().enablePID();
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().disablePID();
		robot.getDriveBase().drive(0.0, 0.0);
	}

}

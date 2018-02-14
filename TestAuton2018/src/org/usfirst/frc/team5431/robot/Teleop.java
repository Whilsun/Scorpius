package org.usfirst.frc.team5431.robot;

public final class Teleop {
	private Titan.Joystick driver;
	private Titan.Xbox operator;
	
	public Teleop() {
		if(!Constants.AUTO_LOG_PATHFINDING) {
			driver = new Titan.FSi6S(0);
			driver.setDeadzone(0.1);
			
			operator = new Titan.Xbox(1);
			operator.setDeadzone(0.1);
		} else {
			driver = new Titan.Xbox(3);
			driver.setDeadzone(0.1);	
		}
	}
	
	public final void periodicDrive(final Robot robot) {
		robot.getDriveBase().drive(((Titan.FSi6S) driver).getRawAxis(Titan.FSi6S.Axis.LEFT_Y), ((Titan.FSi6S) driver).getRawAxis(Titan.FSi6S.Axis.RIGHT_Y));
	}
	
	public final double[] periodicPathfindingDrive(final Robot robot) {
		final double vals[] = {-((Titan.Xbox) driver).getRawAxis(Titan.Xbox.Axis.LEFT_Y), -((Titan.Xbox) driver).getRawAxis(Titan.Xbox.Axis.RIGHT_Y)};
		robot.getDriveBase().drive(vals[0], vals[1]);
		return vals;
	}
	
	public final void periodicIntake(final Robot robot) {
		
	}
	
	public Titan.Xbox getXbox() {
		return (Titan.Xbox) driver;
	}
}

package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.Titan.Xbox.Button;
import org.usfirst.frc.team5431.robot.components.Intake;

public final class Teleop {
	private final Titan.Xbox driver;
	private final Titan.LogitechExtreme3D operator;
	private final Titan.Toggle cubeCaptureToggle = new Titan.Toggle();

	public Teleop() {
		driver = new Titan.Xbox(0);
		driver.setDeadzone(0.1);

		operator = new Titan.LogitechExtreme3D(1);
		operator.setDeadzone(0.1);
	}

	public final void periodicDrive(final Robot robot) {
		robot.getDriveBase().drive(-driver.getRawAxis(Titan.Xbox.Axis.LEFT_Y),
				-driver.getRawAxis(Titan.Xbox.Axis.RIGHT_Y));
	}

	public final double[] periodicPathfindingDrive(final Robot robot) {
		final double vals[] = { -(driver.getRawAxis(Titan.Xbox.Axis.LEFT_Y)),
				-(driver.getRawAxis(Titan.Xbox.Axis.RIGHT_Y)) };
		robot.getDriveBase().drive(vals[0], vals[1]);
		return vals;
	}

	public final void periodicClimb(final Robot robot) {
		if (driver.getRawButton(Button.B)) {
			robot.getClimber().climb();
		} else {
			robot.getClimber().stopClimbing();
		}

		if (driver.getRawButton(Button.Y)) {
			robot.getClimber().scissorUpFast();
		} else if (driver.getRawButton(Button.X)) {
			robot.getClimber().scissorUpSlow();
		} else if (driver.getRawButton(Button.A)) {
			robot.getClimber().scissorDown();
		} else {
			robot.getClimber().stopScissor();
		}
	}

	public final void periodicIntake(final Robot robot) {
		final Intake intake = robot.getIntake();

		intake.setUpSpeed(operator.getRawAxis(Titan.LogitechExtreme3D.Axis.Y));

		if (cubeCaptureToggle.isToggled(operator.getRawButton(Titan.LogitechExtreme3D.Button.TWO))) {
			if(intake.hasCube()){
				cubeCaptureToggle.setState(false);
			}else {
				intake.setIntakeSpeed(0.7);	
			}
		} else if (operator.getRawButton(Titan.LogitechExtreme3D.Button.TRIGGER)) {
			intake.setIntakeSpeed(-0.7);
		} else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.THREE)){
			intake.setIntakeSpeed(0.7);
		}else {
			intake.stopIntake();
		}

		intake.update(robot);
	}

	public Titan.Xbox getXbox() {
		return driver;
	}
}

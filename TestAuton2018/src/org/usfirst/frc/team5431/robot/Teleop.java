package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.components.Elevator;
import org.usfirst.frc.team5431.robot.components.Intake;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.usfirst.frc.team5431.robot.components.Elevator.ControlMode;

public final class Teleop {
	private final Titan.Xbox driver;
	private final Titan.LogitechExtreme3D operator;
	private final Titan.Toggle cubeCaptureToggle = new Titan.Toggle();

	public Teleop() {
		driver = new Titan.Xbox(Constants.DRIVER_PORT);
		driver.setDeadzone(Constants.DRIVER_DEADZONE);

		operator = new Titan.LogitechExtreme3D(Constants.OPERATOR_PORT);
		operator.setDeadzone(Constants.OPERATOR_DEADZONE);
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
	//Matthew Likes Acarde Drive :)
	
	public final void periodicIntake(final Robot robot) {
		final Intake intake = robot.getIntake();
		if(operator.getPOV() == 0) { //operator.getRawButton(Titan.LogitechExtreme3D.Button.SIX)) {
			intake.setMode(Intake.ControlMode.MANUAL);
			intake.setTiltSpeed(Constants.INTAKE_TILT_SPEED);
		} else if(operator.getPOV() == 180) { //operator.getRawButton(Titan.LogitechExtreme3D.Button.FOUR)) {
			intake.setMode(Intake.ControlMode.MANUAL);
			intake.setTiltSpeed(-Constants.INTAKE_TILT_SPEED);
		} else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.SIX)) {
			intake.goToTop();
		} else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.FOUR)) {
			intake.goToBottom();
		} else {
			intake.stopTilting();
		}
		
		if (cubeCaptureToggle.isToggled(operator.getRawButton(Titan.LogitechExtreme3D.Button.THREE))) {
			if(intake.hasCube()){
				cubeCaptureToggle.setState(false);
				//intake.goToTop();
			} else {
				intake.intake();	
			}
		} else if (operator.getRawButton(Titan.LogitechExtreme3D.Button.TRIGGER)) {
			intake.outtake();
		} else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.TWO)){
			intake.intake();
		}else {
			intake.stopIntake();
		}
		
		intake.update(robot);
	}

	public final void periodicElevator(final Robot robot) {
		final Elevator elevator = robot.getElevator();

		final double upSpeed = -operator.getRawAxis(Titan.LogitechExtreme3D.Axis.Y);
		SmartDashboard.putNumber("UpSpeed", upSpeed);
		//if(elevator.getMode() == ControlMode.MANUAL || Math.abs(upSpeed) > Constants.OPERATOR_DEADZONE) {
		//	elevator.setMode(ControlMode.MANUAL);
//			if(robot.getIntake().getTiltPosition() < 60 || (-upSpeed < 0)) {
//				elevator.setUpSpeed(-upSpeed);
//			}
			elevator.setUpSpeed(upSpeed, robot);
		//}
		
//		if(operator.getRawButton(Titan.LogitechExtreme3D.Button.ELEVEN)) {
//			elevator.setWantedHeight(Constants.HEIGHT_STEAL);
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.NINE)) {
//			elevator.setWantedHeight(Constants.HEIGHT_SWITCH);
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.SEVEN)) {
//			//elevator.setWantedHeight(Constants.HEIGHT_SCALE);
//			elevator.goToTop();
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.SIX)) {
//			robot.getIntake().reset();
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.TWELVE)) {
//			elevator.goToBottom();
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.TEN)) {
//			elevator.setWantedHeight(Constants.HEIGHT_SECOND_LAYER);
//		}
//		else if(operator.getRawButton(Titan.LogitechExtreme3D.Button.EIGHT)) {
//			elevator.setWantedHeight(Constants.HEIGHT_THIRD_LAYER);
//		}
		
		elevator.update(robot);
	}

	public Titan.Xbox getXbox() {
		return driver;
	}
	
	public Titan.LogitechExtreme3D getLogitech() {
		return operator;
	}
}

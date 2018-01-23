package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Intake {
	private final WPI_TalonSRX intakeLeft, intakeRight, intakeActuation, intakeUp;
	
	public Intake() {
		intakeLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_LEFT_ID);
		intakeRight = new WPI_TalonSRX(Constants.TALON_INTAKE_RIGHT_ID);
		intakeActuation = new WPI_TalonSRX(Constants.TALON_INTAKE_ACTUATION_ID);
		intakeUp = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_ID);
		
		intakeLeft.setInverted(Constants.TALON_INTAKE_LEFT_INVERTED);
		intakeRight.setInverted(Constants.TALON_INTAKE_RIGHT_INVERTED);
		intakeActuation.setInverted(Constants.TALON_INTAKE_ACTUATION_INVERTED);
		intakeUp.setInverted(Constants.TALON_INTAKE_UP_INVERTED);
		
		intakeRight.set(ControlMode.Follower, intakeLeft.getDeviceID());
	}
	
	public void setIs
}

package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Climber {
	private final WPI_TalonSRX climberLeft, climberRight;

	public Climber() {
		climberLeft = new WPI_TalonSRX(Constants.TALON_CLIMBER_LEFT_ID);
		climberRight = new WPI_TalonSRX(Constants.TALON_CLIMBER_RIGHT_ID);
		
		climberLeft.setInverted(Constants.TALON_CLIMBER_LEFT_INVERTED);
		climberRight.setInverted(Constants.TALON_CLIMBER_RIGHT_INVERTED);
		
		climberRight.set(ControlMode.Follower, climberLeft.getDeviceID());
	}
	
	public void climb(final double val) {
		climberLeft.set(val);
	}
}

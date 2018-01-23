package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Catapult {
	private final WPI_TalonSRX catapultLeft, catapultRight;
	
	public Catapult() {
		catapultLeft = new WPI_TalonSRX(Constants.TALON_CATAPULT_LEFT_ID);
		catapultRight = new WPI_TalonSRX(Constants.TALON_CATAPULT_RIGHT_ID);
		
		catapultLeft.setInverted(Constants.TALON_CATAPULT_LEFT_INVERTED);
		catapultRight.setInverted(Constants.TALON_CATAPULT_RIGHT_INVERTED);
		
		catapultRight.set(ControlMode.Follower, catapultLeft.getDeviceID());
	}
	
	public void setLowering(final boolean isLowering) {
		if(isLowering) {
			catapultLeft.set(Constants.CATAPULT_LOWER_SPEED);
		}else {
			catapultLeft.set(0.0);
		}
	}
}

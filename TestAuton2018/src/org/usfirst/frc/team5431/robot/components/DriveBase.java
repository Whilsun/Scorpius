package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class DriveBase {
	private final WPI_TalonSRX frontLeft, frontRight, backLeft, backRight;
	
	public DriveBase() {
		frontLeft = new WPI_TalonSRX(Constants.TALON_FRONT_LEFT_ID);
		backLeft = new WPI_TalonSRX(Constants.TALON_BACK_LEFT_ID);
		frontRight = new WPI_TalonSRX(Constants.TALON_FRONT_RIGHT_ID);
		backRight = new WPI_TalonSRX(Constants.TALON_BACK_RIGHT_ID);
		
		frontLeft.setInverted(Constants.TALON_FRONT_LEFT_INVERTED);
		backLeft.setInverted(Constants.TALON_BACK_LEFT_INVERTED);
		frontRight.setInverted(Constants.TALON_FRONT_RIGHT_INVERTED);
		backRight.setInverted(Constants.TALON_BACK_RIGHT_INVERTED);
		
		backLeft.set(ControlMode.Follower, frontLeft.getDeviceID());
		backRight.set(ControlMode.Follower, frontRight.getDeviceID());
	}
	
	public void drive(final double left, final double right) {
		frontLeft.set(left);
		frontRight.set(right);
	}
	
}

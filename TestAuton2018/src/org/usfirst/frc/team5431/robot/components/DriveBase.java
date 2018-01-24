package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class DriveBase {
	private final WPI_TalonSRX frontLeft, frontRight, middleLeft, middleRight, backLeft, backRight;
	private final Encoder leftEncoder, rightEncoder;

	public DriveBase() {
		frontLeft = new WPI_TalonSRX(Constants.TALON_FRONT_LEFT_ID);
		middleLeft = new WPI_TalonSRX(Constants.TALON_MIDDLE_LEFT_ID);
		backLeft = new WPI_TalonSRX(Constants.TALON_BACK_LEFT_ID);
		frontRight = new WPI_TalonSRX(Constants.TALON_FRONT_RIGHT_ID);
		middleRight = new WPI_TalonSRX(Constants.TALON_MIDDLE_RIGHT_ID);
		backRight = new WPI_TalonSRX(Constants.TALON_BACK_RIGHT_ID);

		frontLeft.setInverted(Constants.TALON_FRONT_LEFT_INVERTED);
		middleLeft.setInverted(Constants.TALON_MIDDLE_LEFT_INVERTED);
		backLeft.setInverted(Constants.TALON_BACK_LEFT_INVERTED);
		frontRight.setInverted(Constants.TALON_FRONT_RIGHT_INVERTED);
		middleRight.setInverted(Constants.TALON_MIDDLE_RIGHT_INVERTED);
		backRight.setInverted(Constants.TALON_BACK_RIGHT_INVERTED);

		backLeft.set(ControlMode.Follower, frontLeft.getDeviceID());
		middleLeft.set(ControlMode.Follower, frontLeft.getDeviceID());

		backRight.set(ControlMode.Follower, frontRight.getDeviceID());
		middleRight.set(ControlMode.Follower, frontRight.getDeviceID());

		leftEncoder = new Encoder(Constants.ENCODER_LEFT_DRIVE_CHANNEL_1, Constants.ENCODER_LEFT_DRIVE_CHANNEL_2, false,
				EncodingType.k4X);
		leftEncoder.setDistancePerPulse(4 * Math.PI / 360);
		leftEncoder.setSamplesToAverage(1);
		leftEncoder.setReverseDirection(Constants.ENCODER_LEFT_DRIVE_INVERTED);
		rightEncoder = new Encoder(Constants.ENCODER_RIGHT_DRIVE_CHANNEL_1, Constants.ENCODER_RIGHT_DRIVE_CHANNEL_2,
				false, EncodingType.k4X);
		rightEncoder.setDistancePerPulse(4 * Math.PI / 360);
		rightEncoder.setSamplesToAverage(1);
		rightEncoder.setReverseDirection(Constants.ENCODER_RIGHT_DRIVE_INVERTED);
	}

	public void drive(final double left, final double right) {
		frontLeft.set(left);
		frontRight.set(right);

		SmartDashboard.putNumber("left encoder", leftEncoder.getDistance());
		SmartDashboard.putNumber("right encoder", rightEncoder.getDistance());
	}

	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public Encoder getLeftEncoder() {
		return leftEncoder;
	}

	public Encoder getRightEncoder() {
		return rightEncoder;
	}

}

package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Intake {
	private final WPI_TalonSRX intakeLeft, intakeRight, intakePincher, intakeUpLeft, intakeUpRight;
	
	public Intake() {
		intakeLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_LEFT_ID);
		intakeRight = new WPI_TalonSRX(Constants.TALON_INTAKE_RIGHT_ID);
		intakePincher = new WPI_TalonSRX(Constants.TALON_INTAKE_PINCHER_ID);
		intakeUpLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_LEFT_ID);
		intakeUpRight = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_RIGHT_ID);
		
		//Setup the brake modes on the intake
		intakeLeft.setNeutralMode(NeutralMode.Coast);
		intakeRight.setNeutralMode(NeutralMode.Coast);
		intakePincher.setNeutralMode(NeutralMode.Brake);
		intakeUpLeft.setNeutralMode(NeutralMode.Brake);
		intakeUpRight.setNeutralMode(NeutralMode.Brake);
		
		//Set the intake's inversion options
		intakeLeft.setInverted(Constants.TALON_INTAKE_LEFT_INVERTED);
		intakeRight.setInverted(Constants.TALON_INTAKE_RIGHT_INVERTED);
		intakePincher.setInverted(Constants.TALON_INTAKE_PINCHER_INVERTED);
		intakeUpLeft.setInverted(Constants.TALON_INTAKE_UP_LEFT_INVERTED);
		intakeUpRight.setInverted(Constants.TALON_INTAKE_UP_RIGHT_INVERTED);
		
		//Master-Slave Settings
		intakeRight.set(ControlMode.Follower, intakeLeft.getDeviceID());
		intakeUpRight.set(ControlMode.Follower, intakeUpLeft.getDeviceID());
		
		//Setup the pincher encoder
		intakePincher.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		intakePincher.setSensorPhase(Constants.ENCODER_PINCHER_INVERTED); //Invert the sensor
		
		//Setup the pincher limits
		intakePincher.configForwardSoftLimitThreshold(Constants.ENCODER_PINCHER_FORWARD_LIMIT, 0);
		intakePincher.configReverseSoftLimitThreshold(Constants.ENCODER_PINCHER_REVERSE_LIMIT, 0);
		intakePincher.configForwardSoftLimitEnable(Constants.ENCODER_PINCHER_FORWARD_LIMIT_ENABLED, 0);
		intakePincher.configReverseSoftLimitEnable(Constants.ENCODER_PINCHER_REVERSE_LIMIT_ENABLED, 0);
	
		//Setup the intake's current limiting
		intakePincher.configContinuousCurrentLimit(Constants.INTAKE_PINCHER_CONTINUOUS_LIMIT, 0);
		intakePincher.configPeakCurrentLimit(Constants.INTAKE_PINCHER_PEAK_LIMIT, 0);
		intakePincher.configPeakCurrentDuration(Constants.INTAKE_PINCHER_PEAK_DURATION, 0);
		intakePincher.enableCurrentLimit(Constants.INTAKE_PINCHER_ENABLE_CURRENT_LIMITING);
		
		//Setup the intake's ramp rates @TODO ADD THE OTHER MOTORS TOO
		intakePincher.configOpenloopRamp(Constants.INTAKE_PINCHER_RAMP_RATE, 0);
		
		//Setup the limit switches to reset the motors
		intakePincher.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0, 10);
		//intakePincher.pid
	}
}

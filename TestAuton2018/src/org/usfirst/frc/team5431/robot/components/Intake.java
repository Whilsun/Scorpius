package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Intake {
	private final WPI_TalonSRX intakeLeft, intakeRight, intakeUpLeft, intakeUpRight;
	private final DigitalInput intakeStop, cubeDetector;

	private final SpeedControllerGroup intake, intakeUp;
	private final Titan.Pot potent;

	public Intake() {
		intakeLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_LEFT_ID);
		intakeRight = new WPI_TalonSRX(Constants.TALON_INTAKE_RIGHT_ID);
		intakeUpLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_LEFT_ID);
		intakeUpRight = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_RIGHT_ID);

		// Setup the brake modes on the intake
		intakeLeft.setNeutralMode(NeutralMode.Brake);
		intakeRight.setNeutralMode(NeutralMode.Brake);
		intakeUpLeft.setNeutralMode(NeutralMode.Brake);
		intakeUpRight.setNeutralMode(NeutralMode.Brake);

		// Set the intake's inversion options
		intakeLeft.setInverted(Constants.TALON_INTAKE_LEFT_INVERTED);
		intakeRight.setInverted(Constants.TALON_INTAKE_RIGHT_INVERTED);
		intakeUpLeft.setInverted(Constants.TALON_INTAKE_UP_LEFT_INVERTED);
		intakeUpRight.setInverted(Constants.TALON_INTAKE_UP_RIGHT_INVERTED);

		intake = new SpeedControllerGroup(intakeLeft, intakeRight);
		intakeUp = new SpeedControllerGroup(intakeUpLeft, intakeUpRight);

		// intakeUpRight.set(0.0);

		// intakeUpRight.set(ControlMode.PercentOutput, 0.0);

		intakeStop = new DigitalInput(0);
		cubeDetector = new DigitalInput(1);

		potent = new Titan.Pot(0);
		potent.setMinPotValue(0.0);
		potent.setMaxPotValue(5.0);
		potent.setMinAngle(0.0);
		potent.setMaxAngle(270.0);
	}

	public boolean hasCube() {
		return !cubeDetector.get();
	}

	public boolean isDown() {
		return intakeStop.get();
	}

	public double getUpPos() {
		return potent.getAngle();
	}

	public void setUpSpeed(final double speed) {
		intakeUp.set(speed);
	}

	public void stopUp() {
		setUpSpeed(0.0);
	}

	public void setIntakeSpeed(final double speed) {
		intake.set(speed);
	}

	public void stopIntake() {
		setIntakeSpeed(0.0);
	}

	public void update(final Robot robot) {
		SmartDashboard.putBoolean("IntakeDown", intakeStop.get());
		SmartDashboard.putBoolean("HasCube", hasCube());
		SmartDashboard.putNumber("UpPot", getUpPos());
		SmartDashboard.putNumber("IntakeLeftVolts", intakeLeft.getMotorOutputVoltage());
		if (isDown()) {
			stopUp();
			potent.resetAngle();
		}
	}
}

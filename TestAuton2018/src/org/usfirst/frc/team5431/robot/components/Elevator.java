package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class Elevator {
	private final WPI_TalonSRX intakeLeft, intakeRight, intakeUpFrontLeft, intakeUpBackLeft, intakeUpFrontRight, intakeUpBackRight;
	private final DigitalInput intakeStop, cubeDetector;

	private final SpeedControllerGroup intake, intakeUp;
	private final Titan.Lidar lidar;
	
	private double wantedDistance = -1.0;
	
	public Elevator() {
		intakeLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_LEFT_ID);
		intakeRight = new WPI_TalonSRX(Constants.TALON_INTAKE_RIGHT_ID);
		intakeUpFrontLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_FRONT_LEFT_ID);
		intakeUpFrontRight = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_FRONT_RIGHT_ID);
		intakeUpBackLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_BACK_LEFT_ID);
		intakeUpBackRight = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_BACK_RIGHT_ID);

		// Setup the brake modes on the intake
		intakeLeft.setNeutralMode(NeutralMode.Brake);
		intakeRight.setNeutralMode(NeutralMode.Brake);
		intakeUpFrontLeft.setNeutralMode(NeutralMode.Brake);
		intakeUpFrontRight.setNeutralMode(NeutralMode.Brake);
		intakeUpBackLeft.setNeutralMode(NeutralMode.Brake);
		intakeUpBackRight.setNeutralMode(NeutralMode.Brake);

		// Set the intake's inversion options
		intakeLeft.setInverted(Constants.TALON_INTAKE_LEFT_INVERTED);
		intakeRight.setInverted(Constants.TALON_INTAKE_RIGHT_INVERTED);
		intakeUpFrontLeft.setInverted(Constants.TALON_INTAKE_UP_FRONT_LEFT_INVERTED);
		intakeUpFrontRight.setInverted(Constants.TALON_INTAKE_UP_FRONT_RIGHT_INVERTED);
		intakeUpBackLeft.setInverted(Constants.TALON_INTAKE_UP_BACK_LEFT_INVERTED);
		intakeUpBackRight.setInverted(Constants.TALON_INTAKE_UP_BACK_RIGHT_INVERTED);

		intake = new SpeedControllerGroup(intakeLeft, intakeRight);
		intakeUp = new SpeedControllerGroup(intakeUpFrontLeft, intakeUpFrontRight, intakeUpBackLeft, intakeUpBackRight);

		// intakeUpRight.set(0.0);

		// intakeUpRight.set(ControlMode.PercentOutput, 0.0);

		intakeStop = new DigitalInput(0);
		cubeDetector = new DigitalInput(1);

		lidar = new Titan.Lidar(0);
	}

	public boolean hasCube() {
		return !cubeDetector.get();
	}

	public boolean isDown() {
		return !intakeStop.get();
	}

	public double getUpPos() {
		return lidar.getDistance();
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
		setIntakeSpeed(0.08);
	}
	
	public void setWantedDistance(final double dis) {
		wantedDistance = dis;
	}
	
	public void resetWantedDistance() {
		wantedDistance = -1;
	}

	public void update(final Robot robot) {
		SmartDashboard.putBoolean("IntakeDown", intakeStop.get());
		SmartDashboard.putBoolean("HasCube", hasCube());
		SmartDashboard.putNumber("UpPot", getUpPos());
		SmartDashboard.putNumber("IntakeLeftVolts", intakeLeft.getMotorOutputVoltage());
		
		if(wantedDistance >= 0) {
			final double distance = lidar.getDistance();
			if(!Titan.approxEquals(distance, wantedDistance, 0.1)) {
				if(wantedDistance > distance) {
					setUpSpeed(0.5);
				}else if(wantedDistance < distance) {
					setUpSpeed(-0.5);
				}
			}
		}
	}
}

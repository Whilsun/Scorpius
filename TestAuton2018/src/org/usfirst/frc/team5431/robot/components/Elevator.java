package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

public class Elevator {
	public static enum ControlMode {
		AUTO, MANUAL;
	}

	private final WPI_TalonSRX elevatorLeft, elevatorRight;

	private final SpeedControllerGroup elevator;
	private final Titan.Lidar lidar;
	
	private ControlMode mode = ControlMode.MANUAL;
	private double wantedHeight = -1; // only affects AUTO mode
	private double iRate = 0.35;
	private double increaseRate = 0.025;

	public Elevator() {
		elevatorLeft = new WPI_TalonSRX(Constants.TALON_ELEVATOR_LEFT_ID);
		elevatorRight = new WPI_TalonSRX(Constants.TALON_ELEVATOR_RIGHT_ID);

		// Setup the brake modes on the intake
		elevatorLeft.setNeutralMode(NeutralMode.Brake);
		elevatorRight.setNeutralMode(NeutralMode.Brake);

		// Set the intake's inversion options
		
		elevatorLeft.setInverted(Constants.TALON_ELEVATOR_LEFT_INVERTED);
		elevatorRight.setInverted(Constants.TALON_ELEVATOR_RIGHT_INVERTED);
		
		elevatorLeft.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		elevatorLeft.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);

		elevatorRight.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, elevatorLeft.getDeviceID(), 0);
		elevatorRight.configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX, LimitSwitchNormal.NormallyOpen, elevatorLeft.getDeviceID(), 0);
		
		elevator = new SpeedControllerGroup(elevatorLeft, elevatorRight);

		lidar = new Titan.Lidar(Constants.LIDAR_PORT);
	}

	public void saveElevator(final Robot robot) {
		final double pitch = Math.abs(robot.getDriveBase().getNavx().getPitch());
		final double roll = Math.abs(robot.getDriveBase().getNavx().getRoll());
		if((pitch > Constants.SAVE_ELEVATOR_ANGLE || roll > Constants.SAVE_ELEVATOR_ANGLE) && !isAtBottom()) {
			Titan.e("SAVING THE ELEVATOR! THE CURRENT ANGLES ARE PITCH: %.2f AND ROLL %.2f", pitch, roll);
			robot.getElevator().goToBottom();
		}
	}
	
	public double getUpPos() {
		return lidar.getDistance();
	}

	public void setUpSpeed(final double speed) {
		if(speed <= 0 && getUpPos() <= Constants.HEIGHT_LOWEST) {
			return;
		}
		elevator.set(speed);
	}

	public void stopUp() {
		setUpSpeed(Constants.ELEVATOR_STOPPED_SPEED);
	}

	public void setWantedHeight(final double dis, final boolean override) {
		if(override) {
			iRate = 1.0;
		} else {
			iRate = 0.35;
		}
		
		setMode(ControlMode.AUTO);
		wantedHeight = dis;
	}
	
	public void setWantedHeight(final double dis) {
		setWantedHeight(dis, false);
	}

	public ControlMode getMode() {
		return mode;
	}

	public void setMode(ControlMode mode) {
		this.mode = mode;
	}

	public void goToTop() {
		setWantedHeight(Constants.ELEVATOR_TOP_HEIGHT);
	}

	public void goToBottom() {
		setWantedHeight(Constants.HEIGHT_CUBE);
	}
	
	public boolean isAtBottom() {
		return Titan.approxEquals(lidar.getDistance(), Constants.HEIGHT_CUBE, Constants.LIDAR_HEIGHT_EPSILON);
	}

	public void resetWantedHeight() {
		wantedHeight = -1;
	}

	public boolean isAtWantedHeight() {
		return Titan.approxEquals(lidar.getDistance(), wantedHeight, Constants.LIDAR_HEIGHT_EPSILON);
	}

	public void update(final Robot robot) {
		SmartDashboard.putNumber("ElevatorHeight", getUpPos());
		SmartDashboard.putNumber("WantedDistance", wantedHeight);
		SmartDashboard.putString("ControlMode", mode.name());
		SmartDashboard.putNumber("ElevatorAmp", elevatorLeft.getOutputCurrent());

		if (mode == ControlMode.AUTO) {
			if (wantedHeight >= 0) {
				final double distance = lidar.getDistance();
				final double velocity = ((Math.abs(distance - wantedHeight) * Constants.ELEVATOR_SPEED_MULTIPLIER)
						+ Constants.ELEVATOR_SPEED_CONSTANT) * iRate;

				if(iRate < 1.0) {
					iRate += increaseRate;
				}
				
				if (isAtWantedHeight()) {
					//resetWantedHeight();
					//stopUp();
				} else if (wantedHeight > distance) {
					setUpSpeed(velocity);
				} else if (wantedHeight < distance) {
					setUpSpeed(Constants.ELEVATOR_DOWN_SPEED);
				}
			}
		}
	}
}

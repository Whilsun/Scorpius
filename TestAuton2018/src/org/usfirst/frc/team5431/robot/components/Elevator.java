package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;

public class Elevator {
	public static enum ControlMode {
		AUTO, MANUAL, LIMIT_TOP;
	}

	private final WPI_TalonSRX elevatorLeft, elevatorRight;

	private final SpeedControllerGroup elevator;

	private ControlMode mode = ControlMode.MANUAL;
	private double wantedHeight = -1; // only affects AUTO mode
	private double iRate = 0.35;
	private double increaseRate = 0.025;

	private Titan.Lidar lidar;

	public Elevator() {
		elevatorLeft = new WPI_TalonSRX(Constants.TALON_ELEVATOR_LEFT_ID);
		elevatorRight = new WPI_TalonSRX(Constants.TALON_ELEVATOR_RIGHT_ID);

		// Setup the brake modes on the intake
		elevatorLeft.setNeutralMode(NeutralMode.Brake);
		elevatorRight.setNeutralMode(NeutralMode.Brake);

		// Set the intake's inversion options

		elevatorLeft.setInverted(Constants.TALON_ELEVATOR_LEFT_INVERTED);
		elevatorRight.setInverted(Constants.TALON_ELEVATOR_RIGHT_INVERTED);

		// NEVER UNCOMMENT THIS CODE BECAUSE THE PERSISTENT SETTINGS ARE GOOD
		elevatorRight.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		elevatorRight.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);

		elevatorLeft.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX,
				LimitSwitchNormal.NormallyOpen, elevatorRight.getDeviceID(), 0);
		elevatorLeft.configReverseLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX,
				LimitSwitchNormal.NormallyOpen, elevatorRight.getDeviceID(), 0);

		elevator = new SpeedControllerGroup(elevatorLeft, elevatorRight);

		lidar = new Titan.Lidar(Constants.LIDAR_PORT);
	}

	public void saveElevator(final Robot robot) {
		final double pitch = Math.abs(robot.getDriveBase().getNavx().getPitch());
		final double roll = Math.abs(robot.getDriveBase().getNavx().getRoll());
		if ((pitch > Constants.SAVE_ELEVATOR_ANGLE || roll > Constants.SAVE_ELEVATOR_ANGLE) && !isAtBottom()) {
			Titan.e("SAVING THE ELEVATOR! THE CURRENT ANGLES ARE PITCH: %.2f AND ROLL %.2f", pitch, roll);
			robot.getElevator().goToBottom();
		}
	}

	public double getUpPos() {
		return lidar.getDistance();
	}

	public void setUpSpeed(final double speed, final Robot robot) {
		if (speed >= 0 && robot.getIntake().getTiltPosition() >= Constants.HALLFX_UP_POS) {
			elevator.set(0.0);
			return;
		}
		elevator.set(speed);
	}

	public void stopUp(final Robot robot) {
		setUpSpeed(Constants.ELEVATOR_STOPPED_SPEED, robot);
	}

	public void setWantedHeight(final double dis, final boolean override) {
		if (override) {
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
		// mode = ControlMode.LIMIT_TOP;
	}

	public void goToBottom() {
		setWantedHeight(Constants.HEIGHT_CUBE);
	}

	public boolean isAtBottom() {
		return elevatorRight.getSensorCollection().isRevLimitSwitchClosed();
	}

	public boolean isAtTop() {
		return elevatorRight.getSensorCollection().isFwdLimitSwitchClosed();
	}

	public void resetWantedHeight() {
		wantedHeight = -1;
	}

	public boolean isAtWantedHeight() {
		return Titan.approxEquals(lidar.getDistance(), wantedHeight, Constants.LIDAR_HEIGHT_EPSILON);
		// return false;
	}

	public void update(final Robot robot) {
		SmartDashboard.putNumber("ElevatorHeight", getUpPos());
		SmartDashboard.putNumber("WantedDistance", wantedHeight);
		SmartDashboard.putString("ControlMode", mode.name());
		SmartDashboard.putNumber("ElevatorAmp", elevatorLeft.getOutputCurrent());
		SmartDashboard.putBoolean("IsAtTop", isAtTop());
		SmartDashboard.putBoolean("IsAtBottom", isAtBottom());

		if (mode == ControlMode.AUTO) {
			if (wantedHeight >= 0) {
				final double distance = lidar.getDistance();
				final double velocity = ((Math.abs(distance - wantedHeight) * Constants.ELEVATOR_SPEED_MULTIPLIER)
						+ Constants.ELEVATOR_SPEED_CONSTANT) * iRate;

				if (iRate < 1.0) {
					iRate += increaseRate;
				}

				if (isAtWantedHeight()) {
					resetWantedHeight();
					stopUp(robot);
				} else {
					setUpSpeed(velocity, robot);
				}
			}
		} else if (mode == ControlMode.LIMIT_TOP) {
			if (!isAtTop())
				setUpSpeed(1.0, robot);
		}
	}
}

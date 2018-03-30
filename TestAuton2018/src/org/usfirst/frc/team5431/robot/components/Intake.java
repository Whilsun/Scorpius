package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.components.Elevator.ControlMode;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Intake {
	public static enum ControlMode {
		AUTO, MANUAL;
	}
	
	private final WPI_TalonSRX intake, intakeTilt;
	private double tiltSpeed = 0.0;
	private final Titan.SeatMotor intakeHall;
	
	private ControlMode mode = ControlMode.MANUAL;
	private double wantedTilt = -1;
	
	public Intake() {
		intake = new WPI_TalonSRX(Constants.TALON_INTAKE_ID);
		intakeTilt = new WPI_TalonSRX(Constants.TALON_INTAKE_TILT_ID);
		
		intake.setNeutralMode(NeutralMode.Brake);
		intakeTilt.setNeutralMode(NeutralMode.Brake);
		
		intake.setInverted(Constants.TALON_INTAKE_INVERTED);
		intakeTilt.setInverted(Constants.TALON_INTAKE_TILT_INVERTED);
		
		intakeHall = new Titan.SeatMotor(intakeTilt, Constants.HALLFX_PORT);
		intakeHall.setHalInverted(Constants.HALLFX_INVERTED);
	}
	
	public boolean hasCube() {
		return intake.getOutputCurrent() > Constants.LIDAR_CUBE_AMPS;
	}
	
	public double getTiltPosition() {
		return intakeHall.getPosition();
	}
	
	public double getTiltCurrent() {
		return intakeTilt.getOutputCurrent();
	}
	
	public void setTiltSpeed(final double speed) {
		tiltSpeed = speed;
	}
	
	public void stopTilting() {
		setTiltSpeed(0.0);
	}
	
	public void setWantedTilt(final double pos) {
		setMode(ControlMode.AUTO);
		wantedTilt = pos;
	}

	public ControlMode getMode() {
		return mode;
	}

	public void setMode(ControlMode mode) {
		this.mode = mode;
	}
	
	public void goToTop() {
		setWantedTilt(Constants.HALLFX_UP_POS);
	}

	public void goToBottom() {
		setWantedTilt(Constants.HALLFX_DOWN_POS);
	}
	
	public void resetWantedTilt() {
		wantedTilt = -1;
	}

	public boolean isAtWantedTilt() {
		return Titan.approxEquals(getTiltPosition(), wantedTilt, Constants.HALLFX_POS_EPSILON);
	}

	public void setIntakeSpeed(final double speed) {
		intake.set(speed);
	}

	public void intake() {
		setIntakeSpeed(Constants.INTAKE_SPEED);
	}

	public void outtake() {
		setIntakeSpeed(Constants.OUTTAKE_SPEED);
	}

	public void stopIntake() {
		setIntakeSpeed(Constants.INTAKE_STOPPED_SPEED);
	}
	
	public void setHomeUp() {
		intakeHall.setHome(Constants.HALLFX_AUTON_POS, 2.0);
	}
	
	public void setHomeDown() {
		intakeHall.setHome(Constants.HALLFX_AUTON_POS, -2.0);
	}
	
	public void update(final Robot robot) {
		SmartDashboard.putBoolean("HasCube", hasCube());
		SmartDashboard.putNumber("IntakeAmperage", intake.getOutputCurrent());
		SmartDashboard.putNumber("IntakeTiltAngle", getTiltPosition());
		SmartDashboard.putNumber("IntakeTiltCurrent", getTiltCurrent());
		SmartDashboard.putBoolean("IntakeRunning", intake.get() >= 0.5);
		
		if (mode == ControlMode.AUTO) {
			if (wantedTilt >= 0) {
				if (isAtWantedTilt()) {
					resetWantedTilt();
					stopTilting();
				} else if (wantedTilt > getTiltPosition()) {
					setTiltSpeed(Constants.INTAKE_TILT_SPEED);
				} else if (wantedTilt < getTiltPosition()) {
					setTiltSpeed(-Constants.INTAKE_TILT_SPEED);
				}
			}
		}
		
		if(getTiltCurrent() > Constants.RESET_AMPERAGE && tiltSpeed < 0) {
			intakeHall.set(0.0);
			intakeHall.reset();
		} else if(getTiltCurrent() < Constants.MAX_AMPERAGE) {
			intakeHall.set(tiltSpeed); //Update the seat motor
		} else {
			intakeHall.set(0.0);
		}
	}
}

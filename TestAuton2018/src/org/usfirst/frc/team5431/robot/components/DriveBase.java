package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;

public class DriveBase {
	public class DriveBasePIDSource implements PIDSource {
		PIDSourceType filler = PIDSourceType.kDisplacement;

		@Override
		public void setPIDSourceType(PIDSourceType pidSource) {
			filler = pidSource;
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return filler;
		}

		@Override
		public double pidGet() {
			if (pidType == PIDType.DRIVE_FORWARD) {
				return leftEncoder.getDistance();
			} else if (pidType == PIDType.TURN) {
				return navx.getAngle();
			} else {
				return 0.0;
			}
		}
	}

	public class DriveBasePIDOutput implements PIDOutput {
		public double wantedPower = 0.0;
		public double wantedAngle = 0.0, stallPower = 0.22;

		@Override
		public void pidWrite(double output) {
			output = -output;

			if (pidType == PIDType.DRIVE_FORWARD) {
				if (output > 0) {
					drive(wantedPower, wantedPower - output);
				} else {
					drive(wantedPower + output, wantedPower);
				}
			} else if (pidType == PIDType.TURN) {
				SmartDashboard.putNumber("TURN_PID", output);
				if (wantedAngle < 0) {
					drive(-output, output);
				} else {
					drive(output, -output);
				}

			} else if (pidType == PIDType.NONE) {
				return;
			}
		}
	}

	public enum PIDType {
		DRIVE_FORWARD, TURN, NONE;
	}

	private final WPI_TalonSRX frontLeft, frontRight, middleLeft, middleRight, backLeft, backRight;
	private final Encoder leftEncoder, rightEncoder;
	private final Titan.NavX navx = new Titan.NavX();

	private PIDType pidType = PIDType.NONE;
	private final DriveBasePIDSource pidSource = new DriveBasePIDSource();
	private final DriveBasePIDOutput pidOutput = new DriveBasePIDOutput();
	private final PIDController drivePID = new PIDController(0, 0, 0, 0, pidSource, pidOutput);

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

		drivePID.setInputRange(-90, 90);
		drivePID.setOutputRange(-1, 1);
		drivePID.setAbsoluteTolerance(0.5);
		drivePID.setContinuous(true);
		disablePID();
	}

	public void enablePID() {
		drivePID.enable();
	}

	public void disablePID() {
		pidType = PIDType.NONE;
		drivePID.disable();
	}

	public void drive(final double left, final double right) {
		frontLeft.set(left);
		frontRight.set(right);

		SmartDashboard.putNumber("left encoder", leftEncoder.getDistance());
		SmartDashboard.putNumber("right encoder", rightEncoder.getDistance());
		SmartDashboard.putNumber("navx", navx.getAngle());
		SmartDashboard.putNumber("yaw", navx.getYaw());
	}

	public void drivePID(final double power, final double distance) {
		drivePID.reset();
		drivePID.setSetpoint(0);
		drivePID.setPID(Constants.DRIVE_P, Constants.DRIVE_I, Constants.DRIVE_D, 0.0);
		pidType = PIDType.DRIVE_FORWARD;
		pidOutput.wantedPower = power;
		drivePID.setInputRange(-90, 90);
		drivePID.setOutputRange(-1, 1);
		drivePID.setAbsoluteTolerance(0.5);
		drivePID.setContinuous(true);
		drivePID.setSetpoint(distance);
		enablePID();
	}
	
	public void turnPID(final double power, final double angle) {
		drivePID.reset();
		drivePID.setSetpoint(0);
		drivePID.setPID(Constants.TURN_P, Constants.TURN_I, Constants.TURN_D, 0.0);
		pidType = PIDType.TURN;
		pidOutput.wantedPower = power;
		drivePID.setInputRange(-90, 90);
		drivePID.setOutputRange(-0.239, 0.239);
		drivePID.setAbsoluteTolerance(0.5);
		drivePID.setContinuous(true);
		drivePID.setSetpoint(angle);
		enablePID();
	}

	public Encoder getLeftEncoder() {
		return leftEncoder;
	}

	public Encoder getRightEncoder() {
		return rightEncoder;
	}

	public void resetNavx() {
		navx.reset();
		navx.resetDisplacement();
		navx.resetYaw();
	}

	public void resetEncoders() {
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public void reset() {
		resetNavx();
		resetEncoders();
	}

	public Titan.NavX getNavx() {
		return navx;
	}
}

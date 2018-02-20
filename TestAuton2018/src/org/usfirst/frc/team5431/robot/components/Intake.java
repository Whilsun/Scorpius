package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;

public class Intake {
	public enum IntakeState {
		CAPTURE, SHOOT, SHOOT_PROGRESS, STAY_DOWN, DOWN_RELEASE, STAY_UP;
	}
	
	private final WPI_TalonSRX intakeLeft, intakeRight, intakePincher, intakeUpLeft, intakeUpRight;
	private final DigitalInput intakeStop, cubeDetector;
	private IntakeState state = IntakeState.STAY_UP;
	private boolean captureComplete = false;
	private long cubeStart = 0;
	private long cubeShoot = 0;
	private long moveStart = 0;

	private SpeedControllerGroup intake;

	public Intake() {
		intakeLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_LEFT_ID);
		intakeRight = new WPI_TalonSRX(Constants.TALON_INTAKE_RIGHT_ID);
		intakePincher = new WPI_TalonSRX(Constants.TALON_INTAKE_PINCHER_ID);
		intakeUpLeft = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_LEFT_ID);
		intakeUpRight = new WPI_TalonSRX(Constants.TALON_INTAKE_UP_RIGHT_ID);

		// Setup the brake modes on the intake
		intakeLeft.setNeutralMode(NeutralMode.Brake);
		intakeRight.setNeutralMode(NeutralMode.Brake);
		intakePincher.setNeutralMode(NeutralMode.Brake);
		intakeUpLeft.setNeutralMode(NeutralMode.Brake);
		intakeUpRight.setNeutralMode(NeutralMode.Brake);

		// Set the intake's inversion options
		intakeLeft.setInverted(Constants.TALON_INTAKE_LEFT_INVERTED);
		intakeRight.setInverted(Constants.TALON_INTAKE_RIGHT_INVERTED);
		intakePincher.setInverted(Constants.TALON_INTAKE_PINCHER_INVERTED);
		intakeUpLeft.setInverted(Constants.TALON_INTAKE_UP_LEFT_INVERTED);
		intakeUpRight.setInverted(Constants.TALON_INTAKE_UP_RIGHT_INVERTED);

		intake = new SpeedControllerGroup(intakeLeft, intakeRight);

		// Setup the pincher encoder

		// intakePincher.confi
		/*
		 * intakePincher.setSelectedSensorPosition(0, 0, 0);
		 * intakePincher.setSensorPhase(Constants.ENCODER_PINCHER_INVERTED); //Invert
		 * the sensor
		 * 
		 * //Setup the pincher limits
		 * intakePincher.configForwardSoftLimitThreshold(Constants.
		 * ENCODER_PINCHER_FORWARD_LIMIT, 0);
		 * //intakePincher.configReverseSoftLimitThreshold(Constants.
		 * ENCODER_PINCHER_REVERSE_LIMIT, 0);
		 * intakePincher.configForwardSoftLimitEnable(Constants.
		 * ENCODER_PINCHER_FORWARD_LIMIT_ENABLED, 0);
		 * //intakePincher.configReverseSoftLimitEnable(Constants.
		 * ENCODER_PINCHER_REVERSE_LIMIT_ENABLED, 0);
		 * 
		 * //Setup the limit switches
		 * intakePincher.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated,
		 * LimitSwitchNormal.NormallyOpen, 0);
		 * intakePincher.configReverseLimitSwitchSource(LimitSwitchSource.
		 * FeedbackConnector, LimitSwitchNormal.NormallyOpen, 0);
		 */

		intakePincher.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		// intakePincher.setSelectedSensorPosition(0, 0, 0);
		intakePincher.setSensorPhase(true);
		intakePincher.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
				LimitSwitchNormal.NormallyOpen, 0);
		intakePincher.configReverseSoftLimitThreshold(-800, 0);
		intakePincher.configReverseSoftLimitEnable(true, 0);

		// Setup the intake's current limiting
		intakePincher.configContinuousCurrentLimit(Constants.INTAKE_PINCHER_CONTINUOUS_LIMIT, 0);
		intakePincher.configPeakCurrentLimit(Constants.INTAKE_PINCHER_PEAK_LIMIT, 0);
		intakePincher.configPeakCurrentDuration(Constants.INTAKE_PINCHER_PEAK_DURATION, 0);
		intakePincher.enableCurrentLimit(Constants.INTAKE_PINCHER_ENABLE_CURRENT_LIMITING);

		// Setup the intake's ramp rates @TODO ADD THE OTHER MOTORS TOO
		intakePincher.configOpenloopRamp(Constants.INTAKE_PINCHER_RAMP_RATE, 0);

		// Setup the limit switches to reset the motors
		intakePincher.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0, 0);
		// intakePincher.configSetParameter(ParamEnum.eClearPositionOnLimitR, 1, 0, 0,
		// 10);

		/*
		 * pincherEncoder = new Encoder(1, 2, false, EncodingType.k4X);
		 * pincherEncoder.setSamplesToAverage(1);
		 * pincherEncoder.setReverseDirection(false);
		 */

		intakeUpRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		// intakeUpRight.configForwardSoftLimitThreshold(0,
		// Constants.ENCODER_INTAKE_UP_POSITION + 5);
		// intakeUpRight.configForwardSoftLimitEnable(true, 0);
		// intakeUpRight.getSensorCollection().setQuadraturePosition(Constants.ENCODER_INTAKE_UP_POSITION,
		// 0);
		intakeUpRight.configForwardSoftLimitEnable(false, 0);
		intakeUpRight.configReverseSoftLimitEnable(false, 0);
		intakeUpRight.getSensorCollection().setQuadraturePosition(0, 0);

		intakeUpLeft.configForwardSoftLimitEnable(false, 0);
		intakeUpLeft.configReverseSoftLimitEnable(false, 0);

		intakeUpRight.configContinuousCurrentLimit(Constants.INTAKE_PINCHER_CONTINUOUS_LIMIT, 0);
		intakeUpRight.configPeakCurrentLimit(Constants.INTAKE_PINCHER_PEAK_LIMIT, 0);
		intakeUpRight.configPeakCurrentDuration(Constants.INTAKE_PINCHER_PEAK_DURATION, 0);
		intakeUpRight.enableCurrentLimit(Constants.INTAKE_PINCHER_ENABLE_CURRENT_LIMITING);

		intakeUpLeft.configContinuousCurrentLimit(Constants.INTAKE_PINCHER_CONTINUOUS_LIMIT, 0);
		intakeUpLeft.configPeakCurrentLimit(Constants.INTAKE_PINCHER_PEAK_LIMIT, 0);
		intakeUpLeft.configPeakCurrentDuration(Constants.INTAKE_PINCHER_PEAK_DURATION, 0);
		intakeUpLeft.enableCurrentLimit(Constants.INTAKE_PINCHER_ENABLE_CURRENT_LIMITING);

		// intakeUpRight.set(0.0);

		// intakeUpRight.set(ControlMode.PercentOutput, 0.0);

		intakeStop = new DigitalInput(0);
		cubeDetector = new DigitalInput(3);
		
		if(isDown()) {
			intakeUpRight.getSensorCollection().setQuadraturePosition(0, 0);
		} else {
			intakeUpRight.getSensorCollection().setQuadraturePosition(Constants.ENCODER_INTAKE_UP_POSITION, 0);
		}
		
	}

	public boolean isClosed() {
		return intakePincher.getSensorCollection().isFwdLimitSwitchClosed();
	}

	public boolean hasCube() {
		return !cubeDetector.get();
	}

	public boolean isDown() {
		return !intakeStop.get();
	}

	public double getUpPos() {
		return intakeUpRight.getSensorCollection().getQuadraturePosition();
	}

	public boolean isUp() {
		return Math.abs(getUpPos() - Constants.ENCODER_INTAKE_UP_POSITION) < 50;
	}

	public boolean isBelowShootSafe() {
		return getUpPos() < Constants.ENCODER_INTAKE_SAFE_SHOOT_POSITION;
	}
	
	public boolean isBelowSwitch() {
		return getUpPos() < Constants.ENCODER_INTAKE_SWITCH_POSITION;
	}

	public boolean isAboveSwitch() {
		return getUpPos() > Constants.ENCODER_INTAKE_SWITCH_POSITION;
	}

	public boolean isSwitch() {
		return Math.abs(getUpPos() - Constants.ENCODER_INTAKE_SWITCH_POSITION) < 300;
	}

	public void setUpPosition() {
		if(!isDown()) {
			intakeUpRight.getSensorCollection().setQuadraturePosition(Constants.ENCODER_AUTONOMOUS_START_POSITION, 0);
		} else {
			intakeUpRight.getSensorCollection().setQuadraturePosition(0, 0);
		}
	}

	public void setUpSpeed(final double speed) {
		intakeUpLeft.set(speed);
		intakeUpRight.set(speed);
	}

	public void intakeFast() {
		intake.set(0.65);
	}

	public void intakeSlow() {
		intake.set(0.175);
	}

	public void intakeHold() {
		intake.set(0.13);
	}

	public void intakeReverse() {
		intake.set(-0.6);
	}

	public void intakeShoot() {
		intake.set(-0.9);
	}

	public void intakeStop() {
		intake.set(0.0);
	}

	public void pinch() {
		pinch(-0.4);
	}

	public void pinchHold() {
		pinch(-0.14);
	}

	public void pinchSoft() {
		pinch(-0.40);
	}

	public void pinchHard() {
		pinch(-0.45);
	}

	public void pinch(final double value) {
		// if(!isClosed()) intakePincher.set(value);
		// else pincherEncoder.reset();
		intakePincher.set(value);
	}

	public void release() {
		// if(isClosed()) pincherEncoder.reset();
		// if(pincherEncoder.getRaw() <= 800) intakePincher.set(0.2);
		// else pinch(0.05);
		intakePincher.set(0.25);
	}

	public void stopPinch() {
		intakePincher.set(0.0);
	}
	
	public IntakeState getState() {
		return state;
	}

	public void goTo(final int pos, final double divisor, final double stall) {
		double disp = (double) ((long) (((pos - getUpPos()) / (Constants.ENCODER_INTAKE_UP_POSITION * divisor))
				* 1000.0)) / 1000.0;
		disp = (disp > (-stall) && disp < 0.0) ? (-stall) : disp;
		disp = (disp < stall && disp > 0.0) ? stall : disp;
		setUpSpeed(disp);
		SmartDashboard.putNumber("Intake Displacement", disp);
		/*
		 * if(getUpPos() > (pos + 5) && getUpPos() < (pos + 80)) { setUpSpeed(-stall); }
		 * else if(getUpPos() < (pos - 5) && getUpPos() > (pos - 80)) {
		 * setUpSpeed(stall); } else { setUpSpeed(disp); }
		 */
	}

	public void goUp() {
		if(!isUp()) {
			if(isBelowSwitch()) {
				goTo(Constants.ENCODER_INTAKE_UP_POSITION, 1.25, 0.1);
			} else {
				goTo(Constants.ENCODER_INTAKE_UP_POSITION, 0.45, 0.1);
			}
		} else {
			stopUp();
		}
	}
	
	public void goShootSafe() {
		if(!isBelowShootSafe()) {
			goTo(Constants.ENCODER_INTAKE_SAFE_SHOOT_POSITION, 0.45, 0.025);
		}
	}

	public void goDown() {
		if (!isDown()) {
			goTo(-1000, 2.6, 0.1);
		} else {
			stopUp();
		}
	}

	public void goSwitch() {
		if (isBelowSwitch()) {
			goTo(Constants.ENCODER_INTAKE_SWITCH_POSITION, 1.25, 0.025);
		} else {
			goTo(Constants.ENCODER_INTAKE_SWITCH_POSITION, 0.45, 0.135);
		}
	}

	public void stopUp() {
		intakeUpLeft.set(0.0);
		intakeUpRight.set(0.0);
	}

	public void captureCube() {
		state = IntakeState.CAPTURE;
		
		if(!isDown() && !isUp()) {
			cubeStart = 1000;
		} else {
			cubeStart = 0;
		}
	}

	public void stayUp() {
		state = IntakeState.STAY_UP;
	}
	
	public void stayDown() {
		state = IntakeState.STAY_DOWN;
	}
	
	public void downRelease() {
		state = IntakeState.DOWN_RELEASE;
	}
	
	public boolean isCaptured() {
		return captureComplete;
	}

	public void shootCube() {
		captureComplete = false;
		state = IntakeState.SHOOT;
		
		if(!isSwitch()) cubeShoot = 0;
		if(!isDown() && !isUp()) {
			cubeStart = 1000;
		} else {
			cubeStart = 0;
		}
	}

	public void update(final Robot robot) {
		SmartDashboard.putBoolean("IsIntakeClosed", isClosed());
		SmartDashboard.putBoolean("HasCube", hasCube());
		SmartDashboard.putBoolean("AtSwitch", isSwitch());
		SmartDashboard.putNumber("UpEncoder", getUpPos());
		if (isDown())
			intakeUpRight.getSensorCollection().setQuadraturePosition(0, 0);

		switch(state) {
		case SHOOT:
			if (isSwitch()) {
				moveStart = 0;
				
				pinchSoft();
				goSwitch();
				if(hasCube()) {
					intakeShoot();
				} else {
					state = IntakeState.SHOOT_PROGRESS;
					cubeShoot = System.currentTimeMillis();
					intakeStop();
				}
			} else {
				//It wants to shoot the cube
				if (moveStart == 0)
					moveStart = System.currentTimeMillis();
				else if ((System.currentTimeMillis() - moveStart) > 500) {
					if (isBelowSwitch()) {
						pinchSoft();
						intakeSlow();
					} else {
						pinchHold();
						intakeHold();
					}
					goSwitch();
				} else {
					pinchHard();
					intakeFast();
					goSwitch();
				}
			}
			break;
		case SHOOT_PROGRESS:
			if((System.currentTimeMillis() - cubeShoot) > 200) {
				state = IntakeState.STAY_UP;
				goUp();
			} else {
				intakeShoot();
				pinchSoft();
				goSwitch();
			}
			break;
		case CAPTURE:
			SmartDashboard.putBoolean("SeekingCube", true);
			if (hasCube()) { // There's a cube in the proximity
				if (cubeStart == 0) {
					cubeStart = System.currentTimeMillis();
					captureComplete = false;
				}

				if ((System.currentTimeMillis() - cubeStart) < 100) { // Pinch hard and fast for 500 milliseconds
					intakeFast(); // Quickly suck the cube to the back of the intake
					pinchHard(); // Pinch harder to straigten out the cube
				} else {
					intakeSlow(); // Intake the cube so that it sits flush with the back of the intake
					pinchSoft(); // Pinch to hold the cube while it's going up

					// If the catapult isn't lowered then lower it so
					if (!robot.getCatapult().isLowered() && moveStart == 0) {
						moveStart = System.currentTimeMillis();
						robot.getCatapult().lowerCatapult();
					}

					if ((System.currentTimeMillis() - moveStart) > 100 || robot.getCatapult().isLowered()) { // Only lift the intake up when we know the
																			// catapult has had enough time to start
																			// pulling down
						goUp(); //Lift the intake up
						if(isUp()) {
							cubeStart = 0; //Reset the timer
							state = IntakeState.STAY_UP;
							captureComplete = true;
							intakeHold();
							stopUp();
						}
					}
				}
			} else {
				cubeStart = 0; // Reset the cube timer
				goDown();
				intakeFast(); // Try to intake everything
				release(); // Release the pincher
			}
			break;
		case STAY_UP:
			SmartDashboard.putBoolean("SeekingCube", false);
			cubeStart = 0; // Reset the cube start
			if (!robot.getCatapult().isLowered())
				moveStart = 0; // Reset the lower start if the catapult isn't already lowered
			goUp();
			if(isUp()) { //If it's in the catapult then hold onto the cube
				pinchHold();
				if(hasCube()) {
					intakeHold();
				} else {
					intakeStop();
				}
			} else {
				intakeStop(); // Stop rotating
				pinchSoft(); // Pinch it slowly to reset the encoder
			}
			break;
		case STAY_DOWN:
			cubeStart = 0;
			if(!isDown()) goDown();
			if(hasCube()) {
				intakeHold();
				pinchHold();
			} else {
				intakeStop();
				pinchSoft();
			}
			break;
		case DOWN_RELEASE:
			if(!isDown()) {
				if(hasCube()) {
					intakeHold();
					pinchHold();
				} else {
					intakeStop();
					pinchSoft();
				}
				goDown();
			} else {
				intakeReverse();
				pinchSoft();
			}
			break;
		}

		/*
		 * if(isDown()) { stopUp();
		 * intakeUpRight.getSensorCollection().setQuadraturePosition(0, 10); }else
		 * if(isUp()) { stopUp(); }
		 */
	}

	public WPI_TalonSRX getPincher() {
		return intakePincher;
	}

	/*
	 * public Encoder getPincherEncoder() { return pincherEncoder; }
	 */
}

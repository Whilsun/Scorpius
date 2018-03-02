package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Catapult {
	private final WPI_TalonSRX catapultLeft, catapultRight;
	private final Solenoid shooterOne, shooterTwo;
	private final Compressor compressor;
	private final AnalogInput pressureSensor;
	private boolean lowering = false;
	private boolean shooting = false;
	private long shootStart = 0;
	private int triesTaken = 0;

	public Catapult() {
		//Pull down motors
		catapultLeft = new WPI_TalonSRX(Constants.TALON_CATAPULT_LEFT_ID);
		catapultRight = new WPI_TalonSRX(Constants.TALON_CATAPULT_RIGHT_ID);

		//Compressor
		compressor = new Compressor();
		compressor.clearAllPCMStickyFaults();
		compressor.setClosedLoopControl(true);
		compressor.start();
		
		// Inverted or not
		catapultLeft.setInverted(Constants.TALON_CATAPULT_LEFT_INVERTED);
		catapultRight.setInverted(Constants.TALON_CATAPULT_RIGHT_INVERTED);
		catapultLeft.setNeutralMode(NeutralMode.Coast);
		catapultRight.setNeutralMode(NeutralMode.Coast);

		// Follower mode
		catapultRight.set(ControlMode.Follower, Constants.TALON_CATAPULT_LEFT_ID);

		// Set the light break to be normally closed
		catapultLeft.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen,
				0);
		catapultRight.configForwardLimitSwitchSource(RemoteLimitSwitchSource.RemoteTalonSRX,
				LimitSwitchNormal.NormallyOpen, Constants.TALON_CATAPULT_LEFT_ID, 0);

		// Set up the catapult's solenoids
		shooterOne = new Solenoid(1);
		shooterTwo = new Solenoid(2);

		// Pressure transducer
		pressureSensor = new AnalogInput(0);
	}

	public double getCatapultPSI() {
		return (250 * (pressureSensor.getVoltage() / 5.0)) - 25.0;
	}

	public boolean isLowered() {
		return catapultLeft.getSensorCollection().isRevLimitSwitchClosed();
	}

	public void lowerCatapult() {
		lowering = true;
		load();
	}

	public void shoot() {
		shooting = true;
		shootStart = System.currentTimeMillis();
	}

	public void load() {
		shooterOne.set(true);
		shooterTwo.set(false);
	}

	public void update(final Robot robot) {
		SmartDashboard.putNumber("CatapultPSI", getCatapultPSI());
		SmartDashboard.putBoolean("IsLoaded", isLowered());
		if (lowering) {
			if (isLowered()) {
				catapultLeft.set(0.0);
				catapultRight.set(0.0);
				lowering = false;
				return;
			}
			catapultLeft.set(Constants.CATAPULT_LOWER_SPEED);
			catapultRight.set(Constants.CATAPULT_LOWER_SPEED);
		} else {
			catapultLeft.set(0.0);
			catapultRight.set(0.0);
		}
		
		if (shooting && !lowering) {
			if(!robot.getIntake().isBelowShootSafe()) {
				robot.getIntake().shootSafe();
			} else {
				final long timeDisp = System.currentTimeMillis() - shootStart;
				shooterOne.set(false);
				shooterTwo.set(true);
				if(timeDisp >= 100 && timeDisp < 350) {
					Titan.l("Shooting...");
				} else if(timeDisp >= 350 && timeDisp < 480) {
					catapultLeft.overrideLimitSwitchesEnable(true);
					catapultRight.overrideLimitSwitchesEnable(true);
					catapultLeft.set(Constants.CATAPULT_LOWER_SPEED);
					catapultRight.set(Constants.CATAPULT_LOWER_SPEED);
				} else if(timeDisp >= 480 && timeDisp < 510) {
					catapultLeft.set(-1.0);
					catapultRight.set(-1.0);
				} else if(timeDisp >= 480) {
					if(isLowered() && triesTaken < 5) {
						shootStart = System.currentTimeMillis() - 348;
						triesTaken++;
					} else {
						triesTaken = 10; //Manually override the limit
						catapultLeft.overrideLimitSwitchesEnable(false);
						catapultRight.overrideLimitSwitchesEnable(false);
						shooterOne.set(false);
						shooterTwo.set(false);
						Titan.l("Waiting...");
						if(!isLowered()) {
							lowerCatapult();
						} else {
							shooting = false;
							triesTaken = 0; //Reset the tries taken variable
							//robot.getIntake().stayUp();
						}
					}
				}
			}
		}
	}

	public void releaseShooter() {
		// shooterOne.set(false);
		// shooterTwo.set(false);
	}
}

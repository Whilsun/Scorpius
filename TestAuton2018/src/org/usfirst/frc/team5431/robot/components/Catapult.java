package org.usfirst.frc.team5431.robot.components;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Catapult {
	private final WPI_TalonSRX catapultLeft, catapultRight;
	private final Solenoid shooterOne, shooterTwo;
	private final AnalogInput pressureSensor;
	private boolean lowering = false;
	private boolean shooting = false;
	private long shootStart = 0;

	public Catapult() {
		catapultLeft = new WPI_TalonSRX(Constants.TALON_CATAPULT_LEFT_ID);
		catapultRight = new WPI_TalonSRX(Constants.TALON_CATAPULT_RIGHT_ID);

		// Inverted or not
		catapultLeft.setInverted(Constants.TALON_CATAPULT_LEFT_INVERTED);
		catapultRight.setInverted(Constants.TALON_CATAPULT_RIGHT_INVERTED);

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
		} else if (shooting) {
			if(!robot.getIntake().isBelowShootSafe()) {
				robot.getIntake().goShootSafe(); //Go to the shoot safe position
			} else {
				final long timeDisp = System.currentTimeMillis() - shootStart;
				if(timeDisp >= 500 && timeDisp < 800) {
					shooterOne.set(false);
					shooterTwo.set(true);
				} else if(timeDisp > 800){
					shooterOne.set(false);
					shooterTwo.set(false);
					
					if(!isLowered()) {
						lowerCatapult();
					} else {
						shooting = false;
						robot.getIntake().stayUp();
					}
				}
			}
		} else {
			catapultLeft.set(0.0);
			catapultRight.set(0.0);
		}
	}

	public void releaseShooter() {
		// shooterOne.set(false);
		// shooterTwo.set(false);
	}
}

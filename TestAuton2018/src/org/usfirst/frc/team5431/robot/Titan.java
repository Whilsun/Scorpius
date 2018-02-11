package org.usfirst.frc.team5431.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;

/**
 * Namespace for TitanUtil
 */
public final class Titan {
	public static boolean DEBUG = true;
	private Titan() {
	}
	
	/* Log information */
	public static void l(String base, Object... a) {
		if(DEBUG) System.out.println(String.format(base, a));
	}
	
	/* Log error */
	public static void e(String base, Object... a) {
		if(DEBUG) System.err.println(String.format(base, a));
	}
	
	/* Exception error */
	public static void ee(String namespace, Exception e) {
		if(DEBUG) e("%s: %s", namespace, e.getMessage());
	}

	/**
	 * Custom joystick class that is identical to the WPILib version except it has
	 * deadzone management
	 */
	public static class Joystick extends edu.wpi.first.wpilibj.Joystick {
		private double deadzoneMin = 0.0f, deadzoneMax = 0.0f;

		public Joystick(final int port) {
			super(port);
		}

		public double getDeadzoneMin() {
			return deadzoneMin;
		}

		public void setDeadzoneMin(final double deadzoneMin) {
			this.deadzoneMin = deadzoneMin;
		}

		public double getDeadzoneMax() {
			return deadzoneMax;
		}

		public void setDeadzoneMax(final double deadzoneMax) {
			this.deadzoneMax = deadzoneMax;
		}

		public void setDeadzone(final double min, final double max) {
			setDeadzoneMin(min);
			setDeadzoneMax(max);
		}

		public void setDeadzone(final double deadzone) {
			setDeadzone(-deadzone, deadzone);
		}

		@Override
		public double getRawAxis(final int axis) {
			final double val = super.getRawAxis(axis);
			if (val >= deadzoneMin && val <= deadzoneMax) {
				return 0.0;
			} else {
				return val;
			}
		}
	}

	public static class FSi6S extends Titan.Joystick {
		public static enum SwitchPosition {
			DOWN, NEUTRAL, UP
		}

		public static enum Switch {
			A, B, C, D
		}

		public static enum Axis {
			RIGHT_X, RIGHT_Y, LEFT_Y, LEFT_X
		}

		public FSi6S(int port) {
			super(port);
		}

		public SwitchPosition getSwitch(final Switch swit) {
			switch (swit) {
			default:
			case A:
				return getRawButton(1) ? SwitchPosition.UP : SwitchPosition.DOWN;
			case B: {
				final boolean top = getRawButton(3), bottom = getRawButton(2);
				if (top) {
					return SwitchPosition.UP;
				} else if (bottom) {
					return SwitchPosition.DOWN;
				} else {
					return SwitchPosition.NEUTRAL;
				}
			}
			case C: {
				final boolean top = getRawButton(5), bottom = getRawButton(4);
				if (top) {
					return SwitchPosition.UP;
				} else if (bottom) {
					return SwitchPosition.DOWN;
				} else {
					return SwitchPosition.NEUTRAL;
				}
			}
			case D:
				return getRawButton(6) ? SwitchPosition.UP : SwitchPosition.DOWN;
			}
		}

		public boolean getBackLeft() {
			return getRawButton(7);
		}

		public boolean getBackRight() {
			return getRawButton(8);
		}

		public double getRawAxis(final int axis) {
			return getRawAxis(axis);
		}
		
		public double getRawAxis(final Axis axis) {
			return getRawAxis(axis.ordinal());
		}
	}

	public static class Xbox extends Titan.Joystick {

		public static enum Button {
			// ordered correctly, so ordinal reflects real mapping
			A, B, X, Y, BUMPER_L, BUMPER_R, BACK, START;
		}

		public static enum Axis {
			LEFT_X, LEFT_Y, TRIGGER_LEFT, TRIGGER_RIGHT, RIGHT_X, RIGHT_Y
		}

		public Xbox(int port) {
			super(port);
		}

		public boolean getRawButton(final Button but) {
			return getRawButton(but.ordinal() + 1);
		}

		public double getRawAxis(final Axis axis) {
			return getRawAxis(axis.ordinal());
		}
	}

	public static class Toggle {
		private boolean isToggled = false;
		private int prevButton = 0;

		public boolean isToggled(final boolean buttonState) {
			if ((buttonState ? 1 : 0) > prevButton) {
				isToggled = !isToggled;
			}
			prevButton = buttonState ? 1 : 0;
			return isToggled;
		}
	}

	public static class NavX extends AHRS {
		private double absoluteReset = 0;
		private boolean yawDirection = false;

		public NavX() {
			super(SPI.Port.kMXP);

			reset();
			resetDisplacement();
		}

		public void resetYaw() {
			absoluteReset = getYaw();
			if (absoluteReset <= 0) {
				yawDirection = false; // false == left
			} else {
				yawDirection = true; // true == right
			}
		}

		public double getAbsoluteYaw() {
			if (!yawDirection) {
				return getYaw() + absoluteReset;
			} else {
				return getYaw() - absoluteReset;
			}
		}
	}

	public static class Pot extends AnalogInput {
		private double minAngle = 0, maxAngle = 180;
		private double minPotValue = 0, maxPotValue = 4096;
		private double absoluteReset = 0;
		private boolean potDirection = false;

		public Pot(final int port) {
			super(port);
		}

		public double getMinAngle() {
			return minAngle;
		}

		public void setMinAngle(double minAngle) {
			this.minAngle = minAngle;
		}

		public double getMaxAngle() {
			return maxAngle;
		}

		public void setMaxAngle(double maxAngle) {
			this.maxAngle = maxAngle;
		}

		public double getMinPotValue() {
			return minPotValue;
		}

		public void setMinPotValue(double minPotValue) {
			this.minPotValue = minPotValue;
		}

		public double getMaxPotValue() {
			return maxPotValue;
		}

		public void setMaxPotValue(double maxPotValue) {
			this.maxPotValue = maxPotValue;
		}

		public void resetAngle() {
			absoluteReset = getAbsoluteAngle();
			potDirection = absoluteReset > 0; // false == less, true == more
		}

		public double getAngle() {
			final double currentAngle = getAbsoluteAngle();
			return potDirection ? currentAngle - absoluteReset : currentAngle + absoluteReset;
		}

		public double getAbsoluteAngle() {
			return linearMap(getValue(), minPotValue, maxPotValue, minAngle, maxAngle);
		}

		private static double linearMap(final double currentValue, final double minInputValue,
				final double maxInputValue, final double minOutputValue, final double maxOutputValue) {
			return (currentValue - minInputValue) * (maxOutputValue - minOutputValue) / (minInputValue - maxInputValue)
					+ minOutputValue;
		}
	}

	public static class GameData {
		public static enum Position {
			LEFT, RIGHT;

			static Position fromGameData(final char value) {
				if (value == 'L') {
					return Position.LEFT;
				} else if (value == 'R') {
					return Position.RIGHT;
				} else {
					throw new IllegalArgumentException("Illegal Game Data Character: " + value);
				}
			}
		}
		
		public static enum FieldObject {
			SWITCH, SCALE, OPPONENT_SWITCH
		}
		
		public static interface SideChooser {
			void left();
			void right();
		}

		private Position allianceSwitch, scale, opponentSwitch;
		private FieldObject selectedObject;
		
		
		public void init() {
			final String gameData = DriverStation.getInstance().getGameSpecificMessage();
			if (gameData.length() != 3) {
				throw new IllegalArgumentException("Illegal Game Data String: " + gameData);
			}
			allianceSwitch = Position.fromGameData(gameData.charAt(0));
			scale = Position.fromGameData(gameData.charAt(1));
			opponentSwitch = Position.fromGameData(gameData.charAt(2));
		}

		public Position getScale() {
			return scale;
		}

		public Position getOpponentSwitch() {
			return opponentSwitch;
		}

		public Position getAllianceSwitch() {
			return allianceSwitch;
		}
		
		public void setSelectedObject(FieldObject fObject) {
			selectedObject = fObject;
		}
		
		public void runSide(SideChooser toRun) {
			switch(selectedObject) {
			case SWITCH:
				if(getAllianceSwitch() == Position.LEFT) toRun.left();
				else toRun.right();
				break;
			case SCALE:
				if(getScale() == Position.LEFT) toRun.left();
				else toRun.right();
				break;
			case OPPONENT_SWITCH:
				if(getOpponentSwitch() == Position.LEFT) toRun.left();
				else toRun.right();
				break;
			}
		}
	}

	public static boolean approxEquals(final double a, final double b, final double epsilon) {
		if(a == b) {
			return true;
		}
		
		return Math.abs(a - b) < epsilon;
	}
}

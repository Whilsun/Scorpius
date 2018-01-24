package org.usfirst.frc.team5431.robot;

public final class Constants {
	private static enum RobotMappings {
		CATABOT, THRICE
	}

	private static final RobotMappings ROBOT_MAPPINGS = RobotMappings.THRICE;

	// TALONS
	public final static int TALON_FRONT_LEFT_ID;
	public final static boolean TALON_FRONT_LEFT_INVERTED;

	public final static int TALON_MIDDLE_LEFT_ID;
	public final static boolean TALON_MIDDLE_LEFT_INVERTED;

	public final static int TALON_BACK_LEFT_ID;
	public final static boolean TALON_BACK_LEFT_INVERTED;

	public final static int TALON_FRONT_RIGHT_ID;
	public final static boolean TALON_FRONT_RIGHT_INVERTED;

	public final static int TALON_MIDDLE_RIGHT_ID;
	public final static boolean TALON_MIDDLE_RIGHT_INVERTED;

	public final static int TALON_BACK_RIGHT_ID;
	public final static boolean TALON_BACK_RIGHT_INVERTED;

	public final static int TALON_INTAKE_LEFT_ID;
	public final static boolean TALON_INTAKE_LEFT_INVERTED;

	public final static int TALON_INTAKE_RIGHT_ID;
	public final static boolean TALON_INTAKE_RIGHT_INVERTED;

	public final static int TALON_INTAKE_ACTUATION_ID;
	public final static boolean TALON_INTAKE_ACTUATION_INVERTED;

	public final static int TALON_INTAKE_UP_ID;
	public final static boolean TALON_INTAKE_UP_INVERTED;

	public final static int TALON_CATAPULT_LEFT_ID;
	public final static boolean TALON_CATAPULT_LEFT_INVERTED;

	public final static int TALON_CATAPULT_RIGHT_ID;
	public final static boolean TALON_CATAPULT_RIGHT_INVERTED;

	public final static int TALON_CLIMBER_RIGHT_ID;
	public final static boolean TALON_CLIMBER_RIGHT_INVERTED;

	public final static int TALON_CLIMBER_LEFT_ID;
	public final static boolean TALON_CLIMBER_LEFT_INVERTED;
	
	//ENCODERS
	public final static int ENCODER_LEFT_DRIVE_CHANNEL_1 = 0;
	public final static int ENCODER_LEFT_DRIVE_CHANNEL_2 = 1;
	public final static boolean ENCODER_LEFT_DRIVE_INVERTED;
	
	public final static int ENCODER_RIGHT_DRIVE_CHANNEL_1 = 2;
	public final static int ENCODER_RIGHT_DRIVE_CHANNEL_2 = 3;
	public final static boolean ENCODER_RIGHT_DRIVE_INVERTED;

	static {
		// Catabot
		switch (ROBOT_MAPPINGS) {
		case CATABOT:
		default:
			TALON_FRONT_LEFT_ID = 0;
			TALON_FRONT_LEFT_INVERTED = true;

			TALON_MIDDLE_LEFT_ID = 1;
			TALON_MIDDLE_LEFT_INVERTED = false;

			TALON_BACK_LEFT_ID = 2;
			TALON_BACK_LEFT_INVERTED = true;

			TALON_FRONT_RIGHT_ID = 3;
			TALON_FRONT_RIGHT_INVERTED = false;

			TALON_MIDDLE_RIGHT_ID = 4;
			TALON_MIDDLE_RIGHT_INVERTED = false;

			TALON_BACK_RIGHT_ID = 5;
			TALON_BACK_RIGHT_INVERTED = false;

			TALON_INTAKE_LEFT_ID = 6;
			TALON_INTAKE_LEFT_INVERTED = false;

			TALON_INTAKE_RIGHT_ID = 7;
			TALON_INTAKE_RIGHT_INVERTED = false;

			TALON_INTAKE_ACTUATION_ID = 8;
			TALON_INTAKE_ACTUATION_INVERTED = false;

			TALON_INTAKE_UP_ID = 9;
			TALON_INTAKE_UP_INVERTED = false;

			TALON_CATAPULT_LEFT_ID = 10;
			TALON_CATAPULT_LEFT_INVERTED = false;

			TALON_CATAPULT_RIGHT_ID = 11;
			TALON_CATAPULT_RIGHT_INVERTED = false;

			TALON_CLIMBER_RIGHT_ID = 12;
			TALON_CLIMBER_RIGHT_INVERTED = true;

			TALON_CLIMBER_LEFT_ID = 13;
			TALON_CLIMBER_LEFT_INVERTED = false;
			
			ENCODER_LEFT_DRIVE_INVERTED = false;
			ENCODER_RIGHT_DRIVE_INVERTED = false;
			break;

		case THRICE:
			TALON_FRONT_LEFT_ID = 5;
			TALON_FRONT_LEFT_INVERTED = false;

			TALON_MIDDLE_LEFT_ID = 4;
			TALON_MIDDLE_LEFT_INVERTED = false;

			TALON_BACK_LEFT_ID = 4;
			TALON_BACK_LEFT_INVERTED = false;

			TALON_FRONT_RIGHT_ID = 2;
			TALON_FRONT_RIGHT_INVERTED = true;

			TALON_MIDDLE_RIGHT_ID = 3;
			TALON_MIDDLE_RIGHT_INVERTED = true;

			TALON_BACK_RIGHT_ID = 3;
			TALON_BACK_RIGHT_INVERTED = true;

			TALON_INTAKE_LEFT_ID = 0;
			TALON_INTAKE_LEFT_INVERTED = false;

			TALON_INTAKE_RIGHT_ID = 0;
			TALON_INTAKE_RIGHT_INVERTED = false;

			TALON_INTAKE_ACTUATION_ID = 0;
			TALON_INTAKE_ACTUATION_INVERTED = false;

			TALON_INTAKE_UP_ID = 0;
			TALON_INTAKE_UP_INVERTED = false;

			TALON_CATAPULT_LEFT_ID = 0;
			TALON_CATAPULT_LEFT_INVERTED = false;

			TALON_CATAPULT_RIGHT_ID = 0;
			TALON_CATAPULT_RIGHT_INVERTED = false;

			TALON_CLIMBER_RIGHT_ID = 6;
			TALON_CLIMBER_RIGHT_INVERTED = true;

			TALON_CLIMBER_LEFT_ID = 8;
			TALON_CLIMBER_LEFT_INVERTED = false;
			
			ENCODER_LEFT_DRIVE_INVERTED = true;
			ENCODER_RIGHT_DRIVE_INVERTED = false;
			break;
		}
	}

	// SPEEDS
	public final static double CLIMBER_SPEED = 1.0;
	public final static double CATAPULT_LOWER_SPEED = 1.0;
}

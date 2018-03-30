package org.usfirst.frc.team5431.robot;

public final class Constants {
	private static enum CameraSettings {
		LIFECAM
	}

	/*
	 * MAIN CONFIGURATIONS
	 */
	private static final CameraSettings CAMERA_SETTINGS = CameraSettings.LIFECAM;

	/*
	 * DEBUGGGING
	 */
	public final static boolean ENABLE_DEBUGGING = true;

	/*
	 * AUTONOMOUS
	 */
	public final static boolean AUTO_LOG_PATHFINDING = true;
	public final static String AUTO_LOG_PATHFINDING_NAME = "right_switch";
	public final static double AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED = 0.2; // This is the speed to override the
																				// previous step
	public final static double AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_DISTANCE = 3.0; // This is the speed to override the
																					// previous step
	public final static double AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_TURN = 5.0; // Minimum turn offset for the
																				// pathfinding to not skip a step
	public final static double AUTO_ROBOT_DEFAULT_SPEED = 0.45;

	public final static double ELEVATOR_TOP_HEIGHT = 20.0; // in inches
	
	//Times
	public final static long AUTO_OUTTAKE_TIME = 1000;// in ms
	public final static long SWITCH_HEIGHT_TIMEOUT = 3000; // in ms
	public final static long SCALE_HEIGHT_TIMEOUT = 4000; // in ms

	/*
	 * TALONS
	 */
	public final static int TALON_FRONT_LEFT_ID = 1;
	public final static boolean TALON_FRONT_LEFT_INVERTED = true;

	public final static int TALON_MIDDLE_LEFT_ID = 2;
	public final static boolean TALON_MIDDLE_LEFT_INVERTED = true;

	public final static int TALON_BACK_LEFT_ID = 3;
	public final static boolean TALON_BACK_LEFT_INVERTED = true;

	public final static int TALON_FRONT_RIGHT_ID = 4;
	public final static boolean TALON_FRONT_RIGHT_INVERTED = false;

	public final static int TALON_MIDDLE_RIGHT_ID = 5;
	public final static boolean TALON_MIDDLE_RIGHT_INVERTED = false;

	public final static int TALON_BACK_RIGHT_ID = 6;
	public final static boolean TALON_BACK_RIGHT_INVERTED = false;

	public final static int TALON_INTAKE_ID = 7;
	public final static boolean TALON_INTAKE_INVERTED = true;
	
	public final static int TALON_INTAKE_TILT_ID = 8;
	public final static boolean TALON_INTAKE_TILT_INVERTED = true;

	public final static int TALON_ELEVATOR_LEFT_ID = 14;
	public final static boolean TALON_ELEVATOR_LEFT_INVERTED = false;

	public final static int TALON_ELEVATOR_RIGHT_ID = 15;
	public final static boolean TALON_ELEVATOR_RIGHT_INVERTED = false;

	// SPEEDS
	public final static double ELEVATOR_SPEED_MULTIPLIER = 0.025;
	public final static double ELEVATOR_SPEED_CONSTANT = 0.095;
	public final static double ELEVATOR_STOPPED_SPEED = 0.0883;
	public final static double INTAKE_TILT_SPEED = 1.0;
	public final static double INTAKE_SPEED = 1.0;
	public final static double OUTTAKE_SPEED = -INTAKE_SPEED;
	public final static double INTAKE_STOPPED_SPEED = 0.075; //0.12;
	public final static double ELEVATOR_DOWN_SPEED = -0.125;
	// HALLFX
	public final static int HALLFX_PORT = 0;
	public final static boolean HALLFX_INVERTED = false;
	public final static int HALLFX_UP_POS = 45;
	public final static int HALLFX_AUTON_POS = 84;
	public final static int HALLFX_DOWN_POS = 0;
	public final static double RESET_AMPERAGE = 9.0;
	public final static double MAX_AMPERAGE = 11.0;
	public final static int HALLFX_POS_EPSILON = 2;
	
	// LIDAR
	public final static int LIDAR_PORT = 0;
	public final static double LIDAR_CUBE_AMPS = 20.0;
	public final static double LIDAR_HEIGHT_EPSILON = 2;// the epsilon is the percentage that the wanted distance
															// and lidar distance have to equal in order for the
	//HEIGHTS													// elevator to stop
	public final static int HEIGHT_SWITCH = 40;
	public final static int HEIGHT_SCALE = 70;
	public final static int HEIGHT_SECOND_LAYER = 25;
	public final static int HEIGHT_THIRD_LAYER = 39;
	public final static int HEIGHT_CLIMB = 60;
	public final static int HEIGHT_STEAL = 45;
	public final static int HEIGHT_CUBE = 15;
	public final static double HEIGHT_LOWEST = 13;
	
	//ELEVATOR SAVER
	public final static double SAVE_ELEVATOR_ANGLE = 10.0; // Degrees
	
	// CONTROLS
	public final static int DRIVER_PORT = 0;
	public final static double DRIVER_DEADZONE = 0.1;
	public final static int OPERATOR_PORT = 1;
	public final static double OPERATOR_DEADZONE = 0.1;

	// ENCODERS

	// Physical properties
	public final static double ROBOT_WHEEL_DIAMETER = 6.0; // WHEEL DIAMETER IN INCHES

	// Calcs
	public final static int ENCODER_STEPS_PER_FULL_ROTATION = 4096;
	// public final static int ENCODER_SAMPLES_TO_AVERAGE = 25; //5 steps to average
	public final static double ENCODER_DISTANCE_PER_PULSE = (ROBOT_WHEEL_DIAMETER * Math.PI)
			/ ENCODER_STEPS_PER_FULL_ROTATION;

	// Driving
	public final static double DRIVE_HEADING_P = 0.036;// 0.018;
	public final static double DRIVE_HEADING_I = 0.00;
	public final static double DRIVE_HEADING_D = 0.085;
	public final static double DRIVE_HEADING_MIN_MAX = 0.1;

	// Mimic
	public final static double DRIVE_MIMICK_P = 0.016; //0.025
	public final static double DRIVE_MIMICK_I = 0.00;
	public final static double DRIVE_MIMICK_D = 0.060;
	public final static double DRIVE_MIMICK_MIN_MAX = 0.35; //0.4

	// Distance
	public final static double DRIVE_DISTANCE_P = 0.00632;
	public final static double DRIVE_DISTANCE_I = 0.0000000025; // 0.0003; //0.0022;
	public final static double DRIVE_DISTANCE_D = 0.000915; // 0.00031;
	public final static double DRIVE_DISTANCE_MAX_OFFSET = 20; // Maximum error

	// Turning
	public final static double TURN_P = 0.00325;
	public final static double TURN_I = 0.000185;
	public final static double TURN_D = 0.0000095;
	public final static double TURN_MIN_VALUE = 0.1;
	public final static double TURN_PRECISION = 1.0; // Make sure the turn is within the degree

	// Vision
	public final static double VISION_P = 0.006;
	public final static double VISION_I = 0.000125; // 0.00000007;
	public final static double VISION_D = 0.0075; // 0.0005;
	public final static double VISION_MIN_MAX = 0.5;

	public final static double PIVOT_DISTANCE_SCALING = 0.11;

	public static class Vision {
		public final static int IMAGE_WIDTH = 240;
		public final static int IMAGE_HEIGHT = 180;
		public final static int FPS = 20;
		public final static double CAMERA_HORZ_FOV;
		public final static double CAMERA_VERT_FOV;
		public final static double CAMERA_DIAG_DIST;
		public final static double DEGREES_PER_HORZ_PIXEL;
		public final static double DEGREES_PER_VERT_PIXEL;
		public final static double CAMERA_HORZ_OFFSET;
		public final static double CAMERA_VERT_OFFSET;

		static {
			// CAMERA SPECIFIC SETTINGS
			switch (CAMERA_SETTINGS) {
			case LIFECAM:
			default:
				CAMERA_DIAG_DIST = Math.sqrt((IMAGE_WIDTH * IMAGE_WIDTH) + (IMAGE_HEIGHT * IMAGE_HEIGHT));
				CAMERA_HORZ_FOV = (360 * Math.atan((IMAGE_WIDTH / 2) / CAMERA_DIAG_DIST)) / Math.PI; // 58.5
				CAMERA_VERT_FOV = (360 * Math.atan((IMAGE_HEIGHT / 2) / CAMERA_DIAG_DIST)) / Math.PI; // 45.6
				DEGREES_PER_HORZ_PIXEL = CAMERA_HORZ_FOV / (double) IMAGE_WIDTH;
				DEGREES_PER_VERT_PIXEL = CAMERA_VERT_FOV / (double) IMAGE_HEIGHT;
				CAMERA_HORZ_OFFSET = 0.0;
				CAMERA_VERT_OFFSET = 0.0;
			}
		}

		public final static double fromCenter(final double pixel, final double dims) {
			return pixel - (dims / 2.0);
		}
		// The Chicken AlGoul Is Ready, Are You?

		public final static double angleFromCenter(final double center, final double degreesPerPixel) {
			return center * degreesPerPixel;
		}

		public final static double getHorzAngle(final double pixel) {
			return angleFromCenter(fromCenter(pixel, IMAGE_WIDTH), DEGREES_PER_HORZ_PIXEL) + CAMERA_HORZ_OFFSET;
		}

		public final static double getVertAngle(final double pixel) {
			return angleFromCenter(fromCenter(pixel, IMAGE_HEIGHT), DEGREES_PER_VERT_PIXEL) + CAMERA_VERT_OFFSET;
		}
	}
}

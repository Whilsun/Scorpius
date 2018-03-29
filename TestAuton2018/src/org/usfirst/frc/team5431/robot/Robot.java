/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5431.robot;

import org.usfirst.frc.team5431.robot.Titan.CommandQueue;
import org.usfirst.frc.team5431.robot.commands.BuildAutonomousCommand;
import org.usfirst.frc.team5431.robot.commands.CalibrateCommand;
import org.usfirst.frc.team5431.robot.commands.WaitCommand;
import org.usfirst.frc.team5431.robot.pathfinding.Mimick;
import org.usfirst.frc.team5431.robot.pathfinding.Mimick.MimickException;
import org.usfirst.frc.team5431.robot.pathfinding.Mimick.Stepper;
import org.usfirst.frc.team5431.robot.commands.MimickCommand;
import org.usfirst.frc.team5431.robot.commands.MimickCommand.Paths;
import org.usfirst.frc.team5431.robot.components.DriveBase;
import org.usfirst.frc.team5431.robot.components.DriveBase.TitanPIDSource;
import org.usfirst.frc.team5431.robot.components.Elevator;
import org.usfirst.frc.team5431.robot.components.Intake;
import org.usfirst.frc.team5431.robot.vision.Vision;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private final DriveBase driveBase = new DriveBase();
	private final Teleop teleop = new Teleop();
	private final Elevator elevator = new Elevator();
	private final Intake intake = new Intake();
	private Mimick.Observer observer;

	public enum AutonPriority {
		AUTO_LINE, SWITCH, SCALE, SWITCH_SCALE
	}

	public enum AutonPosition {
		LEFT, CENTER, RIGHT;

		public static AutonPosition valueOf(final Titan.GameData.Position pos) {
			if (pos == Titan.GameData.Position.LEFT) {
				return AutonPosition.LEFT;
			} else {
				return AutonPosition.RIGHT;
			}
		}
	}

	private enum PIDTest {
		HEADING, TURNING, NONE
	}

	// Auto steps
	private final Titan.CommandQueue<Robot> aSteps = new Titan.CommandQueue<>();

	private final Titan.GameData game = new Titan.GameData();
	private final SendableChooser<AutonPriority> autonChooser = new SendableChooser<AutonPriority>();
	private final SendableChooser<PIDTest> pidChooser = new SendableChooser<PIDTest>();
	private final SendableChooser<AutonPosition> positionChooser = new SendableChooser<AutonPosition>();
	private final SendableChooser<Integer> waitChooser = new SendableChooser<Integer>();

	@Override
	public void robotInit() {
		// Set the debugging flag
		Titan.DEBUG = Constants.ENABLE_DEBUGGING;

		// Start the vision class
		Vision.setCubeCamera(CameraServer.getInstance().startAutomaticCapture("CubeCamera", 0));
		Vision.setFieldCamera(CameraServer.getInstance().startAutomaticCapture("FieldCamera", 1));
		Vision.init();

		// Used for the autonomous selection
		autonChooser.addDefault("Auto Line", AutonPriority.AUTO_LINE);
		autonChooser.addObject("Switch", AutonPriority.SWITCH);
		autonChooser.addObject("Scale", AutonPriority.SCALE);
		autonChooser.addObject("Switch Check Scale", AutonPriority.SWITCH_SCALE);
		SmartDashboard.putData("Auton Priority", autonChooser);

		// Used for PID Tuning
		pidChooser.addDefault("None", PIDTest.NONE);
		pidChooser.addObject("Heading", PIDTest.HEADING);
		pidChooser.addObject("Turning", PIDTest.TURNING);
		SmartDashboard.putData("PIDTest", pidChooser);

		// Used for Auton positioning
		positionChooser.addDefault("Center", AutonPosition.CENTER);
		positionChooser.addObject("Right", AutonPosition.RIGHT);
		SmartDashboard.putData("AutonPosition", positionChooser);

		// waitChooser
		waitChooser.addDefault("None", 0);
		waitChooser.addObject("One", 1);
		waitChooser.addObject("Two", 2);
		waitChooser.addObject("Three", 3);
		waitChooser.addObject("Four", 4);
		waitChooser.addObject("Five", 5);
		SmartDashboard.putData("AutonWait", waitChooser);

		// Add the driveBase PID Source
		driveBase.setFullSource(TitanPIDSource.NAVX, true, Vision.TargetMode.Cube);
		driveBase.setTurnPIDValues();
		SmartDashboard.putNumber("TestDriveSpeed", 0.25);
		SmartDashboard.putNumber("TestDriveHeading", 0);
		SmartDashboard.putNumber("P", Constants.TURN_P);
		SmartDashboard.putNumber("I", Constants.TURN_I);
		SmartDashboard.putNumber("D", Constants.TURN_D);
		SmartDashboard.putData("Gyro", driveBase.getNavx());
		SmartDashboard.putString("MimickFile", "TEST_MIMIC_FILE");
	}

	@Override
	public void autonomousInit() {
		driveBase.setBrakeMode(true);

		final AutonPriority priority = autonChooser.getSelected();
		final AutonPosition position = positionChooser.getSelected();
		final long waitMillis = ((long) waitChooser.getSelected()) * 1000;

		// Reset the drivebase
		driveBase.setHome();

		// Add the wait selector to the smart dashboard
		aSteps.clear(); //Clear all previous commands
		if (waitMillis != 0)
			aSteps.add(new WaitCommand(waitMillis));
		aSteps.add(new CalibrateCommand());
		/*aSteps.add(new BuildAutonomousCommand(priority, position));*/
		aSteps.add(new MimickCommand(Paths.valueOf(SmartDashboard.getString("MimickFile", "TEST_MIMIC_FILE"))));

		// Test the mimic code
		// aSteps.add(new MimicStep(Paths.RIGHT_SCALE));
		aSteps.init(this);
	}

	@Override
	public void autonomousPeriodic() {
		// As soon as queue is finsihed, stop drivebase
		if (!aSteps.update(this)) {
			driveBase.drive(0.0, 0.0);
		}

		// Update the intake and climber
		elevator.update(this);
		intake.update(this);
	}

	@Override
	public void teleopInit() {
		driveBase.setHome();
		driveBase.setBrakeMode(true);
	}

	@Override
	public void teleopPeriodic() {
		teleop.periodicElevator(this);
		teleop.periodicIntake(this);
		teleop.periodicDrive(this);
		
		//Save the elevator when it tips
		elevator.saveElevator(this);
	}

	@Override
	public void robotPeriodic() {
		Vision.periodic();
	}

	@Override
	public void testInit() {
		Titan.l("Starting test mode!");
		driveBase.setHome();
		driveBase.setBrakeMode(true);
		intake.setHomeUp();

		if (Constants.AUTO_LOG_PATHFINDING) {
			observer = new Mimick.Observer(String.format(MimickCommand.mimicPath, SmartDashboard.getString("MimicFile", Constants.AUTO_LOG_PATHFINDING_NAME)));
			observer.addArguments("left_encoder", "right_encoder", "yaw", "left_power", "right_power", "home", "elevator_height", "intake_tilt", "intake_speed");
			try {
				observer.prepare();
			} catch(MimickException err) {
				Titan.ee("MimickObserver", err);
			}
		}
		/*
		 * PIDTest testPID = pidChooser.getSelected(); double speed =
		 * SmartDashboard.getNumber("TestDriveSpeed", 0.25); double angle =
		 * SmartDashboard.getNumber("TestDriveHeading", 0.0);
		 * Titan.l("Speed: %.4f Angle: %.2f", speed, angle);
		 * 
		 * switch(testPID) { case HEADING: driveBase.disableAllPID(); updatePID();
		 * driveBase.driveAtAnglePID(speed, angle); updatePID(); break; case TURNING:
		 * driveBase.disableAllPID(); updatePID(); driveBase.turnPID(angle);
		 * updatePID(); case NONE: break; default: break; }
		 */
		// Logger.init("right_scale");
	}

	@Override
	public void testPeriodic() {
		SmartDashboard.putNumber("LeftEncoder", driveBase.getLeftDistance());
		SmartDashboard.putNumber("RightEncoder", driveBase.getRightDistance());
		teleop.periodicElevator(this);
		teleop.periodicIntake(this);
		
		if (Constants.AUTO_LOG_PATHFINDING) {
			final double vals[] = teleop.periodicPathfindingDrive(this);
			try {
				Stepper step = observer.createStep();
				step.set("left_encoder", driveBase.getLeftDistance());
				step.set("right_encoder", driveBase.getRightDistance());
				step.set("yaw", driveBase.getNavx().getYaw());
				step.set("left_power", vals[0]);
				step.set("right_power", vals[1]);

				boolean home = teleop.getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.FIVE);

				step.set("home", home);
				step.set("elevator_height", elevator.getUpPos());
				
				double intakeSpeed = Constants.INTAKE_STOPPED_SPEED;
				if(teleop.getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.TRIGGER)) {
					intakeSpeed = Constants.OUTTAKE_SPEED;
				} else if(teleop.getLogitech().getRawButton(Titan.LogitechExtreme3D.Button.THREE)) {
					intakeSpeed = Constants.INTAKE_SPEED;
				}
				
				step.set("intake_speed", intakeSpeed);
				step.set("intake_tilt", intake.getTiltPosition());
				
				if(home) {
					driveBase.setHome();
				}
				
				observer.addStep(step);
			} catch(MimickException err) {
				Titan.ee("MimickObserver", err);
			}
		} else {
			teleop.periodicDrive(this);
		}
	}

	public void disabledInit() {
		Vision.setNormalTargetMode();
		driveBase.disableAllPID();
		if (Constants.AUTO_LOG_PATHFINDING) {
			try {
				observer.save();
			} catch(MimickException err) {
				Titan.ee("MimickObserver", err);
			}
		}
	}

	public void disabledPeriodic() {

	}

	public DriveBase getDriveBase() {
		return driveBase;
	}

	public Teleop getTeleop() {
		return teleop;
	}

	public Elevator getElevator() {
		return elevator;
	}

	public Intake getIntake() {
		return intake;
	}

	public Titan.GameData getGameData() {
		return game;
	}

	public CommandQueue<Robot> getAutonSteps() {
		return aSteps;
	}
}

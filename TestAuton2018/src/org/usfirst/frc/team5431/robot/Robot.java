/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5431.robot;

import java.util.LinkedList;
import java.util.Queue;

import org.usfirst.frc.team5431.robot.Titan.AssignableJoystick;
import org.usfirst.frc.team5431.robot.Titan.GameData.FieldObject;
import org.usfirst.frc.team5431.robot.Titan.GameData.SideChooser;
import org.usfirst.frc.team5431.robot.auton.BuildAutonomousCommand;
import org.usfirst.frc.team5431.robot.auton.CalibrateCommand;
import org.usfirst.frc.team5431.robot.auton.DriveCommand;
import org.usfirst.frc.team5431.robot.auton.MimicCommad;
import org.usfirst.frc.team5431.robot.auton.MimicCommad.Paths;
import org.usfirst.frc.team5431.robot.auton.Step;
import org.usfirst.frc.team5431.robot.auton.Step.StepResult;
import org.usfirst.frc.team5431.robot.auton.TurnCommand;
import org.usfirst.frc.team5431.robot.auton.WaitCommand;
import org.usfirst.frc.team5431.robot.components.Catapult;
import org.usfirst.frc.team5431.robot.components.Climber;
import org.usfirst.frc.team5431.robot.components.DriveBase;
import org.usfirst.frc.team5431.robot.components.DriveBase.TitanPIDSource;
import org.usfirst.frc.team5431.robot.components.Intake;
import org.usfirst.frc.team5431.robot.pathfinding.Mimic;
import org.usfirst.frc.team5431.robot.vision.Vision;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {

	private final DriveBase driveBase = new DriveBase();
	private final Climber climber = new Climber();
	private final Catapult catapult = new Catapult();
	private final Teleop teleop = new Teleop();
	private final Intake intake = new Intake();

	public enum AutonPriority {
		AUTO_LINE, SWITCH, SCALE, SWITCH_SCALE
	}

	public enum AutonPosition {
		CENTER, RIGHT
	}

	private enum PIDTest {
		HEADING, TURNING, NONE
	}

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

		// Vision.setCamera(CameraServer.getInstance().startAutomaticCapture());
		// Vision.init();
		// CubeFinder.start();

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
	}

	@Override
	public void autonomousInit() {
		driveBase.setBrakeMode(true);
		intake.recalibrate(); //Try to calibrate in the up position
		intake.stayUp();

		final AutonPriority priority = autonChooser.getSelected();
		final AutonPosition position = positionChooser.getSelected();
		final long waitMillis = ((long) waitChooser.getSelected()) * 1000;

		// Reset the drivebase
		driveBase.setHome();

		// Add the wait selector to the smart dashboard
		if (waitMillis != 0)
			aSteps.add(new WaitCommand(waitMillis));
		aSteps.add(new CalibrateCommand());
		aSteps.add(new BuildAutonomousCommand(priority, position));

		// Test the mimic code
		//aSteps.add(new MimicStep(Paths.RIGHT_SCALE));
		aSteps.init(this);
	}

	@Override
	public void autonomousPeriodic() {
		AssignableJoystick<Robot> rodgjka;
		rodgjka.getRawAxis(Titan.Xbox.Axis.LEFT_X);
		if (!aSteps.periodic(this)){
			driveBase.drive(0.0, 0.0);
		}
		
		//Update the intake and climber
		intake.update(this);
		catapult.update(this);
	}

	@Override
	public void teleopInit() {
		driveBase.setHome();
		driveBase.setBrakeMode(true);
		intake.stayInPosition();
		// intake.tryToZero(this);
	}

	@Override
	public void teleopPeriodic() {
		// intake.goDown();
		// intake.goUp();

		// intake.pinch();
		//teleop.periodicCatapult(this);
		teleop.periodicIntake(this);
		teleop.periodicDrive(this);
		teleop.periodicClimb(this);
	}

	@Override
	public void robotPeriodic() {
		Vision.periodic();
	}

	@Override
	public void testInit() {
		Titan.l("Starting test mode!");
		driveBase.setBrakeMode(true);

		if (Constants.AUTO_LOG_PATHFINDING) {
			Mimic.Observer.prepare(Constants.AUTO_LOG_PATHFINDING_NAME);
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

		if (Constants.AUTO_LOG_PATHFINDING) {
			final double vals[] = teleop.periodicPathfindingDrive(this);
			Mimic.Observer.addStep(this, vals);
		} else {
			teleop.periodicDrive(this);
		}
		
		intake.update(this);
		catapult.update(this);
	}

	public void disabledInit() {
		Vision.setNormalTargetMode();
		driveBase.disableAllPID();
		if (Constants.AUTO_LOG_PATHFINDING) {
			Mimic.Observer.saveMimic();
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

	public Climber getClimber() {
		return climber;
	}

	public Catapult getCatapult() {
		return catapult;
	}

	public Intake getIntake() {
		return intake;
	}

	public Titan.GameData getGameData() {
		return game;
	}

	public Queue<Step> getAutonSteps() {
		return aSteps;
	}
}

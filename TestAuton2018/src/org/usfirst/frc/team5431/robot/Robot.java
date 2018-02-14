/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5431.robot;

import java.util.LinkedList;
import java.util.Queue;

import org.usfirst.frc.team5431.robot.auton.MimicStep;
import org.usfirst.frc.team5431.robot.auton.MimicStep.Paths;
import org.usfirst.frc.team5431.robot.auton.Step;
import org.usfirst.frc.team5431.robot.auton.Step.StepResult;
import org.usfirst.frc.team5431.robot.components.Catapult;
import org.usfirst.frc.team5431.robot.components.Climber;
import org.usfirst.frc.team5431.robot.components.DriveBase;
import org.usfirst.frc.team5431.robot.components.DriveBase.TitanPIDSource;
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

	private enum AutonPriority {
		AUTO_LINE, SWITCH, SCALE, SWITCH_SCALE
	}
	
	private enum AutonPosition {
		CENTER, RIGHT
	}
	
	private enum PIDTest {
		HEADING, TURNING, NONE
	}

	private final Queue<Step> aSteps = new LinkedList<>();

	private final SendableChooser<AutonPriority> autonChooser = new SendableChooser<AutonPriority>();
	private final SendableChooser<PIDTest> pidChooser = new SendableChooser<PIDTest>();
	private final SendableChooser<AutonPosition> positionChooser = new SendableChooser<AutonPosition>();
	
	@Override
	public void robotInit() {
		//Set the debugging flag
		Titan.DEBUG = Constants.ENABLE_DEBUGGING;
		
		//Vision.setCamera(CameraServer.getInstance().startAutomaticCapture());
		//Vision.init();
		//CubeFinder.start();
		
		//Used for the autonomous selection
		autonChooser.addDefault("Auto Line", AutonPriority.AUTO_LINE);
		autonChooser.addObject("Switch", AutonPriority.SWITCH);
		autonChooser.addObject("Scale", AutonPriority.SCALE);
		autonChooser.addObject("Switch Check Scale", AutonPriority.SWITCH_SCALE);
		SmartDashboard.putData("Auton Priority", autonChooser);
		
		//Used for PID Tuning
		pidChooser.addDefault("None", PIDTest.NONE);
		pidChooser.addObject("Heading", PIDTest.HEADING);
		pidChooser.addObject("Turning", PIDTest.TURNING);
		SmartDashboard.putData("PIDTest", pidChooser);
		
		//Used for Auton positioning
		positionChooser.addDefault("Center", AutonPosition.CENTER);
		positionChooser.addObject("Right", AutonPosition.RIGHT);
		SmartDashboard.putData("AutonPosition", positionChooser);
		
		//Add the driveBase PID Source
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

		final Titan.GameData game = new Titan.GameData();
		game.init();

		final int playerStation;
		if (DriverStation.getInstance().isFMSAttached()) {
			playerStation = DriverStation.getInstance().getLocation();
		} else {
			playerStation = (int) SmartDashboard.getNumber("Autonomous Player Station", 2.0);
		}
		
		/*System.out.println("Performing autonomous for:");
		System.out.println("\tPlayer Station: "+ playerStation);
		System.out.println("\tPriority: " + autonChooser.getSelected());
		System.out.println("\tData: "+ DriverStation.getInstance().getGameSpecificMessage());*/
		
		AutonPriority priority = autonChooser.getSelected();
		AutonPosition position = positionChooser.getSelected();

		
		//Vision.setCubeTargetMode();
		driveBase.setHome();
		/*driveBase.setVisionSource();
		driveBase.setPIDVision();*/
		//driveBase.drivePID(20);
		//driveBase.turnPID(45);
		//driveBase.driveAtAnglePID(0.4, 0, DriveBase.TitanPIDSource.VISION, Vision.TargetMode.Cube);
		
		/*switch(position) {
		case CENTER: {
				switch(priority) {
				case SWITCH: {
						game.setSelectedObject(FieldObject.SWITCH);
						//aSteps.add(new DriveStep(60.0));
						game.runSide(new SideChooser() {
							@Override
							public void left() {
								aSteps.add(new DriveStep(46.5, -65.0));
								aSteps.add(new DriveStep(54.5, 78));
							}

							@Override
							public void right() {
								//aSteps.add(new DriveStep(3.0));
								//aSteps.add(new TurnStep(35.0)); 
								aSteps.add(new DriveStep(60.0, 30.0));
								aSteps.add(new DriveStep(32.0, -70.0));
								//aSteps.add(new TurnStep(-35.0)); 
								//aSteps.add(new DriveStep(10.0));
							}
						});
					}
					break;
				}
			}
			break;
		case RIGHT: {
				switch(priority) { // TODO ADD A MIDDLE CUBE BLOCK THAT TURNS AND SHOOTS A BLOCK
				case SWITCH_SCALE:
				case SWITCH: {
						game.setSelectedObject(FieldObject.SWITCH);
						//aSteps.add(new DriveStep(60.0));
						game.runSide(new SideChooser() {
							@Override
							public void left() {
								//aSteps.add(new DriveStep(46.5, -65.0));
								//aSteps.add(new DriveStep(54.5, 78));
							}
	
							@Override
							public void right() {
								aSteps.add(new DriveStep(88.0, -25.0));
								if(priority == AutonPriority.SWITCH_SCALE) {
									aSteps.add(new DriveStep(-2));
									aSteps.add(new TurnStep(-180));
									aSteps.add(new DriveStep(-15));
								}
							}
						});
					}
					break;
				case SCALE: {
						game.setSelectedObject(FieldObject.SCALE);
						game.runSide(new SideChooser() {
							@Override
							public void left() {
								//aSteps.add
								
							}

							@Override
							public void right() {
								aSteps.add(new DriveStep(265.0));
								aSteps.add(new WaitStep(250));
								aSteps.add(new TurnStep(-60.0));
							}							
						});
					}
					break;
				}
			}
			break;
		}*/
		
		aSteps.add(new MimicStep(Paths.RIGHT_SCALE));
		
		//aSteps.add(new TurnStep(270));
		//aSteps.add(new DriveStep(100)); //new DriveStep(100, 0));
		
		//Initialize the autonomous routine
		final Step initStep = aSteps.peek();
		if(initStep != null) {
			initStep.init(this);
			initStep.startTimer();
			Titan.l("Starting with %s (%s)", initStep.getName(), initStep.getProperties());
		}
	}

	@Override
	public void autonomousPeriodic() {
		if (!aSteps.isEmpty()) {
			final Step step = aSteps.peek();
			final StepResult result = step.periodic(this);
			if (result == StepResult.COMPLETE) {
				final double secondsElapsed = step.getSecondsElapsed();
				Titan.l("Finished %s (Seconds: %.2f)", step.getName(), secondsElapsed);
				step.done(this);
				aSteps.remove();
				final Step nextStep = aSteps.peek();
				if (nextStep != null) {
					nextStep.init(this);
					nextStep.startTimer();
					Titan.l("Starting %s (%s)", nextStep.getName(), nextStep.getProperties());
				}
			}
		} else {
			driveBase.drive(0.0, 0.0);
		}
		//System.out.println(CubeFinder.getAngleFromClosestCube());
	}

	@Override
	public void teleopInit() {
		driveBase.setHome();
		driveBase.setBrakeMode(false);
		
		if(Constants.AUTO_LOG_PATHFINDING) {
			Mimic.Observer.prepare(Constants.AUTO_LOG_PATHFINDING_NAME);
		}
	}

	@Override
	public void teleopPeriodic() {
		if(Constants.AUTO_LOG_PATHFINDING) {
			final double vals[] = teleop.periodicPathfindingDrive(this);
			Mimic.Observer.addStep(this, vals);
		} else {
			teleop.periodicDrive(this);
		}
		
		//teleop.periodicIntake(this);
		//climber.setClimbing(operator.getRawButton(Titan.Xbox.Button.BUMPER_R));
		//catapult.setLowering(operator.getRawButton(Titan.Xbox.Button.B));
	}

	@Override
	public void robotPeriodic() {
		Vision.periodic();
	}
	
	private void updatePID() {
		double p = SmartDashboard.getNumber("P", 0.0);
		double i = SmartDashboard.getNumber("I", 0.0);
		double d = SmartDashboard.getNumber("D", 0.0);
		driveBase.drivePID.setPID(p, i, d, 0.0);
	}
	
	@Override
	public void testInit() {
		Titan.l("Starting test mode!");
		/*PIDTest testPID = pidChooser.getSelected();
		double speed = SmartDashboard.getNumber("TestDriveSpeed", 0.25);
		double angle = SmartDashboard.getNumber("TestDriveHeading", 0.0);
		Titan.l("Speed: %.4f Angle: %.2f", speed, angle);
		
		switch(testPID) {
		case HEADING:
			driveBase.disableAllPID();
			updatePID();
			driveBase.driveAtAnglePID(speed, angle);
			updatePID();
			break;
		case TURNING:
			driveBase.disableAllPID();
			updatePID();
			driveBase.turnPID(angle);
			updatePID();
		case NONE:
			break;
		default:
			break;
		}*/
		//Logger.init("right_scale");
	}
	
	@Override
	public void testPeriodic() {
		teleop.periodicPathfindingDrive(this);
		SmartDashboard.putNumber("LeftEncoder", driveBase.getLeftDistance());
		SmartDashboard.putNumber("RightEncoder", driveBase.getRightDistance());
		/*if(pidChooser.getSelected() == PIDTest.NONE) {
			driveBase.disableAllPID();
		}*/
		///Logger.addData(this);
	}

	public void disabledInit() {
		Vision.setNormalTargetMode();
		driveBase.disableAllPID();
		if(Constants.AUTO_LOG_PATHFINDING) {
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
}

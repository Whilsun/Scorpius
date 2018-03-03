package org.usfirst.frc.team5431.robot.auton;

import java.util.Queue;

import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.Robot.AutonPriority;
import org.usfirst.frc.team5431.robot.Titan.GameData.FieldObject;
import org.usfirst.frc.team5431.robot.Titan.GameData.SideChooser;
import org.usfirst.frc.team5431.robot.Robot.AutonPosition;

public class BuildAutonomousCommand extends Titan.Command<Robot> {
	private final AutonPriority priority;
	private final AutonPosition position;
	
	public BuildAutonomousCommand(final AutonPriority pri, final AutonPosition pos) {
		name = "BuildAutonomousStep ";
		properties = "Build the autonomous based off of the game data";
		
		priority = pri;
		position = pos;
	}

	@Override
	public CommandResult periodic(final Robot robot) {
		return CommandResult.COMPLETE;
	}

	@Override
	public void init(final Robot robot) {
		robot.getGameData().init(); //Capture the current game data
	}

	@Override
	public void done(final Robot robot) {
		final Titan.GameData game = robot.getGameData();
		final Titan.CommandQueue<Robot> aSteps = robot.getAutonSteps();
		if(priority == AutonPriority.AUTO_LINE) {
			aSteps.add(new DriveCommand(120, 0.0, 5000)); //Five second timeout
		} else {
			switch(position) {
			case CENTER: {
					switch(priority) {
					case SWITCH: {
							game.setSelectedObject(FieldObject.SWITCH);
							//aSteps.add(new DriveStep(60.0));
							game.runSide(new SideChooser() {
								@Override
								public void left() {
									aSteps.add(new DriveCommand(-48, -63.5));
									aSteps.add(new DriveCommand(-48, 78, 3000));
								}
	
								@Override
								public void right() {
									aSteps.add(new DriveCommand(-64.0, 21.0));
									aSteps.add(new DriveCommand(-33, -72.0, 3000)); //Three second timeout
								}
							});
							aSteps.add(new SwitchCubeCommand());
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
									aSteps.add(new DriveCommand(-88.0, 25.0));
									if(priority == AutonPriority.SWITCH_SCALE) {
										aSteps.add(new DriveCommand(2));
										aSteps.add(new TurnCommand(180));
										aSteps.add(new DriveCommand(15));
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
									aSteps.add(new DriveCommand(-265.0));
									aSteps.add(new WaitCommand(-250));
									aSteps.add(new TurnCommand(60.0));
								}							
							});
						}
						break;
					}
				}
				break;
			}
		}
	}
}
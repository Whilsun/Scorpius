package org.usfirst.frc.team5431.robot.commands;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.Titan.Command.CommandResult;

public class ScaleAutonomousCommand extends Titan.Command<Robot>{
	private final Titan.CommandQueue<Robot> commands = new Titan.CommandQueue<>();
	private final Titan.GameData data;
	
	public ScaleAutonomousCommand(final Titan.GameData data) {
		this.data = data;
	}
	
	@Override
	public void init(final Robot robot) {
		name = "ScaleAutonomousCommand";
		properties = "Do scale autonomous on a side";
				
		final double autonMultiplier = data.getAllianceSwitch() == Titan.GameData.Position.LEFT ? -1.0 : 1.0;
		
		commands.add(new DriveCommand(265.0));
		commands.add(new WaitCommand(250));
		commands.add(new TurnCommand(60.0 * autonMultiplier));
		commands.add(new ElevatorHeightCommand(Constants.HEIGHT_SCALE));
		commands.add(new OuttakeCommand(Constants.AUTO_OUTTAKE_TIME));
	}

	@Override
	public CommandResult update(final Robot robot) {
		if(!commands.update(robot)) {
			return CommandResult.COMPLETE;
		}
		
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void done(final Robot robot) {
	}
}
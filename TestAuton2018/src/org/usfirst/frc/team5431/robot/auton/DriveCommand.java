package org.usfirst.frc.team5431.robot.auton;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.Titan.Command.CommandResult;
import org.usfirst.frc.team5431.robot.components.DriveBase;
import org.usfirst.frc.team5431.robot.vision.Vision;

public class DriveCommand extends Titan.Command<Robot> {
	private final double distance, angle, speed;
	private long timeout = 1000000;

	public DriveCommand(final double dis) {
		this(dis, 0.0);
	}

	public DriveCommand(final double dis, final double ang) {
		this(dis, ang, Constants.AUTO_ROBOT_DEFAULT_SPEED, 1000000);
	}
	
	public DriveCommand(final double dis, final double ang, final long timet) {
		this(dis, ang, Constants.AUTO_ROBOT_DEFAULT_SPEED, timet);
	}

	public DriveCommand(final double dis, final double ang, final double spd, final long timet) {
		distance = dis;
		angle = ang;
		timeout = timet;

		if (distance < 0)
			speed = -spd;
		else
			speed = spd;

		name = "DriveStep";
		properties = String.format("Distance %.2f : Heading %.2f", distance, angle);
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public CommandResult periodic(final Robot robot) {
		if (robot.getDriveBase().hasTravelled(distance) || getElapsed() > timeout) {
			return CommandResult.COMPLETE;
		}
		return CommandResult.IN_PROGRESS;
	}

	@Override
	public void init(final Robot robot) {
		robot.getDriveBase().reset();
		robot.getDriveBase().drivePID(distance, angle, speed, DriveBase.TitanPIDSource.NAVX, Vision.TargetMode.Normal);
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().disableAllPID();
		robot.getDriveBase().drive(0.0, 0.0);
	}
}

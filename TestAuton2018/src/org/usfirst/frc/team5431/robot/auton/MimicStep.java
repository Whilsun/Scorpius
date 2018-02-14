package org.usfirst.frc.team5431.robot.auton; //Change to allow negative distance values

import java.util.ArrayList;

import org.usfirst.frc.team5431.robot.Constants;
import org.usfirst.frc.team5431.robot.Robot;
import org.usfirst.frc.team5431.robot.Titan;
import org.usfirst.frc.team5431.robot.pathfinding.Mimic;
import org.usfirst.frc.team5431.robot.pathfinding.Mimic.Stepper;

//FIX DUAL ENCODER <!>
public class MimicStep extends Step {
	public static enum Paths {
		RIGHT_SCALE
	}
	
	private int currentStep = 0;
	private int skippedSteps = 0;
	private double previousLeft = 0.0;
	private double previousRight = 0.0;
	private double futureLeft = 1.0;
	private double futureRight = 1.0;
	private double futureIntegral = 0.05;
	private final ArrayList<Stepper> steps;
	
	public MimicStep(final Paths mimic) {
		name = "PathfindingStep";
		
		//Collect the mimic file
		Mimic.Repeater.prepare(mimic.toString().toLowerCase());
		steps = Mimic.Repeater.getData();
		
		properties = String.format("Steps %d", steps.size());
	}
	
	@Override
	public void init(final Robot robot) {
		robot.getDriveBase().setHome();
	}

	@Override
	public StepResult periodic(final Robot robot) {
		boolean nextLeftStep = true;
		boolean nextRightStep = true;
		try {
			final Stepper step = steps.get(currentStep);
			final double lPower = (step.leftPower * futureLeft);
			final double rPower = (step.rightPower * futureRight);
			
			final double curLeft = robot.getDriveBase().getLeftDistance();
			final double curRight = robot.getDriveBase().getRightDistance();
			
			final boolean moveFLeft = (step.leftDistance > previousLeft) || (step.leftPower > 0);
			final boolean moveFRight = (step.rightDistance > previousRight) || (step.rightPower > 0);
			
			if(moveFLeft) {
				if(curLeft < step.leftDistance) {
					Titan.l("Mimic left is falling behind!");
					nextLeftStep = false;
				}
			} else {
				if(curLeft > step.leftDistance) {
					Titan.l("Mimic right is too far ahead!");
					nextLeftStep = false;
				}
			}
			
			if(moveFRight) {
				if(curRight < step.rightDistance) {
					Titan.l("Mimic right is falling behind!");
					nextRightStep = false;
				}
			} else {
				if(curRight > step.rightDistance) {
					Titan.l("Mimic right is too far ahead!");
					nextRightStep = false;
				}
			}
			
			if(!nextLeftStep && nextRightStep) {
v 				futureLeft += futureIntegral;
				futureRight -= futureIntegral;
			}///step.rightPower / (Math.abs(step.leftDistance - step.rightDistance) / 2.0);
			else if(nextLeftStep && !nextRightStep) {
				futureLeft -= futureIntegral;
				futureRight += futureIntegral;
			} else if(nextLeftStep && nextRightStep){
				if(futureLeft < 1.0) futureLeft += futureIntegral;
				else if(futureLeft > 1.0) futureLeft -= futureIntegral;
				
				if(futureRight < 1.0) futureRight += futureIntegral;
				else if(futureRight > 1.0) futureRight -= futureIntegral;
			}
			
			if(futureLeft <= 0.1) futureLeft = 0.2;
			else if(futureLeft >= 1.9) futureLeft = 1.8;
			
			if(futureRight <= 0.1) futureRight = 0.2;
			else if(futureRight >= 1.9) futureRight = 1.8;
			
			if(!nextLeftStep && 
					((Math.abs(step.leftPower) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED)) ||
						(Math.abs(step.leftDistance) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_DISTANCE)) { // && !(Math.abs(step.drivePower) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED)) {
				nextLeftStep = true;
			}
			
			if(!nextRightStep && 
					((Math.abs(step.rightPower) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED)) ||
						(Math.abs(step.rightDistance) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_DISTANCE)) { // && !(Math.abs(step.drivePower) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED)) {
				nextRightStep = true;
			}
			
			previousLeft = curLeft;
			previousRight = curRight;
			
			robot.getDriveBase().drive(lPower, rPower);
		} catch (IndexOutOfBoundsException e) {}
		if(nextLeftStep && nextRightStep || skippedSteps > 5) {
			if(((currentStep++) + 1) > steps.size()) return StepResult.COMPLETE;
			skippedSteps = 0;
		} else {
			skippedSteps++;
		}
		return StepResult.IN_PROGRESS;
		/*try {
			Stepper step = steps.get(currentStep);
			if(!step.isDrive && !step.isTurn) {
				robot.getDriveBase().setHome();
			} else if(step.isDrive && !wasDrive) {
				robot.getDriveBase().driveAtAnglePID(step.drivePower, step.angle, TitanPIDSource.NAVX, Vision.TargetMode.Normal);
				nextStep = false;
			} else if(step.isDrive  && wasDrive) {
				robot.getDriveBase().updateStepResults(step.drivePower, step.angle);
				if(!robot.getDriveBase().hasTravelled(step.distance) && !(Math.abs(step.drivePower) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_SPEED)) {
					Titan.l("Pathfinding is falling behind!");
					nextStep = false;
				}
			} else if(step.isTurn && !wasTurn) {
				robot.getDriveBase().turnPID(step.angle, TitanPIDSource.NAVX, Vision.TargetMode.Normal);
				nextStep = false;
			} else if(step.isTurn && wasTurn) {
				robot.getDriveBase().updateStepResults(step.drivePower, step.angle);
				if(!robot.getDriveBase().hasTurned(step.angle) && !(Math.abs(step.angle) < Constants.AUTO_PATHFINDING_OVERRIDE_NEXT_STEP_TURN)) {
					Titan.l("Pathfinding is falling behind!");
					nextStep = false;
				}
			} else {
				Titan.e("Invalid step command!");
			}
			
			wasDrive = step.isDrive;
			wasTurn = step.isTurn;
		} catch (IndexOutOfBoundsException e) {}
		if(nextStep) if(((currentStep++) + 1) > steps.size()) return StepResult.COMPLETE;*/
	}

	@Override
	public void done(final Robot robot) {
		robot.getDriveBase().disableAllPID();
		robot.getDriveBase().drive(0.0, 0.0);
	}
}

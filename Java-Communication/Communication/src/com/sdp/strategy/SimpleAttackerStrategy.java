package com.sdp.strategy;

import java.awt.geom.Point2D;

import com.sdp.RobotCommunication;
import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.WorldState;


public class SimpleAttackerStrategy extends GeneralStrategy {
	private final double deadZone = 1.0;
	private boolean isRobotFacingBall = false;

	public void sendWorldState(DynamicWorldState dynWorldState, 
			WorldState worldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		// 1. change direction so that robot looks towards the ball
		double diffInHeadings = RobotPlanner.differenceInHeadings(robot, ball);

		if (diffInHeadings < deadZone) {
			isRobotFacingBall = true;
		} else {
			isRobotFacingBall = false;
		}
		// Decide which direction to rotate
		if (diffInHeadings < Math.PI && !isRobotFacingBall) {
			// rotate right
			RobotCommands.rotateRight();
		} else if (diffInHeadings > Math.PI && !isRobotFacingBall) {
			// rotate left
			RobotCommands.rotateLeft();
		}

		// 2. go straight until you can catch the ball
		boolean canCatchBall = RobotPlanner.canCatchBall(robot, ball);
		boolean doesOurRobotHaveBall = RobotPlanner.doesOurRobotHaveBall(robot,
				ball);
		if (!canCatchBall && !doesOurRobotHaveBall && isRobotFacingBall) {
			RobotCommands.goStraight();
			return;
		}

		// 3. catch the ball if we don't have it
		if (!doesOurRobotHaveBall) {
			if (canCatchBall) {
				RobotCommunication.getInstance().sendCatch();
			}
		}
		// resets catch flag if ball is too far away
		boolean catchResetFlag = RobotPlanner.catchReset(robot, ball);
		if (catchResetFlag) {
			RobotCommunication.getInstance().catchReset();
		}

		// 4. go to a position from which robot can score and score
		if (doesOurRobotHaveBall) {
			scoreGoal(dynWorldState, worldState);
		}
	}

	/**
	 * goes to a position from which it can score and scores
	 * 
	 * @param dynWorldState
	 */
	private void scoreGoal(DynamicWorldState dynWorldState, WorldState worldState) {
		Robot robot = dynWorldState.getAttacker();

		/* Establish the centre of the field */
		Point2D centre = new Point2D.Double(0, 0);

		/* Determine if Robot is facing the centre */
		double diffInHeadingsCentre = RobotPlanner.differenceInHeadingsGeneral(
				robot, centre);
		boolean facingCentre;

		/* If facing (within a certain threshold */
		if (diffInHeadingsCentre < deadZone) {
			facingCentre = true;
		} else {
			facingCentre = false;
		}

		// Decide which direction to rotate
		if (!facingCentre) {
			// rotate right
			RobotCommands.rotateRight();
		}

		/* DETERMINE WHEN WE ARE AT THE CENTRE, IF NOT MOVE */
		boolean inCentre = RobotPlanner.inCentreRange(robot, centre);

		// move to the centre
		if (!inCentre && facingCentre) {
			RobotCommands.goStraight();
			return;
		}
		
		/* Determine correct goal to face */
		/* We are shooting right */
		if (worldState.weAreShootingRight){
			Point2D centreGoalR = new Point2D.Double(640, (double)worldState.rightGoal[1]);
			double diffInHeadingsGoal = RobotPlanner.differenceInHeadingsGeneral(
					robot, centreGoalR);
			
			boolean facingGoal;
			if (diffInHeadingsGoal < deadZone) {
				facingGoal = true;
			} else {
				facingGoal = false;
			}
			
			if (!facingGoal) {
				// rotate right
				RobotCommands.rotateRight();
			}
		/* We are shooting left */	
	}else{
		Point2D centreGoalL = new Point2D.Double(640, (double)worldState.leftGoal[1]);
		double diffInHeadingsGoal = RobotPlanner.differenceInHeadingsGeneral(
				robot, centreGoalL);
		
		boolean facingGoal;
		if (diffInHeadingsGoal < deadZone) {
			facingGoal = true;
		} else {
			facingGoal = false;
		}
		
		if (!facingGoal) {
			// rotate right
			RobotCommands.rotateRight();
			}
			
		}
	}
}

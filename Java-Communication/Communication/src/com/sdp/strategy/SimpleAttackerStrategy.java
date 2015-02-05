package com.sdp.strategy;

import java.awt.geom.Point2D;

import com.sdp.RobotCommunication;
import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class SimpleAttackerStrategy extends GeneralStrategy {
	private final double DIFFERENCE_IN_HEADINGS = 0.6;
	private boolean isRobotFacingBall = false;

	public void sendWorldState(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		// 1. change direction so that robot looks towards the ball
		double diffInHeadings = RobotPlanner.differenceInHeadings(robot, ball);

		if (diffInHeadings < DIFFERENCE_IN_HEADINGS) {
			isRobotFacingBall = true;
		} else {
			isRobotFacingBall = false;
		}
		// Decide which direction to rotate
		if (!isRobotFacingBall) {
			// rotate right
			RobotCommands.rotateRight();
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

		boolean catchResetFlag = RobotPlanner.catchReset(robot, ball);
		if (catchResetFlag) {
			RobotCommunication.getInstance().catchReset();
		}

		// 4. go to a position from which robot can score and score
		if (doesOurRobotHaveBall) {
			scoreGoal(dynWorldState);
		}
	}

	/**
	 * goes to a position from which it can score and scores
	 * 
	 * @param dynWorldState
	 */
	private void scoreGoal(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();

		/* Establish the centre of the field */
		Point2D centre = new Point2D.Double(0, 0);

		/* Determine if Robot is facing the centre */
		double diffInHeadingsCentre = RobotPlanner.differenceInHeadingsCentre(
				robot, centre);
		boolean facingCentre;

		/* If facing (within a certain threshold */
		if (diffInHeadingsCentre < DIFFERENCE_IN_HEADINGS) {
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
	}
}

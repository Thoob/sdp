package com.sdp.strategy;

import java.awt.geom.Point2D;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	private final double deadZone = 1.0; // ???
	private boolean isRobotFacingBall = false;
	private final int allowedDegreeError = 20;

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		// FOR USE WITH CALULATE ANGLE
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double robotDir = robot.getHeading();
		double ballX = ball.getPoint().getX();
		double ballY = ball.getPoint().getY();

		// 1. change direction so that robot looks towards the ball
		double desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, ballX, ballY);
		double robotAngleDeg = Math.toDegrees(robotDir);
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);

		/* case we use calculate angle over Calculate desired heading */
		// if robotY < ballY then faces ball normally
		if ((diffInHeadings < allowedDegreeError)
				|| (diffInHeadings > 360 - allowedDegreeError)) {
			isRobotFacingBall = true;

			System.out.println("DESIRED ANGLE");
			// stopping rotation but not other operations
			if (SimpleWorldState.previousOperation == Operation.RIGHT
					|| SimpleWorldState.previousOperation == Operation.LEFT) {
				RobotCommands.stop();
				SimpleWorldState.previousOperation = Operation.NONE;
			}
		} else if ((diffInHeadings < allowedDegreeError * 2)
				|| (diffInHeadings > 360 - allowedDegreeError * 2)) {
			isRobotFacingBall = false;

			boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
					desiredAngleDeg, robotAngleDeg);
			if (shouldRotateRight) {
				RobotCommands.shortRotateRight();
				SimpleWorldState.previousOperation = Operation.SHORT_RIGHT;
			} else if (!shouldRotateRight) {
				RobotCommands.shortRotateLeft();
				SimpleWorldState.previousOperation = Operation.SHORT_LEFT;
			}
			return;

		} else {
			isRobotFacingBall = false;

			boolean shouldRotateRight = RobotPlanner.shouldRotateRight(
					desiredAngleDeg, robotAngleDeg);
			if (shouldRotateRight
					&& SimpleWorldState.previousOperation != Operation.RIGHT) {
				RobotCommands.rotateRight();
				SimpleWorldState.previousOperation = Operation.RIGHT;
			} else if (!shouldRotateRight
					&& SimpleWorldState.previousOperation != Operation.LEFT) {
				RobotCommands.rotateLeft();
				SimpleWorldState.previousOperation = Operation.LEFT;
			}
			return;
		}

		// 2. go straight until you can catch the ball
		// boolean canCatchBall = RobotPlanner.canCatchBall(robot, ball);
		// boolean doesOurRobotHaveBall =
		// RobotPlanner.doesOurRobotHaveBall(robot,
		// ball);
		// if (!canCatchBall && !doesOurRobotHaveBall && isRobotFacingBall
		// && SimpleWorldState.previousOperation != Operation.FORWARD) {
		// RobotCommands.goStraight();
		// SimpleWorldState.previousOperation = Operation.FORWARD;
		// return;
		// } else if (!doesOurRobotHaveBall && isRobotFacingBall) {
		// // 3. catch the ball
		// if (SimpleWorldState.previousOperation != Operation.NONE
		// && SimpleWorldState.previousOperation != Operation.CATCH) {
		// RobotCommands.stop();
		// SimpleWorldState.previousOperation = Operation.NONE;
		// }
		// // avoid multiple catch
		// if (SimpleWorldState.previousOperation != Operation.CATCH) {
		// RobotCommands.catchBall();
		// SimpleWorldState.previousOperation = Operation.CATCH;
		// }
		// } else if(doesOurRobotHaveBall){
		// // 4. go to a position from which robot can score and score
		// // scoreGoal(dynWorldState, worldState);
		// RobotCommands.stop();
		// SimpleWorldState.previousOperation = Operation.NONE;
		// System.out.println("We should have catched the ball");
		// }
	}

	/**
	 * goes to a position from which it can score and scores
	 * 
	 * @param dynWorldState
	 */
	private void scoreGoal(DynamicWorldState dynWorldState,
			WorldState worldState) {
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
		if (worldState.weAreShootingRight) {
			Point2D centreGoalR = new Point2D.Double(640,
					(double) worldState.rightGoal[1]);
			double diffInHeadingsGoal = RobotPlanner
					.differenceInHeadingsGeneral(robot, centreGoalR);

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
		} else {
			Point2D centreGoalL = new Point2D.Double(640,
					(double) worldState.leftGoal[1]);
			double diffInHeadingsGoal = RobotPlanner
					.differenceInHeadingsGeneral(robot, centreGoalL);

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

package com.sdp.strategy;

import java.awt.geom.Point2D;
import java.beans.DesignMode;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	private final int allowedDegreeError = 30;
	private boolean isRobotFacingBall = false;
	private boolean isRobotFacingGoal = false;

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();
		
		// Values that we need to calculate
		// Robot position (x,y) and heading in degrees
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double robotDir = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotDir);
		// Desired angle to face ball
		double ballAngleDeg = RobotPlanner.desiredAngle(robotX, robotY, robotDir, ballX, ballY);
		double ballDiffInHeadings = Math.abs(robotAngleDeg - ballAngleDeg);
		// Robot is facing the ball if within this angle in degrees of the ball
		isRobotFacingBall = ballDiffInHeadings < allowedDegreeError * 2;
		// Desired angle to face goal (goalX and centre of goalY)
		//double goalAngleDeg = RobotPlanner.desiredAngle(robotX, robotY, robotDir, goalX, goalY[0]);
		
		
		// 1 - Rotate to face ball
		if(!RobotPlanner.doesOurRobotHaveBall(robot, ball) && !isRobotFacingBall){
			rotateToDesiredAngle(robotAngleDeg, ballAngleDeg);
			System.out.println("Rotating to face ball.");
		}
		
		// 2 - Go towards ball
		if(!RobotPlanner.doesOurRobotHaveBall(robot, ball) && isRobotFacingBall && !RobotPlanner.canCatchBall(robot, ball)){
			//TODO needs to only happen when the ball is in our zone
			RobotCommands.goStraight();
			System.out.println("Moving towards ball.");
		}
		
		// 3 - Catch ball
		if(!RobotPlanner.doesOurRobotHaveBall(robot, ball) && isRobotFacingBall && RobotPlanner.canCatchBall(robot, ball)){
			RobotCommands.catchBall();
			System.out.println("Catching ball.");
		}
		
		// 4 - Rotate to face goal
//		if(RobotPlanner.doesOurRobotHaveBall(robot, ball) && !isRobotFacingGoal){
//			rotateToDesiredAngle(robotAngleDeg, goalAngleDeg);
//		}
		
		// 5 - Kick ball (hopefully into the goal!)
		if(RobotPlanner.doesOurRobotHaveBall(robot, ball) && isRobotFacingGoal){
			//RobotCommands.kick();
			scoreGoal(dynWorldState, worldState);
			System.out.println("Scoring goal!");
		}
	}

	private void rotateToDesiredAngle(double robotAngleDeg,
			double desiredAngleDeg) {
		double diffInHeadings = Math.abs(robotAngleDeg - desiredAngleDeg);
		System.out.println("Difference in headings: " + diffInHeadings);
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
		} else {
			System.out.println("Current robot heading:" + robotAngleDeg);
			System.out.println("Angle to face:" + desiredAngleDeg);

			if ((diffInHeadings < allowedDegreeError * 2)
					|| (diffInHeadings > 360 - allowedDegreeError * 2)) {
				isRobotFacingBall = false;

				RobotCommands.stop();
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
		}

	}

	private void scoreAGoal(DynamicWorldState dynWorldState,
			WorldState worldState) {
		// turn towards the goal
		Ball ball = dynWorldState.getBall();
		double ballX = ball.getPoint().getX();
		double ballY = ball.getPoint().getY();
		Robot robot = dynWorldState.getAttacker();
		double robotX = robot.getCenter().getX();
		double robotY = robot.getCenter().getY();
		double robotDir = robot.getHeading();
		double robotAngleDeg = Math.toDegrees(robotDir);
		double goalX = SimpleGeneralStrategy.rightGoalX;
		double goalY = SimpleGeneralStrategy.rightGoalY;
		boolean robotHasBall = RobotPlanner.doesOurRobotHaveBall(robot, ball);

		System.out.println("goal " + goalX + " " + goalY);
		System.out.println("robot " + robotX + " " + robotY);
		System.out.println("ball " + ballX + " " + ballY);

		double desiredAngleDegb = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, ballX, ballY);

		System.out.println("desiredAngleBall " + desiredAngleDegb);

		double desiredAngleDeg = RobotPlanner.desiredAngle(robotX, robotY,
				robotDir, goalX, goalY);

		System.out.println("desiredAngleGoal " + desiredAngleDeg);

		rotateToDesiredAngle(robotAngleDeg, desiredAngleDeg);

		if (SimpleWorldState.previousOperation != Operation.KICK
				&& robotHasBall) { //change to doesRobotHaveBall
			RobotCommands.kick();
			//SimpleWorldState.previousOperation = Operation.KICK;
		}
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

		if (diffInHeadingsCentre < allowedDegreeError) {
			facingCentre = true;
		} else {
			facingCentre = false;
		}

		if (!facingCentre) {
			RobotCommands.rotateRight();
		}

		boolean inCentre = RobotPlanner.inCentreRange(robot, centre);

		if (!inCentre && facingCentre) {
			RobotCommands.goStraight();
			return;
		}

		if (worldState.weAreShootingRight) {
			Point2D centreGoalR = new Point2D.Double(640,
					(double) worldState.rightGoal[1]);
			double diffInHeadingsGoal = RobotPlanner
					.differenceInHeadingsGeneral(robot, centreGoalR);

			boolean facingGoal;
			if (diffInHeadingsGoal < allowedDegreeError) {
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
			if (diffInHeadingsGoal < allowedDegreeError) {
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

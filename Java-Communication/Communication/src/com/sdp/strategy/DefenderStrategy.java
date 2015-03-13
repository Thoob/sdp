package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
import com.sdp.prediction.Calculations;
import com.sdp.prediction.Oracle;
import com.sdp.world.MovingObject;
import com.sdp.world.Point2;
import com.sdp.world.WorldState;

public class DefenderStrategy extends GeneralStrategy {
	private Oracle predictor = null;
	private final int framesForward = 20;
	StrategyHelper sh;

	public DefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);
		// TODO check if enemyAttackerHasBall?

		boolean movingTowardsUs = isBallMovingTowardsUs(worldState);
		double goalCenterX = getOurGoalX(worldState);
		boolean enemyAttackerHasBall = RobotPlanner.doesEnemyAttackerHaveBall(
				worldState, robotX, ballX);

		double predictedY = getEnemyAttackerHeadingY(worldState);
		if (enemyAttackerHasBall && predictedY != -1) {
			System.out
					.println("predicted y " + predictedY + " our y " + robotY);
			sh.goTo(goalCenterX, predictedY, worldState);
			// } else if (movingTowardsUs) {
			// defendMovingBall(worldState);
		} else {
			// Move to the center of the goal and head straight
			System.out.println("going to default position");
			double goalCenterY = getOurGoalY(worldState);

			sh.goTo(goalCenterX, goalCenterY, worldState);
		}
	}

	private void defendMovingBall(WorldState worldState) {
		System.out.println("moving towards us");
		// Predicting ball's y coordinate
		double collisionY = predictedYCoord(worldState);

		// Checking if it is within goal range
		boolean isInGoalRange = isInGoalRange(collisionY, worldState);
		if (isInGoalRange) {
			System.out.println("ball moving towards the goal");
			// Moving to this position
			if (Math.abs(collisionY - robotY) > allowedDistError) {
				if (shouldWeMoveForward(collisionY, robotY)) {
					RobotCommands.goStraightFast();
				} else if (shouldWeMoveBackward(collisionY, robotY)) {
					RobotCommands.goStraightBackwardsFast();
				}
			}
		}

	}

	private boolean isInGoalRange(double collisionY, WorldState worldState) {
		if (worldState.weAreShootingRight) {
			return collisionY - allowedDistError > worldState.leftGoal[0]
					&& collisionY + allowedDistError < worldState.leftGoal[2];
		} else {
			return collisionY - allowedDistError > worldState.rightGoal[0]
					&& collisionY + allowedDistError < worldState.rightGoal[2];
		}
	}

	private float getSlope(WorldState worldState) {
		ArrayList<Point2> ballPositionHistory = worldState
				.getBallPositionHistory();
		Point2 ballPos = new Point2((float) ballX, (float) ballY);
		Point2 predictedPos = this.predictor.predictState(ballPositionHistory,
				framesForward);
		System.out.println("current pos " + ballPos.getY() + " Predicted pos "
				+ predictedPos.getY());
		float slope = Calculations.getSlopeOfLine(ballPos, predictedPos);

		boolean shaky = isBallShaky(ballPos, predictedPos);
		if (Float.isInfinite(slope) || shaky) {
			return 0;
		} else {
			return slope;
		}
	}

	private boolean isBallMovingTowardsUs(WorldState worldState) {
		ArrayList<Point2> ballPositionHistory = worldState
				.getBallPositionHistory();

		boolean predictionIsGenerated = ballPositionHistory.size() > framesForward;
		if (!predictionIsGenerated)
			return false;
		else {
			Point2 predictedPos = this.predictor.predictState(
					ballPositionHistory, framesForward);

			if (worldState.weAreShootingRight
					&& predictedPos.getX() - ballX > 2
					|| !worldState.weAreShootingRight
					&& predictedPos.getX() - ballX < -2) {
				return true;
			}
			return false;
		}
	}

	private double predictedYCoord(WorldState worldState) {
		float slope = getSlope(worldState);

		double collisionX = robotX;
		double collisionY = -1;
		if (slope * collisionX > leftGoalY[2]) {
			collisionY = leftGoalY[2];
		} else if (slope * collisionX < leftGoalY[0]) {
			collisionY = leftGoalY[0];
		} else {
			collisionY = collisionX * slope;
		}

		System.out.println("Collision coordinates " + collisionX + " "
				+ collisionY);

		return robotY;
	}

	private boolean isBallShaky(Point2 ballPos, Point2 predictedPos) {
		double crosshairThreshX = Math
				.abs(ballPos.getX() - predictedPos.getX());
		double crosshairThreshY = Math
				.abs(ballPos.getY() - predictedPos.getY());
		if (crosshairThreshX > 2 || crosshairThreshY > 2) {
			return false;
		}
		return true;
	}

	private boolean shouldWeMoveForward(double collisionY, double robotY) {
		return collisionY < robotY && robotY > -120;
	}

	private boolean shouldWeMoveBackward(double collisionY, double robotY) {
		return collisionY > robotY && robotY < 220;
	}

	public static double getEnemyAttackerHeadingY(WorldState worldState) {
		MovingObject enemyAttacker = worldState.getEnemyAttackerRobot();
		double heading = enemyAttacker.orientationAngle;
		double angle = RobotPlanner.getAngleFromZero(heading);
		System.out.println("angle from zero " + angle);
		if ((Math.abs(angle) > 50 && !worldState.weAreShootingRight)
				|| ((Math.abs(angle) > 230 || Math.abs(angle) < 150) && worldState.weAreShootingRight))
			return -1;
		double deltaX = Math.abs(enemyAttacker.x - getOurGoalX(worldState));
		double deltaY = deltaX * Math.tan(Math.toRadians(angle));
		double predictedY = enemyAttacker.y - deltaY;
		return predictedY;

	}
}

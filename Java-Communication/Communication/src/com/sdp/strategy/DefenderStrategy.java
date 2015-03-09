package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.planner.RobotCommands;
import com.sdp.prediction.Calculations;
import com.sdp.prediction.Oracle;
import com.sdp.world.Point2;
import com.sdp.world.WorldState;

public class DefenderStrategy extends GeneralStrategy {
	private Oracle predictor = null;
	final int framesForward = 20;
	private final int defaultAngleDeg = 90;

	public DefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
	}

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);

		// Determine if the ball is moving towards us
		boolean movingTowardsUs = isBallMovingTowardsUs(worldState);
		if (movingTowardsUs) {
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
		} else {
			// Move to the center of the goal and head straight
			double goalCenterY = (worldState.weAreShootingRight) ? worldState.leftGoal[1]
					: worldState.rightGoal[1];
			System.out
					.println("Ball is not moving towards. Going to the center of the goal");
			System.out.println("RobotY " + robotY + " goal center y "
					+ goalCenterY);
			if (Math.abs(goalCenterY - robotY) > allowedDistError) {
				if (shouldWeMoveForward(goalCenterY, robotY)) {
					RobotCommands.goStraightFast();
				} else if (shouldWeMoveBackward(goalCenterY, robotY)) {
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

}

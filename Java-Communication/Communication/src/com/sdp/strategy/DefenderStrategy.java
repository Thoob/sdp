package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.Debug;
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
	boolean debug = true;

	public DefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);

		boolean movingTowardsUs = isBallMovingTowardsUs(worldState);
		double goalCenterX = getOurGoalX(worldState);
		// TODO check if enemyAttackerHasBall?
		boolean enemyAttackerHasBall = RobotPlanner.doesEnemyAttackerHaveBall(
				worldState, robotX, ballX);

		double predictedY = getEnemyAttackerHeadingY(worldState);
		if (enemyAttackerHasBall
				&& RobotPlanner.isInGoalRange(leftGoalY, rightGoalY,
						predictedY, worldState.weAreShootingRight, worldState)) {
			Debug.out("Going to attacker heading. Go to y ", predictedY);
			sh.goTo(goalCenterX, predictedY, worldState);
		} else if (movingTowardsUs) {
			Debug.out("Going to ball moving. Go to y ", ballY);
			defendMovingBall(worldState);
		} else {
			Debug.out("Going to default position.");
			double goalCenterY = getOurGoalY(worldState);
			sh.goTo(goalCenterX, goalCenterY, worldState);
		}

	}

	private void defendMovingBall(WorldState worldState) {
		double collisionY = ballY;
		double goalCenterX = getOurGoalX(worldState);
		boolean isInGoalRange = isInGoalRange(collisionY, worldState);

		if (isInGoalRange) {
			if (RobotPlanner.isHeadingVertically(robotAngleDeg)) {
				if (shouldWeMoveForward(collisionY, robotY, robotAngleDeg,
						worldState)) {
					RobotCommands.goStraightFast();
				} else if (shouldWeMoveBackward(collisionY, robotY,
						robotAngleDeg, worldState)) {
					RobotCommands.goStraightBackwardsFast();
				} else {
					RobotCommands.brakeStop();
				}
			} else {
				sh.goTo(goalCenterX, collisionY, worldState);
			}
		} else {
			Debug.out("Ball is not in goal range");
		}
	}

	private boolean isInGoalRange(double collisionY, WorldState worldState) {
		if (worldState.weAreShootingRight) {
			return collisionY > worldState.leftGoal[0] - 10
					&& collisionY < worldState.leftGoal[2] + 10;
		} else {
			return collisionY > worldState.rightGoal[0] - 10
					&& collisionY < worldState.rightGoal[2] + 10;
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

	private boolean shouldWeMoveForward(double collisionY, double robotY,
			double robotAngleDeg, WorldState worldState) {
		if (Math.abs(robotY - collisionY) < 10) {
			return false;
		}
		return ((collisionY > robotY && RobotPlanner
				.isHeadingDown(robotAngleDeg)) || (collisionY < robotY && !RobotPlanner
				.isHeadingDown(robotAngleDeg)));
	}

	private boolean shouldWeMoveBackward(double collisionY, double robotY,
			double robotAngleDeg, WorldState worldState) {
		if (Math.abs(robotY - collisionY) < 10) {
			return false;
		}
		return (collisionY < robotY && RobotPlanner
				.isHeadingDown(robotAngleDeg))
				|| collisionY > robotY
				&& !RobotPlanner.isHeadingDown(robotAngleDeg);
	}

	public static double getEnemyAttackerHeadingY(WorldState worldState) {
		MovingObject enemyAttacker = worldState.getEnemyAttackerRobot();
		if (enemyAttacker.x == 0 && enemyAttacker.y == 0)
			return -1;
		double heading = enemyAttacker.orientationAngle;
		double angle = RobotPlanner.getAngleFromZero(heading);
		if ((Math.abs(angle) > 50 && !worldState.weAreShootingRight)
				|| ((Math.abs(angle) > 230 || Math.abs(angle) < 150) && worldState.weAreShootingRight))
			return -1;
		double deltaX = Math.abs(enemyAttacker.x - getOurGoalX(worldState));
		double deltaY = deltaX * Math.tan(Math.toRadians(angle));
		double predictedY = enemyAttacker.y - deltaY;
		return predictedY;

	}
}

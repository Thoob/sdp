package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.Debug;
import com.sdp.planner.RobotCommands;
import com.sdp.planner.RobotPlanner;
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

		boolean movingTowardsUs = isBallMovingTowardsUs(worldState);
		boolean enemyAttackerHasBall = RobotPlanner.doesEnemyAttackerHaveBall(
				worldState, robotX, ballX);
		System.out.println("robotX " + robotX + " robotY " + robotY);
		double predictedY = getEnemyAttackerHeadingY(worldState);
		if (movingTowardsUs) {
			Debug.out("Going to ball moving. Go to y ", ballY);
			defendMovingBall(worldState);
		} else if (enemyAttackerHasBall
				&& RobotPlanner.isInGoalRange(predictedY, worldState)) {
			Debug.out("Going to attacker heading. Go to y ", predictedY);
			double goalCenterX = getOurGoalX(worldState);
			sh.goTo(goalCenterX, predictedY, worldState);
		} else {
			Debug.out("Going to default position.");
			double goalCenterY = getOurGoalY(worldState);
			double goalCenterX = getOurGoalX(worldState);
			sh.goTo(goalCenterX, goalCenterY, worldState);
		}
	}

	private void defendMovingBall(WorldState worldState) {
		double collisionY = ballY;
		double goalCenterX = getOurGoalX(worldState);
		boolean isInGoalRange = RobotPlanner.isInGoalRange(collisionY,
				worldState);
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
		double predictedY = enemyAttacker.y + deltaY;
		return predictedY;
	}
}

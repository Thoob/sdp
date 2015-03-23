package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.Debug;
//import com.sdp.Debug;
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
	private final int FRAMES_TO_AVERAGE = 3;
	private int currentFrames = 0;
	MovingObject ourRobotAvg;

	public DefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
		sh = new StrategyHelper();
	}

	public void sendWorldState(WorldState worldState) {
		initializeVars(worldState);

		if (currentFrames < FRAMES_TO_AVERAGE) {
			calcOurRobotAvg(worldState.getDefenderRobot());
			currentFrames++;
		} else {
			currentFrames = 0;
		}

		boolean movingTowardsUs = isBallMovingTowardsUs(worldState);
		boolean enemyAttackerHasBall = RobotPlanner.doesEnemyAttackerHaveBall(
				worldState, ourRobotAvg.x, ballX);
		System.out.println("robotX " + ourRobotAvg.x + " robotY " + robotY);
		double predictedY = getEnemyAttackerHeadingY(worldState);
		if (movingTowardsUs) {
			Debug.out("Going to ball moving. Go to y ", ballY);
			defendMovingBall(worldState);
		} else if (enemyAttackerHasBall
				&& RobotPlanner.isInGoalRange(predictedY, worldState)) {
			Debug.out("Going to attacker heading. Go to y ", predictedY);
			double goalCenterX = getOurGoalX(worldState);
			boolean shouldGoTo = shouldGoTo(goalCenterX, predictedY,
					ourRobotAvg.x, ourRobotAvg.y, worldState);
			if (shouldGoTo)
				sh.goToForDef(goalCenterX, predictedY, ourRobotAvg.x,
						ourRobotAvg.y, worldState);
			else {
				double neutralAngle = (robotAngleDeg > 180) ? 270 : 90;
				sh.rotateToDesiredAngleForDef(robotAngleDeg, neutralAngle, true);
			}
		} else {
			Debug.out("Going to default position.");
			double goalCenterY = getOurGoalY(worldState);
			double goalCenterX = getOurGoalX(worldState);

			boolean shouldGoTo = shouldGoTo(goalCenterX, goalCenterY,
					ourRobotAvg.x, ourRobotAvg.y, worldState);
			if (shouldGoTo)
				sh.goToForDef(goalCenterX, goalCenterY, ourRobotAvg.x,
						ourRobotAvg.y, worldState);
			else {
				double neutralAngle = (robotAngleDeg > 180) ? 270 : 90;
				Debug.out("Rotating to neutral angle ", neutralAngle);
				sh.rotateToDesiredAngleForDef(robotAngleDeg, neutralAngle, true);
			}
		}
	}

	private boolean shouldGoTo(double targetX, double targetY, double robotX,
			double robotY, WorldState worldState) {
		boolean isNearTarget = (RobotPlanner.nearTargetForDef(robotY, targetY));
		System.out.println("goTo for def " + targetY + " robotY " + robotY
				+ " near " + isNearTarget);
		if (isNearTarget || !sh.isSameZone(robotX, targetX, worldState)) {
			return false;
		} else {
			return true;
		}
	}

	private void calcOurRobotAvg(MovingObject ourRobot) {
		if (currentFrames == 0)
			ourRobotAvg = ourRobot;
		else {
			ourRobotAvg.x = (ourRobotAvg.x * currentFrames + ourRobot.x)
					/ (currentFrames + 1);
			ourRobotAvg.y = (ourRobotAvg.y * currentFrames + ourRobot.y)
					/ (currentFrames + 1);
			ourRobotAvg.orientationAngle = (ourRobotAvg.orientationAngle
					* currentFrames + ourRobot.orientationAngle)
					/ (currentFrames + 1);
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
				sh.goTo(goalCenterX, collisionY, ourRobotAvg.x, ourRobotAvg.y,
						worldState);
			}
		} else {
			// Debug.out("Ball is not in goal range");
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

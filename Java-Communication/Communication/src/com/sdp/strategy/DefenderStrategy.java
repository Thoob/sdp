package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.planner.RobotCommands;
import com.sdp.prediction.Calculations;
import com.sdp.prediction.Oracle;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.Point2;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.WorldState;


public class DefenderStrategy extends GeneralStrategy {
	private Oracle predictor = null;
	final int framesForward = 20;

	public DefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
	}

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		if(robot==null||ball==null)return;
		Point2 ballPos = new Point2((float) ball.getPoint().getX(),
				(float) ball.getPoint().getY());

		ArrayList<Point2> ballPositionHistory = worldState
				.getBallPositionHistory();

		// 1. Predict position where the ball will go
		System.out.println("Ball X: " + ball.getPoint().getX());
		System.out.println("Ball Y: " + ball.getPoint().getY());

		Point2 predictedPos = this.predictor.predictState(ballPositionHistory,
				framesForward);

		System.out.println("Current ball coordinates " + ballPos.getX() + " "
				+ ballPos.getY());

		System.out.println("Predicted ball coordinates " + predictedPos.getX()
				+ " " + predictedPos.getY());

		// 2. Predict ball y coordinate at near goal x coordinate
		float slope = Calculations.getSlopeOfLine(ballPos, predictedPos);

		// Boolean variable to ensure a prediction exists (as default is (0,0)
		// this leads a legitimate yet incorrect result
		boolean predictionIsGenerated = ballPositionHistory.size() > framesForward;
		boolean notShaky = isNotShaky(predictionIsGenerated, ballPos,
				predictedPos);

		if (!Float.isInfinite(slope) && notShaky) {
			System.out.println("BALL IS MOVING");
			double collisionX = robot.getCenter().getX();
			double collisionY = 50;
			if (slope * collisionX > leftGoalTopY) {
				collisionY = leftGoalTopY;
			} else if (slope * collisionX < leftGoalBotY) {
				collisionY = leftGoalBotY;
			} else {
				collisionY = collisionX * slope;
			}

			System.out.println("Slope " + slope);
			System.out.println("Collision coordinates " + collisionX + " "
					+ collisionY);
			double robotX = robot.getCenter().getX();
			double robotY = robot.getCenter().getY();
			System.out.println("Robot coordinates " + robotX + " " + robotY);

			if (Math.abs(collisionY - robotY) > allowedDistError) {
				if (shouldWeMoveForward(collisionY, robotY)) {
					RobotCommands.goStraightFast();
					System.out.println("GO FORWARD!");
				} else if (shouldWeMoveBackward(collisionY, robotY)) {
					RobotCommands.goStraightBackwardsFast();
					System.out.println("GO BACKWARD!");
				}
			} else {
				RobotCommands.stop();
			}
			// 3. Turn (if necessary) and go to this position
		} else if (SimpleWorldState.previousOperation != Operation.NONE) {
			RobotCommands.stop();
			SimpleWorldState.previousOperation = Operation.NONE;
		}
	}

	private boolean isNotShaky(boolean predictionIsGenerated, Point2 ballPos,
			Point2 predictedPos) {
		if (predictionIsGenerated) {
			double crosshairThreshX = Math.abs(ballPos.getX()
					- predictedPos.getX());
			double crosshairThreshY = Math.abs(ballPos.getY()
					- predictedPos.getY());
			if (crosshairThreshX > 2 || crosshairThreshY > 2) {
				return true;
			}
		}
		return false;
	}

	private boolean shouldWeMoveForward(double collisionY, double robotY) {
		return collisionY < robotY && robotY > -120;
	}

	private boolean shouldWeMoveBackward(double collisionY, double robotY) {
		return collisionY > robotY && robotY < 220;
	}
}

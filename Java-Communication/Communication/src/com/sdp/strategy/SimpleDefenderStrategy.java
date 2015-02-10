package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.planner.RobotCommands;
import com.sdp.prediction.Calculations;
import com.sdp.prediction.Oracle;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.Point2;
import com.sdp.world.WorldState;

public class SimpleDefenderStrategy extends GeneralStrategy {
	private Oracle predictor = null;
	final int framesForward = 20;
	final int allowedDistError = 20;

	public SimpleDefenderStrategy() {
		this.predictor = new Oracle(300, 300, 600, 600);
	}

	public void sendWorldState(DynamicWorldState dynWorldState,
			WorldState worldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();
		Point2 ballPos = new Point2((float) ball.getPoint().getX(),
				(float) ball.getPoint().getY());

		ArrayList<Point2> ballPositionHistory = worldState
				.getBallPositionHistory();

		// 1. Predict position where the ball will go
		Point2 predictedPos = this.predictor.predictState(ballPositionHistory,
				framesForward);

		System.out.println("Current ball coordinates " + ballPos.getX() + " "
				+ ballPos.getY());

		System.out.println("Predicted ball coordinates " + predictedPos.getX()
				+ " " + predictedPos.getY());

		// 2. Predict ball y coordinate at near goal x coordinate
		float slope = Calculations.getSlopeOfLine(ballPos, predictedPos);

		if (!Float.isInfinite(slope) && isBallMoving(slope)) {
			float collisionX = (float) robot.getCenter().getX();
			float collisionY = slope * collisionX;

			System.out.println("Slope " + slope);
			System.out.println("Collision coordinates " + collisionX + " "
					+ collisionY);
			double robotX = robot.getCenter().getX();
			double robotY = robot.getCenter().getY();
			System.out.println("Robot coordinates " + robotX + " " + robotY);

			if (Math.abs(collisionY - robotY) > allowedDistError
					&& robotY < SimpleGeneralStrategy.leftGoalBotY) {
				RobotCommands.goStraightFast();
			} else {
				RobotCommands.stop();
			}

			// 3. Turn (if necessary) and go to this position
		} else {
			// stay in current position
			RobotCommands.stop();
		}
	}

	private boolean isBallMoving(float slope) {
		return slope < 5;
	}
}

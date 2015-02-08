package com.sdp.strategy;

import java.util.ArrayList;

import com.sdp.prediction.Calculations;
import com.sdp.prediction.Oracle;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.Point2;
import com.sdp.world.WorldState;

public class SimpleDefenderStrategy extends GeneralStrategy {
	private Oracle predictor = null;

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
		int framesForward = 10;

		// 1. Predict position where the ball will go
		Point2 prediction = this.predictor.predictState(ballPositionHistory,
				framesForward);

		System.out.println("Current " + ballPos.getX() + " " + ballPos.getY());
		System.out.println("Prediction " + prediction.getX() + " "
				+ prediction.getY());

		// 2. Predict ball y coordinate at near goal x coordinate
		float slope = Calculations.getSlopeOfLine(ballPos, prediction);
		System.out.println("Slope " + slope);

		if (!Float.isInfinite(slope)) { // if ball is moving
			float collisionX = (float) robot.getCenter().getX();
			float collisionY = slope * collisionX;

			System.out.println("Collision coordinates " + collisionX + " "
					+ collisionY);

			// 3. Turn (if necessary) and go to this position
		} else {
			// stay in current position
		}
	}
}

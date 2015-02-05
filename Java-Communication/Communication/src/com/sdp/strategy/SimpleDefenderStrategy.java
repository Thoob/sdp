package com.sdp.strategy;

import java.awt.geom.Point2D;
import java.util.ArrayList;

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
		Point2D ballPos = ball.getPoint();

		ArrayList<Point2> ballPositionHistory = worldState
				.getBallPositionHistory();
		int framesForward = 10;

		// 1. Predict position where the ball will go
		Point2 prediction = this.predictor.predictState(ballPositionHistory,
				framesForward);

		System.out.println("Prediction " + prediction.getX());

		// 2. Go to this position

		// 3. Catch the ball
	}
}

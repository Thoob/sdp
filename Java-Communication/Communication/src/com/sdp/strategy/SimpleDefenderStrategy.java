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
			
		/* TODO: discuss if correct estimate */
		int framesForward = 10;

		// 1. Predict position where the ball will go
		Point2 prediction = this.predictor.predictState(ballPositionHistory,
				framesForward);

		/* DEBUG
		System.out.println("Current " + ballPos.getX() + " " + ballPos.getY());
		System.out.println("Prediction " + prediction.getX() + " "
				+ prediction.getY());
		System.out.println(ballPositionHistory.size());					*/
		
		// 2. Predict ball y coordinate at near goal x coordinate
		float slope = Calculations.getSlopeOfLine(ballPos, prediction);
		///System.out.println("Slope " + slope);
		
		double ballY1 = ballPos.getY(), ballX1 = ballPos.getX();
		double ballY2 = prediction.getY(), ballX2 = prediction.getX();
		
		// Could be used as a neater, normalised slope //
		double ballMovement =  Math.abs((ballY2 - ballY1)) / Math.abs(((ballX2 - ballX1)) + 0.0001);

		boolean isMoving = false;
		boolean futureExists = ballPositionHistory.size() > framesForward;
		// Can adjust to make more sensitive //
		if ((Math.abs(ballY2 - ballY1) > 2) || (Math.abs(ballX2 - ballX1) > 2)){
			System.out.println("X diff: " + Math.abs(ballX2 - ballX1));
			System.out.println("Y diff: " + Math.abs(ballY2 - ballY1));
			System.out.println("Ball is moving");
			isMoving = true;
		}
		//System.out.println(ballMovement);

		
		// TODO: Test 
		if (!Float.isInfinite(slope) && isMoving && futureExists){ // if ball is moving
			System.out.println("Ball is moving");
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

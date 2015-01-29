package com.sdp.strategy;

import java.awt.Point;

import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.oldmodel.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	
	private boolean ballInEnemyAttackerArea = false;
	private boolean fromSide = false;
	
	public boolean BallLeftQ = false;
	public boolean BallCentreLQ = false;
	public boolean BallCentreRQ = false;
	public boolean BallRightQ = false;

	
	public void goToPosition(float x, float y) {
		
	}

	public void sendDynamicWorldState(DynamicWorldState dynWorldState) {
		Point ballPos = (Point) dynWorldState.getBall().getPoint();
		Point robotPos = (Point) dynWorldState.getAttacker().getCenter();
		double ballX = ballPos.getX();
		
		/*  Rough code to determine sections based on an object location
		 *  TODO: extend to robot location
		 */
		
		if (ballX < -315){
			System.out.println("Ball in left 1/4 ");
			BallLeftQ = true;
		} else if (ballX > 345){
			System.out.println("Ball in Right 1/4");
			BallCentreLQ = true;
		} else if (ballX > 0 && ballX < 315){
			System.out.println("Ball in Centre Right 1/4");
			BallCentreRQ = true;
		} else{
			System.out.println("Ball in Centre left 1/4");
			BallRightQ = true;
		}


		
	//	System.out.println("ball pos " + ballPos.getX() + " " +
	//	ballPos.getY());
		 
	}
	
	public void sendWorldState(WorldState worldState) {
		super.sendWorldState(worldState);

		if (worldState.weAreShootingRight && ballX > defenderCheck
				&& ballX < leftCheck || !worldState.weAreShootingRight
				&& ballX < defenderCheck && ballX > rightCheck) {
			this.ballInEnemyAttackerArea = true;
		} else {
			this.ballInEnemyAttackerArea = false;
		}
		

	}
}

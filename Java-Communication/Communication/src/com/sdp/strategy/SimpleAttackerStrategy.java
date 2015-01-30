package com.sdp.strategy;

import java.awt.Point;

import com.sdp.RobotCommunication;
import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.oldmodel.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	
	private boolean ballInEnemyAttackerArea;
	private boolean fromSide;
	public boolean BallLeftQ;
	public boolean BallCentreLQ;
	public boolean BallCentreRQ;
	public boolean BallRightQ;
	public boolean isBallCatchable;

	public void sendDynamicWorldState(DynamicWorldState dynWorldState) {
		Point ballPos = (Point) dynWorldState.getBall().getPoint();
		Point robotPos = (Point) dynWorldState.getAttacker().getCenter();
 
	}
	
	public void determineBallLocation(Point ballPos){
		/*  Rough code to determine sections based on an object location
		 *  TODO: extend to robot location
		 */
		double BallX = ballPos.getX();
		
		if (BallX < -315){
		//	System.out.println("Ball in left 1/4 ");
			BallLeftQ = true;
		} else if (BallX > 345){
		//	System.out.println("Ball in Right 1/4");
			BallCentreLQ = true;
		} else if (BallX > 0 && BallX < 315){
		//	System.out.println("Ball in Centre Right 1/4");
			BallCentreRQ = true;
		} else{
		//	System.out.println("Ball in Centre left 1/4");
			BallRightQ = true;
		}
		
		double distanceToBall = Math.hypot(ballX - attackerRobotX, ballY - attackerRobotY);
		double angToBall = calculateAngle(attackerRobotX, attackerRobotY, attackerOrientation, ballX, ballY);

		float targetY = ballY;
		float targetX = ballX;
		double catchDist = 32;
		int catchThresh = 32;
		int ballDistFromTop = (int) Math.abs(ballY
				- PitchConstants.getPitchOutlineTop());
		int ballDistFromBot = (int) Math.abs(ballY
				- PitchConstants.getPitchOutlineBottom());
		
		if (ballDistFromBot < 20) {
			targetY = ballY - 40;
			catchDist = 35;
			catchThresh = 15;
			if (Math.abs(leftCheck - ballX) < 15
					|| Math.abs(rightCheck - ballX) < 15) {
				isBallCatchable = false;
			}
		} else if (ballDistFromTop < 20) {
			targetY = ballY + 40;
			catchDist = 35;
			catchThresh = 15;
			if (Math.abs(leftCheck - ballX) < 15
					|| Math.abs(rightCheck - ballX) < 15) {
				isBallCatchable = false;
			}
		} else {
			attackerHasArrived = false;
		}
		
		/* TODO: coordanate how high the power should be when naviagting
		 * 	This means we can then coordanate how long it takes to pass / move
		 * then use these values in RobotCommunication
		 */
		
		if (!attackerHasArrived && isBallCatchable) {
	/*	toExecute = travelTo(robot, ballX, targetY, catchThresh);
			if (toExecute.op == Operation.Type.DO_NOTHING) {
				attackerHasArrived = true;
			}														*/
		} else if (isBallCatchable) {
			if (Math.abs(angToBall) > 2) {
				int rotatetime = (int) angToBall /* as function of power */;
				RobotCommunication.getInstance().ATKRotate(rotatetime);
			} else if (Math.abs(distanceToBall) > catchDist) {
				int travelDistancetime = (int) distanceToBall /* as a function of power */;
				RobotCommunication.getInstance().ATKTravel(travelDistancetime);
			}
			//toExecute.rotateSpeed = (int) Math.abs(angToBall);
		}
	}
	
	public void determineBallArea(WorldState worldState){
		if (worldState.weAreShootingRight && ballX > defenderCheck
				&& ballX < leftCheck || !worldState.weAreShootingRight
				&& ballX < defenderCheck && ballX > rightCheck) {
			this.ballInEnemyAttackerArea = true;
		} else {
			this.ballInEnemyAttackerArea = false;
		}
	}
	
	public void sendWorldState(WorldState worldState) {
		super.sendWorldState(worldState);

	}
}

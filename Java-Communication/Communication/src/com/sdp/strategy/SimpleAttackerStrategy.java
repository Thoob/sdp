package com.sdp.strategy;

import java.awt.Point;

import com.sdp.RobotCommunication;
import com.sdp.planner.RobotPlanner;
import com.sdp.strategy.StrategyController.BallLocation;
import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.oldmodel.WorldState;

public class SimpleAttackerStrategy extends GeneralStrategy {
	
	private boolean ballInEnemyAttackerArea;
	private boolean fromSide;	
	public enum BallLocation{
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}
	private BallLocation ballLocation;
	public boolean isBallCatchable;

	public void sendWorldState(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();
		// 1. change direction so that robot looks towards the ball
		boolean isHeadingTowardsBall = RobotPlanner.getInstance().isRobotHeadingTowardsBall(robot, ball);
		if(!isHeadingTowardsBall){
			RobotCommunication.getInstance().holdLeft();
			return;
		}else{
			RobotCommunication.getInstance().stop();
		}
		// 2. go straight until you can catch the ball
		boolean canCatchBall = RobotPlanner.getInstance().canCatchBall(robot, ball);
		if(!canCatchBall){
			RobotCommunication.getInstance().holdForward();
			return;
		}else{
			// 3. catch the ball if we don't have it
			RobotCommunication.getInstance().sendCatch();
		}

		// 4. go to a position from which robot can score
		// 5. score 
	}
	
	public void determineBallLocation(Point ballPos){
		
		double BallX = ballPos.getX();
		
		if (BallX < -315){ //TODO check if correct
			ballLocation = BallLocation.DEFENDER;
		} else if (BallX > 345){
			ballLocation = BallLocation.ENEMY_ATTACKER;
		} else if (BallX > 0 && BallX < 315){
			ballLocation = BallLocation.ATTACKER;
		} else{
			ballLocation = BallLocation.ENEMY_DEFENDER;
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

		
		if (!attackerHasArrived && isBallCatchable) {

		} else if (isBallCatchable) {
			if (Math.abs(angToBall) > 2) {
				int rotatetime = (int) angToBall /* as function of power */;
				RobotCommunication.getInstance().ATKRotate(rotatetime);
			} else if (Math.abs(distanceToBall) > catchDist) {
				int travelDistancetime = (int) distanceToBall /* as a function of power */;
				RobotCommunication.getInstance().ATKTravel(travelDistancetime);
			}
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
}

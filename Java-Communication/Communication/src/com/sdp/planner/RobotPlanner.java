package com.sdp.planner;

import java.awt.geom.Point2D;

import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class RobotPlanner {
	
	public enum BallLocation {
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}

	private BallLocation ballLocation;

	private RobotPlanner() {

	}

	/* TODO: Make more general functions, ones compatible with an abstract
	 * 		 objects we may need to rotate toward / move to
	 */
	
	private static double calculateDesiredRobotHeading(Point2D robotPos,
			Point2D ballPos) {
		double deltaX = Math.abs(robotPos.getX() - ballPos.getX());
		double deltaY = Math.abs(robotPos.getY() - ballPos.getY());
		double desiredRobotHeading = Math.atan2(deltaY, deltaX);

		return desiredRobotHeading;
	}

	public static double differenceInHeadings(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double currentRobotHeading = robot.getHeading();
		double desiredRobotHeading = calculateDesiredRobotHeading(robotPos,
				ballPos) - robot.getHeading();

		// TODO Deciding whether it is better to turn left or right
		double difference = Math.abs(currentRobotHeading - desiredRobotHeading);
		
		System.out.println("Current robot heading:" + currentRobotHeading);
		System.out.println("Desired robot heading:" + desiredRobotHeading);
		System.out.println("Difference in headings:" + difference);
		
		return difference;
	} 
	
	public static double differenceInHeadingsGeneral(Robot robot, Point2D desiredAngle) {
		Point2D robotPos = robot.getCenter();
		
		double currentRobotHeading = robot.getHeading() - robot.getHeading();
		double desiredRobotHeading = calculateDesiredRobotHeading(robotPos,
				desiredAngle) - robot.getHeading();

		// TODO Deciding whether it is better to turn left or right
		double difference = Math.abs(currentRobotHeading - desiredRobotHeading);
		
		return difference;
	} 

	public static boolean canCatchBall(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double deltaX = robotPos.getX() - ballPos.getX();
		double deltaY = robotPos.getY() - ballPos.getY();

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);
		System.out.println("Distance from ball: " + deltaTotal);

		if (deltaTotal < 160) {// TODO test and discuss precision needed
			return true;
		}else{
			return false;
		}		
	}
	
	public static boolean inCentreRange(Robot robot, Point2D centre) {
		Point2D robotPos = robot.getCenter();

		double deltaX = robotPos.getX() - centre.getX();
		double deltaY = robotPos.getY() - centre.getY();

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);
		System.out.println("Distance from Centre: " + deltaTotal);

		if (deltaTotal < 160) {// TODO test and discuss precision needed
			return true;
		}else{
			return false;
		}		
	}

	public static boolean doesOurRobotHaveBall(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double deltaX = robotPos.getX() - ballPos.getX();
		double deltaY = robotPos.getY() - ballPos.getY();

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (deltaTotal < 120) {// TODO test and discuss precision needed
			return true;
		}else{
			return false;
		}		
	}
	
	public static  boolean catchReset(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double deltaX = robotPos.getX() - ballPos.getX();
		double deltaY = robotPos.getY() - ballPos.getY();

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (deltaTotal > 220) {// TODO test and discuss precision needed
			return true;
		}else{
			return false;
		}		
	}

	// Not important for milestone 2
	// public void determineBallLocation(Point ballPos) {
	// double BallX = ballPos.getX();
	//
	// if (BallX < -315) { // TODO check if correct
	// ballLocation = BallLocation.DEFENDER;
	// } else if (BallX > 345) {
	// ballLocation = BallLocation.ENEMY_ATTACKER;
	// } else if (BallX > 0 && BallX < 315) {
	// ballLocation = BallLocation.ATTACKER;
	// } else {
	// ballLocation = BallLocation.ENEMY_DEFENDER;
	// }
	//
	// double distanceToBall = Math.hypot(ballX - attackerRobotX, ballY
	// - attackerRobotY);
	// double angToBall = calculateAngle(attackerRobotX, attackerRobotY,
	// attackerOrientation, ballX, ballY);
	//
	// float targetY = ballY;
	// float targetX = ballX;
	// double catchDist = 32;
	// int catchThresh = 32;
	// int ballDistFromTop = (int) Math.abs(ballY
	// - PitchConstants.getPitchOutlineTop());
	// int ballDistFromBot = (int) Math.abs(ballY
	// - PitchConstants.getPitchOutlineBottom());
	//
	// if (ballDistFromBot < 20) {
	// targetY = ballY - 40;
	// catchDist = 35;
	// catchThresh = 15;
	// if (Math.abs(leftCheck - ballX) < 15
	// || Math.abs(rightCheck - ballX) < 15) {
	// isBallCatchable = false;
	// }
	// } else if (ballDistFromTop < 20) {
	// targetY = ballY + 40;
	// catchDist = 35;
	// catchThresh = 15;
	// if (Math.abs(leftCheck - ballX) < 15
	// || Math.abs(rightCheck - ballX) < 15) {
	// isBallCatchable = false;
	// }
	// } else {
	// attackerHasArrived = false;
	// }
	//
	// if (!attackerHasArrived && isBallCatchable) {
	//
	// } else if (isBallCatchable) {
	// if (Math.abs(angToBall) > 2) {
	// int rotatetime = (int) angToBall;
	// RobotCommunication.getInstance().ATKRotate(rotatetime);
	// } else if (Math.abs(distanceToBall) > catchDist) {
	// int travelDistancetime = (int) distanceToBall;
	// RobotCommunication.getInstance().ATKTravel(travelDistancetime);
	// }
	// }
	// }
	//
	// public void determineBallArea(WorldState worldState) {
	// if (worldState.weAreShootingRight && ballX > defenderCheck
	// && ballX < leftCheck || !worldState.weAreShootingRight
	// && ballX < defenderCheck && ballX > rightCheck) {
	// this.ballInEnemyAttackerArea = true;
	// } else {
	// this.ballInEnemyAttackerArea = false;
	// }
	// }
}

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

		
		// ALT METHOD
		
		
		double currentRobotHeading = robot.getHeading();
		double desiredRobotHeading = calculateDesiredRobotHeading(robotPos, ballPos);

		// TODO Deciding whether it is better to turn left or right
		double difference = Math.abs(currentRobotHeading - desiredRobotHeading);
		
		System.out.println("Current robot heading:" + Math.toDegrees(currentRobotHeading));
		System.out.println("Desired robot heading:" + Math.toDegrees(desiredRobotHeading));
		System.out.println("Difference in headings:" + Math.toDegrees(difference));
		
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
	
	
	/* TODO: Change back cast to float */
	public static double calculateAngle(double robotX, double robotY,
			double robotOrientation, double targetX, double targetY) {
		double robotRad = Math.toRadians(robotOrientation);
		
		System.out.println("Current robot heading:" + Math.toDegrees(robotOrientation));
		double targetRad = Math.atan2(targetY - robotY, targetX - robotX);

		if (robotRad > Math.PI)
			robotRad -= 2 * Math.PI;

		double angle = robotRad - targetRad;
		while (angle > Math.PI)
			angle -= 2 * Math.PI;
		while (angle < -Math.PI)
			angle += 2 * Math.PI;

		/* Adjustments for Radians */ 
		double heading = Math.toDegrees(angle);
			if(heading < 0){
				heading = Math.abs(heading);
			}
			if (heading > 0)
				heading = 360 - heading;
			
			/* Debug Info */
		System.out.println("Angle to face:" + heading);
		System.out.println("In Rads :" + angle);
		System.out.println("In rads to deg: " + Math.toDegrees(angle));
		double differenceInDeg = Math.toDegrees(robotOrientation) - heading;
		System.out.println("Difference is: " + differenceInDeg);
			
		return differenceInDeg;
	
	}
}

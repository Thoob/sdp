package com.sdp.planner;

import java.awt.geom.Point2D;

import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;

public class RobotPlanner {

	public enum BallLocation {
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}

	// Distance the robot must be from the ball in order to catch it
	private static final int MAX_CATCH_DIST = 50;
	private static final int MIN_CATCH_DIST = 20;

	private RobotPlanner() {

	}

	/*
	 * TODO: Make more general functions, ones compatible with an abstract
	 * objects we may need to rotate toward / move to
	 */

	private static double calculateDesiredRobotHeading(Point2D robotPos,
			Point2D ballPos) {
		double deltaX = Math.abs(robotPos.getX() - ballPos.getX());
		double deltaY = Math.abs(robotPos.getY() - ballPos.getY());
		double desiredRobotHeading = Math.atan2(deltaY, deltaX);

		return desiredRobotHeading;
	}

	public static double differenceInHeadingsGeneral(Robot robot,
			Point2D desiredAngle) {
		Point2D robotPos = robot.getCenter();

		double currentRobotHeading = robot.getHeading() - robot.getHeading();
		double desiredRobotHeading = calculateDesiredRobotHeading(robotPos,
				desiredAngle) - robot.getHeading();

		// TODO Deciding whether it is better to turn left or right
		double difference = Math.abs(currentRobotHeading - desiredRobotHeading);

		return difference;
	}

	public static boolean canCatchBall(double robotX, double robotY,
			double ballX, double ballY) {
		double deltaX = robotX - ballX;
		double deltaY = robotY - ballY;

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);
		System.out.println("Distance from ball: " + deltaTotal);

		if (deltaTotal < MAX_CATCH_DIST && deltaTotal > MIN_CATCH_DIST) {
			return true;
		} else if (deltaTotal < MIN_CATCH_DIST && deltaTotal > 95) {
			if (SimpleWorldState.previousOperation != Operation.SHORT_BACK) {
				RobotCommands.shortMoveBackwards();
			}
			SimpleWorldState.previousOperation = Operation.SHORT_BACK;
			return false;
		} else {
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
		} else {
			return false;
		}
	}

	public static boolean doesOurRobotHaveBall(double robotX, double robotY,
			double ballX, double ballY) {
		/*
		double deltaX = robotX - ballX;
		double deltaY = robotY - ballY;
		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (deltaTotal < 90) {// TODO test and discuss precision needed
			return true;
		} else {
			return false;
		}*/
		return false;
	}

	public static double desiredAngle(double robotX, double robotY, double targetX, double targetY) {

		double targetRad = Math.atan2(targetY - robotY, targetX - robotX);
		double targetDeg = Math.toDegrees(targetRad);
		targetDeg = (targetDeg < 0) ? targetDeg + 360 : targetDeg;

		return targetDeg;
	}

	public static boolean shouldRotateRight(double desiredAngle,
			double currentAngle) {
		int qDes = getQuarter((int) desiredAngle);
		int qCur = getQuarter((int) currentAngle);
		double angleDiff = Math.abs(currentAngle - desiredAngle);
		if (qDes - qCur == 3 || qDes - qCur == -1)
			return true;
		else if (qDes == qCur && desiredAngle > currentAngle)
			return true;
		else if ((qDes - qCur == 2 || qDes - qCur == -2) && angleDiff < 180)
			return true;
		else
			return false;
	}

	private static int getQuarter(int angle) {
		if (angle > 270)
			return 1;
		if (angle > 180)
			return 2;
		if (angle > 90)
			return 3;
		return 4;
	}

	// Returns the zone an object with a given X value is in
	// I'm so sorry for all these magic numbers! - Theo
	public int inZone(double objX) {
		System.out.println("Ball X: " + (objX));
		if (objX < -324) {
			return 0;
		} else if (objX < 25) {
			return 1;
		} else if (objX < 374) {
			return 2;
		} else if (objX < 650) {
			return 3;
		} else {
			return -1;
		}
	}
}

package com.sdp.planner;

import java.awt.geom.Point2D;

import com.sdp.strategy.GeneralStrategy;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.SimpleWorldState;
import com.sdp.world.SimpleWorldState.Operation;
import com.sdp.world.WorldState;

public class RobotPlanner {

	public enum BallLocation {
		DEFENDER, ATTACKER, ENEMY_DEFENDER, ENEMY_ATTACKER
	}

	// Distance the robot must be from the ball in order to catch it
	private static final int MAX_CATCH_DIST = 50;
	private static final int MIN_CATCH_DIST = 20;

	private RobotPlanner() {

	}

	public static double robotCenterX(double robotX, double robotHeading) {
		robotHeading = Math.abs(getAngleFromZero(robotHeading));
		if (robotHeading < 10) {
			return robotX + 10;
		} else if (Math.abs(robotHeading - 180) < 10) {
			return robotX - 10;
		} else {
			return robotX;
		}
	}

	public static double robotCenterY(double robotY, double robotHeading) {
		if (Math.abs(robotHeading - 90) < 10) {
			return robotY - 10;
		} else if (Math.abs(robotHeading - 270) < 10) {
			return robotY + 10;
		} else {
			return robotY;
		}
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

		double deltaTotal = getDeltaTotal(robotX, robotY, ballX, ballY);
		System.out.println("Distance from ball: " + deltaTotal);

		if (deltaTotal < MAX_CATCH_DIST && deltaTotal > MIN_CATCH_DIST) {
			return true;
		} else if (deltaTotal < MIN_CATCH_DIST && deltaTotal > 95) {
			return false;
		} else {
			return false;
		}
	}

	public static boolean prepareCatch(double robotX, double robotY,
			double ballX, double ballY) {
		double deltaX = robotX - ballX;
		double deltaY = robotY - ballY;

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);
		System.out.println("Distance from ball: " + deltaTotal);

		if (deltaTotal < MAX_CATCH_DIST * 2 && deltaTotal > MIN_CATCH_DIST) {
			return true;
		} else {
			return false;
		}
	}

	public static double getDeltaTotal(double aX, double aY, double bX,
			double bY) {

		double deltaX = aX - bX;
		double deltaY = aY - bY;
		return Math.abs(deltaX) + Math.abs(deltaY);
	}

	public static boolean isCloseEnough(double robotX, double robotY,
			double ballX, double ballY) {
		double deltaX = robotX - ballX;
		double deltaY = robotY - ballY;

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);
		System.out.println("Distance from ball: " + deltaTotal);

		if (deltaTotal < MAX_CATCH_DIST && deltaTotal > MIN_CATCH_DIST) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean nearTarget(double robotX, double robotY,
			double targetX, double targetY) {
		double deltaX = robotX - targetX;
		double deltaY = robotY - targetY;

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (deltaTotal < 15) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean nearTargetForDef(double robotY, double targetY) {
		double deltaY = robotY - targetY;

		if (Math.abs(deltaY) < 10) {
			return true;
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
		int diffInPos = 30;
		double deltaX = robotX - ballX;
		double deltaY = robotY - ballY;
		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (SimpleWorldState.previousOperation == Operation.CATCH
				&& deltaTotal < diffInPos || ballX == 0 && ballY == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static double desiredAngle(double robotX, double robotY,
			double targetX, double targetY) {

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

	public static double getOppositeAngle(double angle) {
		if (angle > 180)
			return angle - 180;
		return angle + 180;
	}

	public static int inZone(double objX, WorldState worldState) {
		if (objX < worldState.dividers[0]) {
			return 0;
		} else if (objX < worldState.dividers[1]) {
			return 1;
		} else if (objX < worldState.dividers[2]) {
			return 2;
		} else {
			return 3;
		}
	}

	public static double getAngleFromZero(double heading) {
		if (heading > 270) {
			return heading - 360;
		} else {
			return heading;
		}
	}

	// TODO improve?
	public static boolean doesEnemyAttackerHaveBall(WorldState worldState,
			double robotX, double ballX) {
		if (RobotPlanner.inZone(robotX, worldState) == RobotPlanner.inZone(
				ballX, worldState)) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean shouldMoveBackwards(double robotAngleDeg,
			double targetAngleDeg) {
		if (Math.abs(robotAngleDeg - targetAngleDeg) > 90)
			return true;
		return false;
	}

	public static boolean isInGoalRange(double predictedY, WorldState worldState) {
		float[] ourGoal = GeneralStrategy.getOurGoalYArr(worldState);
		if (ourGoal[0] - 10 <= predictedY && ourGoal[2] + 10 >= predictedY) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isHeadingDown(double robotAngleDeg) {
		return robotAngleDeg < 180;
	}

	public static boolean isHeadingVertically(double robotAngleDeg) {
		if (Math.abs(robotAngleDeg - 90) < 25
				|| Math.abs(robotAngleDeg - 270) < 25) {
			return true;
		}
		return false;
	}
}

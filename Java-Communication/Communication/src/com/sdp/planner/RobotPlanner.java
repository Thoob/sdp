package com.sdp.planner;

import java.awt.geom.Point2D;

import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class RobotPlanner {

	private RobotPlanner() {

	}

	static RobotPlanner instance = null;

	public static RobotPlanner getInstance() {
		if (instance == null) {
			instance = new RobotPlanner();
		}
		return instance;
	}

	private double calculateDesiredRobotHeading(Point2D robotPos,
			Point2D ballPos) {
		double deltaX = robotPos.getX() - ballPos.getX();
		double deltaY = robotPos.getY() - ballPos.getY();
		double desiredRobotHeading = Math.atan((deltaY / deltaX));

		return desiredRobotHeading;
	}

	public boolean isRobotHeadingTowardsBall(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double currentRobotHeading = robot.getHeading();
		double desiredRobotHeading = calculateDesiredRobotHeading(robotPos,
				ballPos);

		// TODO Deciding whether it is better to turn left or right
		double difference = Math.abs(currentRobotHeading - desiredRobotHeading);

		System.out.println("Difference in headings:" + desiredRobotHeading);

		if (difference < 0.01) { // TODO test and discuss precision needed
			return true;
		} else {
			return false;
		}
	}

	public boolean canCatchBall(Robot robot, Ball ball) {
		Point2D robotPos = robot.getCenter();
		Point2D ballPos = ball.getPoint();

		double deltaX = robotPos.getX() - ballPos.getX();
		double deltaY = robotPos.getY() - ballPos.getY();

		double deltaTotal = Math.abs(deltaX) + Math.abs(deltaY);

		if (deltaTotal < 3) {// TODO test and discuss precision needed
			return true;
		}else{
			return false;
		}		
	}
}

package com.sdp.commands;

import java.awt.geom.Point2D;

import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class RobotCommands {

	private RobotCommands() {

	}

	static RobotCommands instance = null;

	public static RobotCommands getInstance() {
		if (instance == null) {
			instance = new RobotCommands();
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

		System.out.println("Current robot heading " + currentRobotHeading);
		System.out.println("Desired robot heading " + desiredRobotHeading);
		System.out.println("Difference:" + desiredRobotHeading);

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

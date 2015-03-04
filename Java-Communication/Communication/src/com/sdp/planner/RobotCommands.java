package com.sdp.planner;

import com.sdp.RobotCommunication;
import com.sdp.world.DynamicWorldState.Robot;
import com.sdp.world.Point2;

/**
 * Higher-level robot commands
 */
public class RobotCommands {
	public void changeHeading(Robot robot, double desiredDir) {

	}

	public void changePosition(Robot robot, Point2 desiredPos) {

	}
	
	public static void passKick() {
		System.out.println("passKick");
		RobotCommunication.getInstance().passKick();
	}


	public static void kick() {
		System.out.println("kick");
		RobotCommunication.getInstance().sendKick(400);
	}

	public static void shortRotateLeft() {
		System.out.println("shortRotateLeft");
		RobotCommunication.getInstance().shortRotateLeft();
	}

	public static void shortRotateRight() {
		System.out.println("shortRotateRight");
		RobotCommunication.getInstance().shortRotateRight();
	}
	
	public static void rotateLeft() {
		System.out.println("rotateLeft");
		RobotCommunication.getInstance().move(-35, 35);
	}

	public static void rotateRight() {
		System.out.println("rotateRight");
		RobotCommunication.getInstance().move(30, -30);
	}

	public static void goStraight() {
		System.out.println("goStraight");
		RobotCommunication.getInstance().move(45, 45);
	}
	
	public static void goStraightFast() {
		System.out.println("goStraightFast");
		RobotCommunication.getInstance().move(100, 100);
	}
	
	
	public static void goStraightBackwardsFast() {
		System.out.println("goStraightBackwardsFast");
		RobotCommunication.getInstance().move(-100, -100);
	}

	public static void goStraightBackwards() {
		System.out.println("goStraightBackwards");
		RobotCommunication.getInstance().move(-45, -45);
	}

	public static void stop() {
		System.out.println("stop");
		RobotCommunication.getInstance().stop();

	}

	public static void catchBall() {
		System.out.println("catchBall");
		RobotCommunication.getInstance().sendCatch();
	}
	
	public static void catchUp(){
		RobotCommunication.getInstance().catchUp();
	}
	
	public static void catchDown(){
		System.out.println("Catching!");
		RobotCommunication.getInstance().catchDown();
	}

	public static void rotateStop() {
		RobotCommunication.getInstance().rotateStop();
	}

	public static void shortMoveBackwards() {
		RobotCommunication.getInstance().shortMoveBackwards();
	}
}

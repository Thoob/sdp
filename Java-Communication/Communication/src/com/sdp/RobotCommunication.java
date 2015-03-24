package com.sdp;

/**
 * Low-level robot commands
 */
public class RobotCommunication {

	static RobotCommunication instance;

	public static RobotCommunication getInstance() {
		if (instance == null) {
			instance = new RobotCommunication();
		}
		return instance;
	}

	public void move(int leftspeed, int rightspeed) {
		String command = String.format("MOVE %d %d\n", leftspeed, rightspeed);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void passKick() {
		String command = String.format("KICK 400 100\n");
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendKick(int time) {
		String command = String.format("KICK %d 100\n", time);
		Communication.getInstance().sendCommandViaPort(command);
	}

	// *Not on Arduino*
	public void sendCatch() {
		String command = "CATCH\n";
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public String haveBall() {
		String command = "HAVEBALL\n";
		Communication.getInstance().sendCommandViaPort(command);
//		String result = null;
//		while (result == null) {
//			result = Communication.getInstance().readStringFromSerialPort();
//		}
//		System.out.println(result);
		return "NO";
	}

	public void stop() {
		String command = "MOVE 0 0\n";
		Communication.getInstance().sendCommandViaPort(command);
	}

	/*
	 * RC Commands
	 */
	public void holdForward() {
		String command = String.format("RCFORWARD\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void holdBackward() {
		String command = String.format("RCBACKWARD\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void holdLeft(double diffInHeadings) {
		String command = String.format("RCROTATL %f\n", diffInHeadings);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void holdRight(double diffInHeadings) {
		String command = String.format("RCROTATR %f\n", diffInHeadings);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void shortRotateLeft() {
		String command = "SROTL 200 60\n"; // old power value 70, time 200
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void shortRotateRight() {
		String command = "SROTR 200 60\n"; // old power value 60, time 200
		Communication.getInstance().sendCommandViaPort(command);
	}

	// *Not on Arduino*
	public void shortMoveBackwards() {
		String command = "SMOVB 150 40\n";
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void catchUp() {
		Communication.getInstance().sendCommandViaPort("CATCHUP\n");

	}

	public void catchDown() {
		Communication.getInstance().sendCommandViaPort("CATCHDOWN\n");

	}
}

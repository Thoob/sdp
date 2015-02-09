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
		String command = String.format("KICK 400 60\n");
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendKick(int time) {
		String command = String.format("KICK %d 100\n", time);
		;
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendCatch() {
		String command = "CATCH\n";
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void stop() {
		String command = "FSTOP\n";
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

	public void catchReset() {
		String command = String.format("CFRESET\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void rotateStop() {
		String command = "RSTOP\n";
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void shortRotateLeft() {
		String command = "SROTL\n";
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void shortRotateRight() {
		String command = "SROTR\n";
		Communication.getInstance().sendCommandViaPort(command);
	}
}

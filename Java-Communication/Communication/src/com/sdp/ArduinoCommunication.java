package com.sdp;

public interface ArduinoCommunication {

	public void sendMove(int leftSpeed, int rightSpeed);

	public void sendCatch(int catchPosition);

	public void sendFKick(int kickPosition);

	public void sendGrab(int grabPosition);

	public void sendShoot(int shootPosition);

	public void sendTShoot(int shootPosition);

	// Temporary (not agreed yet)
	public void sendStartMoveUp();

	public void sendStopMoveUp();

	public void sendStartMoveDown();

	public void sendStopMoveDown();

	public void sendStartTurnRight();

	public void sendStopTurnRight();

	public void sendStartTurnLeft();

	public void sendStopTurnLeft();

	// commands needed for the 1st milestone
	public void sendMoveForward(int timeInMillis);

	public void sendMoveBackward(int timeInMillis);

	public void sendKick(int timeIntMillis);

}

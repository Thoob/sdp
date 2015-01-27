package com.sdp;

public interface ArduinoCommunication {

	// commands needed for the 1st milestone
	public void sendMoveForward(int timeInMillis);

	public void sendMoveBackward(int timeInMillis);

	public void sendKick(int timeIntMillis);

}

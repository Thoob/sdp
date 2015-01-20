package com.sdp.group13;


public interface ArduinoCommunication {
	
	public void sendMove(int leftSpeed, int rightSpeed);
	
	public void sendCatch(int catchPosition);
	
	public void sendKick(int kickPosition);
	
	public void sendFKick(int kickPosition);
	
	public void sendGrab(int grabPosition);
	
	public void sendShoot(int shootPosition);
	
	public void sendTShoot(int shootPosition);
}

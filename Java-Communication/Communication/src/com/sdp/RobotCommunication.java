package com.sdp;

public class RobotCommunication implements ArduinoCommunication {

	static RobotCommunication instance;

	public static RobotCommunication getInstance() {
		if (instance == null) {
			instance = new RobotCommunication();
		}
		return instance;
	}

	@Override
	public void sendMoveForward(int time) {
		String command = String.format("FORWARD %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendMoveForward10() {
		String command = "FORWARD 300 100\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}	
	
	public void sendMoveForward50() {
		String command = "FORWARD 1500 100\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendMoveBackward(int time) {
		String command = String.format("BACKWARD %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void sendMoveBackward20() {
		String command = String.format("BACKWARD 600 100\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void passKick() {
		String command = String.format("KICK 400 60\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendKick(int time) {
		String command = String.format("KICK %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void sendCatch(){
		String command = "CATCH\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void holdForward(){
		String command = String.format("RCFORWARD\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void holdBackward(){
		String command = String.format("RCBACKWARD\n");
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void holdLeft(double diffInHeadings){
		String command = String.format("RCROTATL %f\n", diffInHeadings);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void holdRight(double diffInHeadings){
		String command = String.format("RCROTATR %f\n", diffInHeadings);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void sendRotateH(double diffInHeadings){
		String command = String.format("ROTATEH %f\n", diffInHeadings);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void sendRotateP(int rotatePower){
		String command = String.format("ROTATEP %d\n", rotatePower);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command); 
	}
	
	public void stop(){
		String command = "STOP\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void ATKRotate(int time){
		String command = String.format("ATKROTATE %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void ATKTravel(int time){
		String command = String.format("ATKTravel %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
}

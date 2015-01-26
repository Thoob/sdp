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
	public void sendMove(int leftSpeed, int rightSpeed) {
		String command = String.format("A_SET_ENGINE %d %d\r", leftSpeed,
				rightSpeed);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendCatch(int catchPosition) {
		String command = String.format("A_SET_CATCH %d\r", catchPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendKick(int kickPosition) {
		String command = String.format("A_SET_KICK %d\r", kickPosition);
		Communication.getInstance().sendCommandViaPort(command);

	}

	@Override
	public void sendFKick(int kickPosition) {
		String command = String.format("A_SET_FKICK %d\r", kickPosition);
		Communication.getInstance().sendCommandViaPort(command);

	}

	@Override
	public void sendGrab(int grabPosition) {
		String command = String.format("A_SET_GRAB %d\r", grabPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendShoot(int shootPosition) {
		String command = String.format("A_SET_SHOOT %d\r", shootPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendTShoot(int shootPosition) {
		String command = String.format("A_SET_TSHOOT %d\r", shootPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartMoveUp() {
		String command = "A_START_MOVE_UP\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopMoveUp() {
		String command = "A_STOP_MOVE_UP\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartMoveDown() {
		String command = "A_START_MOVE_DOWN\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopMoveDown() {
		String command = "A_STOP_MOVE_DOWN\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartTurnRight() {
		String command = "A_START_TURN_RIGHT\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopTurnRight() {
		String command = "A_STOP_TURN_RIGHT\r;
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartTurnLeft() {
		String command = "A_START_TURN_LEFT\r";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopTurnLeft() {
		String command = "A_STOP_TURN_LEFT\r";
		Communication.getInstance().sendCommandViaPort(command);

	}
	@Override
	public void sendMoveForward10() {
		String command = "A_MOVE_FORWARD_10 500\r";
		Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void sendMoveForward50() {
	String command = "A_MOVE_FORWARD_50 500\r";
	Communication.getInstance().sendCommandViaPort(command);
	}
	
	public void sendMoveBackward10() {
	String command = "A_MOVE_BACKWARD_10 500\r";
	Communication.getInstance().sendCommandViaPort(command);
	}
	
	 
}

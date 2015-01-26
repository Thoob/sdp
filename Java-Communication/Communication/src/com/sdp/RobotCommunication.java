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
		String command = String.format("A_SET_ENGINE %d %d", leftSpeed,
				rightSpeed);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendCatch(int catchPosition) {
		String command = String.format("A_SET_CATCH %d", catchPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendFKick(int kickPosition) {
		String command = String.format("A_SET_FKICK %d", kickPosition);
		Communication.getInstance().sendCommandViaPort(command);

	}

	@Override
	public void sendGrab(int grabPosition) {
		String command = String.format("A_SET_GRAB %d", grabPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendShoot(int shootPosition) {
		String command = String.format("A_SET_SHOOT %d", shootPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendTShoot(int shootPosition) {
		String command = String.format("A_SET_TSHOOT %d", shootPosition);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartMoveUp() {
		String command = "A_START_MOVE_UP";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopMoveUp() {
		String command = "A_STOP_MOVE_UP";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartMoveDown() {
		String command = "A_START_MOVE_DOWN";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopMoveDown() {
		String command = "A_STOP_MOVE_DOWN";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartTurnRight() {
		String command = "A_START_TURN_RIGHT";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopTurnRight() {
		String command = "A_STOP_TURN_RIGHT";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStartTurnLeft() {
		String command = "A_START_TURN_LEFT";
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendStopTurnLeft() {
		String command = "A_STOP_TURN_LEFT";
		Communication.getInstance().sendCommandViaPort(command);

	}

	@Override
	public void sendMoveForward(int time) {
		String command = String.format("FORWARD %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendMoveForward50() {
		String command = "FORWARD 5000 100\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	public void sendMoveBackward(int time) {
		String command = String.format("BACKWARD %d 100\n", time);
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}

	@Override
	public void sendKick() {
		String command = "KICK 300 100\n";
		System.out.println(command);
		Communication.getInstance().sendCommandViaPort(command);
	}
}

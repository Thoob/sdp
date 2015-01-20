package com.sdp.group13;

public class RobotCommunication implements ArduinoCommunication {

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
	public void sendKick(int kickPosition) {
		String command = String.format("A_SET_KICK %d", kickPosition);
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
}

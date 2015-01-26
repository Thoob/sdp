package com.sdp;

public class Main {
	public static void main(String[] args) {
		
		if (args.length == 0) {
			GUI gui = new GUI();
			gui.setVisible(true);
		} else {
			String action = args[0].toLowerCase();
			String portName = "COM3";
			Communication.getInstance().initializeSerialPort(portName);

			if (action.equals("kick")) {
				RobotCommunication.getInstance().sendKick();
			} else if (action.equals("forward")) {
				int time = Integer.parseInt(args[1]);
				RobotCommunication.getInstance().sendMoveForward(time);
			} else if (action.equals("backward")) {
				int time = Integer.parseInt(args[1]);
				RobotCommunication.getInstance().sendMoveBackward(time);
			}
		}
	}
}

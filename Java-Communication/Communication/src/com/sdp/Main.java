package com.sdp;

public class Main {
	public static void main(String[] args) {
		// GUI gui = new GUI();
		// gui.setVisible(true);
		if (args.length == 0) {
			System.out.println("You need to specify parameters");
		} else {
			String action = args[0].toLowerCase();
			if (action.equals("gui")) {
				GUI mGui = new GUI();
				mGui.setVisible(true);
			} else {
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
}

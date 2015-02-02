package com.sdp;

public class Main {
	public static void main(String[] args) {

		if (args.length == 0) {
			GUI gui = new GUI();
			gui.setVisible(true);
		} else {
			String action = args[0].toLowerCase();
			Communication.getInstance().initializeSerialPort(Constants.PORT_NAME);

			int time = Integer.parseInt(args[1]);
			if (action.equals("kick")) {
				RobotCommunication.getInstance().sendKick(time);
			} else if (action.equals("forward")) {
				RobotCommunication.getInstance().sendMoveForward(time);
			} else if (action.equals("backward")) {
				RobotCommunication.getInstance().sendMoveBackward(time);
			}

			System.exit(0);
		}
	}
}

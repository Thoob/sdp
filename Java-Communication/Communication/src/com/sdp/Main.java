package com.sdp;

import com.sdp.Communication.ReadStringRunnable;
import com.sdp.planner.RobotCommands;

public class Main {
	public static void main(String[] args) {
		Communication.getInstance().initializeSerialPort("COM3");
		RobotCommands.kick();

		ReadStringRunnable mRunnable = Communication.getInstance().new ReadStringRunnable();
		Thread thread = new Thread(mRunnable);
		thread.start();

		RobotCommands.kick();
//		GUI gui = new GUI();
//		gui.setVisible(true);
	}
}

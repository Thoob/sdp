package com.sdp.strategy;

import com.sdp.Communication;
import com.sdp.commands.RobotCommands;

public class TempMain {
	public static void main(String[] args) {
		String portName = "COM3";
		Communication.getInstance().initializeSerialPort(portName);

		RobotCommands rc = new RobotCommands();
		rc.changeDirection(0, 5);
	}
}

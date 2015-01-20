package com.sdp.group13;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

// From http://www.codeproject.com/Tips/801262/Sending-and-receiving-strings-from-COM-port-via-jS 

class Communication {

	private Communication() {

	}

	static Communication instance;

	public static Communication getInstance() {
		if (instance == null) {
			instance = new Communication();
		}
		return instance;
	}

	private SerialPort serialPort;

	public String[] getAvailablePorts() {
		String[] portNames = SerialPortList.getPortNames();

		return portNames;
	}

	void initializeSerialPort(String portName) {
		serialPort = new SerialPort(portName);
	}

	void sendCommandViaPort(String command) {
		try {
			serialPort.openPort();
			serialPort.writeString(command);
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
}
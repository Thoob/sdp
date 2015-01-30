package com.sdp;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Communication {

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

	public void initializeSerialPort(String portName) {
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
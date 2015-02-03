package com.sdp;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Communication {
	private boolean isPortInitialized = false;
	static Communication instance;

	private Communication() {

	}

	public boolean isPortInitialized(){
		return isPortInitialized;
	}

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
		isPortInitialized  = true;
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
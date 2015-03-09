package com.sdp;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class Communication {
	private boolean isPortInitialized = false;
	static Communication instance;

	private Communication() {

	}

	public boolean isPortInitialized() {
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
		isPortInitialized = true;
		try {
			serialPort.openPort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	void sendCommandViaPort(String command) {
		try {
			serialPort.writeString(command);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}

	public String readStringFromSerialPort() {
		String input = null;
		try {
			input = serialPort.readString();
			System.out.println("String received via serial port " + input);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		return input;
	}

	public class ReadStringRunnable implements Runnable {
		public ReadStringRunnable() {
			System.out.println("Read String Runnable started ");
		}

		@Override
		public void run() {
			while (true) {
				String input;
				try {
					input = serialPort.readString();
					System.out.println("String received via serial port "
							+ input);
					Thread.sleep(100);
				} catch (SerialPortException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
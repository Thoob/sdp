package com.sdp;


import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;

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
		String lastLine = null;
		try {
			input = serialPort.readString();
			lastLine = input.substring(input.lastIndexOf("\n")-4).replaceAll("[^0-9]+", "");
			System.out.println("String received via serial port " + lastLine);
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
		return lastLine;
	}
	
    public String getReply() {


        int receiveState = 0;
        byte[] recvdBytes; // bytes received
        byte[] oneByte;
        int byteCounter = 0;


        recvdBytes = new byte[80];
        oneByte = new byte[80];

        // initialize array
        for (int i = 0; i < 80; i++) {
            recvdBytes[i] = 0;
        }

        /* wait for reply */
        oneByte[0] = 0;

        // keep collecting data until newline is received
        while ((oneByte[0] != ('\n'))) {
            try {

                while (receiveState == 0) {
                    receiveState =
                            serialPort.getEventsMask();
                    receiveState &= SerialPort.MASK_RXCHAR;
                }

                // wait up until 20 seconds for data
                // when we get data, put it into the buffer
                oneByte = serialPort.readBytes(1, 20000);
                recvdBytes[byteCounter] = oneByte[0];
                byteCounter++;
                //arduinoReply += oneChar;

            } catch (SerialPortException | SerialPortTimeoutException ex) {
                ex.printStackTrace();
            }
        }
        // put the bytes into string format
        String arduinoReply;
        arduinoReply = new String(recvdBytes, 0, byteCounter);

        char[] charArray;
        charArray = arduinoReply.toCharArray();

        // send the reply back to caller
        System.out.println(arduinoReply);
        return arduinoReply;
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
					if (input != null) {
						System.out.println("String received via serial port "
								+ input);
						break;
					}
				} catch (SerialPortException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
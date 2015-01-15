import java.io.IOException;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

class SerialTest {

	static SerialPort serialPort;

	public static void main(String[] args) {
		printAvailablePorts();
		String message = "Go up";
		//sendMessageViaPort(message);

		try {
			serialPort = new SerialPort("/dev/ttyACM1");
			serialPort.openPort();
			serialPort.writeInt(1);
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void printAvailablePorts() {
		// getting serial ports list into the array
		String[] portNames = SerialPortList.getPortNames();

		if (portNames.length == 0) {
			System.out
					.println("There are no serial-ports :( You can use an emulator, such ad VSPE, to create a virtual serial port.");
			System.out.println("Press Enter to exit...");
			try {
				System.in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);
		}
	}

	static void sendMessageViaPort(String message) {
		serialPort = new SerialPort("/dev/ttyACM0");
		try {
			serialPort.openPort();

			serialPort.setParams(SerialPort.BAUDRATE_115200,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
					| SerialPort.FLOWCONTROL_RTSCTS_OUT);

			serialPort.addEventListener(new PortReader(),
					SerialPort.MASK_RXCHAR);

			serialPort.writeInt(1);
		} catch (SerialPortException ex) {
			System.out
					.println("There are an error on writing string to port т: "
							+ ex);
		}
	}

	private static class PortReader implements SerialPortEventListener {

		@Override
		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR() && event.getEventValue() > 0) {
				try {
					String receivedData = serialPort.readString(event
							.getEventValue());
					System.out.println("Received response: " + receivedData);
				} catch (SerialPortException ex) {
					System.out
							.println("Error in receiving string from COM-port: "
									+ ex);
				}
			}
		}

	}
}
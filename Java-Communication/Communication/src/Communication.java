import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

// From http://www.codeproject.com/Tips/801262/Sending-and-receiving-strings-from-COM-port-via-jS 

class Communication {

	private SerialPort serialPort;

	public String[] getAvailablePorts() {
		String[] portNames = SerialPortList.getPortNames();
		
		return portNames;
	}
	
	void initializeSerialPort(String portName){
		serialPort = new SerialPort(portName);
	}

	void sendNumberViaPort(int number) {
		try {
			serialPort.openPort();
			serialPort.writeInt(number);
			serialPort.closePort();
		} catch (SerialPortException e) {
			e.printStackTrace();
		}
	}
}
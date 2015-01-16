import javax.swing.JLabel;


public class Main {
	public static void main(String[] args) {
		Communication mCommunication = new Communication();
		String[] portNames = mCommunication.getAvailablePorts();
		for (int i = 0; i < portNames.length; i++) {
			System.out.println(portNames[i]);
		}
		GUI gui = new GUI();
		mCommunication.initializeSerialPort(portNames[0]);
		mCommunication.sendNumberViaPort(1);
		
		
	}
}

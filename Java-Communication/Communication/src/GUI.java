import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private String[] portNames;
	private JPanel buttonsPanel;
	private JComboBox<String> portNamesList;
	private ActionListener listener;
	private boolean serialPortInitialized = false;

	private Communication mCommunication;

	public GUI() {
		mCommunication = new Communication();
		this.portNames = mCommunication.getAvailablePorts();

		createFrame();
		addMovementBtns();
	}

	private void createFrame() {
		setTitle("Controller");

		setSize(120, 360);
		setResizable(false);

		// set window to middle of screen
		setLocationRelativeTo(null);

		buttonsPanel = new JPanel();
		buttonsPanel.setBackground(Color.WHITE);
		add(buttonsPanel);

		portNamesList = new JComboBox<String>(portNames);
		buttonsPanel.add(portNamesList);

		JButton initBtn = new JButton("Initialize");
		initBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mCommunication.initializeSerialPort(String.valueOf(portNamesList.getSelectedItem()));
				serialPortInitialized = true;
				System.out.println("SerialPort initialized");
			}
		});
		buttonsPanel.add(initBtn);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serialPortInitialized) {					
					String action = e.getActionCommand();
					int value = Movement.getValue(action);
					mCommunication.sendNumberViaPort(value);
					System.out.println(action + ": " + value);
				} else {
					System.out.println("Serial port not initialized");
				}
			}
		};
	}

	private void addMovementBtns() {

		JButton startBtn = new JButton("Start");
		startBtn.setForeground(Color.BLACK);
		startBtn.setBackground(Color.GREEN);
		startBtn.setSize(100, 50);
		buttonsPanel.add(startBtn, BorderLayout.SOUTH);
		startBtn.addActionListener(listener);

		JButton stopBtn = new JButton("Stop");
		stopBtn.setBackground(Color.RED);
		stopBtn.setForeground(Color.BLACK);
		stopBtn.setSize(100, 50);
		buttonsPanel.add(stopBtn);
		stopBtn.addActionListener(listener);

		JButton leftBtn = new JButton("Left");
		leftBtn.setSize(100, 50);
		buttonsPanel.add(leftBtn);
		leftBtn.addActionListener(listener);

		JButton upBtn = new JButton("Up");
		upBtn.setSize(100, 50);
		buttonsPanel.add(upBtn);
		upBtn.addActionListener(listener);

		JButton rightBtn = new JButton("Right");
		rightBtn.setSize(100, 50);
		buttonsPanel.add(rightBtn);
		rightBtn.addActionListener(listener);

		JButton downBtn = new JButton("Down");
		downBtn.setSize(100, 50);
		buttonsPanel.add(downBtn);
		downBtn.addActionListener(listener);
		
		JButton kickBtn = new JButton("Kick");
		kickBtn.setSize(100, 50);
		kickBtn.setBackground(Color.GRAY);
		kickBtn.setForeground(Color.BLACK);
		buttonsPanel.add(kickBtn);
		kickBtn.addActionListener(listener);

	}
}

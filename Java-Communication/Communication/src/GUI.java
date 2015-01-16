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

		setSize(300, 400);
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
		leftBtn.setSize(50, 50);
		ImageIcon leftIcon = new ImageIcon("assets\\left.png");
		leftBtn.setIcon(leftIcon);
		leftIcon.setImage(leftIcon.getImage().getScaledInstance(
				leftBtn.getWidth(), leftBtn.getHeight(), Image.SCALE_DEFAULT));
		buttonsPanel.add(leftBtn);
		leftBtn.addActionListener(listener);

		JButton upBtn = new JButton("Up");
		upBtn.setSize(50, 50);
		ImageIcon upIcon = new ImageIcon("assets\\up.png");
		upBtn.setIcon(upIcon);
		upIcon.setImage(upIcon.getImage().getScaledInstance(upBtn.getWidth(),
				upBtn.getHeight(), Image.SCALE_DEFAULT));
		buttonsPanel.add(upBtn);
		upBtn.addActionListener(listener);

		JButton rightBtn = new JButton("Right");
		rightBtn.setSize(50, 50);
		ImageIcon rightIcon = new ImageIcon("assets\\right.png");
		rightBtn.setIcon(rightIcon);
		rightIcon
				.setImage(rightIcon.getImage().getScaledInstance(
						rightBtn.getWidth(), rightBtn.getHeight(),
						Image.SCALE_DEFAULT));
		buttonsPanel.add(rightBtn);
		rightBtn.addActionListener(listener);

		JButton downBtn = new JButton("Down");
		downBtn.setSize(50, 50);
		ImageIcon downIcon = new ImageIcon("assets\\down.png");
		downBtn.setIcon(downIcon);
		downIcon.setImage(downIcon.getImage().getScaledInstance(
				downBtn.getWidth(), downBtn.getHeight(), Image.SCALE_DEFAULT));
		buttonsPanel.add(downBtn);
		downBtn.addActionListener(listener);
		
		JButton kickBtn = new JButton("Kick");
		kickBtn.setSize(50, 50);
		kickBtn.setBackground(Color.GRAY);
		kickBtn.setForeground(Color.BLACK);
		buttonsPanel.add(kickBtn);
		kickBtn.addActionListener(listener);

	}
}

package com.sdp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

	public GUI() {
		this.portNames = Communication.getInstance().getAvailablePorts();
		setTitle("Robot Controller");
		setSize(120, 360);
		setResizable(false);

		// set window to middle of screen
		setLocationRelativeTo(null);

		buttonsPanel = new JPanel();

		createFrame();
		addBtns();
	}

	// to get keyboard buttons working
	private void focusToButtonPanel() {
		buttonsPanel.setFocusable(true);
		buttonsPanel.requestFocusInWindow();
	}

	private void createFrame() {

		buttonsPanel.setBackground(Color.WHITE);
		add(buttonsPanel);

		portNamesList = new JComboBox<String>(portNames);
		buttonsPanel.add(portNamesList);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String action = e.getActionCommand();
				if (serialPortInitialized) {
					if(action.equals("Kick")){
						RobotCommunication.getInstance().sendKick(300);
						System.out.println("KICK");
					}
					focusToButtonPanel();
				} else {
					System.out.println("Serial port not initialized");
				}
			}
		};

		buttonsPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (serialPortInitialized) {
					RobotCommunication.getInstance().stop();

				} else {
					System.out.println("Serial port not initialized");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				int code = e.getKeyCode();
				if(serialPortInitialized){
					switch (code) {
					case KeyEvent.VK_W:
						System.out.println("W");
						RobotCommunication.getInstance().holdForward();
						break;
					case KeyEvent.VK_A:
						System.out.println("A");
						RobotCommunication.getInstance().holdLeft(3);
						break;
					case KeyEvent.VK_S:
						System.out.println("S");
						RobotCommunication.getInstance().holdBackward();
						break;
					case KeyEvent.VK_D:
						System.out.println("D");
						RobotCommunication.getInstance().holdRight(3);
						break;
					case KeyEvent.VK_SPACE:
						System.out.println("KICK");
						RobotCommunication.getInstance().sendKick(
								Constants.KICK_TIME);
						break;
					case KeyEvent.VK_E:
						System.out.println("CATCH");
						RobotCommunication.getInstance().sendCatch();
						break;	
				/*	//movement test 1 (10cm forward)
					case KeyEvent.VK_1:
						RobotCommunication.getInstance().sendMoveForward10();
						break;
					//movement test 2 (50cm forward)
					case KeyEvent.VK_2:
						RobotCommunication.getInstance().sendMoveForward50();
						break;
					//movement test 3 (20cm backward)
					case KeyEvent.VK_3:
						RobotCommunication.getInstance().sendMoveBackward20();
						break;
					*/
					//kick test (defence to attack zone)
					case KeyEvent.VK_4:
						RobotCommunication.getInstance().passKick();
						break;
				}
			}
		}
	});

		buttonsPanel.setFocusable(true);
		buttonsPanel.requestFocusInWindow();
	}

	private void addBtns() {

		JButton initBtn = new JButton("Initialize");
		initBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String portName = String.valueOf(portNamesList
						.getSelectedItem());
				Communication.getInstance().initializeSerialPort(portName);
				serialPortInitialized = true;
				System.out.println(portName.concat(" serial port initialized"));
			}
		});
		buttonsPanel.add(initBtn);

		JButton kickBtn = new JButton("Kick");
		kickBtn.setSize(100, 50);
		kickBtn.setBackground(Color.GRAY);
		kickBtn.setForeground(Color.BLACK);
		buttonsPanel.add(kickBtn);
		kickBtn.addActionListener(listener);

	}
}

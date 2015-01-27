package com.sdp;

import java.awt.BorderLayout;
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

	private boolean oneBtnPressed, twoBtnPressed, threeBtnPressed;

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
					switch (action) {
					case "Kick":
						RobotCommunication.getInstance().sendKick(300);
						break;
					default:
						break;
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
				int code = e.getKeyCode();
				if (serialPortInitialized) {
					switch (code) {
					case KeyEvent.VK_1:
						oneBtnPressed = false;
						RobotCommunication.getInstance().sendMoveForward(1000);
						System.out.println("Released 1");
						break;
					case KeyEvent.VK_2:
						twoBtnPressed = false;
						RobotCommunication.getInstance().sendMoveForward(3000);
						System.out.println("Released 2");
						break;
					case KeyEvent.VK_3:
						threeBtnPressed = false;
						RobotCommunication.getInstance().sendMoveBackward(1000);
						System.out.println("Released 3");
						break;
					}
				} else {
					System.out.println("Serial port not initialized");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (serialPortInitialized) {
					int code = e.getKeyCode();
					switch (code) {
					case KeyEvent.VK_1:
						if (!oneBtnPressed) {
							RobotCommunication.getInstance().sendMoveForward(
									1000);
						}
						break;
					case KeyEvent.VK_2:
						if (!twoBtnPressed) {
							RobotCommunication.getInstance()
									.sendMoveForward50();
						}
						break;
					case KeyEvent.VK_3:
						if (!threeBtnPressed) {
							RobotCommunication.getInstance().sendMoveBackward(
									1000);
						}
						break;
					}
				} else {
					System.out.println("Serial port not initialized");
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
		/*
		 * JButton startBtn = new JButton("Start");
		 * startBtn.setForeground(Color.BLACK);
		 * startBtn.setBackground(Color.GREEN); startBtn.setSize(100, 50);
		 * buttonsPanel.add(startBtn, BorderLayout.SOUTH);
		 * startBtn.addActionListener(listener);
		 * 
		 * JButton stopBtn = new JButton("Stop");
		 * stopBtn.setBackground(Color.RED); stopBtn.setForeground(Color.BLACK);
		 * stopBtn.setSize(100, 50); buttonsPanel.add(stopBtn);
		 * stopBtn.addActionListener(listener);
		 */
		JButton kickBtn = new JButton("Kick");
		kickBtn.setSize(100, 50);
		kickBtn.setBackground(Color.GRAY);
		kickBtn.setForeground(Color.BLACK);
		buttonsPanel.add(kickBtn);
		kickBtn.addActionListener(listener);

	}
}

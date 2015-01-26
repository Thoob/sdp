package com.sdp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private String[] portNames;
	private JPanel buttonsPanel;
	private JComboBox<String> portNamesList;
	private ActionListener listener;
	private boolean serialPortInitialized = false;

	private boolean upBtnPressed, downBtnPressed, rightBtnPressed,
			leftBtnPressed, oneBtnPressed, twoBtnPressed, threeBtnPressed;

	public GUI() {
		this.portNames = Communication.getInstance().getAvailablePorts();
		setTitle("Robot Controller");
		setSize(120, 360);
		setResizable(false);

		// set window to middle of screen
		setLocationRelativeTo(null);

		buttonsPanel = new JPanel();

		createFrame();
		addMovementBtns();

	}

	private void createFrame() {

		buttonsPanel.setBackground(Color.WHITE);
		add(buttonsPanel);

		portNamesList = new JComboBox<String>(portNames);
		buttonsPanel.add(portNamesList);

		JButton initBtn = new JButton("Initialize");
		initBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Communication.getInstance().initializeSerialPort(
						String.valueOf(portNamesList.getSelectedItem()));
				serialPortInitialized = true;
				System.out.println("SerialPort initialized");
				buttonsPanel.setFocusable(true);
				buttonsPanel.requestFocusInWindow();
			}
		});
		buttonsPanel.add(initBtn);

		listener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (serialPortInitialized) {
					String action = e.getActionCommand();
					switch (action) {
					case "Start":
						break;
					case "Stop":
						break;
					case "Kick":
						RobotCommunication.getInstance().sendKick();
						System.out.println("Kick");
						break;
					default:
						break;
					}
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
					case KeyEvent.VK_UP:
						System.out.println("Released UP");
						RobotCommunication.getInstance().sendStopMoveUp();
						upBtnPressed = false;
						break;
					case KeyEvent.VK_DOWN:
						RobotCommunication.getInstance().sendStopMoveDown();
						System.out.println("Released DOWN");
						downBtnPressed = false;
						break;
					case KeyEvent.VK_LEFT:
						RobotCommunication.getInstance().sendStopTurnLeft();
						System.out.println("Released LEFT");
						leftBtnPressed = false;
						break;
					case KeyEvent.VK_RIGHT:
						RobotCommunication.getInstance().sendStopTurnRight();
						System.out.println("Released RIGHT");
						rightBtnPressed = false;
						break;
					case KeyEvent.VK_1:
						oneBtnPressed = false;
						RobotCommunication.getInstance().sendMoveForward10();
						System.out.println("Released 1");
						break;
					case KeyEvent.VK_2:
						twoBtnPressed = false;
						RobotCommunication.getInstance().sendMoveForward50();
						System.out.println("Released 2");
						break;
					case KeyEvent.VK_3:
						threeBtnPressed = false;
						RobotCommunication.getInstance().sendMoveBackward10();
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
					case KeyEvent.VK_UP:
						if (!upBtnPressed) {
							RobotCommunication.getInstance().sendStartMoveUp();
							System.out.println("Pressed UP");
							upBtnPressed = true;
						}
						break;
					case KeyEvent.VK_DOWN:
						if (!downBtnPressed) {
							RobotCommunication.getInstance()
									.sendStartMoveDown();
							System.out.println("Pressed DOWN");
							downBtnPressed = true;
						}
						break;
					case KeyEvent.VK_LEFT:
						if (!leftBtnPressed) {
							RobotCommunication.getInstance()
									.sendStartTurnLeft();
							System.out.println("Pressed LEFT");
							leftBtnPressed = true;
						}
						break;
					case KeyEvent.VK_RIGHT:
						if (!rightBtnPressed) {
							RobotCommunication.getInstance()
									.sendStartTurnRight();
							System.out.println("Pressed RIGHT");
							rightBtnPressed = true;
						}
						break;
					case KeyEvent.VK_1:
						if (!oneBtnPressed) {
							RobotCommunication.getInstance()
									.sendMoveForward10();
							System.out.println("Moved Forward 10cm");
						}
						break;
					case KeyEvent.VK_2:
						if (!twoBtnPressed) {
							RobotCommunication.getInstance()
									.sendMoveForward50();
							System.out.println("Moved Forward 50cm");
						}
						break;
					case KeyEvent.VK_3:
						if (!threeBtnPressed) {
							RobotCommunication.getInstance()
									.sendMoveBackward10();
							System.out.println("Moved Backward 10cm");
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

		JButton kickBtn = new JButton("Kick");
		kickBtn.setSize(100, 50);
		kickBtn.setBackground(Color.GRAY);
		kickBtn.setForeground(Color.BLACK);
		buttonsPanel.add(kickBtn);
		kickBtn.addActionListener(listener);

	}
}

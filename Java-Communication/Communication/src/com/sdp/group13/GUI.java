package com.sdp.group13;

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

	// private Communication mCommunication;

	public GUI() {
		this.portNames = Communication.getInstance().getAvailablePorts();

		createFrame();
		addMovementBtns();
	}

	private void createFrame() {
		setTitle("Robot Controller");

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
				Communication.getInstance().initializeSerialPort(
						String.valueOf(portNamesList.getSelectedItem()));
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
					RobotCommunication rc = new RobotCommunication();
					switch (action) {
					case "Start":

						break;
					case "Stop":

						break;
					case "Up":

						break;
					case "Down":

						break;
					case "Right":

						break;
					case "Left":

						break;
					case "Kick":
						rc.sendFKick(1);
						break;
					default:
						break;
					}
					System.out.println(action + ": ");
				} else {
					System.out.println("Serial port not initialized");
				}
			}
		};

		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				int code = e.getKeyCode();
				switch (code) {
				case KeyEvent.VK_UP:
					System.out.println("UP");
					break;
				case KeyEvent.VK_DOWN:
					break;
				case KeyEvent.VK_LEFT:
					break;
				case KeyEvent.VK_RIGHT:
					break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
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

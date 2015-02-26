package com.sdp.vision.gui.tools;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.sdp.Constants;
import com.sdp.planner.RobotCommands;
import com.sdp.strategy.StrategyController;
import com.sdp.strategy.StrategyController.StrategyType;
import com.sdp.vision.gui.GUITool;
import com.sdp.vision.gui.VisionGUI;

public class StrategySelectorTool implements GUITool {

	private VisionGUI gui;
	private JFrame subWindow;
	private StrategyController sc;
	private JLabel infoLabel = new JLabel();

	public StrategySelectorTool(VisionGUI gui, StrategyController sc) {
		this.gui = gui;
		this.sc = sc;

		subWindow = new JFrame("Strategy Selector");
		subWindow.setResizable(false);
		subWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		infoLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);

		Container contentPane = subWindow.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.add(infoLabel);
		contentPane.add(new StrategyPicker());
		contentPane.add(new AdvancedStrategyEnabler());
		updateInfoLabel();

	}

	private void updateInfoLabel() {
		infoLabel.setText("<html>Current strategy: <b>"
				+ sc.getCurrentStrategy() + "</b><br />Auto control: <b>"
				+ (sc.isPaused() ? "paused" : "running") + "</b></html>");
		subWindow.pack();
	}

	@Override
	public void activate() {
		Rectangle mainWindowBounds = gui.getBounds();
		subWindow.setLocation(mainWindowBounds.x, mainWindowBounds.y
				+ mainWindowBounds.height);
		subWindow.pack();
		subWindow.setVisible(true);
	}

	@Override
	public boolean deactivate() {
		subWindow.setVisible(false);
		return true;
	}

	@Override
	public void dispose() {
		subWindow.dispose();
	}

	@SuppressWarnings("serial")
	public class StrategyPicker extends JPanel {
		private JButton atkStrat = new JButton("Attacking");
		private JButton defStrat = new JButton("Defending");
		private JButton passStrat = new JButton("Passing");
		private JButton nullStrat = new JButton("Do nothing");
		private JButton pauseController = new JButton("Pause");
		private JButton startController = new JButton("Start");

		public StrategyPicker() {
			this.add(atkStrat);
			this.add(defStrat);
			this.add(passStrat);
			this.add(nullStrat);

			atkStrat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Constants.currentStrategy = Constants.Strategy.ATTACK;
					sc.changeToStrategy(StrategyType.ATTACKING);
				}
			});
			defStrat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Constants.currentStrategy = Constants.Strategy.DEFEND;
					sc.changeToStrategy(StrategyType.DEFENDING);
				}
			});
			passStrat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Constants.currentStrategy = Constants.Strategy.PASSING;
					sc.changeToStrategy(StrategyType.PASSING);
				}
			});
			nullStrat.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Constants.currentStrategy = Constants.Strategy.NONE;
					RobotCommands.stop();
					sc.changeToStrategy(StrategyType.DO_NOTHING);
				}
			});
			pauseController.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					Constants.currentStrategy = Constants.Strategy.NONE;
					RobotCommands.stop();
					sc.setPaused(true);
				}
			});
			startController.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					sc.setPaused(false);
				}
			});
		}

	}

	@SuppressWarnings("serial")
	public class AdvancedStrategyEnabler extends JPanel {
		private JCheckBox confusionEnabled = new JCheckBox("Confuse Shot");
		private JCheckBox bouncePassEnabled = new JCheckBox("Bounce Pass");

		public AdvancedStrategyEnabler() {
			this.add(confusionEnabled);
			this.add(bouncePassEnabled);

			confusionEnabled.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					StrategyController.confusionEnabled = confusionEnabled
							.isSelected();
				}
			});

			bouncePassEnabled.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					StrategyController.bouncePassEnabled = bouncePassEnabled
							.isSelected();
				}
			});
		}
	}

}

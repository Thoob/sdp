package com.sdp;

import com.sdp.planner.RobotPlanner;

public class Main {
	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setVisible(true);

		System.out.println(RobotPlanner.shouldRotateRight(300, 80)); // false
		System.out.println(RobotPlanner.shouldRotateRight(75, 80));  // false
		System.out.println(RobotPlanner.shouldRotateRight(95, 80));  // true
		System.out.println(RobotPlanner.shouldRotateRight(80, 200));  // true
	}
}

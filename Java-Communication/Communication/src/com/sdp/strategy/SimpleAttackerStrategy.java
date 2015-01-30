package com.sdp.strategy;

import java.awt.Point;

import com.sdp.world.DynamicWorldState;

public class SimpleAttackerStrategy {
	public void goToPosition(float x, float y) {

	}

	public void sendDynamicWorldState(DynamicWorldState dynWorldState) {
		Point ballPos = (Point) dynWorldState.getBall().getPoint();
		Point robotPos = (Point) dynWorldState.getDefender().getCenter();

		System.out.println("Robot pos " + robotPos.getX() + " "
				+ robotPos.getY());

	}
}

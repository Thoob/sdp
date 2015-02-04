package com.sdp.strategy;

import com.sdp.Communication;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class SimpleDefenderStrategy extends GeneralStrategy {

	public void sendWorldState(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();
		
		if (!Communication.getInstance().isPortInitialized()) {
			System.out.println("Port not initialized");
			return;
		}
		
		// 1. Predict position where the ball will go
		
		// 2. Go to this position
		
		// 3. Catch the ball
	}
}

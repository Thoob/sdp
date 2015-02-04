package com.sdp.strategy;

import com.sdp.Communication;
import com.sdp.RobotCommunication;
import com.sdp.planner.RobotPlanner;
import com.sdp.prediction.Calculations;
import com.sdp.strategy.GeneralStrategy.RobotType;
import com.sdp.strategy.Operation.Type;
import com.sdp.vision.PitchConstants;
import com.sdp.world.DynamicWorldState;
import com.sdp.world.DynamicWorldState.Ball;
import com.sdp.world.DynamicWorldState.Robot;

public class SimpleAttackerStrategy extends GeneralStrategy {

	public void sendWorldState(DynamicWorldState dynWorldState) {
		Robot robot = dynWorldState.getAttacker();
		Ball ball = dynWorldState.getBall();

		if (!Communication.getInstance().isPortInitialized()) {
			System.out.println("Port not initialized");
			return;
		}

		// 1. change direction so that robot looks towards the ball
				double diffInHeadings = RobotPlanner.getInstance()
						.differenceInHeadings(robot, ball);
				boolean facingBall;
				if(diffInHeadings<0.6){
					facingBall = true;
				} else {
					facingBall = false;
				}
				// Decide which direction to rotate
				if(!facingBall){
					// rotate right
					RobotCommunication.getInstance().move(0, 40);
				} 

				
		// 2. go straight until you can catch the ball
				boolean canCatchBall = RobotPlanner.getInstance().canCatchBall(robot,
						ball);
				boolean doesOurRobotHaveBall = RobotPlanner.getInstance()
						.doesOurRobotHaveBall(robot, ball);
				if (!canCatchBall && !doesOurRobotHaveBall && facingBall) {
					RobotCommunication.getInstance().move(40, 30); //TODO fix by replacing motors!
					return;
				} 
		
		// 3. catch the ball if we don't have it
				if(canCatchBall && !doesOurRobotHaveBall){
					RobotCommunication.getInstance().sendCatch();
				}

		// 4. go to a position from which robot can score
				if(doesOurRobotHaveBall){
					//TODO
				}
		// 5. score
//				if(canScore){
//					RobotCommunication.getInstance().sendKick(300);
//				}
	}


	/**
	 * goes to a position from which it can score and scores
	 * 
	 * @param dynWorldState
	 */
	private void scoreGoal(DynamicWorldState dynWorldState) {
		// go to the center before scoring

		// turn towards the goal

		// kick
	}
}

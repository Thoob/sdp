package com.sdp.strategy.interfaces;

import com.sdp.vision.interfaces.WorldStateReceiver;

public interface Strategy extends WorldStateReceiver{
	
	public void startControlThread();
	public void stopControlThread();

}
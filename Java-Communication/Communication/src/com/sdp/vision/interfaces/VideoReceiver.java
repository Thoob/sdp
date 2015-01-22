package com.sdp.vision.interfaces;

import java.awt.image.BufferedImage;

public interface VideoReceiver {
	void sendFrame(BufferedImage frame, float delta, int frameCounter,
			long timestamp);
}
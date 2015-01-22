package com.sdp.vision;

import java.awt.Color;

public class PixelInfo {
	
	public int column,row; // Position of pixel in frame
	public int r,g,b; // RGB values of pixel in frame
	public float h,s,v; // HSV values of pixel in frame
	
	public PixelInfo(Color c){
		this.r = c.getRed();
		this.g = c.getGreen();
		this.b = c.getBlue();
		
		float[] hsvals = Color.RGBtoHSB(r, g, b, null);
		this.h = hsvals[0];
		this.s = hsvals[1];
		this.v = hsvals[2];
	}

}
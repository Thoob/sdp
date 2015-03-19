package com.sdp;

public class Debug {
	private static String previousOut = "";

	public static void out(String out) {
		out(out, null);
	}

	public static void out(String out, Object param) {
		if (!out.equals(previousOut)) {
			if (param != null)
				System.out.println(out + " " + param);
			else
				System.out.println(out);
			previousOut = out;
		}
	}
}

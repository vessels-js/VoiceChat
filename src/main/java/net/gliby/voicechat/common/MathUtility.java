package net.gliby.voicechat.common;

public class MathUtility {

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

}

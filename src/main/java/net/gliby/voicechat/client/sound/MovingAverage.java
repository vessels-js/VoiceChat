package net.gliby.voicechat.client.sound;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage {

	private final Queue<BigDecimal> window = new LinkedList<BigDecimal>();
	private final int period;
	private BigDecimal sum = BigDecimal.ZERO;

	public MovingAverage(int period) {
		if (period < 0) System.err.println("Period must be a positive integer");
		this.period = period;
	}

	public void add(int val) {
		final BigDecimal num = new BigDecimal(val);
		sum = sum.add(num);
		window.add(num);
		if (window.size() > period) {
			sum = sum.subtract(window.remove());
		}
	}

	public BigDecimal getAverage() {
		if (window.isEmpty()) return BigDecimal.ZERO; // technically the average
		final BigDecimal divisor = BigDecimal.valueOf(window.size());
		return sum.divide(divisor, 2, RoundingMode.HALF_UP);
	}
}
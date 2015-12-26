package net.gliby.voicechat.client.gui;

/*
 * @Author - Jeremy Trifilo (Digistr).
 */

public class ValueFormat {
	private static final char[] PREFIXS = { 'K', 'M', 'B', 'T' };

	public static final byte COMMAS = 0x1;
	public static final byte THOUSANDS = 0x40;
	public static final short MILLIONS = 0x80;
	public static final short BILLIONS = 0xC0;
	public static final short TRILLIONS = 0x100;

	public static String format(long value, int settings) {
		final StringBuilder sb = new StringBuilder(32);
		sb.append(value);
		final char[] data = sb.toString().toCharArray();
		final boolean commas = (settings & COMMAS) == COMMAS;
		int precision = 0;
		int prefix = 0;
		if (settings >= 0x40) {
			prefix = settings >> 6;
			if (prefix > PREFIXS.length) prefix = PREFIXS.length;
		}
		if (settings > COMMAS) precision = (settings >> 2) & 0xF;
		sb.setLength(0);
		int negative = 0;
		if (data[0] == '-') {
			negative = 1;
		}
		final int length = data.length - negative;
		if (prefix * 3 >= length) {
			prefix = (int) (length * 0.334);
			if (prefix * 3 == length && precision == 0) {
				--prefix;
			}
		}
		int end = length - (prefix * 3);
		int start = (length % 3);
		if (start == 0) start = 3;
		start += negative;
		if (end > 0 && negative == 1) sb.append('-');
		int max = end + negative;
		for (int i = negative; i < max; i++) {
			if (i == start && i + 2 < max && commas) {
				start += 3;
				sb.append(',');
			}
			sb.append(data[i]);
		}
		if (prefix > 0) {
			if (end == 0) {
				if (negative == 1 && precision > 0) sb.append('-');
				sb.append('0');
			}
			max = precision + end + negative;
			if (max > data.length) max = data.length;
			end += negative;
			while (max > end) {
				if (data[max - 1] == '0') {
					--max;
					continue;
				}
				break;
			}
			if ((max - end) != 0) sb.append('.');
			for (int i = end; i < max; i++) {
				sb.append(data[i]);
			}
			sb.append(PREFIXS[prefix - 1]);
		}
		return sb.toString();
	}

	public static final int PRECISION(int precision) {
		return precision << 2;
	}

	public static final int PREFIX(int prefix) {
		return prefix << 6;
	}

	public static String toString(int settings) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Prefix: ");
		sb.append(settings >> 6 > PREFIXS.length ? PREFIXS.length : settings >> 6);
		sb.append(", Precision: ");
		sb.append((settings >> 2) & 0xF);
		sb.append(", Commas: ");
		sb.append((settings & COMMAS) == COMMAS);
		return sb.toString();
	}
}
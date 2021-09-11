package com.jasperlorelai;

import java.util.Arrays;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.StringUtils;

public class Util {

	public static String setPrecision(String str, String precision) {
		// Return value if value isn't a floating point - can't be scaled.
		try {
			float floatValue = Float.parseFloat(str);

			// Return value if precision isn't a floating point.
			int toScale = Integer.parseInt(precision);

			// Return the scaled value.
			return BigDecimal.valueOf(floatValue).setScale(toScale, RoundingMode.HALF_UP).toString();
		} catch (NumberFormatException | NullPointerException ignored) {
			return str;
		}
	}

	public static String joinArgs(String[] args, int index) {
		return StringUtils.join(Arrays.copyOfRange(args, index, args.length), '_');
	}

}

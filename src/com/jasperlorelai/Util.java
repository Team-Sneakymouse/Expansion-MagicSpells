package com.jasperlorelai;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Util {

	public static String setPrecision(String str, String precision) {
		// Return value if value isn't a floating point - can't be scaled.
		float floatValue;
		try {
			floatValue = Float.parseFloat(str);
		} catch (NumberFormatException | NullPointerException nfe) {
			return str;
		}

		// Return value if precision isn't a floating point.
		int toScale;
		try {
			toScale = Integer.parseInt(precision);
		} catch (NumberFormatException | NullPointerException nfe) {
			return str;
		}

		// Return the scaled value.
		return BigDecimal.valueOf(floatValue).setScale(toScale, RoundingMode.HALF_UP).toString();
	}
}

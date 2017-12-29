package com.jald.reserve.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckUtil {

	public static boolean vaildateIdCard(String idCardStr) {
		if (isStrEmpty(idCardStr)) {
			return false;
		}
		String str = "^(\\d{6})(18|19|20)?(\\d{2})([01]\\d)([0123]\\d)(\\d{3})(\\d|X|x)?";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(idCardStr);
		return m.matches();
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^1[34578]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	private static boolean isStrEmpty(String value) {
		if (null == value || "".equals(value.trim())) {
			return true;
		} else {
			value = value.replaceAll(" ", "").trim();
			if (null == value || "".equals(value.trim())) {
				return true;
			}
		}
		return false;
	}
}

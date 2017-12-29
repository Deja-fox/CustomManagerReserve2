package com.jald.reserve.util;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jald.reserve.KBaseApplication;

public class ValueUtil {
	public static final String TAG = "ValueUtil";

	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern.compile("^1[34578]\\d{9}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	public static boolean isRightRegName(String name) {
		Pattern p = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_]{4,19}$");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	public static boolean isRightMoneyInput(String money) {
		Pattern p = Pattern.compile("^[0-9]+(.[0-9]{1,2})?$");
		Matcher m = p.matcher(money);
		return m.matches();
	}

	public static boolean isRightLoginName(String name) {
		Pattern p = Pattern.compile("[a-zA-Z0-9_]{3,20}$");
		Matcher m = p.matcher(name);
		return m.matches();
	}

	public static boolean isDecimalNumThan(int num, String content) {
		if (num <= 0) {
			return false;
		}
		int index = content.indexOf('.');
		if (index < 0) {
			return false;
		}
		int len = content.length();

		return (len - index) > num + 1;
	}

	public static String formarPhone(String phoneNum) {
		if (isStrEmpty(phoneNum)) {
			return "";
		}
		String space = " ";
		int len = phoneNum.length();
		int temp = 0;
		String reuslt = "";
		for (int i = len - 1; i > -1; i--) {
			reuslt = phoneNum.charAt(i) + reuslt;
			if (temp > 2) {
				reuslt = space + reuslt;
				temp = 0;
			} else {
				temp++;
			}
		}
		return reuslt;
	}

	public static String compileCode(String content) {
		StringBuffer result = new StringBuffer();
		content = content.trim();
		if (null == content || "".equals(content)) {
			return result.toString().trim();
		}
		int len = content.length();
		for (int i = 0; i < len; i++) {
			char temp = content.charAt(i);
			result.append(temp);
			if (i > 0 && i < len - 1 && ((i + 1) % 4 == 0)) {
				result.append(" ");
			}
		}

		return result.toString().trim();
	}

	public static boolean isStrNotEmpty(String value) {
		if (null == value || "".equals(value.trim())) {
			return false;
		} else {
			// 判断是否全是全角空格
			value = value.replaceAll(" ", "").trim();
			if (null == value || "".equals(value.trim())) {
				return false;
			}
		}
		return true;
	}

	public static String getLastThreeWords(String content) {
		if (isStrEmpty(content) || content.length() < 3) {
			return "";
		}
		return "(" + content.substring(content.length() - 3, content.length()) + ")";
	}

	public static boolean isStrEmpty(String value) {
		if (null == value || "".equals(value.trim())) {
			return true;
		} else {
			// 判断是否全是全角空格
			value = value.replaceAll(" ", "").trim();
			if (null == value || "".equals(value.trim())) {
				return true;
			}
		}
		return false;
	}

	public static short parseShort(String shortString) {
		if (shortString != null && !shortString.trim().equals("")) {
			try {
				return Short.valueOf(shortString);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public static int parseInt(String intString) {
		if (intString != null && !intString.trim().equals("")) {
			try {
				return Integer.valueOf(intString);
			} catch (Exception e) {
			}
		}
		return 0;
	}

	public static String newUUID() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static int containsNum(String content, String target) {
		if (content == null || "".equals(content) || target == null || "".equals(target)) {
			return 0;
		}
		return content.length() - content.replace(target, "").length();
	}

	public static double formula2(double a, double b, double c, double total) {
		double result, delta;

		c = c - total;

		if (doubleEqual(a, 0.0)) {
			if (doubleEqual(b, 0.0)) {
				return 0.0;
			} else {
				return -c / b;
			}
		}

		delta = b * b - 4 * a * c;

		if (delta < 0) {
			return 0;
		}
		result = Math.sqrt(delta);
		result = (-b + result) / 2.0;
		return result / a;
	}

	private static final double EPS = 1e-6;

	public static boolean doubleEqual(double a, double b) {
		return Math.abs(a - b) < EPS;
	}

	public static boolean validateChineseLength(String value, int length) {
		String chinese = "[\u4e00-\u9fa5]";
		Pattern pattern = Pattern.compile(chinese);
		int len = 0;
		for (int i = 0; i < value.length(); i++) {
			String temp = value.substring(i, i + 1);
			Matcher matcher = pattern.matcher(temp);
			if (matcher.matches()) {
				len += 2;
			} else {
				len += 1;
			}
		}
		return len > length;
	}

	public static boolean isListNotEmpty(List<?> noteList) {
		return null != noteList && noteList.size() > 0;
	}

	public static boolean isListEmpty(List<?> noteList) {
		return null == noteList || noteList.size() == 0;
	}

	public static boolean isNotEmpty(Object object) {
		return null != object;
	}

	public static boolean isEmpty(Object object) {
		return null == object;
	}

	public static boolean isArrayEmpty(String[] lastAccounts) {
		return lastAccounts != null && lastAccounts.length > 0;
	}

	public static double formatCoord(String optString) {
		if (isStrEmpty(optString)) {
			return 0;
		}
		double result = Double.parseDouble(optString);
		return result;
	}

	private static double EARTH_RADIUS = 6378137;
	private static double MATHPI = 3.141592653;

	public final static double PI = 3.14159265358979323;
	public final static double R = 6371229;

	public static double GetDistanceWithdrift(double lat1, double lng1, double lng2, double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 100;
		return s;
	}

	public static double rad(double d) {
		double degree;
		double cent;
		degree = d / 100;
		cent = (d - degree * 100) / 60;
		return (degree + cent) * MATHPI / 180.0;
	}

	public static double getRangle(double lat1, double lng1, double lat2, double lng2) {
		if (lat2 >= lat1 && lng2 >= lng1) {
			return Math.atan((lat2 - lat1) / (lng2 - lng1));
		} else if (lat2 >= lat1 && lng2 < lng1) {
			return Math.PI - Math.atan((lat2 - lat1) / (lng1 - lng2));
		} else if (lat2 < lat1 && lng2 >= lng1) {
			return 2 * Math.PI - Math.atan((lat1 - lat2) / (lng2 - lng1));
		} else {
			return Math.PI + Math.atan((lat1 - lat2) / (lng1 - lng2));
		}
	}

	public static String getString(int resId) {
		String result = KBaseApplication.getInstance().getString(resId);
		return result;
	}

	public static boolean checkEmail(String email) {
		String format = "\\p{Alpha}\\w{2,15}[@][a-z0-9]{3,}[.]\\p{Lower}{2,}";
		if (email.matches(format)) {
			return true;
		} else {
			return false;
		}
	}

}

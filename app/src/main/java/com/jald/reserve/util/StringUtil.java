package com.jald.reserve.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringUtil {

	/**
	 * 将传入的数字进行鉴别，如为数字，则转换为HEX，并进行补长处理前补零
	 * 
	 * @param inStr
	 *            传入的数据
	 * @param needLen
	 *            设定长度
	 * @return
	 */
	public static String str2HexStrFrontAdd0(String inStr, int needLen) {
		if (inStr == null) {
			return null;
		}

		String temp = String.format("%h", Integer.valueOf(inStr));

		return appendStr(temp, needLen, "0", true, true);
	}

	public static String formatDebitCardAccount(String accountNo) {
		StringBuilder sb = new StringBuilder();
		sb.append(accountNo.substring(0, 4));
		sb.append(" ");
		sb.append("****");
		sb.append(" ");
		sb.append("****");
		sb.append(" ");
		sb.append(accountNo.substring(accountNo.length() - 4, accountNo.length()));
		return sb.toString();
	}

	public static String appendStr(String inStr, int needLen, String appendWord, boolean isFront, boolean isMoreCut) {
		if (inStr == null || appendWord == null || appendWord.length() != 1) {
			return null;
		}
		int len = inStr.length();
		if (needLen <= len) {
			if (isMoreCut) {
				return inStr.substring(len - needLen, len);
			} else {
				return inStr;
			}
		} else {
			StringBuffer sb = new StringBuffer();
			if (!isFront) {
				sb.append(inStr);
			}
			for (int i = 0; i < (needLen - len); i++) {
				sb.append(appendWord);

			}
			if (isFront) {
				sb.append(inStr);
			}
			return sb.toString();
		}
	}

	/**
	 * ASCII字符串转STR
	 * 
	 * @param inStr
	 * @return
	 */
	public static String asciiToString(String inStr) {
		StringBuffer sbu = new StringBuffer();

		for (int i = 0; i < inStr.length(); i += 2) {
			sbu.append((char) Integer.parseInt(inStr.substring(i, i + 2), 16));
		}
		return sbu.toString();
	}

	/**
	 * 数据添加长度头，并拼组数据
	 * 
	 * @param inStr
	 * @return
	 */
	public static String writeLenAndStr4Str(String inStr) {

		return writeLenAndStr4Str(inStr, false);
	}

	/**
	 * 数据添加长度头，并拼组HEX数据
	 * 
	 * @param inStr
	 * @return
	 */
	public static String writeLenAndHexStr4Str(String inStr) {

		return writeLenAndStr4Str(inStr, true);
	}

	/**
	 * 数据添加长度头，并拼组数据（可选择是否需要转换数据为HEX字符串）
	 * 
	 * @param inStr
	 * @param inStr
	 * @return
	 */
	private static String writeLenAndStr4Str(String inStr, boolean isChangeFlag) {
		if (inStr == null || isStrEmpty(inStr)) {
			return null;
		}

		int len;
		String tempInStr = inStr;

		if (isChangeFlag) {
			tempInStr = str2HexStr(inStr);
		}

		String lenStr = Integer.toHexString(tempInStr.length() / 2);

		if (lenStr.length() == 1) {
			lenStr = "0" + lenStr;
		}

		return lenStr + tempInStr;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String HEX字符串
	 */
	public static String str2HexStr(String str) {
		if (isStrEmpty(str)) {
			throw new NullPointerException("makePLVStr cmd输入为空");
		}// 排除输入错误

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) { // 将两个高低位进行拆分获取
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	/**
	 * 字符串转换成十六进制字符串，限制长度，如长度不足则前方补零
	 * 
	 * @param String
	 *            str 待转换的ASCII字符串
	 * @return String HEX字符串
	 * 
	 * @return int len 有多少字节长
	 */
	public static String str2HexStr(String str, int len) {
		String temp = str2HexStr(str);
		if (temp.length() > len * 2) {
			return temp;
		} else {
			StringBuffer s = new StringBuffer(temp);
			// 这里补充的是30 即
			for (int i = temp.length(); i < len * 2; i += 2) {
				s.insert(0, "30");
			}
			return s.toString();
		}

	}

	/**
	 * 十六进制转换字符串
	 * 
	 * @param String
	 *            str Byte字符串(Byte之间无分隔符 如:[616C6B])
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;

		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	/**
	 * bytes转换成十六进制字符串
	 * 
	 * @param byte[] b byte数组
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b) {
		String stmp = "";
		StringBuilder sb = new StringBuilder("");
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
			sb.append(" ");
		}
		return sb.toString().toUpperCase().trim();
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param String
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		System.out.println(l);
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
		}
		return ret;
	}

	public static byte[] toBytes(String str) {
		if (str == null) {
			throw new IllegalArgumentException("binary string is null");
		}
		char[] chs = str.toCharArray();
		byte[] bys = new byte[chs.length / 2];
		// byte[] bys = new byte[chs.length];
		int offset = 0;
		int k = 0;
		while (k < chs.length) {
			bys[offset++] = (byte) ((toInt(chs[k++]) << 4) | toInt(chs[k++]));
		}
		return bys;
	}

	private static int toInt(char a) {
		if (a >= '0' && a <= '9') {
			return a - '0';
		}
		if (a >= 'a' && a <= 'f') {
			return a - 'a' + 10;
		}
		if (a >= 'A' && a <= 'F') {
			return a - 'A' + 10;
		}
		throw new IllegalArgumentException("parameter \"" + a + "\"is not hex number!");
	}

	/**
	 * String的字符串转换成unicode的String
	 * 
	 * @param String
	 *            strText 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else
				// 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * 
	 * @param String
	 *            hex 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex) {
		int t = hex.length() / 6;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++) {
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 高位需要补上00再转
			String s1 = s.substring(2, 4) + "00";
			// 低位直接转
			String s2 = s.substring(4);
			// 将16进制的string转为int
			int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
			// 将int转换为字符
			char[] chars = Character.toChars(n);
			str.append(new String(chars));
		}
		return str.toString();
	}

	/**
	 * 判断字符串是否为空 为空的定义包含 半角空格 和 全角空格
	 * 
	 * @param value
	 *            输入的数据
	 * @return 如果为空，返回真
	 */
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

	/**
	 * 转为价钱格式
	 * 
	 * @param money
	 * @return
	 */
	public static String formatMoneySeparated(Object money) {
		if (null == money) {
			return "";
		}
		BigDecimal bignumber = toBigDecimal(money, 2);
		DecimalFormat df = new DecimalFormat("#,##0.00");
		return df.format(bignumber);
	}

	/**
	 * String转数字，scale是保留到小数点后N位，四舍五入
	 * 
	 * @param obj
	 * @param scale
	 * @return
	 */
	public static BigDecimal toBigDecimal(Object obj, int scale) {
		if (obj == null)
			obj = "0";
		String value = obj.toString();
		if (value.length() == 0)
			obj = "0";
		BigDecimal bignumber = (new BigDecimal(value)).setScale(scale, 4);
		return bignumber;
	}

}

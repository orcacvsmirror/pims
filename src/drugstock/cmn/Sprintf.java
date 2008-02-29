/*
 *******************************************************************
 * Project code name "ORCA"
 * 日医標準レセプトソフト（JMA standard receipt software）
 * Copyright(C) 2002 JMA (Japan Medical Association)
 *
 * This program is part of "JMA standard receipt software".
 *
 *     This program is distributed in the hope that it will be useful
 * for further advancement in medical care, according to JMA Open
 * Source License, but WITHOUT ANY WARRANTY.
 *     Everyone is granted permission to use, copy, modify and
 * redistribute this program, but only under the conditions described
 * in the JMA Open Source License. You should have received a copy of
 * this license along with this program. If not, stop using this
 * program and contact JMA, 2-28-16 Honkomagome, Bunkyo-ku, Tokyo,
 * 113-8621, Japan.
 ********************************************************************
 */
package drugstock.cmn;

/**
 * 文字列操作汎用クラス
 */

import java.text.NumberFormat;

//
// Sprintf クラス
//

//
// 使用方法
//
// Sprintf.format("'%5d'", 42) -> ' 42'
// Sprintf.format("'%-10s'", "hi") -> 'hi '
// Sprintf.format("'%s: 0x%08x'", "address", 8592837) -> 'address: 0831dc5'
//

public class Sprintf {

	static char outL[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	        'a', 'b', 'c', 'd', 'e', 'f' };
	static char outU[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
	        'A', 'B', 'C', 'D', 'E', 'F' };

	// コンストラクタ
	private Sprintf() {
	}

	// フォーマット形式の定義
	private static boolean intFormat(StringBuffer out, int width, boolean left,
	        boolean zeroes, int base, long n) {
		char digits[] = outL;
		boolean negative = false;

		switch (base) {
		case 'd':
		case 'i':
			base = 10;
			break;

		case 'o':
			base = 8;
			break;

		case 'x':
			base = 16;
			break;

		case 'X':
			base = 16;
			digits = outU;
			break;

		default:
			return false; // Unrecognized format character
		}

		char output[] = new char[20];
		int index = 0;
		if (n < 0) {
			negative = true;
			n = -n;
			width--;
		}

		while (n > 0) {
			long c = n % base;
			n = n / base;
			output[index++] = digits[(int)c];
		}
		if (index == 0) {
			output[index++] = digits[0];
		}

		if (index > width) {
			if (negative)
				out.append('-');
			for (int i = index - 1; i >= 0; i--) {
				out.append(output[i]);
			}
		} else {
			if (left) {
				if (negative)
					out.append('-');
				for (int i = index - 1; i >= 0; i--) {
					out.append(output[i]);
				}
				addChars(out, width - index, ' ');
			} else {
				if (zeroes) {
					if (negative)
						out.append('-');
					addChars(out, width - index, '0');
				} else {
					addChars(out, width - index, ' ');
					if (negative)
						out.append('-');
				}
				for (int i = index - 1; i >= 0; i--) {
					out.append(output[i]);
				}
			}
		}
		return true;
	}

	// 文字列を指定のフォーマットで初期化する。
	// @param fmt フォーマット形式
	// @param n long型の文字列変換前
	// @return 変換後文字列
	public static String format(String fmt, long n) {
		int position = 0;
		StringBuffer output = new StringBuffer();
		char character;
		int width;
		boolean left = false;
		boolean leadingZeroes = false;

		while (position < fmt.length()) {
			character = fmt.charAt(position++);
			if (character == '%') {
				character = fmt.charAt(position++);
				if (character == '-') {
					left = true;
					character = fmt.charAt(position++);
				}
				if (character == '0') {
					leadingZeroes = true;
					character = fmt.charAt(position++);
				}
				width = 0;
				while (character >= '0' && character <= '9') {
					width = width * 10 + (character - '0');
					character = fmt.charAt(position++);
				}
				if (!intFormat(output, width, left, leadingZeroes, character, n)) {
					output.append("**FORMAT ERROR**");
					continue;
				}
			} else {
				output.append(character);
			}
		}
		return output.toString();
	}

	// 文字列を指定のフォーマットで初期化する。
	// @param fmt フォーマット形式
	// @param n String型の文字列変換前
	// @return 変換後文字列
	public static String format(String fmt, String s) {
		int position = 0;
		StringBuffer output = new StringBuffer();
		char character;
		int width;
		boolean left = false;

		while (position < fmt.length()) {
			character = fmt.charAt(position++);
			if (character == '%') {
				character = fmt.charAt(position++);
				if (character == '-') {
					left = true;
					character = fmt.charAt(position++);
				}
				width = 0;
				while (character >= '0' && character <= '9') {
					width = width * 10 + (character - '0');
					character = fmt.charAt(position++);
				}
				if (character != 's') {
					output.append("**FORMAT ERROR**");
					continue;
				}

				if (s.length() > width) {
					output.append(s);
				} else {
					if (left) {
						output.append(s);
						addChars(output, width - s.length(), ' ');
					} else {
						addChars(output, width - s.length(), ' ');
						output.append(s);
					}
				}
			} else {
				output.append(character);
			}
		}

		return output.toString();
	}

	// 文字列を指定のフォーマットで初期化する。
	// @param fmt フォーマット形式
	// @param n char型の文字列変換前
	// @return 変換後文字列
	public static String format(String fmt, char c) {
		int position = 0;
		StringBuffer output = new StringBuffer();
		char character;
		int width;
		boolean left = false;

		while (position < fmt.length()) {
			character = fmt.charAt(position++);
			if (character == '%') {
				character = fmt.charAt(position++);
				if (character == '-') {
					left = true;
					character = fmt.charAt(position++);
				}
				width = 0;
				while (character >= '0' && character <= '9') {
					width = width * 10 + (character - '0');
					character = fmt.charAt(position++);
				}
				if (character != 'c') {
					output.append("**FORMAT ERROR**");
					continue;
				}

				if (left) {
					output.append(c);
					addChars(output, width - 1, ' ');
				} else {
					addChars(output, width - 1, ' ');
					output.append(c);
				}
			} else {
				output.append(character);
			}
		}

		return output.toString();
	}

	// 数字を小数点、カンマ付き指定のフォーマットで初期化する。
	// @param int_col 整数部桁数
	// @param fraction_col 小数部桁数
	// @param d 入力数字
	// @return 変換後文字列
	public static String formatCanma(int int_col, int fraction_col, double d) {
		// NumberFormatのインスタンスの取得
		NumberFormat nf = NumberFormat.getInstance();

		// 整数部分の桁数の設定
		nf.setMaximumIntegerDigits(int_col);
		nf.setMinimumIntegerDigits(1);

		// 小数点以下の桁数の設定
		nf.setMaximumFractionDigits(fraction_col);
		nf.setMinimumFractionDigits(fraction_col);

		return nf.format(d);
	}

	// 数字をカンマ付き指定のフォーマットで初期化する。
	// @param int_col 整数部桁数
	// @param d 入力数字
	// @return 変換後文字列
	public static String formatCanma(int int_col, long value) {
		// NumberFormatのインスタンスの取得
		NumberFormat nf = NumberFormat.getInstance();

		// 整数部分の桁数の設定
		nf.setMaximumIntegerDigits(int_col);
		nf.setMinimumIntegerDigits(1);

		// 小数点以下の桁数の設定
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);

		return nf.format(value);
	}

	public static String formatCanma(int int_col, int value) {
		return formatCanma(int_col, (long)value);
	}

	// 数字を指定のフォーマットで初期化する。
	// @param int_col 整数部桁数
	// @param fraction_col 小数部桁数
	// @param d 入力数字
	// @return 変換後文字列
	public static String format(int int_col, int fraction_col, double d) {
		// NumberFormatのインスタンスの取得
		NumberFormat nf = NumberFormat.getInstance();

		// 整数部分の桁数の設定
		nf.setMaximumIntegerDigits(int_col);
		nf.setMinimumIntegerDigits(1);

		// 小数点以下の桁数の設定
		nf.setMaximumFractionDigits(fraction_col);
		nf.setMinimumFractionDigits(fraction_col);

		String canma = nf.format(d);
		String ret = "";
		for (int i = 0; i < canma.length(); i++) {
			String wk = canma.substring(i, i + 1);
			if (wk.equals("1") || wk.equals("2") || wk.equals("3")
			        || wk.equals("4") || wk.equals("5") || wk.equals("6")
			        || wk.equals("7") || wk.equals("8") || wk.equals("9")
			        || wk.equals("0") || wk.equals("-") || wk.equals(".")) {
				ret += canma.substring(i, i + 1);
			}
		}
		return ret;
	}

	// 数字を指定のフォーマットで初期化する。
	// @param fmt フォーマット形式
	// @param n double型の文字列変換前
	// @return 変換後文字列
	public static String format(String fmt, double d) {
		int position = 0;
		StringBuffer output = new StringBuffer();
		char character;
		int width = 0;
		boolean left = false;
		boolean leadingZeroes = false;

		while (position < fmt.length()) {
			character = fmt.charAt(position++);
			if (character == '%') {
				character = fmt.charAt(position++);
				if (character == '-') {
					left = true;
					character = fmt.charAt(position++);
				}
				while (character >= '0' && character <= '9') {
					width = width * 10 + (character - '0');
					character = fmt.charAt(position++);
				}
				if (character != 'f') {
					output.append("**FORMAT ERROR**");
					continue;
				}

				String temp = doubleFormat(d, 0, width);
				output.append(temp);
			} else {
				output.append(character);
			}
		}
		return output.toString();
	}

	private static String doubleFormat(double d, int width, int afterPeriod) {
		StringBuffer output = new StringBuffer();
		int exponent;
		double temp;
		int digit;
		int zeroesNeeded = 0;

		exponent = (int)(Math.floor(Math.log(d) / Math.log(10)));
		d += 0.5 * Math.pow(10, -afterPeriod);

		temp = d / Math.pow(10, exponent);

		output.append((char)(Math.floor(temp) + '0'));
		output.append('.');
		for (int i = 0; i < afterPeriod; i++) {
			temp *= 10;
			digit = ((int)temp) % 10;
			if (digit == 0) {
				zeroesNeeded++;
			} else {
				if (zeroesNeeded > 0) {
					addChars(output, zeroesNeeded, '0');
					zeroesNeeded = 0;
				}
				output.append((char)(digit + '0'));
			}

		}
		output.append('e');
		output.append(exponent);
		return output.toString();
	}

	/**
	 * Produce a string depending on the format pattern. This version will
	 * format two integers.
	 * 
	 * The formatting rules are identical to those of the sprintf() C library
	 * function. The only restriction is that only two '%' escapes can be used
	 * and the commands have to be 'd'.
	 * 
	 * @param fmt
	 *            the format string
	 * @param n1
	 *            the first integer to be formatted
	 * @param n2
	 *            the second integer to be formatted
	 * @return a new String with the formatted text.
	 */
	public static String format(String fmt, long n1, long n2) {
		StringBuffer output = new StringBuffer();
		int first = 0;
		int last;

		last = fmt.indexOf('%');
		if (last < 0)
			return fmt;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), n1));
		first = last;

		output.append(format(fmt.substring(first), n2));

		return output.toString();
	}

	/**
	 * Produce a string depending on the format pattern. This version will
	 * format a string and an integer.
	 * 
	 * The formatting rules are identical to those of the sprintf() C library
	 * function. The only restriction is that only two '%' escapes can be used
	 * and the commands have to be 's' and 'd' (in that order).
	 * 
	 * @param fmt
	 *            the format string
	 * @param s1
	 *            the string to be formatted
	 * @param n2
	 *            the integer to be formatted
	 * @return a new String with the formatted text.
	 */
	public static String format(String fmt, String s1, long n2) {
		StringBuffer output = new StringBuffer();
		int first = 0;
		int last;

		last = fmt.indexOf('%');
		if (last < 0)
			return fmt;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), s1));
		first = last;

		output.append(format(fmt.substring(first), n2));

		return output.toString();
	}

	/**
	 * Produce a string depending on the format pattern. This version will
	 * format an integer and a string.
	 * 
	 * The formatting rules are identical to those of the sprintf() C library
	 * function. The only restriction is that only two '%' escapes can be used
	 * and the commands have to be 'd' and 's' (in that order).
	 * 
	 * @param fmt
	 *            the format string
	 * @param n1
	 *            the integer to be formatted
	 * @param s2
	 *            the string to be formatted
	 * @return a new String with the formatted text.
	 */
	public static String format(String fmt, long n1, String s2) {
		StringBuffer output = new StringBuffer();
		int first = 0;
		int last;

		last = fmt.indexOf('%');
		if (last < 0)
			return fmt;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), n1));
		first = last;

		output.append(format(fmt.substring(first), s2));

		return output.toString();
	}

	/**
	 * Produce a string depending on the format pattern. This version will
	 * format two strings.
	 * 
	 * The formatting rules are identical to those of the sprintf() C library
	 * function. The only restriction is that only two '%' escapes can be used
	 * and the commands have to be '2'.
	 * 
	 * @param fmt
	 *            the format string
	 * @param s1
	 *            the first string to be formatted
	 * @param s2
	 *            the second string to be formatted
	 * @return a new String with the formatted text.
	 */
	public static String format(String fmt, String s1, String s2) {
		StringBuffer output = new StringBuffer();
		int first = 0;
		int last;

		last = fmt.indexOf('%');
		if (last < 0)
			return fmt;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), s1));
		first = last;

		output.append(format(fmt.substring(first), s2));

		return output.toString();
	}

	/**
	 * Produce a string depending on the format pattern. This version will
	 * format three integers.
	 * 
	 * The formatting rules are identical to those of the sprintf() C library
	 * function. The only restriction is that only three '%' escapes can be used
	 * and the commands have to be 'd'.
	 * 
	 * @param fmt
	 *            the format string
	 * @param n1
	 *            the first integer to be formatted
	 * @param n2
	 *            the second integer to be formatted
	 * @param n3
	 *            the third integer to be formatted
	 * @return a new String with the formatted text.
	 */
	public static String format(String fmt, long n1, long n2, long n3) {
		StringBuffer output = new StringBuffer();
		int first = 0;
		int last;

		last = fmt.indexOf('%');
		if (last < 0)
			return fmt;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), n1));
		first = last;

		last = fmt.indexOf('%', last + 1);
		output.append(format(fmt.substring(first, last), n2));
		first = last;

		output.append(format(fmt.substring(first), n3));

		return output.toString();
	}

	private static void addChars(StringBuffer s, int n, char c) {
		for (int i = 0; i < n; i++)
			s.append(c);
	}

}

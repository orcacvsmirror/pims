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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 汎用クラス
 */

public class Common {

	public Common() {
	}

	/**
	 * double型変数を四捨五入する
	 * 
	 * @param double型変数
	 * @param down_to_decimal
	 *            返り値を、"0":整数値、"1":小数点以下２桁にする
	 * @return 小数点以下を編集したdouble型変数
	 */
	public double getRoundDouble(double d, String down_to_decimal) {
		double ans;
		if (down_to_decimal.equals("1")) {
			d *= 10;
			int i = (int)d; // 小数点２位以下を切り捨て
			if (i % 10 >= 5) {
				i += 10;
			}
			i /= 10;
			ans = i;
		} else {
			ans = d;
		}
		return ans;
	}

	/**
	 * 年月日を引数として受け取り、指定のフォーマット(桁数)で日付文字列を生成する
	 * 
	 * @param year,month,day
	 *            日付要素
	 * @param format
	 *            指定のフォーマット
	 * @return 指定のフォーマットに編集した日付文字列
	 */
	public String setStrDate(int year, int month, int day, String format) {
		SimpleDateFormat tmpDateFormat = new SimpleDateFormat(format);
		Date tmpDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, day);
		tmpDate = calendar.getTime();

		return tmpDateFormat.format(tmpDate);
	}

	/**
	 * 実数値のStringをchar[]に変換、かつ指定バイトにする（残りはスペース） カンマは付加しない
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @param downDecimal
	 *            小数点以下の桁数
	 * @return char[]の指定バイト変数
	 */
	public char[] getByteDataElseSpaceDownDecimal(String src, int maxLen,
	        int downDecimal) {
		if (src.equals("") || src.equals("-"))
			return this.getByteData(src, maxLen);
		else
			return this.getByteData(Sprintf.format(12, downDecimal, Double.parseDouble(src)), maxLen);
	}

	/**
	 * 実数値のStringをchar[]に変換、かつ指定バイトにする（残りはスペース） また、カンマを付加する
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @param upDecimal
	 *            整数部の桁数
	 * @param downDecimal
	 *            小数点以下の桁数
	 * @return char[]の指定バイト変数
	 */
	public char[] getByteDataElseSpaceWithCanmaDownDecimal(String src,
	        int maxLen, int upDecimal, int downDecimal) {
		if (src.equals("") || src.equals("-"))
			return this.getByteData(src, maxLen);
		else
			return this.getByteData(Sprintf.formatCanma(upDecimal, downDecimal,
			        Double.parseDouble(src)), maxLen);
	}

	/**
	 * 実数値のStringをchar[]に変換、かつ指定バイトにする（残りはスペース）<BR>
	 * また、カンマを付加する<BR>
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @param downDecimal
	 *            小数点以下の桁数
	 * @return char[]の指定バイト変数
	 */
	public char[] getByteDataElseSpaceWithCanmaDownDecimal(String src,
	        int maxLen, int downDecimal) {
		// return
		// this.getByteDataElseSpaceWithCanmaDownDecimal(src,maxLen,12,downDecimal)
		// ;
		if (src.equals("") || src.equals("-"))
			return this.getByteData(src, maxLen);
		else
			return this.getByteData(Sprintf.formatCanma(12, downDecimal, Double.parseDouble(src)), maxLen);
	}

	/**
	 * 実数値のStringをchar[]に変換、かつ指定バイトにする（残りはスペース） また、設定ファイルから小数点桁数を読み取る
	 * また、カンマを付加する
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @return char[]の指定バイト変数
	 */
	public char[] getByteDataElseSpaceWithCanmaDownDecimal(String src,
	        int maxLen) {
		return this.getByteDataElseSpaceWithCanmaDownDecimal(src, maxLen, this.getDownDecimal());
	}

	/**
	 * 整数値のStringを返す また、カンマを付加する
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @param downDecimal
	 *            小数点以下の桁数
	 * @return char[]の指定バイト変数
	 */
	public String getStringDataElseSpaceWithCanmaDownDecimal(String src,
	        int upDecimal, int downDecimal) {
		if (src.equals("") || src.equals("-"))
			return src;
		else
			return Sprintf.formatCanma(upDecimal, downDecimal, Double.parseDouble(src));
	}

	/**
	 * 整数値のStringを返す また、カンマを付加する
	 * 
	 * @param src
	 *            入力文字列
	 * @param downDecimal
	 *            小数点以下の桁数
	 * @return 編集文字列
	 */
	public String getStringDataElseSpaceWithCanmaDownDecimal(String src,
	        int downDecimal) {
		// return
		// getStringDataElseSpaceWithCanmaDownDecimal(src,12,this.getDownDecimal())
		// ;
		if (src.equals("") || src.equals("-"))
			return src;
		else
			return Sprintf.formatCanma(12, downDecimal, Double.parseDouble(src));
	}

	/**
	 * 整数値のStringを返す また、設定ファイルから小数点桁数を読み取る また、カンマを付加する
	 * 
	 * @param src
	 *            入力文字列
	 * @return 編集文字列
	 */
	public String getStringDataElseSpaceWithCanmaDownDecimal(String src) {
		return getStringDataElseSpaceWithCanmaDownDecimal(src, this.getDownDecimal());
	}

	/**
	 * Stringをchar[]に変換、かつ指定バイトにする（残りはスペース）
	 * 
	 * @param src
	 *            入力文字列
	 * @param maxLen
	 *            指定バイト数
	 * @return char[]の指定バイト変数
	 */
	public char[] getByteData(String src, int maxLen) {
		int i;
		byte[] retByte = new byte[maxLen];
		byte[] wkByte = src.getBytes();

		if (wkByte.length <= maxLen) {
			for (i = 0; i < wkByte.length; i++) {
				retByte[i] = wkByte[i];
			}
			for (i = wkByte.length; i < maxLen; i++) {
				retByte[i] = ' ';
			}
		} else {
			for (i = 0; i < maxLen; i++) {
				retByte[i] = wkByte[i];
			}
		}

		String ret = new String(retByte);
		return ret.toCharArray();
	}

	/**
	 * 設定ファイルから小数点桁数を読み取ります。 設定ファイルのプロパティ"down_to_decimal"が
	 * "0"のときは整数、"1"のときは小数点以下第2位の変数を返します。
	 * 
	 * @return 整数または小数点以下第2位の変数
	 */
	private int getDownDecimal() {
		PropRead prop = new PropRead();
		String down_to_decimal = prop.getProp("down_to_decimal");
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}
		return intDownDecimal;
	}

	/**
	 * 指定時間ウェイトします。
	 * 
	 * @param msec
	 *            指定時間(単位：msec)
	 * 
	 * public synchronized void sleep(long msec) { try { wait(msec); }
	 * catch(InterruptedException e){} }
	 */
	/**
	 * 年月日の値をチェックします。 １)文字列として解析できるかどうか ２)月であれば１〜１２、日であれば１〜３１(３０)の値であるか
	 * 
	 * @param yyyy,mm,dd
	 *            年月日の文字列
	 * @return 正常であれば"true"、異常があれば"false"を返します。
	 */
	public boolean DateCheck(String yyyy, String mm, String dd) {
		MsgDlg msgdlg = new MsgDlg();

		boolean rets = true;
		int iy;
		int im;
		int id;
		try {
			iy = Integer.parseInt(yyyy);
			im = Integer.parseInt(mm);
			id = Integer.parseInt(dd);
		} catch (NumberFormatException ier) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			rets = false;
			return rets;
		}

		try {
			Calendar cal = Calendar.getInstance();
			cal.setLenient(false);
			cal.set(iy, im - 1, id); // この−１がミソ
			cal.get(Calendar.YEAR);
		} catch (IllegalArgumentException cer) {
			msgdlg.msgdsp("日付を正しく入力してください。", MsgDlg.ERROR_MESSAGE);
			rets = false;
			return rets;
		}

		return rets;

	}

	/**
	 * 日付範囲：開始日付が終了日付より以前かどうかをチェックします。
	 * 
	 * @param f_yy,f_mm,f_dd
	 *            開始年月日の文字列
	 * @param t_yy,t_mm,t_dd
	 *            終了年月日の文字列
	 * @return 正常であれば"true"、異常があれば"false"を返します。
	 */
	public boolean FromToDateCheck(String f_yy, String f_mm, String f_dd,
	        String t_yy, String t_mm, String t_dd) {

		MsgDlg msgdlg = new MsgDlg();

		boolean rets = true;
		int i_from;
		int i_to;
		String f_ymd = f_yy + Sprintf.format("%02d", Integer.parseInt(f_mm))
		        + Sprintf.format("%02d", Integer.parseInt(f_dd));
		String t_ymd = t_yy + Sprintf.format("%02d", Integer.parseInt(t_mm))
		        + Sprintf.format("%02d", Integer.parseInt(t_dd));
		i_from = Integer.parseInt(f_ymd);
		i_to = Integer.parseInt(t_ymd);

		if (i_from > i_to) {
			msgdlg.msgdsp("日付の範囲が正しくありません。", MsgDlg.ERROR_MESSAGE);
			rets = false;
		}

		return rets;

	}

	/**
	 * 西暦（年）から和暦を取得します。 年号は、『明治、大正、昭和、平成』です。
	 * 
	 * @param sei
	 *            西暦(年)の整数
	 * @return 年号付き和暦文字列
	 */
	public static String getWareki(int Sei) {
		String Wa = null;
		if (Sei >= 1868 && Sei <= 1911)
			Wa = "明治" + String.valueOf(Sei - 1867);
		else if (Sei >= 1912 && Sei <= 1925)
			Wa = "大正" + String.valueOf(Sei - 1911);
		else if (Sei >= 1926 && Sei <= 1988)
			Wa = "昭和" + String.valueOf(Sei - 1925);
		else if (Sei >= 1989)
			Wa = "平成" + String.valueOf(Sei - 1988);
		return Wa;
	}

	/**
	 * 西暦（年）から和暦を取得します。 年号は、『M、T、S、H』です。
	 * 
	 * @param sei
	 *            西暦(年)の整数
	 * @return 年号付き和暦文字列
	 */
	public static String getWareki2(int Sei) {
		String Wa = null;
		if (Sei >= 1868 && Sei <= 1911)
			Wa = "M" + String.valueOf(Sei - 1867);
		else if (Sei >= 1912 && Sei <= 1925)
			Wa = "T" + String.valueOf(Sei - 1911);
		else if (Sei >= 1926 && Sei <= 1988)
			Wa = "S" + String.valueOf(Sei - 1925);
		else if (Sei >= 1989)
			Wa = "H" + String.valueOf(Sei - 1988);
		return Wa;
	}

}

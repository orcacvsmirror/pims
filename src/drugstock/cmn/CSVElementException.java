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
 * CSVファイルによる初期薬剤情報入力処理で、 CSVの項目にエラーがあった場合に明示的にExceptionを返します。
 */

public class CSVElementException extends RuntimeException {

	String ErrorLog = null;

	public CSVElementException() {
		new Exception();
	}

	/**
	 * @param str
	 *            エラーログとなる文字列
	 */
	public CSVElementException(String str) {
		new Exception();
		ErrorLog = str;
	}

	/**
	 * コンストラクタで指定したエラーログを返します。
	 */
	public String toString() {
		return ErrorLog;
	}
}
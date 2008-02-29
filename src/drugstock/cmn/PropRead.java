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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 設定ファイル読み込み処理
 */

public class PropRead {

	private final String profile_path = System.getProperty("user.home")
	        + "/stock/ap/";
	private final String main_file = "orca.properties";
	private final String printer_file = "printer.properties";

	public PropRead() {
	}

	/**
	 * 設定ファイルから指定キーの値を取得します。
	 * 
	 * @param 指定キー
	 * @return プロパティ値の文字列
	 */
	public String getProp(String key) {
		return getPropMain(key, main_file);
	}

	/**
	 * プリンタ設定ファイルからプリンタ設定を取得します。
	 * 
	 * @param 指定キー
	 * @return プロパティ値の文字列
	 */
	public String getPropPrinter(String key) {
		return getPropMain(key, printer_file);
	}

	/**
	 * 設定ファイルから指定キーの値を取得します。
	 * 
	 * @param 指定キー
	 * @param 設定ファイル(基本設定ファイルorプリンタ設定ファイル)
	 * @return プロパティ値の文字列
	 */
	private String getPropMain(String key, String profile) {
		String value = null;
		try {
			Properties defaultProps = new Properties();
			// System.out.println(profile_path);

			FileInputStream defaultStream = new FileInputStream(profile_path
			        + profile);
			defaultProps.load(defaultStream);
			value = defaultProps.getProperty(key);
			defaultStream.close();
		} catch (Exception e) {
			System.out.println("PropRead Exception" + e.toString());
		}
		return value;
	}

	/**
	 * プリンタ設定処理により、プリンタ設定ファイルからを更新します。
	 * 
	 * @param 指定キー
	 * @param プリンタ名の文字列
	 */
	public void setPropPrinter(String key, String value) {
		try {
			Properties defaultProps = new Properties();
			FileOutputStream outputStream = new FileOutputStream(profile_path
			        + printer_file);
			defaultProps.setProperty(key, value);
			defaultProps.store(outputStream, "---system writer[" + key + "]---");
			outputStream.close();
		} catch (Exception e) {
			System.out.println("PropRead Exception" + e.toString());
		}
	}
}

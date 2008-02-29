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
package drugstock.model;

import java.io.Serializable;

/**
 * 業者データ要素
 */

public class CodeName implements Serializable {

	/**
	 * ＩＤ
	 */
	private String id;

	/**
	 * コード
	 */
	private String code;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * カナ名称
	 */
	private String namekn;

	/**
	 * 値引率
	 */
	private String nebikiritu;

	/**
	 * 消費税区分
	 */
	private String zeikbn;

	/**
	 * コンストラクタ
	 */
	public CodeName() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称 nebikiritu 値引率
	 */
	public CodeName(String id, String code, String name, String nebikiritu) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.nebikiritu = nebikiritu;
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称 namekn カナ名称 nebikiritu 値引率 zeikbn 消費税区分
	 */
	public CodeName(String id, String code, String name, String namekn,
	        String nebikiritu, String zeikbn) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.namekn = namekn;
		this.nebikiritu = nebikiritu;
		this.zeikbn = zeikbn;
	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

	/**
	 * IDの取得
	 */
	public String getid() {
		return id;
	}

	/**
	 * コードの取得
	 */
	public String getCode() {
		return code;
	}

	/**
	 * 名称の取得
	 */
	public String getName() {
		return name;
	}

	/**
	 * カナ名称の取得
	 */
	public String getNamekn() {
		return namekn;
	}

	/**
	 * 値引率の取得
	 */
	public String getNebikiritu() {
		return nebikiritu;
	}

	/**
	 * 消費税区分の取得
	 */
	public String getZeikbn() {
		return zeikbn;
	}

}
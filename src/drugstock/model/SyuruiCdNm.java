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
 * 薬剤区分コード用要素
 */

public class SyuruiCdNm implements Serializable {

	/**
	 * コード
	 */
	public String code;

	/**
	 * ＯＲＣＡ用コード
	 */
	public String orcacd;

	/**
	 * 名称
	 */
	public String name;

	/**
	 * コンストラクタ
	 */
	public SyuruiCdNm() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称
	 */
	public SyuruiCdNm(String code, String orcacd, String name) {
		this.code = code;
		this.orcacd = orcacd;
		this.name = name;
	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

	/**
	 * コードの取得
	 */
	public String getCode() {
		return code;
	}

	/**
	 * ＯＲＣＡ用コードの取得
	 */
	public String getOrcaCd() {
		return orcacd;
	}

	/**
	 * 名称の取得
	 */
	public String getName() {
		return name;
	}

}
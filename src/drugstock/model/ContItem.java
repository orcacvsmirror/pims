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
 * 薬剤品目DB"m_cont_item"要素
 */

public class ContItem implements Serializable {

	public String cont_id; // 業者ＩＤ
	public String orca_med_cd; // ＯＲＣＡ薬剤ＣＤ
	public String item_no; // 品番
	public String med_nm; // 薬剤名称
	public String med_kn; // 薬剤名称カナ
	public String med_kind1; // 薬剤種類１
	public String med_kind_name; // 薬剤種類１名称
	public String med_kind2; // 薬剤種類２
	public String med_kind3; // 薬剤種類３
	public String pack_unit3; // 梱包単位
	public String pack_unit2; // 包装単位
	public String pack_unit1; // バラ単位
	public String unit_price; // 最新納入単価
	public String discount; // 単品値引率
	public String unit_nm; // 単位名
	// 独自薬剤入力のため、薬価を追加 04.04.02 onuki
	public String med_price; // 薬価
	public String hacchu_p; // 発注用P点

	/**
	 * コンストラクタ
	 */
	public ContItem() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称
	 */
	public ContItem(String cont_id, // 業者ＩＤ
	        String orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
	        String item_no, // 品番
	        String med_nm, // 薬剤名称
	        String med_kn, // 薬剤名称カナ
	        String med_kind1, // 薬剤種類１
	        String med_kind_name, // 薬剤種類１名称
	        String med_kind2, // 薬剤種類２
	        String med_kind3, // 薬剤種類３
	        String pack_unit3, // 梱包単位
	        String pack_unit2, // 包装単位
	        String pack_unit1, // バラ単位
	        String unit_price, // 最新納入単価
	        String discount, // 単品値引率
	        String unit_nm, // 単位名
	        String med_price, // 薬価
	        String hacchu_p // 発注用P点
	) {

		this.cont_id = cont_id; // 業者ＩＤ
		this.orca_med_cd = orca_med_cd; // ＯＲＣＡ薬剤ＣＤ
		this.item_no = item_no; // 品番
		this.med_nm = med_nm; // 薬剤名称
		this.med_kn = med_kn; // 薬剤名称カナ
		this.med_kind1 = med_kind1; // 薬剤種類１
		this.med_kind_name = med_kind_name; // 薬剤種類１名称
		this.med_kind2 = med_kind2; // 薬剤種類２
		this.med_kind3 = med_kind3; // 薬剤種類３
		this.pack_unit3 = pack_unit3; // 梱包単位
		this.pack_unit2 = pack_unit2; // 包装単位
		this.pack_unit1 = pack_unit1; // バラ単位
		this.unit_price = unit_price; // 最新納入単価
		this.discount = discount; // 単品値引率
		this.unit_nm = unit_nm; // 単位名
		this.med_price = med_price; // 薬価
		this.hacchu_p = hacchu_p; // 発注用P点

	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

}

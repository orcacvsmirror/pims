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
 * 日レセ薬剤マスタDB"OrcaMedicine"要素
 */

public class OrcaMedicine implements Serializable {

	public String orca_med_cd; // ＯＲＣＡ薬剤ＣＤ
	public String day_from; // 有効開始日
	public String day_to; // 有効終了日
	public String med_nm; // 薬剤名称
	public String med_kn; // 薬剤名称カナ
	public String unit_nm; // 単位名
	public String med_kind; // 薬剤区分
	public String med_kind_name; // 薬剤区分名称
	public String med_price; // 薬価
	public String discount; // 単品値引率
	public String item_no; // 品番

	/**
	 * コンストラクタ
	 */
	public OrcaMedicine() {
	}

	/**
	 * コンストラクタ
	 */
	public OrcaMedicine(String orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
	        String day_from, // 有効開始日
	        String day_to, // 有効終了日
	        String med_nm, // 薬剤名称
	        String med_kn, // 薬剤名称カナ
	        String unit_nm, // 単位名
	        String med_kind, // 薬剤区分
	        String med_kind_name, // 薬剤区分名称
	        String med_price, // 薬価
	        String discount, // 単品値引率
	        String item_no // 品番

	) {

		this.orca_med_cd = orca_med_cd; // ＯＲＣＡ薬剤ＣＤ
		this.day_from = day_from; // 有効開始日
		this.day_to = day_to; // 有効終了日
		this.med_nm = med_nm; // 薬剤名称
		this.med_kn = med_kn; // 薬剤名称カナ
		this.unit_nm = unit_nm; // 単位名
		this.med_kind = med_kind; // 薬剤区分
		this.med_kind_name = med_kind_name; // 薬剤区分名称
		this.med_price = med_price; // 薬価
		this.discount = discount; // 単品値引率
		this.item_no = item_no; // 品番

	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

	public void setContId(String str) {
		this.item_no = str;
	}
}
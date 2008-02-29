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
 * DB"Stocking"要素
 */

public class Stocking implements Serializable {

	public String stc_id; // 仕入ＮＯ
	public String stc_date; // 仕入日
	//
	public String slip_no = ""; // 伝票番号
	public String stc_cd; // 仕入区分
	public String cont_id; // 業者区分
	public String item_no; // 品番
	public String stc_unit; // 仕入単価
	public String stc_num; // 仕入数量
	public String tax_flg; // 税区分
	public String amount; // 金額
	public String discount; // 値引
	public String stc_amount; // 購入金額
	public String tax; // 税金額
	public String pack3_num; // 仕入梱包数
	public String pack2_num; // 仕入包装数
	public String pack1_num; // 仕入バラ数
	public String del_flg; // 削除フラグ
	public String med_nm; // 薬剤名称
	public String med_kn; // 薬剤カナ名称
	public String med_kind1; // 種類１
	public String med_kind2; // 種類２
	public String med_kind3; // 種類３
	public String pack_unit3; // 梱包入数
	public String pack_unit2; // 包装入数
	public String pack_unit1; // バラ入数
	public String unit_price; // 最新納入単価
	public String discountritu; // 値引率

	/**
	 * コンストラクタ
	 */
	public Stocking() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称
	 */
	public Stocking(String stc_id, // 仕入ＮＯ
	        String stc_date, // 仕入日
	        String slip_no, // 伝票番号
	        String stc_cd, // 仕入区分
	        String cont_id, // 業者区分
	        String item_no, // 品番
	        String stc_unit, // 仕入単価
	        String stc_num, // 仕入数量
	        String tax_flg, // 税区分
	        String amount, // 金額
	        String discount, // 値引
	        String stc_amount, // 購入金額
	        String tax, // 税金額
	        String pack3_num, // 仕入梱包数
	        String pack2_num, // 仕入包装数
	        String pack1_num, // 仕入バラ数
	        String del_flg, // 削除フラグ
	        String med_nm, // 薬剤名称
	        String med_kn, // 薬剤カナ名称
	        String med_kind1, // 種類１
	        String med_kind2, // 種類２
	        String med_kind3, // 種類３
	        String pack_unit3, // 梱包入数
	        String pack_unit2, // 包装入数
	        String pack_unit1, // バラ入数
	        String unit_price, // 最新納入単価
	        String discountritu // 値引率
	) {

		this.stc_id = stc_id; // 仕入ＮＯ
		this.stc_date = stc_date; // 仕入日
		this.slip_no = slip_no; // 伝票番号
		this.stc_cd = stc_cd; // 仕入区分
		this.cont_id = cont_id; // 業者区分
		this.item_no = item_no; // 品番
		this.stc_unit = stc_unit; // 仕入単価
		this.stc_num = stc_num; // 仕入数量
		this.tax_flg = tax_flg; // 税区分
		this.amount = amount; // 金額
		this.discount = discount; // 値引
		this.stc_amount = stc_amount; // 購入金額
		this.tax = tax; // 税金額
		this.pack3_num = pack3_num; // 仕入梱包数
		this.pack2_num = pack2_num; // 仕入包装数
		this.pack1_num = pack1_num; // 仕入バラ数
		this.del_flg = del_flg; // 削除フラグ
		this.med_nm = med_nm; // 薬剤名称
		this.med_kn = med_kn; // 薬剤カナ名称
		this.med_kind1 = med_kind1; // 種類１
		this.med_kind2 = med_kind2; // 種類２
		this.med_kind3 = med_kind3; // 種類３
		this.pack_unit3 = pack_unit3; // 梱包入数
		this.pack_unit2 = pack_unit2; // 包装入数
		this.pack_unit1 = pack_unit1; // バラ入数
		this.unit_price = unit_price; // 最新納入単価
		this.discountritu = discountritu; // 値引率

	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

}
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
 * DB"InventStock"用ファクタ
 */

public class InventStockFactor implements Serializable {

	public String yyyymm; // 年月
	// public String cont_nm; // 業者名
	public String item_no; // 薬剤品番
	public String med_nm; // 薬剤名
	public String med_kn; // 薬剤カナ名
	public String stock_theory; // 理論在庫
	public String stock_truth; // 実在庫
	public String flag_truth; // 実在庫フラグ

	/**
	 * コンストラクタ
	 */
	public InventStockFactor() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称
	 */
	public InventStockFactor(String yyyymm, // 年月
	        // String cont_nm, // 業者名
	        String item_no, // 品番
	        String med_nm, // 薬剤名
	        String med_kn, // 薬剤カナ名
	        String stock_theory, // 理論在庫
	        String stock_truth, // 実在庫
	        String flag_truth // 実在庫フラグ
	) {

		this.yyyymm = yyyymm; // 年月
		// this.cont_nm = cont_nm; // 業者名
		this.item_no = item_no; // 品番
		this.med_nm = med_nm; // 薬剤名
		this.med_kn = med_kn; // 薬剤カナ名
		this.stock_theory = stock_theory; // 理論在庫
		this.stock_truth = stock_truth; // 実在庫
		this.flag_truth = flag_truth; // 実在庫フラグ

	}

	// /////////////////////////////////////////////////////////////////////
	// ////////////////////// Getter ////////////////////
	// /////////////////////////////////////////////////////////////////////

}
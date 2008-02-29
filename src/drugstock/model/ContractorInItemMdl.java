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
 * 複数業者選択用モデル
 */

public class ContractorInItemMdl implements Serializable {

	/** 薬剤品番 */
	public String item_no;
	/** 通し番号 */
	public String cont_count;
	/** 業者ID */
	public String cont_id;

	/** コンストラクタ */
	public ContractorInItemMdl() {
	}

	/** コンストラクタ */
	public ContractorInItemMdl(String item_no, String cont_count, String cont_id) {
		this.item_no = item_no;
		this.cont_count = cont_count;
		this.cont_id = cont_id;
	}

}
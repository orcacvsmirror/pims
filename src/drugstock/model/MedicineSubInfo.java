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

/* ***************************************************************************
 *
 * 更新履歴	: 
 *              2006.04.24 Hasegawa 新規作成
 *                 在庫一覧表表示時にＤＢからデータを取得する際に使用する。
 *
 *
 *
 *
 *
 *
 *
 *
 ******************************************************************************/

package drugstock.model;

import java.io.Serializable;

/**
 * 在庫一覧表示時 一時使用クラス
 */

public class MedicineSubInfo implements Serializable {

	private String item_no; // 品番
	private String med_nm; // 薬剤名称
	private String unit_nm; // 単位名
	private String med_kind; // 薬剤種類
	private String day_start; // 有効開始日
	private String day_end; // 有効終了日
	private String med_price; // 薬価

	/**
	 * コンストラクタ
	 * 
	 * @param code
	 *            コード name 名称
	 * @see "2006.04.24 Hasegawa 新規作成"
	 */
	public MedicineSubInfo() {
		item_no = ""; // 品番
		med_nm = ""; // 薬剤名称
		unit_nm = ""; // 単位名
		med_kind = ""; // 薬剤種類
		day_start = ""; // 有効開始日
		day_end = ""; // 有効終了日
		med_price = ""; // 薬価

	}

	// アクセッサメソッド
	//
	// setterメソッド
	//

	// 品番
	public void setItemNo(String strItemNo) {
		this.item_no = strItemNo;
	}

	// 薬剤名称
	public void setMedName(String strMedName) {
		this.med_nm = strMedName;
	}

	// 単位名
	public void setMedUnit(String strMedUnit) {
		this.unit_nm = strMedUnit;
	}

	// 薬剤種類
	public void setMedKind(String strMedKind) {
		this.med_kind = strMedKind;
	}

	// 有効開始日
	public void setDayStart(String strDayStart) {
		this.day_start = strDayStart;
	}

	// 有効終了日
	public void setDayEnd(String strDayEnd) {
		this.day_end = strDayEnd;
	}

	// 薬価
	public void setMedPrice(String strMedPrice) {
		this.med_price = strMedPrice;
	}

	//
	// getterメソッド
	//
	// 品番
	public String getItemNo() {
		return this.item_no;
	}

	// 薬剤名称
	public String getMedName() {
		return this.med_nm;
	}

	// 単位名
	public String getMedUnit() {
		return this.unit_nm;
	}

	// 薬剤種類
	public String getMedKind() {
		return this.med_kind;
	}

	// 有効開始日
	public String getDayStart() // 文字列で返す
	{
		return this.day_start;
	}

	public int getIntDayStart() // 整数型で返す
	{
		Integer ITmp = new Integer(this.day_start);
		return ITmp.intValue();
	}

	// 有効終了日
	public String getDayEnd() // 文字列で返す
	{
		return this.day_end;
	}

	public int getIntDayEnd() // 整数型で返す
	{
		Integer ITmp = new Integer(this.day_end);
		return ITmp.intValue();
	}

	// 薬価
	public String getMedPrice() // 文字列で返す
	{
		return this.med_price;
	}

	public double getDoubleMedPrice() // double型で返す
	{
		Double DTmp = new Double(this.med_price);
		return DTmp.doubleValue();
	}

}
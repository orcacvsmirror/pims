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

import drugstock.cmn.Common;
import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;

/**
 * 「品目別差益高分析表」モデル
 */

public class StockListSaekiMdl implements Serializable {

	private Common com = new Common();

	private String expend_Rank; // 使用高順位
	private String margin_Rank; // 差益高順位
	private String itemNum; // 品番
	private String itemName; // 品名
	private String med_Price; // 薬価
	// 04.02.25 onuki
	private String stc_Price; // 納入価
	private String margin; // 差益
	//
	private String unitName; // 単位
	private String expend_num; // 使用量
	private String expend_Price;// 使用高
	// 04.02.25 onuki
	private String margin_Price;// 差益高
	private String rate; // 割合
	private String margin_rate; // 差益割合

	private String medKind; // 薬剤区分

	private final int expend_RankMax = 4;
	private final int margin_RankMax = 4;
	private final int itemNumMax = 10;
	private final int itemNameMax = 34;
	private final int med_PriceMax = 9;
	private final int stc_PriceMax = 9;
	private final int marginMax = 9;
	private final int unitNameMax = 4;
	private final int expend_numMax = 12;
	private final int expend_PriceMax = 12;
	private final int margin_PriceMax = 12;
	private final int rateMax = 6;
	private final int margin_rateMax = 6;

	public StockListSaekiMdl() {
		expend_Rank = ""; // 使用高順位
		margin_Rank = ""; // 差益高順位
		itemNum = ""; // 品番
		itemName = ""; // 品名
		med_Price = ""; // 薬価
		stc_Price = ""; // 納入価
		margin = ""; // 差益
		unitName = ""; // 単位
		expend_num = ""; // 使用量
		expend_Price = "";// 使用高
		margin_Price = "";// 差益高
		rate = ""; // 割合
		margin_rate = ""; // 差益割合
		medKind = ""; // 薬剤種別
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setExpend_Rank(String setDate) {
		this.expend_Rank = setDate;
	}

	public void setMargin_Rank(String setDate) {
		this.margin_Rank = setDate;
	}

	public void setItemNum(String setDate) {
		this.itemNum = setDate;
	}

	public void setItemName(String setDate) {
		this.itemName = setDate;
	}

	public void setMed_Price(String setDate) {
		this.med_Price = setDate;
	}

	// 04.02.25 onuki
	public void setStc_Price(String setDate) {
		this.stc_Price = setDate;
	}

	public void setMargin(String setDate) {
		this.margin = setDate;
	}

	//
	public void setUnitName(String setDate) {
		this.unitName = setDate;
	}

	public void setExpend_Num(String setDate) {
		this.expend_num = setDate;
	}

	public void setExpend_Price(String setDate) {
		this.expend_Price = setDate;
	}

	// 04.02.25 onuki
	public void setMargin_Price(String setDate) {
		this.margin_Price = setDate;
	}

	//
	public void setRate(String setDate) {
		this.rate = setDate;
	}

	public void setMargin_Rate(String setDate) {
		this.margin_rate = setDate;
	}

	public void setMedKind(String setDate) {
		this.medKind = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesExpend_Rank() {
		return com.getByteData(this.expend_Rank, expend_RankMax);
	}

	public char[] getBytesMargin_Rank() {
		return com.getByteData(this.margin_Rank, margin_RankMax);
	}

	public char[] getBytesItemNum() {
		return com.getByteData(this.itemNum, itemNumMax);
	}

	public char[] getBytesItemName() {
		return com.getByteData(this.itemName, itemNameMax);
	}

	public char[] getBytesMed_Price() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.med_Price,
		        med_PriceMax, 2);
	}

	// 04.02.25 onuki
	public char[] getBytesStc_Price() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stc_Price,
		        stc_PriceMax, 2);
	}

	public char[] getBytesMargin() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.margin,
		        marginMax, 2);
	}

	//
	public char[] getBytesUnitName() {
		return com.getByteData(this.unitName, unitNameMax);
	}

	public char[] getBytesExpend_num() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.expend_num,
		        expend_numMax, 2);
	}

	public char[] getBytesExpend_Price() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.expend_Price,
		        expend_PriceMax);
	}

	// 合計値算出用の数列表現(カンマなし)
	// 04.02.10 onuki
	public char[] getBytesExpend_Price_num() {

		if (this.expend_Price.equals(""))
			return com.getByteData(this.expend_Price, expend_PriceMax);
		else
			// return
			// com.getByteData(Sprintf.format(12,intDownDecimal,Double.parseDouble(this.expend_Price)),
			// expend_PriceMax);
			return com.getByteData(Sprintf.format(12, getDownDecimal(), Double.parseDouble(this.expend_Price)), expend_PriceMax);
	}

	// 04.02.25 onuki
	public char[] getBytesMargin_Price() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.margin_Price,
		        margin_PriceMax);
	}

	//
	public char[] getBytesRate() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.rate, rateMax,
		        2);
	}

	public char[] getBytesMargin_Rate() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.margin_rate,
		        margin_rateMax, 2);
	}

	// ************************************************
	public String getStrExpend_Rank() {
		return this.expend_Rank;
	}

	public String getStrMargin_Rank() {
		return this.margin_Rank;
	}

	public String getStrItemNum() {
		return this.itemNum;
	}

	public String getStrItemName() {
		return this.itemName;
	}

	public String getStrMed_Price() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.med_Price, 2);
	}

	// 04.02.25 onuki
	public String getStrStc_Price() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stc_Price, 2);
	}

	public String getStrMargin() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.margin, 2);
	}

	public String getStrUnitName() {
		return this.unitName;
	}

	public String getStrExpend_num() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.expend_num,
		        2);
	}

	public String getStrExpend_Price() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.expend_Price);
	}

	public String getMedKind() {
		return medKind;
	}

	public String getStrMargin_Price() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.margin_Price);
	}

	//
	public String getStrRate() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.rate, 2);
	}

	public String getStrMargin_Rate() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.margin_rate,
		        2);
	}

	private int getDownDecimal() {
		PropRead prop = new PropRead();
		String down_to_decimal = prop.getProp("down_to_decimal");
		if (down_to_decimal == null)
			down_to_decimal = "0";
		if (down_to_decimal.equals("1") == false)
			down_to_decimal = "0";

		int intDownDecimal = 2;
		if (down_to_decimal.equals("1")) {
			intDownDecimal = 0;
		}
		return intDownDecimal;
	}
}

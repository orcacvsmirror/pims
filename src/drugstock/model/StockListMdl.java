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
import drugstock.cmn.Sprintf;

/**
 * 「棚卸準備一覧表」モデル
 */

public class StockListMdl implements Serializable {

	private String itemNum; // 品番
	private String itemName; // 品名
	private String stockPrice; // 在庫単価
	private String beforeStock; // 前月在庫数
	private String unit3Num; // 梱包数
	private String unit3; // 梱包単位
	private String unit2Num; // 包装数
	private String unit2; // 包装単位
	private String unitName; // 単位
	private String unit1Num; // バラ数
	private String medKind; // 薬剤種別

	private final int itemNumMax = 10;
	private final int itemNameMax = 40;
	private final int stockPriceMax = 10;
	private final int beforeStockMax = 20;
	private final int unit3NumMax = 5;
	private final int unit3Max = 8;
	private final int unit2NumMax = 5;
	private final int unit2Max = 9;
	private final int unitNameMax = 8;
	private final int unit1NumMax = 20;

	public StockListMdl() {
		itemNum = ""; // 品番
		itemName = ""; // 品名
		stockPrice = ""; // 在庫単価
		beforeStock = ""; // 前月在庫数
		unit3Num = ""; // 梱包数
		unit3 = ""; // 梱包単位
		unit2Num = ""; // 包装数
		unit2 = ""; // 包装単位
		unit1Num = ""; // バラ数
		unitName = ""; // 単位
		medKind = ""; // 薬剤種別
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setItemNum(String setDate) {
		this.itemNum = setDate;
	}

	public void setItemName(String setDate) {
		this.itemName = setDate;
	}

	public void setStockPrice(String setDate) {
		this.stockPrice = setDate;
	}

	public void setNowStock(String setDate) {
		this.beforeStock = setDate;
	}

	public void setBytesUnit3Num(String setDate) {
		this.unit3Num = setDate;
	}

	public void setBytesUnit3(String setDate) {
		this.unit3 = setDate;
	}

	public void setBytesUnit2Num(String setDate) {
		this.unit2Num = setDate;
	}

	public void setBytesUnit2(String setDate) {
		this.unit2 = setDate;
	}

	public void setBytesUnit1Num(String setDate) {
		this.unit1Num = setDate;
	}

	public void setUnitName(String setDate) {
		this.unitName = setDate;
	}

	public void setUnit1Num(String setDate) {
		this.unit1Num = setDate;
	}

	public void setMedKind(String setDate) {
		this.medKind = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesItemNum() {
		Common com = new Common();
		return com.getByteData(this.itemNum, itemNumMax);
	}

	public char[] getBytesItemName() {
		Common com = new Common();
		return com.getByteData(this.itemName, itemNameMax);
	}

	public char[] getBytesStockPrice() {
		Common com = new Common();
		if (this.stockPrice.equals("") || this.stockPrice.equals("-"))
			return com.getByteData(this.stockPrice, stockPriceMax);
		else
			return com.getByteData(Sprintf.formatCanma(12, 2, Double.parseDouble(this.stockPrice)), stockPriceMax);
	}

	public char[] getBytesNowStock() {
		Common com = new Common();
		if (this.beforeStock.equals(""))
			return com.getByteData(this.beforeStock, beforeStockMax);
		else
			return com.getByteData(Sprintf.formatCanma(12, 2, Double.parseDouble(this.beforeStock)), beforeStockMax);
	}

	public char[] getBytesUnit3Num() {
		Common com = new Common();
		return com.getByteData(this.unit3Num, unit3NumMax);
	}

	public char[] getBytesUnit3() {
		Common com = new Common();
		if (this.unit3.equals(""))
			return com.getByteData(this.unit3, unit3Max);
		else
			return com.getByteData(Sprintf.formatCanma(12, 2, Double.parseDouble(this.unit3)), unit3Max);
	}

	public char[] getBytesUnit2Num() {
		Common com = new Common();
		return com.getByteData(this.unit2Num, unit2NumMax);
	}

	public char[] getBytesUnit2() {
		Common com = new Common();
		if (this.unit2.equals(""))
			return com.getByteData(this.unit2, unit2Max);
		else
			return com.getByteData(Sprintf.formatCanma(12, 3, Double.parseDouble(this.unit2)), unit2Max);
	}

	public char[] getUnit1Num() {
		Common com = new Common();
		return com.getByteData(this.unit1Num, unit1NumMax);
	}

	public char[] getBytesUnit1Num() {
		Common com = new Common();
		if (this.unit1Num.equals("") || this.unit1Num.equals("-"))
			return com.getByteData(this.unit1Num, unit1NumMax);
		else
			return com.getByteData(Sprintf.formatCanma(12, 0, Double.parseDouble(this.unit1Num)), unit1NumMax);
	}

	public char[] getBytesUnitName() {
		Common com = new Common();
		return com.getByteData(this.unitName, unitNameMax);
	}

	public String getMedKind() {
		return medKind;
	}
}
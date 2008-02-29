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

/**
 * 「棚卸準備一覧表」モデル
 */

public class StockListJunbiMdl implements Serializable {

	private Common com = new Common();

	private String itemNum; // 品番
	private String itemName; // 品名
	private String stockPrice; // 在庫単価
	private String beforeStock; // 当月在庫数
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
	private final int medKindMax = 12;

	public StockListJunbiMdl() {
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
		return com.getByteData(this.itemNum, itemNumMax);
	}

	public char[] getBytesItemName() {
		return com.getByteData(this.itemName, itemNameMax);
	}

	public char[] getBytesStockPrice() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stockPrice,
		        stockPriceMax, 2);
	}

	public char[] getBytesNowStock() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.beforeStock,
		        beforeStockMax, 2);
	}

	public char[] getBytesUnit3Num() {
		return com.getByteData(this.unit3Num, unit3NumMax);
	}

	public char[] getBytesUnit3() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.unit3,
		        unit3Max, 2);
	}

	public char[] getBytesUnit2Num() {
		return com.getByteData(this.unit2Num, unit2NumMax);
	}

	public char[] getBytesUnit2() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.unit2,
		        unit2Max, 2);
	}

	public char[] getUnit1Num() {
		return com.getByteData(this.unit1Num, unit1NumMax);
	}

	public char[] getBytesUnit1Num() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.unit1Num,
		        unit1NumMax, 0);
	}

	public char[] getBytesUnitName() {
		return com.getByteData(this.unitName, unitNameMax);
	}

	public char[] getBytesMedKind() {
		return com.getByteData(this.medKind, medKindMax);
	}

	// ************************************************
	// Stringゲッター
	// ************************************************
	public String getStrItemNum() {
		return this.itemNum;
	}

	public String getStrItemName() {
		return this.itemName;
	}

	public String getStrStockPrice() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stockPrice,
		        2);
	}

	public String getStrNowStock() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.beforeStock,
		        2);
	}

	public String getStrUnit3Num() {
		return this.unit3Num;
	}

	public String getStrUnit3() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.unit3, 2);
	}

	public String getStrUnit2Num() {
		return this.unit2Num;
	}

	public String getStrUnit2() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.unit2, 2);
	}

	public String getStrUnit1Num() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.unit1Num, 0);
	}

	public String getStrUnitName() {
		return this.unitName;
	}

	public String getMedKind() {
		return medKind;
	}

}
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
 * 「薬剤マスタ一覧表」モデル
 */

public class DeadStockListMdl implements Serializable {

	private Common com = new Common();

	private String cont; // 業者
	private String num; // 品番
	private String name; // 品名
	private String date; // 最終出庫日付
	private String stockNum; // 在庫量
	private String stockPrice; // 在庫金額
	private String medKind; // 薬剤種別

	private final int numMax = 10;
	private final int nameMax = 40;
	private final int dateMax = 12;
	private final int stockNumMax = 12;
	private final int stockPriceMax = 12;

	public DeadStockListMdl() {
		cont = ""; // 業者
		num = ""; // 品番
		name = ""; // 品名
		date = ""; // 最終出庫日付
		stockNum = ""; // 在庫量
		stockPrice = ""; // 在庫金額
		medKind = ""; // 薬剤種別
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setCont(String setDate) {
		this.cont = setDate;
	}

	public void setNum(String setDate) {
		this.num = setDate;
	}

	public void setName(String setDate) {
		this.name = setDate;
	}

	public void setDate(String setDate) {
		this.date = setDate;
	}

	public void setStockNum(String setDate) {
		this.stockNum = setDate;
	}

	public void setStockPrice(String setDate) {
		this.stockPrice = setDate;
	}

	public void setMedKind(String setDate) {
		this.medKind = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesNum() {
		return com.getByteData(this.num, numMax);
	}

	public char[] getBytesName() {
		return com.getByteData(this.name, nameMax);
	}

	public char[] getBytesDate() {
		return com.getByteData(this.date, dateMax);
	}

	public char[] getBytesStockNum() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stockNum,
		        stockNumMax, 2);
	}

	public char[] getBytesStockPrice() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stockPrice,
		        stockPriceMax, 2);
	}

	// ***********************************************
	public String getStrCont() {
		return cont;
	}

	public String getStrNum() {
		return this.num;
	}

	public String getStrName() {
		return this.name;
	}

	public String getStrDate() {
		return this.date;
	}

	public String getStrStockNum() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stockNum, 2);
	}

	public String getStrStockPrice() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stockPrice,
		        2);
	}

	public String getMedKind() {
		return medKind;
	}
}

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
 * 「仕入先別仕入伝票一覧表 合計エリア」モデル
 */

public class StockListDenpyoSumMdl implements Serializable {

	private Common com = new Common();

	private String cont; // 業者名
	private String sumStcNum; // 合計量計
	private String sumAmnt; // 金額計
	private String sumDiscnt; // 値引計
	private String sumTaxAmnt; // 消費税額計
	private String sumStcAmnt; // 購入金額計

	private final int sumStcNumMax = 14;
	private final int sumAmntMax = 11;
	private final int sumDiscntMax = 7;
	private final int sumTaxAmntMax = 7;
	private final int sumStcAmntMax = 11;

	public StockListDenpyoSumMdl() {
		cont = ""; // 業者
		sumStcNum = ""; // 合計量
		sumAmnt = ""; // 金額
		sumDiscnt = ""; // 値引
		sumTaxAmnt = ""; // 消費税額
		sumStcAmnt = ""; // 購入金額
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setBytesCont(String setDate) {
		this.cont = setDate;
	}

	public void setBytesSumStcNum(String setDate) {
		this.sumStcNum = setDate;
	}

	public void setBytesSumAmnt(String setDate) {
		this.sumAmnt = setDate;
	}

	public void setBytesSumDiscnt(String setDate) {
		this.sumDiscnt = setDate;
	}

	public void setBytesSumTaxAmnt(String setDate) {
		this.sumTaxAmnt = setDate;
	}

	public void setBytesSumStcAmnt(String setDate) {
		this.sumStcAmnt = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesSumStcNum() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.sumStcNum,
		        sumStcNumMax, 3);
	}

	public char[] getBytesSumAmnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.sumAmnt,
		        sumAmntMax);
	}

	public char[] getBytesSumDiscnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.sumDiscnt,
		        sumDiscntMax, 0);
	}

	public char[] getBytesSumTaxAmnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.sumTaxAmnt,
		        sumTaxAmntMax);
	}

	public char[] getBytesSumStcAmnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.sumStcAmnt,
		        sumStcAmntMax);
	}

	// ************************************************
	public String getStrSumStcNum() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.sumStcNum, 3);
	}

	public String getStrSumAmnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.sumAmnt);
	}

	public String getStrSumDiscnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.sumDiscnt, 0);
	}

	public String getStrSumTaxAmnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.sumTaxAmnt);
	}

	public String getStrSumStcAmnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.sumStcAmnt);
	}

	public String getStrCont() {
		return cont;
	}

}
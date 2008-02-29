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
 * 「仕入先別仕入伝票一覧表 明細エリア」モデル
 */

public class StockListDenpyoDetailMdl implements Serializable {

	private Common com = new Common();

	private String stcNo; // Ｎｏ
	private String stcDate; // 日付
	private String itemNum; // 品番
	private String itemName; // 品名
	private String unitName; // 単位
	private String unit3Num; // 梱包数
	private String unit3; // 梱包単位
	private String unit2Num; // 包装数
	private String unit2; // 包装単位
	private String unit1Num; // バラ数
	private String stcNum; // 合計量
	private String amnt; // 金額
	private String discnt; // 値引
	private String taxAmnt; // 消費税額
	private String stcAmnt; // 購入金額
	private String stcCd; // 仕入区分
	private String crdName; // 伝票
	private String cont; // 業者名

	private final int stcNoMax = 8;
	private final int stcDateMax = 9;
	private final int itemNumMax = 10;
	private final int itemNameMax = 40;
	private final int unitNameMax = 4;
	private final int unit3NumMax = 3;
	private final int unit3Max = 9;
	private final int unit2NumMax = 3;
	private final int unit2Max = 10;
	private final int unit1NumMax = 11;
	private final int stcNumMax = 14;
	private final int amntMax = 11;
	private final int discntMax = 7;
	private final int taxAmntMax = 7;
	private final int stcAmntMax = 11;
	private final int stcCdMax = 1;
	// private final int crdNameMax = 4;
	private final int crdNameMax = 8;

	public StockListDenpyoDetailMdl() {
		stcNo = ""; // Ｎｏ
		stcDate = ""; // 日付
		itemNum = ""; // 品番
		itemName = ""; // 品名
		unitName = ""; // 単位
		unit3Num = ""; // 梱包数
		unit3 = ""; // 梱包単位
		unit2Num = ""; // 包装数
		unit2 = ""; // 包装単位
		unit1Num = ""; // バラ数
		stcNum = ""; // 合計量
		amnt = ""; // 金額
		discnt = ""; // 値引
		taxAmnt = ""; // 消費税額
		stcAmnt = ""; // 購入金額
		stcCd = ""; // 仕入区分
		crdName = ""; // 伝票
		cont = ""; // 業者名
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setBytesStcNo(String setDate) {
		this.stcNo = setDate;
	}

	public void setBytesStcDate(String setDate) {
		this.stcDate = setDate;
	}

	public void setBytesItemNum(String setDate) {
		this.itemNum = setDate;
	}

	public void setBytesItemName(String setDate) {
		this.itemName = setDate;
	}

	public void setBytesUnitName(String setDate) {
		this.unitName = setDate;
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

	public void setBytesStcNum(String setDate) {
		this.stcNum = setDate;
	}

	public void setBytesAmnt(String setDate) {
		this.amnt = setDate;
	}

	public void setBytesDiscnt(String setDate) {
		this.discnt = setDate;
	}

	public void setBytesTaxAmnt(String setDate) {
		this.taxAmnt = setDate;
	}

	public void setBytesStcAmnt(String setDate) {
		this.stcAmnt = setDate;
	}

	// public void setBytesStcCd(String setDate)
	// {
	// this.stcCd = setDate;
	// }
	public void setBytesCrdName(String setDate) {
		this.crdName = setDate;
	}

	public void setBytesCont(String setDate) {
		this.cont = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesStcNo() {
		return com.getByteData(this.stcNo, stcNoMax);
	}

	public char[] getBytesStcDate() {
		return com.getByteData(this.stcDate, stcDateMax);
	}

	public char[] getBytesItemNum() {
		return com.getByteData(this.itemNum, itemNumMax);
	}

	public char[] getBytesItemName() {
		return com.getByteData(this.itemName, itemNameMax);
	}

	public char[] getBytesUnitName() {
		return com.getByteData(this.unitName, unitNameMax);
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
		        unit2Max, 3);
	}

	public char[] getBytesUnit1Num() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.unit1Num,
		        unit1NumMax, 3);
	}

	public char[] getBytesStcNum() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stcNum,
		        stcNumMax, 3);
	}

	public char[] getBytesStcNum2() {
		return com.getByteDataElseSpaceDownDecimal(this.stcNum, stcNumMax, 2);
	}

	public char[] getBytesAmnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.amnt, amntMax);
	}

	public char[] getBytesAmnt2() {
		return com.getByteDataElseSpaceDownDecimal(this.amnt, amntMax, 2);
	}

	public char[] getBytesDiscnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.discnt,
		        discntMax, 0);
	}

	public char[] getBytesDiscnt2() {
		return com.getByteDataElseSpaceDownDecimal(this.discnt, discntMax, 0);
	}

	public char[] getBytesTaxAmnt() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.taxAmnt,
		        taxAmntMax);
	}

	public char[] getBytesTaxAmnt2() {
		return com.getByteDataElseSpaceDownDecimal(this.taxAmnt, taxAmntMax, 0);
	}

	public char[] getBytesStcAmnt() {
		if (this.stcAmnt.equals(""))
			return com.getByteData(this.stcAmnt, stcAmntMax);
		else {
			double tmpStcAmnt = Double.parseDouble(this.stcAmnt);
			double tmpTaxAmnt = Double.parseDouble(this.taxAmnt);
			return com.getByteData(Sprintf.formatCanma(12, 0, tmpStcAmnt
			        + tmpTaxAmnt), stcAmntMax);
		}
	}

	public char[] getBytesStcAmnt2() {
		return com.getByteDataElseSpaceDownDecimal(this.stcAmnt, stcAmntMax, 0);
		// if (this.stcAmnt.equals(""))
		// return com.getByteData(this.stcAmnt, stcAmntMax);
		// else
		// return
		// com.getByteData(Sprintf.format(12,0,Double.parseDouble(this.stcAmnt)),
		// stcAmntMax);
	}

	public char[] getBytesStcCd() {
		return com.getByteData(this.stcCd, stcCdMax);
	}

	public char[] getBytesCrdName() {
		return com.getByteData(this.crdName, crdNameMax);
	}

	// ***********************************************
	public String getStrStcNo() {
		return this.stcNo;
	}

	public String getStrStcDate() {
		return this.stcDate;
	}

	public String getStrItemNum() {
		return this.itemNum;
	}

	public String getStrItemName() {
		return this.itemName;
	}

	public String getStrUnitName() {
		return this.unitName;
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
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.unit2, 3);
	}

	public String getStrUnit1Num() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.unit1Num, 3);
	}

	public String getStrStcNum() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stcNum, 3);
	}

	public String getStrAmnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.amnt);
	}

	public String getStrDiscnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.discnt, 0);
	}

	public String getStrTaxAmnt() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.taxAmnt);
	}

	public String getStrStcAmnt() {
		if (this.stcAmnt.equals(""))
			return this.stcAmnt;
		else {
			double tmpStcAmnt = Double.parseDouble(this.stcAmnt);
			double tmpTaxAmnt = Double.parseDouble(this.taxAmnt);
			return Sprintf.formatCanma(12, 0, tmpStcAmnt + tmpTaxAmnt);
		}
	}

	public String getStrStcCd() {
		return this.stcCd;
	}

	public String getStrCrdName() {
		return this.crdName;
	}

	public String getStrCont() {
		return cont;
	}
}
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
 * 帳票汎用ヘッダ部
 */

public class StockListHead implements Serializable {

	private String srcDateFrom; // 検索条件 日付年月FROM
	private String srcDateTo; // 検索条件 日付年月To
	private String srcYMDFrom; // 検索条件 日付年月日From
	private String srcYMDTo; // 検索条件 日付年月日To
	private String prtContId; // 仕入先コード
	private String prtContNm; // 仕入先名
	private String prtDate; // 印刷日付
	private String prtPage; // 印刷ページ
	private String prtDrugKind; // 印刷薬剤種別（内用薬、外用薬。。。）
	private String prtOrder; // 印刷順序
	private String prtAndOr; // 品番条件（ＡＮＤ、ＯＲ）
	private String prtItemNo; // 品番
	private String prtItemNm; // 品名

	private final int srcDateFromMax = 16;
	private final int srcDateToMax = 16;
	private final int srcYMDFromMax = 16;
	private final int srcYMDToMax = 16;
	private final int prtContIdMax = 10;
	private final int prtContNmMax = 40;
	private final int prtDateMax = 20;
	private final int prtPageMax = 3;
	private final int prtDrugKindMax = 14;
	private final int prtOrderMax = 10;
	private final int prtAndOrMax = 6;
	private final int prtItemNoMax = 5;
	private final int prtItemNmMax = 40;

	public StockListHead() {
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setDateFrom(String setDate) {
		this.srcDateFrom = setDate;
	}

	public void setDateTo(String setDate) {
		this.srcDateTo = setDate;
	}

	public void setYMDFrom(String setDate) {
		this.srcYMDFrom = setDate;
	}

	public void setYMDTo(String setDate) {
		this.srcYMDTo = setDate;
	}

	public void setPrtContId(String setDate) {
		this.prtContId = setDate;
	}

	public void setPrtContNm(String setDate) {
		this.prtContNm = setDate;
	}

	public void setPrtDate(String setDate) {
		this.prtDate = setDate;
	}

	public void setPrtPage(String setPage) {
		this.prtPage = setPage;
	}

	public void setPrtDrugKind(String setKind) {
		this.prtDrugKind = setKind;
	}

	// 04.03.01 onuki
	public void setPrtOrder(String setOrder) {
		this.prtOrder = setOrder;
	}

	//
	public void setPrtAndOr(String setAndOr) {
		this.prtAndOr = setAndOr;
	}

	public void setPrtItemNo(String setItemNo) {
		this.prtItemNo = setItemNo;
	}

	public void setPrtItemNm(String setItemNm) {
		this.prtItemNm = setItemNm;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesDateFrom() {
		Common com = new Common();
		return com.getByteData(this.srcDateFrom, srcDateFromMax);
	}

	public char[] getBytesDateTo() {
		Common com = new Common();
		return com.getByteData(this.srcDateTo, srcDateToMax);
	}

	public char[] getBytesYMDFrom() {
		Common com = new Common();
		return com.getByteData(this.srcYMDFrom, srcYMDFromMax);
	}

	public char[] getBytesYMDTo() {
		Common com = new Common();
		return com.getByteData(this.srcYMDTo, srcYMDToMax);
	}

	public char[] getBytesPrtContId() {
		Common com = new Common();
		return com.getByteData(this.prtContId, prtContIdMax);
	}

	public char[] getBytesPrtContNm() {
		Common com = new Common();
		return com.getByteData(this.prtContNm, prtContNmMax);
	}

	public char[] getBytesPrtDate() {
		Common com = new Common();
		return com.getByteData(this.prtDate, prtDateMax);
	}

	public char[] getBytesPrtPage() {
		Common com = new Common();
		return com.getByteData(this.prtPage, prtPageMax);
	}

	public char[] getBytesPrtDrugKind() {
		Common com = new Common();
		return com.getByteData(this.prtDrugKind, prtDrugKindMax);
	}

	// 04.03.01 onuki
	public char[] getBytesPrtOrder() {
		Common com = new Common();
		return com.getByteData(this.prtOrder, prtOrderMax);
	}

	//
	public char[] getBytesPrtAndOr() {
		Common com = new Common();
		return com.getByteData(this.prtAndOr, prtAndOrMax);
	}

	public char[] getBytesPrtItemNo() {
		Common com = new Common();
		return com.getByteData(this.prtItemNo, prtItemNoMax);
	}

	public char[] getBytesPrtItemNm() {
		Common com = new Common();
		return com.getByteData(this.prtItemNm, prtItemNmMax);
	}
}
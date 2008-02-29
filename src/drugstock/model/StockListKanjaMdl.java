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
 * 「指定品目使用患者一覧表」モデル
 */

public class StockListKanjaMdl implements Serializable {

	private Common com = new Common();

	private String seqNo; // 連番
	private String orca_User_No; // 患者番号
	private String Name; // 氏名
	private String Insurance; // 保険
	private String use_Med; // 使用品目
	private String use_Day; // 使用日
	private String expend_Num; // 出庫数

	private final int seqNoMax = 5;
	private final int orca_User_NoMax = 20;
	private final int NameMax = 16;
	private final int InsuMax = 3;
	private final int use_MedMax = 5;
	private final int use_DayMax = 62;
	private final int expend_NumMax = 11;

	public StockListKanjaMdl() {
		seqNo = ""; // 連番
		orca_User_No = ""; // 患者番号
		Name = ""; // 氏名
		Insurance = ""; // 保険
		use_Med = ""; // 使用品目
		use_Day = ""; // 使用日
		expend_Num = ""; // 出庫数
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setSeqNo(String setDate) {
		this.seqNo = setDate;
	}

	public void setOrca_User_No(String setDate) {
		this.orca_User_No = setDate;
	}

	public void setName(String setDate) {
		this.Name = setDate;
	}

	public void setInsurance(String setDate) {
		this.Insurance = setDate;
	}

	public void setUse_Med(String setDate) {
		this.use_Med = setDate;
	}

	public void setUse_Day(String setDate) {
		this.use_Day = setDate;
	}

	public void setExpend_Num(String setDate) {
		this.expend_Num = setDate;
	}

	// ************************************************
	// ゲッター
	// ************************************************
	public char[] getBytesSeqNo() {
		return com.getByteData(this.seqNo, seqNoMax);
	}

	public char[] getBytesOrca_User_No() {
		return com.getByteData(this.orca_User_No, orca_User_NoMax);
	}

	public char[] getBytesName() {
		return com.getByteData(this.Name, NameMax);
	}

	public char[] getBytesInsurance() {
		return com.getByteData(this.Insurance, InsuMax);
	}

	public char[] getBytesUse_Med() {
		return com.getByteData(this.use_Med, use_MedMax);
	}

	public char[] getBytesUse_Day() {
		return com.getByteData(this.use_Day, use_DayMax);
	}

	public char[] getBytesExpend_Num() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.expend_Num,
		        expend_NumMax, 3);
	}

	// **************************************************
	public String getStrSeqNo() {
		return this.seqNo;
	}

	public String getStrOrca_User_No() {
		return this.orca_User_No;
	}

	public String getStrName() {
		return this.Name;
	}

	public String getStrInsurance() {
		return this.Insurance;
	}

	public String getStrUse_Med() {
		return this.use_Med;
	}

	public String getStrUse_Day() {
		return this.use_Day;
	}

	public String getStrExpend_Num() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.expend_Num,
		        7, 3);
	}

}

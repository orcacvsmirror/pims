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
/*******************************************************************
 * 更新履歴：
 *           2006.04.26 Hasegawa :doubule型で返す関数  新規追加
 *
 *
 *
 *
 *
 *
 *
 ********************************************************************/
package drugstock.model;

import java.io.Serializable;

import drugstock.cmn.Common;

/**
 * 「在庫一覧表」モデル
 */

public class StockListZaikoMdl implements Serializable {

	private Common com = new Common();

	private String num; // 品番
	private String name; // 品名
	private String unit; // 単位
	private String base; // 薬価基準
	private String stock; // 在庫単価
	private String rate; // 単価率
	private String kurikosi; // 繰越量
	private String nyuko; // 入庫量
	private String harai; // 払出量
	private String henpin; // 返品量
	private String tyousei; // 調整量
	private String zaiko; // 在庫量
	private String kingaku; // 在庫金額
	private String medKind; // 薬剤区分

	private final int numMax = 10;
	private final int nameMax = 34;
	private final int unitMax = 2;
	// private final int unitMax = 4;
	private final int baseMax = 9;
	private final int stockMax = 9;
	private final int rateMax = 9;
	private final int kurikosiMax = 11;
	private final int nyukoMax = 10;
	private final int haraiMax = 10;
	private final int henpinMax = 10;
	private final int tyouseiMax = 11;
	private final int zaikoMax = 11;
	private final int kingakuMax = 13;

	public StockListZaikoMdl() {
		num = "";
		name = "";
		unit = "";
		base = "";
		stock = "";
		rate = "";
		kurikosi = "";
		nyuko = "";
		harai = "";
		henpin = "";
		tyousei = "";
		zaiko = "";
		kingaku = "";
		medKind = "";
	}

	// ************************************************
	// セッター
	// ************************************************
	public void setNum(String setDate) {
		this.num = setDate;
	}

	public void setName(String setDate) {
		this.name = setDate;
	}

	public void setUnit(String setDate) {
		this.unit = setDate;
	}

	public void setBase(String setDate) {
		this.base = setDate;
	}

	public void setStock(String setDate) {
		this.stock = setDate;
	}

	public void setRate(String setDate) {
		this.rate = setDate;
	}

	public void setKurikosi(String setDate) {
		this.kurikosi = setDate;
	}

	public void setNyuko(String setDate) {
		this.nyuko = setDate;
	}

	public void setHarai(String setDate) {
		this.harai = setDate;
	}

	public void setHenpin(String setDate) {
		this.henpin = setDate;
	}

	public void setTyousei(String setDate) {
		this.tyousei = setDate;
	}

	public void setZaiko(String setDate) {
		this.zaiko = setDate;
	}

	public void setKingaku(String setDate) {
		this.kingaku = setDate;
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

	public char[] getBytesUnit() {
		return com.getByteData(this.unit, unitMax);
	}

	public char[] getBytesBase() {
		return com.getByteData(this.base, baseMax);
	}

	public char[] getBytesStock() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stock,
		        stockMax, 2);
	}

	public char[] getBytesRate() {
		return com.getByteData(this.rate, rateMax);
	}

	public char[] getBytesKurikosi() {
		return com.getByteData(this.kurikosi, kurikosiMax);
	}

	public char[] getBytesNyuko() {
		return com.getByteData(this.nyuko, nyukoMax);
	}

	public char[] getBytesHarai() {
		return com.getByteData(this.harai, haraiMax);
	}

	public char[] getBytesHenpin() {
		return com.getByteData(this.henpin, henpinMax);
	}

	public char[] getBytesTyousei() {
		return com.getByteData(this.tyousei, tyouseiMax);
	}

	public char[] getBytesZaiko() {
		return com.getByteData(this.zaiko, zaikoMax);
	}

	public char[] getBytesKingaku() {
		return com.getByteData(this.kingaku, kingakuMax);
	}

	// *************************************************************
	public String getStrNum() {
		return this.num;
	}

	public String getStrName() {
		return this.name;
	}

	public String getStrUnit() {
		return this.unit;
	}

	public String getStrBase() {
		return this.base;
	}

	public String getStrStock() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stock, 2);
	}

	public String getStrRate() {
		return this.rate;
	}

	public String getStrKurikosi() {
		return this.kurikosi;
	}

	public String getStrNyuko() {
		return this.nyuko;
	}

	public String getStrHarai() {
		return this.harai;
	}

	public String getStrHenpin() {
		return this.henpin;
	}

	public String getStrTyousei() {
		return this.tyousei;
	}

	public String getStrZaiko() {
		return this.zaiko;
	}

	public String getStrKingaku() {
		return this.kingaku;
	}

	public String getMedKind() {
		return this.medKind;
	}

	// doubule型で返す関数 2006.04.26 Hasegawa 新規追加
	public double getDoubleStock() {
		Double DTmp = new Double(this.stock);
		return DTmp.doubleValue();
	}

}

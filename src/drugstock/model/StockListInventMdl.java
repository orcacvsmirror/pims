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
 * 「棚卸一覧表」モデル
 */

public class StockListInventMdl implements Serializable {

	private Common com = new Common();

	private String num; // 品番
	private String name; // 品名
	private String unit; // 単位
	// private String base; // 薬価基準
	private String stock; // 在庫単価
	private String zaiko; // 在庫量
	private String kingaku; // 在庫金額
	private String zaikoInvent; // 棚卸在庫量
	private String kingakuInvent; // 棚卸在庫金額
	private String zaikoDif; // 差分在庫量
	private String kingakuDif; // 差分在庫金額
	private String medKind; // 薬剤区分

	private final int numMax = 10;
	private final int nameMax = 34;
	private final int unitMax = 2;
	// private final int baseMax = 9;
	private final int stockMax = 9;
	private final int zaikoMax = 11;
	private final int kingakuMax = 13;
	private final int zaikoInventMax = 11;
	private final int kingakuInventMax = 13;
	private final int zaikoDifMax = 11;
	private final int kingakuDifMax = 13;

	public StockListInventMdl() {
		num = "";
		name = "";
		unit = "";
		// base = "";
		stock = "";
		zaiko = "";
		kingaku = "";
		zaikoInvent = "";
		kingakuInvent = "";
		zaikoDif = "";
		kingakuDif = "";
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

	// public void setBase(String setDate)
	// {
	// this.base = setDate;
	// }

	public void setStock(String setDate) {
		this.stock = setDate;
	}

	public void setZaiko(String setDate) {
		this.zaiko = setDate;
	}

	public void setKingaku(String setDate) {
		this.kingaku = setDate;
	}

	public void setZaikoInvent(String setDate) {
		this.zaikoInvent = setDate;
	}

	public void setKingakuInvent(String setDate) {
		this.kingakuInvent = setDate;
	}

	public void setZaikoDif(String setDate) {
		this.zaikoDif = setDate;
	}

	public void setKingakuDif(String setDate) {
		this.kingakuDif = setDate;
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

	// public char[] getBytesBase()
	// {
	// return com.getByteData(this.base, baseMax);
	// }

	public char[] getBytesStock() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.stock,
		        stockMax, 2);
	}

	public char[] getBytesZaiko() {
		return com.getByteData(this.zaiko, zaikoMax);
	}

	public char[] getBytesKingaku() {
		// return com.getByteData(this.kingaku, kingakuMax);
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.kingaku,
		        kingakuMax);
	}

	public char[] getBytesZaikoInvent() {
		return com.getByteData(this.zaikoInvent, zaikoInventMax);
	}

	public char[] getBytesKingakuInvent() {
		// 合計金額計算のため、小数点処理はBiz
		return com.getByteData(this.kingakuInvent, kingakuInventMax);
		// return
		// com.getByteDataElseSpaceWithCanmaDownDecimal(this.kingakuInvent,
		// kingakuInventMax) ;
	}

	public char[] getBytesZaikoDif() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.zaikoDif,
		        zaikoDifMax, 2);
	}

	public char[] getBytesKingakuDif() {
		return com.getByteDataElseSpaceWithCanmaDownDecimal(this.kingakuDif,
		        kingakuDifMax);
	}

	// **************************************************
	public String getStrNum() {
		return this.num;
	}

	public String getStrName() {
		return this.name;
	}

	public String getStrUnit() {
		return this.unit;
	}

	public String getStrStock() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.stock, 2);
	}

	public String getStrZaiko() {
		return this.zaiko;
	}

	public String getStrKingaku() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.kingaku);
	}

	public String getStrZaikoInvent() {
		return this.zaikoInvent;
	}

	public String getStrKingakuInvent() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.kingakuInvent);
	}

	public String getStrZaikoDif() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.zaikoDif, 2);
		// return this.zaikoDif;
	}

	public String getStrKingakuDif() {
		return com.getStringDataElseSpaceWithCanmaDownDecimal(this.kingakuDif);
	}

	public String getMedKind() {
		return this.medKind;
	}

}

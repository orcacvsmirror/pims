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
package drugstock.biz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import drugstock.db.ComDatabase;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * 「薬剤区分設定」DB処理
 */

public class BizDrugSyurui {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizDrugSyurui() {
	}

	/** 薬剤区分情報登録 */
	public String insSyuruiCdNm(SyuruiCdNm item) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			rets = checkSyuruiCdNm(item);
			if (rets.equals("FOUND") == false) {
				db.bigin();
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_med_kind VALUES (");
				bsql.append(" '" + item.code + "',");
				bsql.append(" '" + item.orcacd + "',");
				bsql.append(" '" + item.name + "',");
				bsql.append("'0',");
				bsql.append("'" + hospnum + "')");
				int stats = db.execute(bsql.toString());
				if (stats != 0) {
					db.rollback();
				} else {
					db.commit();
					rets = "OK";
				}
			}
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizDrugSyurui insSyuruiCdNm Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;
	}

	/** 薬剤区分情報修正 */
	public String updtSyuruiCdNm(SyuruiCdNm selectItem, SyuruiCdNm item) {

		String rets = "NG";

		// 一度、修正元の薬剤区分情報を消去し、重複チェック
		delSyuruiCdNm(selectItem.code);
		rets = checkSyuruiCdNm(item);
		// 一要素でも重複していた(修正できない)場合は、元の情報を再度新規作成
		if (rets.equals("FOUND")) {
			insSyuruiCdNm(selectItem);
			// 重複がない場合は、修正情報を新規作成
		} else {
			insSyuruiCdNm(item);
			rets = "OK";
		}

		return rets;
	}

	/** 薬剤区分情報削除 */
	public String delSyuruiCdNm(String code) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			db.bigin();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_med_kind");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND stock_med_kind = '" + code + "'");
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizDrugSyurui delSyuruiCdNm Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/** 薬剤区分が既存かどうかチェック */
	private String checkSyuruiCdNm(SyuruiCdNm item) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			// 重複チェック
			bsql.delete(0, bsql.length());
			bsql.append("SELECT stock_med_kind, orca_med_kind, med_kind_name, del_flg");
			bsql.append(" FROM m_med_kind");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND ( stock_med_kind = '" + item.code + "'");
			bsql.append(" OR orca_med_kind = '" + item.orcacd + "'");
			bsql.append(" OR med_kind_name = '" + item.name + "')");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rets = "FOUND";
			}
		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizDrugSyurui insSyuruiCdNm SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizDrugSyurui insSyuruiCdNm Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;
	}

	/** 該当コードの薬剤区分情報を取得する */
	private SyuruiCdNm getSyuruiCdNm(String code) {
		BizContradrug biz = new BizContradrug();
		SyuruiCdNm[] item = biz.getMed_kind_list();

		int i = 0;
		boolean isFound = false;
		for (i = 0; i < item.length; i++) {
			if (item[i].code.equals(code)) {
				isFound = true;
				break;
			}
		}
		if (isFound) {
			return item[i];
		} else {
			return null;
		}
	}

}

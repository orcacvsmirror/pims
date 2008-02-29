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
import java.util.ArrayList;

import drugstock.cmn.PropRead;
import drugstock.db.ComDatabase;
import drugstock.model.InventStockFactor;

import drugstock.batch.OrcaHospNumImport;

/**
 * 「棚卸処理」DB処理
 */

public class BizInventStock {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizInventStock() {
	}

	/** 仕入明細リストの取得 */
	public InventStockFactor[] getInventStockFactor(String med_syurui_srach,
	        String stc_month_srach) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		InventStockFactor cItem[] = null;
		String sql = null;
		ComDatabase db = new ComDatabase();

		// 薬剤出力並び順プロパティ取得
		PropRead prop = new PropRead();
		String stock_med_order = prop.getProp("stock_med_order");
		if (stock_med_order == null)
			stock_med_order = "0";
		if (stock_med_order.equals("1") == false)
			stock_med_order = "0";
		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(512);
			bsql.delete(0, bsql.length());

			bsql.append("SELECT");
			bsql.append(" t_stock_invent.yyyymm,");
			bsql.append(" t_stock_invent.item_no,");
			bsql.append(" m_cont_item.med_nm,");
			bsql.append(" m_cont_item.med_kn,");
			bsql.append(" t_stock_invent.stock_theory,");
			bsql.append(" t_stock_invent.stock_truth,");
			bsql.append(" t_stock_invent.flag_truth");
			bsql.append(" FROM  t_stock_invent ");
			bsql.append(" LEFT JOIN m_cont_item ON t_stock_invent.item_no = m_cont_item.item_no");
			bsql.append(" WHERE m_cont_item.hospnum='" + hospnum + "'");
			bsql.append(" AND t_stock_invent.hospnum='" + hospnum + "'");
			bsql.append(" AND m_cont_item.med_kind1='" + med_syurui_srach
			        + "' ");
			bsql.append(" AND t_stock_invent.yyyymm='" + stc_month_srach + "'");
			bsql.append(" AND m_cont_item.del_flg='0'");
			// 複数業者薬剤の重複を避ける
			bsql.append(" AND m_cont_item.cont_id=");
			bsql.append("(SELECT MAX(cont_id) FROM m_cont_item");
			bsql.append(" WHERE t_stock_invent.item_no = m_cont_item.item_no");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "')");
			// 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる)
			bsql.append(" ORDER BY");
			if (stock_med_order.equals("0")) {
				bsql.append(" m_cont_item.item_no");
			} else {
				bsql.append(" m_cont_item.med_kn");
			}
			sql = bsql.toString();
			// System.out.println(sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			while (rs.next()) {
				String yyyymm = rs.getString("yyyymm"); // 年月
				// String cont_nm = rs.getString("cont_nm"); // 業者名
				String item_no = rs.getString("item_no"); // 薬剤品番
				String med_nm = rs.getString("med_nm"); // 薬剤名
				String med_kn = rs.getString("med_kn"); // 薬剤カナ名
				String stock_theory = rs.getString("stock_theory"); // 理論在庫
				String stock_truth = rs.getString("stock_truth"); // 実在庫
				String flag_truth = rs.getString("flag_truth"); // 実在庫フラグ

				InventStockFactor wk = new InventStockFactor(yyyymm, // 仕入ＮＯ
				        // cont_nm, // 業者名
				        item_no, // 薬剤品番
				        med_nm, // 薬剤名
				        med_kn, // 薬剤カナ名
				        stock_theory, // 理論在庫
				        stock_truth, // 実在庫
				        flag_truth); // 実在庫フラグ
				aList.add(wk);
			}
			rs.close();
			stmt.close();
			cItem = new InventStockFactor[aList.size()];
			cItem = (InventStockFactor[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizInventStock getInventStockFactor SQLException"
			                + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizInventStock getInventStockFactor Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/** 仕入１明細追加 */
	public String inputInventStock(String[] medTblValue,
	        String[] inputTblValue, String stc_yyyymm) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = null;
		String sql = null;
		String flg_continue = "GO";

		ComDatabase db = new ComDatabase();

		try {
			int stats;
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);

			for (int i = 0; i < medTblValue.length; i++) {
				if (inputTblValue[i].equals("----") == false) {
					db.bigin();
					bsql.delete(0, bsql.length());
					bsql.append("UPDATE t_stock_invent SET");
					bsql.append(" stock_truth='" + inputTblValue[i] + "',");
					bsql.append(" flag_truth='1'");
					//bsql.append(" hospnum = '" + hospnum +  "'");
					bsql.append(" WHERE item_no='" + medTblValue[i] + "'");
					bsql.append(" AND yyyymm='" + stc_yyyymm + "'");
					bsql.append(" AND hospnum = '" + hospnum +  "'");
					stats = db.execute(bsql.toString());
					// System.out.println(bsql.toString());
					if (stats != 0) {
						flg_continue = "ERR";
					}
				}
			}

			if (flg_continue.equals("ERR")) {
				db.rollback();
				rets = "NG";
			} else {
				db.commit();
			}
		} catch (Exception e) {
			System.out.println("BizInventStock inputInventStock Exception"
			        + e.toString());
			rets = "NG";
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;

	}

	/** 仕入１明細追加 */
	public String newInventStock(String item_no, String bara, String yyyymm) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = null;
		String sql = null;
		String flg_continue = "GO";

		ComDatabase db = new ComDatabase();

		try {
			int stats;
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);

			bsql.append("SELECT");
			bsql.append(" yyyymm,");
			bsql.append(" item_no");
			bsql.append(" FROM t_stock_invent ");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock_invent.yyyymm='" + yyyymm + "'");
			bsql.append(" AND t_stock_invent.item_no='" + item_no + "'");

			sql = bsql.toString();
			// System.out.println(sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				rets = "既に同じ年月、品番のデータが存在します";
			}
			rs.close();
			stmt.close();

			if (rets == null) {
				db.bigin();
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_stock_invent (");
				bsql.append(" stock_truth,");
				bsql.append(" flag_truth,");
				bsql.append(" item_no,");
				bsql.append(" yyyymm, ");
				bsql.append(" hospnum ");
				bsql.append(" ) VALUES ( ");
				bsql.append(bara + ",");
				bsql.append(" '1',");
				bsql.append(" '" + item_no + "',");
				bsql.append(" '" + yyyymm + "',");
				bsql.append(" '" + hospnum + "')");
				stats = db.execute(bsql.toString());
				// System.out.println(bsql.toString());
				if (stats != 0) {
					flg_continue = "ERR";
				}
			}

			if (flg_continue.equals("ERR")) {
				db.rollback();
				rets = "データ登録時のエラーです";
			} else {
				db.commit();
			}
		} catch (Exception e) {
			System.out.println("BizInventStock newInventStock Exception"
			        + e.toString());
			rets = "データ登録時のエラーです";
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;

	}

}

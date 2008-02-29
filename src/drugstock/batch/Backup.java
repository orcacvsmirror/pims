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
package drugstock.batch;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import drugstock.cmn.WaitMsg;
import drugstock.db.ComDatabase;

/**
 * バックアップ用クラス
 * 
 * @version 3.00
 */

public class Backup implements Runnable {

	Connection conn_src = null;
	Connection conn_dest = null;
	Statement stmt = null;
	ResultSet rs = null;

	Thread thread;

	public Backup() {
		thread = new Thread(this);
	}

	public void start() {
		thread.start();
	}

	public void stop() {
		thread = null;
	}

	public void run() {
		String sql = null;
		ComDatabase db_src = new ComDatabase();
		ComDatabase db_dest = new ComDatabase(1);

		WaitMsg wait = new WaitMsg();
		wait.setMsg1("データベースをバックアップ中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();

		try {

			conn_src = db_src.getConnection();
			conn_dest = db_dest.getConnection();
			StringBuffer bsql = new StringBuffer(256);

			// 業者
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_contractor");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_contractor");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_contractor VALUES (");
				bsql.append("" + getNumber(rs.getString("cont_id")) + ",");
				bsql.append("'" + getCharcter(rs.getString("cont_cd")) + "',");
				bsql.append("'" + getCharcter(rs.getString("cont_nm")) + "',");
				bsql.append("'" + getCharcter(rs.getString("short_nm")) + "',");
				bsql.append("" + getNumber(rs.getString("discount")) + ",");
				bsql.append("'" + getCharcter(rs.getString("tax_flg")) + "',");
				bsql.append("'" + getCharcter(rs.getString("del_flg")) + "',");
				bsql.append("'" + getCharcter(rs.getString("hospnum")) + "'");

				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 業者別品目マスタ
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_cont_item");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_cont_item");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_cont_item VALUES (");
				bsql.append("" + getNumber(rs.getString("cont_id")) + ",");
				bsql.append("'" + getCharcter(rs.getString("orca_med_cd"))
				        + "',");
				bsql.append("'" + getCharcter(rs.getString("item_no")) + "',");
				bsql.append("'" + getCharcter(rs.getString("med_nm")) + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kn")) + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind1"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind2"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind3"))
				                + "',");
				bsql.append("" + getNumber(rs.getString("pack_unit3")) + ",");
				bsql.append("" + getNumber(rs.getString("pack_unit2")) + ",");
				bsql.append("" + getNumber(rs.getString("pack_unit1")) + ",");
				bsql.append("" + getNumber(rs.getString("unit_price")) + ",");
				bsql.append("" + getNumber(rs.getString("discount")) + ",");
				bsql.append("'" + getCharcter(rs.getString("del_flg")) + "',");
				bsql.append("" + getCharcter(rs.getString("hacchu_p")) + ",");
				bsql.append("'" + getCharcter(rs.getString("hospnum")) + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 日レセ薬剤マスタ
			/*
			 * bsql.delete(0,bsql.length()); bsql.append("SELECT * FROM
			 * m_orca_medicine"); sql = bsql.toString(); stmt =
			 * conn_src.createStatement(); rs = stmt.executeQuery(sql);
			 * 
			 * bsql.delete(0,bsql.length()); bsql.append("DELETE FROM
			 * m_orca_medicine"); db_dest.execute(bsql.toString()); while
			 * (rs.next()) { bsql.delete(0,bsql.length()); bsql.append("INSERT
			 * INTO m_orca_medicine VALUES ("); bsql.append("'" +
			 * getCharcter(rs.getString("orca_med_cd")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("day_from")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("day_to")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("med_nm")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("med_kn")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("unit_nm")) + "',"); bsql.append("'" +
			 * getCharcter(rs.getString("med_kind")) + "',"); bsql.append("" +
			 * getNumber(rs.getString("med_price")) + ","); bsql.append("'" +
			 * getCharcter(rs.getString("kousei_cd")) + "'"); bsql.append(")");
			 * db_dest.execute(bsql.toString()); }
			 */

			// 薬剤区分マスタ
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_med_kind");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_med_kind");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_med_kind VALUES (");
				bsql.append("'" + getCharcter(rs.getString("stock_med_kind"))
				        + "',");
				bsql.append("'" + getCharcter(rs.getString("orca_med_kind"))
				        + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind_name"))
				        + "',");
				bsql.append("'" + getCharcter(rs.getString("del_flg")) + "',");
				bsql.append("'" + getCharcter(rs.getString("hospnum")) + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 入庫（仕入）
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM t_stocking");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_stocking");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_stocking VALUES (");
				bsql.append("" + getNumber(rs.getString("stc_id")) + ",");
				bsql.append("'" + getCharcter(rs.getString("stc_date")) + "',");
				bsql.append("'" + getCharcter(rs.getString("stc_cd")) + "',");
				bsql.append("" + getNumber(rs.getString("cont_id")) + ",");
				bsql.append("'" + getCharcter(rs.getString("item_no")) + "',");
				bsql.append("" + getNumber(rs.getString("stc_unit")) + ",");
				bsql.append("" + getNumber(rs.getString("stc_num")) + ",");
				bsql.append("'" + getCharcter(rs.getString("tax_flg")) + "',");
				bsql.append("" + getNumber(rs.getString("amount")) + ",");
				bsql.append("" + getNumber(rs.getString("discount")) + ",");
				bsql.append("" + getNumber(rs.getString("stc_amount")) + ",");
				bsql.append("" + getNumber(rs.getString("tax")) + ",");
				bsql.append("" + getNumber(rs.getString("pack3_num")) + ",");
				bsql.append("" + getNumber(rs.getString("pack2_num")) + ",");
				bsql.append("" + getNumber(rs.getString("pack1_num")) + ",");
				bsql.append("'" + getCharcter(rs.getString("del_flg")) + "',");
				bsql.append("'" + getCharcter(rs.getString("slip_no")) + "',");
				bsql.append("'" + getCharcter(rs.getString("hospnum")) + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 在庫
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM t_stock");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_stock");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_stock VALUES (");
				bsql.append("'" + getCharcter(rs.getString("yyyymm")) + "',");
				bsql.append("'" + getCharcter(rs.getString("item_no")) + "',");
				bsql.append("" + getNumber(rs.getString("unit_price")) + ",");
				bsql.append("" + getNumber(rs.getString("amount")) + ",");
				bsql.append("" + getNumber(rs.getString("before_stock")) + ",");
				bsql.append("" + getNumber(rs.getString("stocking_num")) + ",");
				bsql.append("" + getNumber(rs.getString("expend_num")) + ",");
				bsql.append("" + getNumber(rs.getString("adjust_num")) + ",");
				bsql.append("" + getNumber(rs.getString("stock_num")) + ",");
				bsql.append("'" + getCharcter(rs.getString("med_kind1"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind2"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("med_kind3"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("del_flg")) + "',");
				bsql.append("'" + getCharcter(rs.getString("total_num"))
				                + "',");
				bsql.append("'" + getCharcter(rs.getString("total_amount"))
				        + "',");
				bsql.append("'" + getCharcter(rs.getString("back_num")) + "',");
				bsql.append("'" + getCharcter(rs.getString("hospnum")) + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 出庫
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM t_expend");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_expend");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_expend VALUES (");
				bsql.append("'" + getCharcter(rs.getString("exp_date")) + "',");
				bsql.append("" + getCharcter(rs.getString("orca_user")) + ",");
				bsql.append("'" + getCharcter(rs.getString("orca_med_cd"))
				        + "',");
				bsql.append("'" + getNumber(rs.getString("insurance")) + "',");
				bsql.append("" + getNumber(rs.getString("expend_num")) + ",");
				bsql.append("'" + getNumber(rs.getString("name")) + "',");
				bsql.append("'" + getNumber(rs.getString("orca_user_no"))
				                + "',");
				bsql.append("'" + getNumber(rs.getString("hospnum"))
				                + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 棚卸量 04.04.08 onuki
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM t_stock_invent");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);

			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_stock_invent");
			db_dest.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_stock_invent VALUES (");
				bsql.append("'" + getCharcter(rs.getString("yyyymm")) + "',");
				bsql.append("'" + getCharcter(rs.getString("item_no")) + "',");
				bsql.append("" + getCharcter(rs.getString("stock_theory"))
				        + ",");
				bsql.append("" + getNumber(rs.getString("stock_truth")) + ",");
				bsql.append("'" + getNumber(rs.getString("flag_truth")) + "',");
				bsql.append("'" + getNumber(rs.getString("hospnum")) + "'");
				bsql.append(")");
				db_dest.execute(bsql.toString());
			}

			// 業者シーケンス
			bsql.delete(0, bsql.length());
			bsql.append("SELECT last_value FROM sq_contractor");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("SELECT setval('sq_contractor', "
				        + rs.getString("last_value") + ")");
				db_dest.execute(bsql.toString());
			}

			// 入庫（仕入）シーケンス
			bsql.delete(0, bsql.length());
			bsql.append("SELECT last_value FROM sq_stocking");
			sql = bsql.toString();
			stmt = conn_src.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("SELECT setval('sq_stocking', "
				        + rs.getString("last_value") + ")");
				db_dest.execute(bsql.toString());
			}

			conn_dest.close();
			db_dest.close();

		} catch (SQLException sqle) {
			System.out.println("OrcaDrugImport run SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("OrcaDrugImport run Exception" + e.toString());
		} finally {
			db_src.closeAllResource(rs, stmt, conn_src);
			wait.destroy();
		}
	}

	/**
	 * 数値チェック
	 */
	private String getNumber(String value) {
		if (value == null) {
			return "0";
		} else {
			return value;
		}
	}

	/**
	 * 文字チェック
	 */
	private String getCharcter(String value) {
		if (value == null) {
			return "";
		} else {
			return value;
		}
	}
}

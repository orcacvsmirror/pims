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

import drugstock.db.ComDatabase;
import drugstock.model.Stocking;

import drugstock.batch.OrcaHospNumImport;

/**
 * 「伝票入力」DB処理
 */

public class BizStocinput {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizStocinput() {
	}

	/** 仕入明細リストの取得 */
	public Stocking[] getStockingData(String contractor_id,
	        String Stockingdate, String slip_no_srach) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		Stocking cItem[] = null;
		String sql = null;
		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(512);
			bsql.delete(0, bsql.length());

			bsql.append("SELECT t_stocking.*,");
			bsql.append(" m_cont_item.med_nm,");
			bsql.append(" m_cont_item.med_kn,");
			bsql.append(" m_cont_item.med_kind1,");
			bsql.append(" m_cont_item.med_kind2,");
			bsql.append(" m_cont_item.med_kind3,");
			bsql.append(" m_cont_item.pack_unit3,");
			bsql.append(" m_cont_item.pack_unit2,");
			bsql.append(" m_cont_item.pack_unit1,");
			bsql.append(" m_cont_item.unit_price,");
			bsql.append(" m_cont_item.discount AS discountritu");
			bsql.append(" FROM t_stocking INNER JOIN");
			bsql.append(" m_cont_item ON (t_stocking.cont_id = m_cont_item.cont_id)");
			bsql.append(" AND (t_stocking.item_no = m_cont_item.item_no)");
			// bsql.append(" WHERE t_stocking.del_flg='0' ");
			bsql.append(" WHERE t_stocking.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stocking.del_flg='0' ");
			bsql.append(" AND m_cont_item.del_flg='0' ");
			bsql.append(" AND t_stocking.cont_id = " + contractor_id);
			bsql.append(" AND t_stocking.stc_date = '" + Stockingdate + "'");
			// 04.03.26 onuki
			if (slip_no_srach.equals("")) {
				bsql.append(" AND (t_stocking.slip_no is null OR t_stocking.slip_no = '') ");
			} else {
				bsql.append(" AND t_stocking.slip_no = '" + slip_no_srach
				                + "'");
			}
			bsql.append(" ORDER BY t_stocking.stc_id");
			sql = bsql.toString();
			// System.out.println(sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			while (rs.next()) {
				String stc_id = rs.getString("stc_id"); // 仕入ＮＯ
				String stc_date = rs.getString("stc_date"); // 仕入日
				// 04.03.26 onuki
				String slip_no = rs.getString("slip_no"); // 伝票番号
				String stc_cd = rs.getString("stc_cd"); // 仕入区分
				String cont_id = rs.getString("cont_id"); // 業者区分
				String item_no = rs.getString("item_no"); // 品番
				String stc_unit = rs.getString("stc_unit"); // 仕入単価
				String stc_num = rs.getString("stc_num"); // 仕入数量
				String tax_flg = rs.getString("tax_flg"); // 税区分
				String amount = rs.getString("amount"); // 金額
				String discount = rs.getString("discount"); // 値引
				String stc_amount = rs.getString("stc_amount"); // 購入金額
				String tax = rs.getString("tax"); // 税金額
				String pack3_num = rs.getString("pack3_num"); // 仕入梱包数
				String pack2_num = rs.getString("pack2_num"); // 仕入包装数
				String pack1_num = rs.getString("pack1_num"); // 仕入バラ数
				String del_flg = rs.getString("del_flg"); // 削除フラグ
				String med_nm = rs.getString("med_nm"); // 薬剤名称
				String med_kn = rs.getString("med_kn"); // 薬剤カナ名称
				String med_kind1 = rs.getString("med_kind1"); // 種類１
				String med_kind2 = rs.getString("med_kind2"); // 種類２
				String med_kind3 = rs.getString("med_kind3"); // 種類３
				String pack_unit3 = rs.getString("pack_unit3"); // 梱包入数
				String pack_unit2 = rs.getString("pack_unit2"); // 包装入数
				String pack_unit1 = rs.getString("pack_unit1"); // バラ入数
				String unit_price = rs.getString("unit_price"); // 最新納入単価
				String discountritu = rs.getString("discountritu"); // 値引率

				Stocking wk = new Stocking(stc_id, // 仕入ＮＯ
				        stc_date, // 仕入日
				        // 04.03.26 onuki
				        slip_no, // 伝票番号
				        stc_cd, // 仕入区分
				        cont_id, // 業者区分
				        item_no, // 品番
				        stc_unit, // 仕入単価
				        stc_num, // 仕入数量
				        tax_flg, // 税区分
				        amount, // 金額
				        discount, // 値引
				        stc_amount, // 購入金額
				        tax, // 税金額
				        pack3_num, // 仕入梱包数
				        pack2_num, // 仕入包装数
				        pack1_num, // 仕入バラ数
				        del_flg, // 削除フラグ
				        med_nm, // 薬剤名称
				        med_kn, // 薬剤カナ名称
				        med_kind1, // 種類１
				        med_kind2, // 種類２
				        med_kind3, // 種類３
				        pack_unit3, // 梱包入数
				        pack_unit2, // 包装入数
				        pack_unit1, // バラ入数
				        unit_price, // 最新納入単価
				        discountritu); // 値引率
				aList.add(wk);
			}
			rs.close();
			stmt.close();
			cItem = new Stocking[aList.size()];
			cItem = (Stocking[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getStockingData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getStockingData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/** 仕入１明細追加 */
	public String insStocking(Stocking item) {

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
			db.bigin();
			// 仕入明細１レコード追加
			bsql.delete(0, bsql.length());
			bsql.append("INSERT INTO t_stocking (");
			bsql.append(" stc_id, ");
			bsql.append(" stc_date, ");
			bsql.append(" slip_no, ");
			bsql.append(" stc_cd, ");
			bsql.append(" cont_id, ");
			bsql.append(" item_no, ");
			bsql.append(" stc_unit, ");
			bsql.append(" stc_num, ");
			bsql.append(" tax_flg, ");
			bsql.append(" amount, ");
			bsql.append(" discount, ");
			bsql.append(" stc_amount, ");
			bsql.append(" tax, ");
			bsql.append(" pack3_num, ");
			bsql.append(" pack2_num, ");
			bsql.append(" pack1_num, ");
			bsql.append(" del_flg, ");
			bsql.append(" hospnum ");
			bsql.append(" ) VALUES ( ");
			bsql.append("nextval('sq_stocking'),");
			bsql.append("'" + item.stc_date + "',");
			// 04.03.26 onuki
			bsql.append("'" + item.slip_no + "',");
			bsql.append("'" + item.stc_cd + "',");
			bsql.append(item.cont_id + ",");
			bsql.append("'" + item.item_no + "',");
			bsql.append(item.stc_unit + ",");
			bsql.append(item.stc_num + ",");
			bsql.append("'" + item.tax_flg + "',");
			bsql.append(item.amount + ",");
			bsql.append(item.discount + ",");
			bsql.append(item.stc_amount + ",");
			bsql.append(item.tax + ",");
			bsql.append(item.pack3_num + ",");
			bsql.append(item.pack2_num + ",");
			bsql.append(item.pack1_num + ",");
			bsql.append("'0'" + ",");
			bsql.append("'" + hospnum + "')");
			stats = db.execute(bsql.toString());
			// System.out.println(bsql.toString());
			if (stats != 0) {
				flg_continue = "ERR";
			} else {
				// 追加した仕入明細の仕入ＮＯを取得
				bsql.delete(0, bsql.length());
				bsql.append("SELECT currval('sq_stocking')");
				stmt = conn.createStatement();
				rs = stmt.executeQuery(bsql.toString());
				if (rs.next()) {
					rets = rs.getString("currval");
				}
				stmt.close();
				rs.close();
				if (item.stc_cd.equals("1")) { // 仕入区分が仕入の場合のみ
					// 業者別薬剤マスタ 最新納入単価更新
					bsql.delete(0, bsql.length());
					bsql.append("UPDATE m_cont_item");
					bsql.append(" SET unit_price=" + item.stc_unit +",");
					bsql.append(" hospnum = '" + hospnum + "'");
					bsql.append(" WHERE hospnum = '" + hospnum + "'");
					bsql.append(" AND del_flg = '0'");
					// bsql.append(" AND cont_id = " + item.cont_id );
					bsql.append(" AND item_no = '" + item.item_no + "'");
					stats = db.execute(bsql.toString());
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
		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizStocinput insStocking SQLException"
			        + sqle.toString());
			rets = "NG";
		} catch (Exception e) {
			System.out.println("BizStocinput insStocking Exception"
			        + e.toString());
			rets = "NG";
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;

	}

	/** 仕入１明細更新 */
	public String updtStocking(Stocking item) {

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
			bsql.append("UPDATE t_stocking");
			bsql.append(" SET stc_date='" + item.stc_date + "',");
			bsql.append(" stc_cd='" + item.stc_cd + "',");
			bsql.append(" cont_id=" + item.cont_id + ",");
			// 04.03.26 onuki
			bsql.append(" slip_no='" + item.slip_no + "',");
			bsql.append(" item_no='" + item.item_no + "',");
			bsql.append(" stc_unit=" + item.stc_unit + ",");
			bsql.append(" stc_num=" + item.stc_num + ",");
			bsql.append(" tax_flg='" + item.tax_flg + "',");
			bsql.append(" amount=" + item.amount + ",");
			bsql.append(" discount=" + item.discount + ",");
			bsql.append(" stc_amount=" + item.stc_amount + ",");
			bsql.append(" tax=" + item.tax + ",");
			bsql.append(" pack3_num=" + item.pack3_num + ",");
			bsql.append(" pack2_num=" + item.pack2_num + ",");
			bsql.append(" pack1_num=" + item.pack1_num + ",");
			bsql.append(" hospnum = '" + hospnum + "'");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND stc_id = " + item.stc_id);
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
			// } catch (SQLException sqle) {
			// db.rollback();
			// System.out.println("BizStocinput updtStocking SQLException" +
			// sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizStocinput updtStocking Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/** 仕入１明細削除 */
	public String delStocking(String stocking_id) {

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
			bsql.append("UPDATE t_stocking");
			bsql.append(" SET del_flg = '1',");
			bsql.append(" hospnum = '" + hospnum + "'");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND stc_id = " + stocking_id);
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
			// } catch (SQLException sqle) {
			// db.rollback();
			// System.out.println("BizStocinput delStocking SQLException" +
			// sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizStocinput delStocking Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

}

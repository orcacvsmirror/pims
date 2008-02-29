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

import drugstock.db.OrcaDatabase;
import drugstock.db.ComDatabase;
import drugstock.model.CodeName;

import drugstock.batch.OrcaHospNumImport;

/**
 * 「業者設定」DB処理
 */

public class BizContractor {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizContractor() {
	}

	/**
	 * 登録されているすべての業者データを取得します。
	 * 
	 * @return CodeName[] 業者データの配列
	 */
	public CodeName[] getCodeName() {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		CodeName cItem[] = null;
		String sql = null;
		String id = null;
		String cd = null;
		String nm = null;
		String nmkn = null;
		String nbkrt = null;
		String zeikbn = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_contractor");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" ORDER BY cont_id ");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			while (rs.next()) {
				id = rs.getString("cont_id");
				cd = rs.getString("cont_cd");
				nm = rs.getString("cont_nm");
				nmkn = rs.getString("short_nm");
				nbkrt = rs.getString("discount");
				zeikbn = rs.getString("tax_flg");

				CodeName wk = new CodeName(id, cd, nm, nmkn, nbkrt, zeikbn);
				aList.add(wk);
			}
			cItem = new CodeName[aList.size()];
			cItem = (CodeName[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizContractor GetCodeName SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContractor GetCodeName Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 特定の業者データを取得します。
	 * 
	 * @param code
	 *            システム内の業者コード
	 * @return CodeName 業者データモデル
	 */
	public CodeName getContractor(String code) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		CodeName cItem = null;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_contractor");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND cont_cd = '" + code + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			if (rs.next()) {
				String id = rs.getString("cont_id");
				String cd = rs.getString("cont_cd");
				String nm = rs.getString("cont_nm");
				String snm = rs.getString("short_nm");
				String ds = rs.getString("discount");
				String taxf = rs.getString("tax_flg");
				cItem = new CodeName(id, cd, nm, snm, ds, taxf);
			}

		} catch (SQLException sqle) {
			System.out.println("BizContractor getContractor SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContractor getContractor Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 指定された業者データモデルを新規登録します。
	 * 
	 * @param item
	 *            業者データモデル
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String insContractor(CodeName item) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			// code 重複チェック
			bsql.delete(0, bsql.length());
			bsql.append("SELECT cont_cd FROM m_contractor");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND cont_cd = '" + item.getCode() + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rets = "FOUND";
			} else {
				db.bigin();
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_contractor VALUES (");
				bsql.append("nextval('sq_contractor'),");
				bsql.append("'" + item.getCode() + "',");
				bsql.append("'" + item.getName() + "',");
				bsql.append("'" + item.getNamekn() + "',");
				bsql.append(item.getNebikiritu() + ",");
				bsql.append(item.getZeikbn() + ",");
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
		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizContractor insContractor SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContractor insContractor Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;
	}

	/**
	 * 業者データモデルで指定された業者情報を修正します。
	 * 
	 * @param item
	 *            業者データモデル
	 * @return 処理が正常終了したなら"OK"、異常があった場合には"NG"を返します。
	 */
	public String updtContractor(CodeName item) {

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
			bsql.append("UPDATE m_contractor");
			bsql.append(" SET cont_nm='" + item.getName() + "',");
			bsql.append(" short_nm='" + item.getNamekn() + "',");
			bsql.append(" discount=" + item.getNebikiritu() + ",");
			bsql.append(" tax_flg='" + item.getZeikbn() + "',");
			bsql.append(" del_flg = '0'");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND cont_cd = '" + item.getCode() + "'");
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
			// } catch (SQLException sqle) {
			// db.rollback();
			// System.out.println("BizContractor insContractor SQLException" +
			// sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContractor insContractor Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 業者コードで指定された業者情報を削除します。
	 * 
	 * @param 業者コード
	 * @return 処理が正常終了したなら"OK"、異常があった場合には"NG"を返します。
	 */
	public String delContractor(String code) {

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
			bsql.append("UPDATE m_contractor");
			bsql.append(" SET del_flg = '1'");
			bsql.append(" WHERE hospnum = '" + hospnum  + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND cont_cd = '" + code + "'");
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
			// } catch (SQLException sqle) {
			// db.rollback();
			// System.out.println("BizContractor insContractor SQLException" +
			// sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContractor insContractor Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

}

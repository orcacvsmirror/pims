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

import drugstock.batch.OrcaHospNumImport;

/**
 * 月次処理：DB処理
 */

public class BizMonthlyBatch {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizMonthlyBatch() {
	}

	/*
	 * 指定した日付データが存在するかチェックする。 日付：yyyymm
	 */
	public boolean isData(String cDate) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		boolean bResult = false;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT item_no");
			bsql.append(" FROM t_stock");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND yyyymm='" + cDate + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				bResult = true;
			}

		} catch (SQLException sqle) {
			System.out.println("BizMonthlyBatch isData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizMonthlyBatch isData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return bResult;
	}

}

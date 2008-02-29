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
import drugstock.db.OrcaDatabase;

import drugstock.cmn.PropRead;

import drugstock.batch.OrcaHospNumImport;

/**
 * 日レセDBから基本薬剤マスタを在庫管理システムに取込む
 */

public class OrcaDrugImport implements Runnable {

	Connection conn_orca = null;
	Connection conn_drug = null;
	Statement stmt = null;
	Statement stmt_drug = null;
	ResultSet rs = null;

	Thread thread;

	public OrcaDrugImport() {
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
    String sysUser = null;
    String hospnum = null;
		OrcaDatabase db_orca = new OrcaDatabase();
		ComDatabase db_drug = new ComDatabase();
    PropRead prop = new PropRead();

		WaitMsg wait = new WaitMsg();
		wait.setMsg1("日レセ薬剤マスタを取込中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();

    sysUser = prop.getProp("orca_sysuser");

		try {

      conn_orca = db_orca.getConnection();

		  OrcaHospNumImport hosp = new OrcaHospNumImport();
      hospnum = hosp.getHospNum();

			StringBuffer bsql = new StringBuffer(256);
			bsql.append("SELECT srycd, ten, taniname, name, kananame, yukostymd, yukoedymd, yakkakjncd, ykzkbn");
			bsql.append(" FROM tbl_tensu");
			bsql.append(" WHERE ten Is Not Null");
			bsql.append(" AND srycd > '600000000'");
			bsql.append(" AND srycd < '700000000'");
			// 単位がない薬剤が存在するため、単位がなくても取り込む
			// bsql.append(" AND taniname <> ''");
			// bsql.append(" And taniname Is Not Null");
			bsql.append(" AND yakkakjncd<>''");
			bsql.append(" And yakkakjncd Is Not Null");
			bsql.append(" AND hospnum = '" + hospnum + "'");

			sql = bsql.toString();
			stmt = conn_orca.createStatement();
			rs = stmt.executeQuery(sql);

			conn_drug = db_drug.getConnection();
			db_drug.bigin();
			bsql.delete(0, bsql.length());
			// 独自薬剤マスタ以外を消去
			bsql.append("DELETE FROM m_orca_medicine");
			bsql.append(" WHERE kousei_cd != 'XXX'");
			bsql.append(" AND hospnum = '" + hospnum + "'");
			db_drug.execute(bsql.toString());
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO m_orca_medicine VALUES (");
				bsql.append("'" + rs.getString("srycd") + "',");
				bsql.append("'" + rs.getString("yukostymd") + "',");
				bsql.append("'" + rs.getString("yukoedymd") + "',");
				bsql.append("'" + rs.getString("name") + "',");
				bsql.append("'" + rs.getString("kananame") + "',");
				bsql.append("'" + rs.getString("taniname") + "',");
				bsql.append("'0" + rs.getString("ykzkbn") + "',");
				bsql.append(rs.getString("ten") + ",");
				bsql.append("'" + rs.getString("yakkakjncd") + "',");
				bsql.append("'" + hospnum + "'");
				bsql.append(")");
				db_drug.execute(bsql.toString());
			}
			db_drug.commit();
			conn_drug.close();
			db_drug.close();

		} catch (SQLException sqle) {
			if (db_drug != null) {
				db_drug.rollback();
			}
			System.out.println("OrcaDrugImport run SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			if (db_drug != null) {
				db_drug.rollback();
			}
			System.out.println("OrcaDrugImport run Exception" + e.toString());
		} finally {
			db_orca.closeAllResource(rs, stmt, conn_orca);
			wait.destroy();
		}
	}
}

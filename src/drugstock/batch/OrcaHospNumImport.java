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

//import drugstock.cmn.WaitMsg;
//import drugstock.db.ComDatabase;
import drugstock.db.OrcaDatabase;

import drugstock.cmn.PropRead;

/**
 * 日レセDBから基本薬剤マスタを在庫管理システムに取込む
 */

public class OrcaHospNumImport {

	Connection conn_orca = null;
	//Connection conn_drug = null;
	Statement stmt = null;
	//Statement stmt_drug = null;
	ResultSet rs = null;

  String hospNum = null;

  public String getHospNum(){
		String sql = null;
    String sysUser = null;
		OrcaDatabase db_orca = new OrcaDatabase();
    PropRead prop = new PropRead();

    sysUser = prop.getProp("orca_sysuser");

		try {
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			conn_orca = db_orca.getConnection();
			bsql.append("SELECT hospnum");
			bsql.append(" FROM tbl_sysuser");
			bsql.append(" WHERE userid = ");
			bsql.append("'" + sysUser + "'");

			sql = bsql.toString();

			stmt = conn_orca.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
        hospNum = rs.getString("hospnum");
      }
      if (hospNum == null ) {
        System.out.println("Hospnum error:" + hospNum);
        hospNum = "1";
        System.out.println("Hospnum set:1");
      }
      System.out.println(hospNum);
    } catch (Exception e) {
      System.out.println("OrcaDrugImport run Exception" + e.toString());
    } finally {
      db_orca.closeAllResource(rs, stmt, conn_orca);
    }
    return hospNum;
  }
}

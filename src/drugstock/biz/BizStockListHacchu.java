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
import drugstock.cmn.Sprintf;
import drugstock.db.ComDatabase;
import drugstock.model.CodeName;
import drugstock.model.StockListHacchuMdl;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「薬剤発注リスト」DB処理
 */

public class BizStockListHacchu {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
  String hospnum = null;

	public BizStockListHacchu() {
	}

	/**
	 * 入力条件から、帳票：「薬剤発注リスト」出力用のモデルを返します。
	 * 
	 * @param strNowDate
	 *            現在の日付
	 * @param contKindArr
	 *            業者指定
	 * @param itemKindArr
	 *            薬剤区分指定
	 * @return 薬剤発注リスト用モデルの配列
	 */
	public StockListHacchuMdl[] getListData(String strNowDate,
	        CodeName[] contKindArr, SyuruiCdNm[] itemKindArr) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListHacchuMdl cItem[] = null;
		String sql = null;
		String itemNo = null;

		String sZaiko;
		String sWork;
		double dWork;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);

			PropRead prop = new PropRead();
			String down_to_decimal = prop.getProp("down_to_decimal");
			if (down_to_decimal == null)
				down_to_decimal = "0";
			if (down_to_decimal.equals("1") == false)
				down_to_decimal = "0";
			int intDownDecimal = 2;
			if (down_to_decimal.equals("1")) {
				intDownDecimal = 0;
			}

			// 薬剤マスタの個数をカウント
			int count = 0;

			bsql.delete(0, bsql.length());
			bsql.append("SELECT");
			bsql.append(" count(*) as count");
			bsql.append(" FROM m_cont_item ");
			bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg='0'");
			bsql.append(" AND ( m_cont_item.med_kind1='" + itemKindArr[0].code
			        + "'");
			for (int i = 0; i < itemKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.med_kind1='"
				        + itemKindArr[i].code + "')");
			}
			bsql.append(" )");
			bsql.append(" AND ( m_cont_item.cont_id='" + contKindArr[0].getid()
			        + "'");
			for (int i = 0; i < contKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.cont_id='"
				        + contKindArr[i].getid() + "')");
			}
			bsql.append(" )");
			bsql.append(" AND m_cont_item.hacchu_p IS NOT null");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				count = Integer.parseInt(rs.getString("count"));
			}
			rs.close();
			stmt.close();

			// まず、全薬剤マスタの品番と発注点を列挙
			int j = 0;
			String item_no[] = new String[count];
			String hacchuP[] = new String[count];
			String cont_id[] = new String[count];

			bsql.delete(0, bsql.length());
			bsql.append("SELECT");
			bsql.append(" m_cont_item.item_no as item_no,");
			bsql.append(" m_cont_item.hacchu_p as hacchuP,");
			bsql.append(" m_cont_item.cont_id as cont_id");
			bsql.append(" FROM m_cont_item ");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg='0'");
			bsql.append(" AND ( m_cont_item.med_kind1='" + itemKindArr[0].code
			        + "'");
			for (int i = 0; i < itemKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.med_kind1='"
				        + itemKindArr[i].code + "')");
			}
			bsql.append(" )");
			bsql.append(" AND ( m_cont_item.cont_id='" + contKindArr[0].getid()
			        + "'");
			for (int i = 0; i < contKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.cont_id='"
				        + contKindArr[i].getid() + "')");
			}
			bsql.append(" )");
			bsql.append(" AND m_cont_item.hacchu_p IS NOT null");
			bsql.append(" ORDER BY m_cont_item.med_kind1");
			sql = bsql.toString();
			// System.out.println(sql) ;
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				item_no[j] = rs.getString("item_no");
				hacchuP[j] = rs.getString("hacchuP");
				cont_id[j] = rs.getString("cont_id");
				j++;
			}
			rs.close();
			stmt.close();

			// 薬剤量が発注点未満の薬剤を選択し、該当薬剤の情報を出力
			ArrayList aList = new ArrayList();

			for (int i = 0; i < item_no.length; i++) {
				boolean isHacchuP = false;

				StockListHacchuMdl wk = new StockListHacchuMdl();

				double stockNum = 0.0;
				bsql.delete(0, bsql.length());
				bsql.append("SELECT t_stock_invent.stock_truth as stock_truth");
				bsql.append(" FROM t_stock_invent ");
				bsql.append(" WHERE t_stock_invent.hospnum = '" + hospnum + "'");
				bsql.append(" AND t_stock_invent.item_no = '" + item_no[i]
				        + "' ");
				bsql.append(" AND t_stock_invent.yyyymm = ");
				//bsql.append(" (SELECT MAX(yyyymm) FROM t_stock_invent) ");
				bsql.append(" (SELECT MAX(yyyymm) FROM t_stock_invent");
				bsql.append(" WHERE t_stock_invent.hospnum = '" + hospnum + "')");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					wk.setStockNum(rs.getString("stock_truth"));
					stockNum = Double.parseDouble(rs.getString("stock_truth"));
					double tmpHacchuP = Double.parseDouble(hacchuP[i]);
					// 薬剤量が発注点未満かどうか判別
					if (tmpHacchuP > stockNum) {
						isHacchuP = true;
					}
				}
				rs.close();
				stmt.close();
        
				if (isHacchuP) {

					bsql.delete(0, bsql.length());
					bsql.append("SELECT m_cont_item.cont_id as cont_id,");
					bsql.append(" m_cont_item.med_nm as med_nm,");
					bsql.append(" m_cont_item.med_kind1 as med_kind1");
					bsql.append(" FROM m_cont_item ");
					bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum +  "'");
					bsql.append(" AND m_cont_item.item_no = '" + item_no[i]
					        + "' ");
					sql = bsql.toString();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						wk.setNum(item_no[i]);
						wk.setHacchuP(hacchuP[i]);
						wk.setCont(cont_id[i]);
						wk.setName(rs.getString("med_nm"));
						wk.setMedKind(rs.getString("med_kind1"));
					}
					rs.close();
					stmt.close();

					double unitPrice = 0.0;
					bsql.delete(0, bsql.length());
					bsql.append("SELECT t_stock.unit_price as unit_price");
					bsql.append(" FROM t_stock ");
					bsql.append(" WHERE t_stock.hospnum = '" + hospnum + "'");
					bsql.append(" AND t_stock.item_no = '" + item_no[i]
					        + "' ");
					bsql.append(" AND t_stock.yyyymm = ");
					//bsql.append(" (SELECT MAX(yyyymm) FROM t_stock) ");
					bsql.append(" (SELECT MAX(yyyymm) FROM t_stock ");
					bsql.append(" WHERE t_stock.hospnum = '" + hospnum + "')");
					sql = bsql.toString();
					sql = bsql.toString();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						unitPrice = Double.parseDouble(rs.getString("unit_price"));
					}
					rs.close();
					stmt.close();

					stockNum *= unitPrice;
					wk.setStockPrice(Sprintf.format(12, intDownDecimal,
					        stockNum));

					aList.add(wk);
				}
			}

			cItem = new StockListHacchuMdl[aList.size()];
			cItem = (StockListHacchuMdl[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizStockListHacchu getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListHacchu getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

}

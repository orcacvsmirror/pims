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
import drugstock.model.DeadStockListMdl;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「デッドストックリスト」DB処理
 */

public class BizDeadStockList {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
  String hospnum = null;

	public BizDeadStockList() {
	}

	/**
	 * 入力条件から、帳票：「デッドストックリスト」出力用のモデルを返します。
	 * 
	 * @param contKindArr
	 *            業者指定
	 * @param itemKindArr
	 *            薬剤区分指定
	 * @param strDeadDate
	 *            日付指定
	 * @return デッドストックリスト用モデルの配列
	 */
	public DeadStockListMdl[] getListData(String strNowDate,
	        CodeName[] contKindArr, SyuruiCdNm[] itemKindArr, String strDeadDate) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		DeadStockListMdl cItem[] = null;
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
			bsql.append(" WHERE hospnum='" + hospnum + "'");
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
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				count = Integer.parseInt(rs.getString("count"));
			}
			rs.close();
			stmt.close();

			// まず、全薬剤マスタの品番とORCA番号を列挙
			int j = 0;
			String item_no[] = new String[count];
			String orca_med_cd[] = new String[count];
			String cont_id[] = new String[count];

			bsql.delete(0, bsql.length());
			bsql.append("SELECT");
			bsql.append(" m_cont_item.item_no as item_no,");
			bsql.append(" m_cont_item.orca_med_cd as orca_med_cd,");
			bsql.append(" m_cont_item.cont_id as cont_id");
			bsql.append(" FROM m_cont_item ");
			bsql.append(" WHERE m_cont_item.hospnum='" + hospnum + "'");
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
			bsql.append(" ORDER BY m_cont_item.med_kind1");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				item_no[j] = rs.getString("item_no");
				orca_med_cd[j] = rs.getString("orca_med_cd");
				cont_id[j] = rs.getString("cont_id");
				j++;
			}
			rs.close();
			stmt.close();

			// 最新の出庫日付と自動出庫日付を比較、新しい日付を取得
			String exp_date[] = new String[count];
			// item_no = item_all_no ;

			for (int i = 0; i < item_no.length; i++) {
				bsql.delete(0, bsql.length());
				bsql.append("SELECT");
				bsql.append(" MAX(t_expend.exp_date) as exp_date");
				bsql.append(" FROM t_expend ");
				bsql.append(" WHERE t_expend.hospnum = '" + hospnum + "'");
				bsql.append(" AND t_expend.orca_med_cd = '" + orca_med_cd[i]
				        + "' ");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					exp_date[i] = rs.getString("exp_date");
				}
				rs.close();
				stmt.close();
				if ((exp_date[i] == null) || (exp_date[i].equals(""))) {
					exp_date[i] = "0";
				}

				String tmp_stock = null;
				bsql.delete(0, bsql.length());
				bsql.append("SELECT");
				bsql.append(" MAX(t_stocking.stc_date) as stc_date");
				bsql.append(" FROM t_stocking ");
				bsql.append(" WHERE t_stocking.hospnum = '" + hospnum + "'");
				bsql.append(" AND t_stocking.item_no = '" + item_no[i]
				                + "' ");
				bsql.append(" AND t_stocking.stc_cd = '2'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					tmp_stock = rs.getString("stc_date");
				}
				rs.close();
				stmt.close();
				if ((tmp_stock == null) || (tmp_stock.equals(""))) {
					tmp_stock = "0";
				}

				if (Integer.parseInt(exp_date[i]) < Integer.parseInt(tmp_stock)) {
					exp_date[i] = tmp_stock;
				}

				// System.out.println(exp_date[i]) ;
			}

			// 指定期間内に出庫していない薬剤を選択し、該当薬剤の情報を出力
			ArrayList aList = new ArrayList();

			for (int i = 0; i < item_no.length; i++) {
				if (Integer.parseInt(exp_date[i]) < Integer.parseInt(strDeadDate)) {
					DeadStockListMdl wk = new DeadStockListMdl();
					// System.out.println(item_no[i]) ;
					bsql.delete(0, bsql.length());
					bsql.append("SELECT ");
					// bsql.append(" m_cont_item.cont_id as cont_id,");
					bsql.append(" m_cont_item.med_nm as med_nm,");
					bsql.append(" m_cont_item.med_kind1 as med_kind1");
					bsql.append(" FROM m_cont_item ");
					bsql.append(" WHERE ( m_cont_item.hospnum='" + hospnum + "'");
					bsql.append(" AND m_cont_item.med_kind1='"
					        + itemKindArr[0].code + "'");
					for (int k = 0; k < itemKindArr.length; k++) {
						bsql.append(" OR ( m_cont_item.med_kind1='"
						        + itemKindArr[k].code + "')");
					}
					bsql.append(" )");
					// bsql.append(" AND ( m_cont_item.cont_id='" +
					// contKindArr[0].getid() + "'" ) ;
					// for( int k=0; k< contKindArr.length ;k++ ){
					// bsql.append(" OR ( m_cont_item.cont_id='" +
					// contKindArr[k].getid() + "')");
					// }
					// bsql.append(" )") ;

					bsql.append(" AND m_cont_item.item_no = '" + item_no[i]
					        + "' ");
					sql = bsql.toString();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						wk.setNum(item_no[i]);
						wk.setName(rs.getString("med_nm"));
						wk.setCont(cont_id[i]);
						wk.setMedKind(rs.getString("med_kind1"));
					}
					rs.close();
					stmt.close();

					double stockNum = 0.0;
					bsql.delete(0, bsql.length());
					bsql.append("SELECT t_stock_invent.stock_truth as stock_truth");
					bsql.append(" FROM t_stock_invent ");
					bsql.append(" WHERE t_stock_invent.hospnum = '" + hospnum + "'");
					bsql.append(" AND t_stock_invent.item_no = '"
					        + item_no[i] + "' ");
					bsql.append(" AND t_stock_invent.yyyymm = ");
					bsql.append(" (SELECT MAX(yyyymm) FROM t_stock_invent) ");
					sql = bsql.toString();
					// System.out.println(sql) ;
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					if (rs.next()) {
						wk.setDate(exp_date[i]);
						wk.setStockNum(rs.getString("stock_truth"));
						stockNum = Double.parseDouble(rs.getString("stock_truth"));
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
					bsql.append(" AND t_stock.yyyymm = '"
					        + strNowDate.substring(0, 6) + "'");
					// bsql.append(" (SELECT MAX(yyyymm) FROM t_stock) ");
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

			cItem = new DeadStockListMdl[aList.size()];
			cItem = (DeadStockListMdl[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizDeadStockList getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizDeadStockList getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

}

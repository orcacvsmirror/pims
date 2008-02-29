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
import drugstock.model.CodeName;
import drugstock.model.StockListMedMasterMdl;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「薬剤マスタ一覧表」DB処理
 */

public class BizStockListMedMaster {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
	String hospnum = null;

	public BizStockListMedMaster() {
	}

	/**
	 * 入力条件から、帳票：「薬剤マスタ一覧表」出力用のモデルを返します。
	 * 
	 * @param strNowDate
	 *            現在の日付指定
	 * @param contKindArr
	 *            業者指定
	 * @param itemKindArr
	 *            薬剤区分指定
	 * @return 薬剤マスタ一覧表用モデルの配列
	 */
	public StockListMedMasterMdl[] getListData(String strNowDate,
	        CodeName[] contKindArr, SyuruiCdNm[] itemKindArr) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListMedMasterMdl cItem[] = null;
		String sql = null;
		String itemNo = null;

		String sZaiko;
		String sWork;
		double dWork;

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

			StringBuffer bsql = new StringBuffer(256);

			ArrayList aList = new ArrayList();

			bsql.delete(0, bsql.length());
			bsql.append("SELECT m_cont_item.item_no as item_no,");
			bsql.append(" m_cont_item.med_nm as med_nm,");
			bsql.append(" m_orca_medicine.unit_nm as unit_nm,");
			bsql.append(" m_orca_medicine.med_price as med_price,");
			bsql.append(" m_cont_item.cont_id as cont_id, ");
			bsql.append(" m_cont_item.med_kind1 as med_kind1");
			bsql.append(" FROM m_cont_item ");
			bsql.append(" INNER JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd ");
			// bsql.append(" INNER JOIN m_contractor_in_item ON
			// m_cont_item.item_no = m_contractor_in_item.item_no");
			bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND  m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND  (m_cont_item.med_kind1='"
			        + itemKindArr[0].code + "'");
			for (int i = 0; i < itemKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.med_kind1='"
				        + itemKindArr[i].code + "')");
			}
			bsql.append(" )");
			// bsql.append(" AND m_cont_item.cont_id='" + contKind + "'");
			bsql.append(" AND ( m_cont_item.cont_id='" + contKindArr[0].getid()
			        + "'");
			for (int i = 0; i < contKindArr.length; i++) {
				bsql.append(" OR ( m_cont_item.cont_id='"
				        + contKindArr[i].getid() + "')");
			}
			bsql.append(" )");

			bsql.append(" AND m_cont_item.del_flg='0'");
			// 薬剤の有効期限を無視し、かつ薬剤の重複を避ける04.07.14 onuki
			// bsql.append(" AND m_orca_medicine.day_from<='" + strNowDate + "'
			// AND '" + strNowDate + "'<=m_orca_medicine.day_to");
			bsql.append(" AND m_orca_medicine.day_from<='" + strNowDate + "'");
			bsql.append(" AND m_orca_medicine.day_to=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			bsql.append(" WHERE m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");

			bsql.append(" ORDER BY m_cont_item.med_kind1,");
			// 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる) 04.04.08 onuki
			if (stock_med_order.equals("0")) {
				bsql.append(" m_cont_item.item_no");
			} else {
				bsql.append(" m_cont_item.med_kn");
			}
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StockListMedMasterMdl wk = new StockListMedMasterMdl();
				wk.setNum(rs.getString("item_no"));
				wk.setName(rs.getString("med_nm"));
				wk.setUnit(rs.getString("unit_nm"));
				wk.setCont(rs.getString("cont_id"));
				wk.setMedKind(rs.getString("med_kind1"));

				dWork = rs.getDouble("med_price");

				if (dWork == 0)
					wk.setStockPrice("-");
				else
					wk.setStockPrice(String.valueOf(dWork));

				aList.add(wk);
			}
			cItem = new StockListMedMasterMdl[aList.size()];
			cItem = (StockListMedMasterMdl[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizStockListMedMaster getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListMedMaster getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

}

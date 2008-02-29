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

/* **************************************************************************************
 *
 * 更新履歴：　2004.12.08 Onuki
 *                伝票入力に部門出庫は含まれないので除外
 *             2006.04.13 Hasegawa
 *                薬価変更があった薬剤が変更前の月の表示／印刷で抜ける現象を修正した
 *                同一薬剤を複数業者で扱う場合のDB取込時の重複の排他処理をSQL文で吸収
 *                するように修正した
 *
 *
 *
 ************************************************************************************** */

package drugstock.biz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import drugstock.db.ComDatabase;
import drugstock.model.CodeName;
import drugstock.model.StockListDenpyoDetailMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「仕入先別仕入伝票一覧表」DB処理
 */

public class BizStockListDenpyo {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;

	public BizStockListDenpyo() {
	}

	/**
	 * 入力条件から、帳票：「仕入先別伝票一覧」出力用のモデルを返します。
	 * 
	 * @param k_FromDate
	 *            集計開始日付指定
	 * @param k_ToDate
	 *            集計終了日付指定
	 * @param contKindArr
	 *            業者指定
	 * @return 仕入先別伝票一覧用モデルの配列
	 */
	public StockListDenpyoDetailMdl[] getListData(String k_FromDate,
	        String k_ToDate, CodeName[] contKindArr) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListDenpyoDetailMdl cItem[] = null;
		String sql = null;

		// 仕入区分ワーク
		int w_stccd;
		// 伝票ワーク
		String crdname1 = "仕入";
		String crdname2 = "部門出庫";
		String crdname3 = "部門返品";
		String crdname4 = "廃棄";
		String crdname5 = "値引き";
		String crdname8 = "調整";
		String crdname9 = "返品";

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			ArrayList aList = new ArrayList();

			bsql.delete(0, bsql.length());

			bsql.append("SELECT t_stocking.cont_id as cont_id, t_stocking.stc_id as stc_id,");
			bsql.append(" t_stocking.stc_date as stc_date, t_stocking.stc_cd as stc_cd,");
			bsql.append(" t_stocking.item_no as item_no, t_stocking.stc_num as stc_num,");
			bsql.append(" t_stocking.amount as amount, t_stocking.discount as discount,");
			bsql.append(" t_stocking.stc_amount as stc_amount, t_stocking.tax as tax,");
			bsql.append(" t_stocking.pack3_num as pack3_num, t_stocking.pack2_num as pack2_num,");
			bsql.append(" t_stocking.pack1_num as pack1_num, m_cont_item.med_nm as med_nm,");
			bsql.append(" t_stocking.slip_no as slip_no,");
			bsql.append(" t_stocking.cont_id as cont_id,");
			bsql.append(" m_cont_item.pack_unit3 as pack_unit3, m_cont_item.pack_unit2 as pack_unit2,");
			bsql.append(" m_cont_item.pack_unit1 as pack_unit1, m_orca_medicine.unit_nm as unit_nm,");
			bsql.append(" m_contractor.cont_nm as cont_nm");
			bsql.append(" FROM ((t_stocking LEFT JOIN m_cont_item ON t_stocking.cont_id = m_cont_item.cont_id AND ");
			bsql.append(" t_stocking.item_no = m_cont_item.item_no) ");
			bsql.append(" LEFT JOIN m_contractor ON m_cont_item.cont_id = m_contractor.cont_id) ");
			bsql.append(" LEFT JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd");
			// 業者コード取得
			bsql.append(" WHERE t_stocking.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_contractor.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND ( t_stocking.cont_id='"
			        + contKindArr[0].getid() + "'");
			for (int i = 0; i < contKindArr.length; i++) {
				bsql.append(" OR ( t_stocking.cont_id='"
				        + contKindArr[i].getid() + "')");
			}
			bsql.append(" )");

			bsql.append(" AND t_stocking.stc_date>='" + k_FromDate + "'");
			bsql.append(" AND t_stocking.stc_date<='" + k_ToDate + "'");
			bsql.append(" AND m_cont_item.del_flg='0'");
			// 2006.04.13 Hasegawa以下3行修正 集計月時の期間に適用されていた薬剤情報を取得する
			bsql.append(" AND m_orca_medicine.day_from<=t_stocking.stc_date ");
			bsql.append(" AND m_orca_medicine.day_to>=t_stocking.stc_date ");
			bsql.append(" AND m_orca_medicine.day_to<=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			// bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");
			bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "')");

			bsql.append(" AND t_stocking.del_flg='0'");
			// 伝票入力のみ有効
			bsql.append(" AND (t_stocking.stc_cd='1'");
			// 伝票入力に部門出庫は含まれないので除外する。
			bsql.append(" OR t_stocking.stc_cd='5'");
			bsql.append(" OR t_stocking.stc_cd='9')");

			bsql.append(" ORDER BY t_stocking.stc_date, m_cont_item.med_kn");

			sql = bsql.toString();
			// System.out.println(sql);

			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int SeqNo = 0;
			while (rs.next()) {
				StockListDenpyoDetailMdl wk = new StockListDenpyoDetailMdl();
				SeqNo++;
				wk.setBytesStcNo(rs.getString("slip_no"));
				wk.setBytesStcDate(rs.getString("stc_date"));
				wk.setBytesItemNum(rs.getString("item_no"));
				wk.setBytesItemName(rs.getString("med_nm"));
				wk.setBytesUnitName(rs.getString("unit_nm"));
				wk.setBytesUnit3Num(rs.getString("pack3_num"));
				wk.setBytesUnit3(rs.getString("pack_unit3"));
				wk.setBytesUnit2Num(rs.getString("pack2_num"));
				wk.setBytesUnit2(rs.getString("pack_unit2"));
				wk.setBytesUnit1Num(rs.getString("pack1_num"));
				wk.setBytesStcNum(rs.getString("stc_num"));
				wk.setBytesAmnt(rs.getString("amount"));
				wk.setBytesDiscnt(rs.getString("discount"));
				wk.setBytesTaxAmnt(rs.getString("tax"));
				wk.setBytesStcAmnt(rs.getString("stc_amount"));
				wk.setBytesCont(rs.getString("cont_id"));
				w_stccd = Integer.parseInt(rs.getString("stc_cd"));
				switch (w_stccd) {
				case 1:
					wk.setBytesCrdName(crdname1);
					break;
				case 2:
					wk.setBytesCrdName(crdname2);
					break;
				case 3:
					wk.setBytesCrdName(crdname3);
					break;
				case 4:
					wk.setBytesCrdName(crdname4);
					break;
				case 5:
					wk.setBytesCrdName(crdname5);
					break;
				case 8:
					wk.setBytesCrdName(crdname8);
					break;
				case 9:
					wk.setBytesCrdName(crdname9);
					break;
				}
				aList.add(wk);
			}
			cItem = new StockListDenpyoDetailMdl[aList.size()];
			cItem = (StockListDenpyoDetailMdl[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizStockListDenpyo getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListDenpyo getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}
}

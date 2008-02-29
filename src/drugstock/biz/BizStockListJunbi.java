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
 * 更新履歴  : 2004.XX.XX Onuki
 *               薬剤の有効期限を無視し、かつ薬剤の重複を避ける。
 *             2006.04.13 Hasegawa
 *               上記修正により、薬価変更があった薬剤が変更前の月の表示／印刷で抜ける
 *               現象を修正した。
 *            2006.04.26 Hasegawa
 *               有効期間が切れた薬剤に関しても、在庫として残っている場合は表示するように
 *               修正した。
 *
 *               
 **************************************************************************************** */

package drugstock.biz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;
import drugstock.db.ComDatabase;
import drugstock.model.MedicineSubInfo;
import drugstock.model.StockListJunbiMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「棚卸準備一覧表」DB処理
 */

public class BizStockListJunbi {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
	String hospnum = null;

	public BizStockListJunbi() {
	}

	/**
	 * 入力条件から、帳票：「棚卸準備一覧表」出力用のモデルを返します。
	 * 
	 * @param yyyymm
	 *            年月指定
	 * @param drugKind
	 *            薬剤区分指定
	 * @return 棚卸準備一覧表用モデルの配列
	 * @see "2006.04.26 Hasegawa 修正"
	 */
	public StockListJunbiMdl[] getListData(String yyyymm, String drugKind) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListJunbiMdl cItem[] = null;
		MedicineSubInfo cYaku[] = null;
		String sql = null;
		String sql1 = null;
		String itemNo = null;
		String nowDate = yyyymm + "01";

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

			StringBuffer bsql = new StringBuffer(256);// 棚卸一覧取得用ＳＱＬ文
			StringBuffer bsqlsub = new StringBuffer(256);// 薬剤情報取得用ＳＱＬ文

			ArrayList aList = new ArrayList();
			ArrayList aListSub = new ArrayList(); // 薬剤情報

			// ＤＢ＞薬剤情報取得ＳＱＬ文作成
			bsqlsub.delete(0, bsql.length());
			bsqlsub.append("SELECT ");
			bsqlsub.append("m_cont_item.item_no AS ITEM_NO,");
			bsqlsub.append("m_orca_medicine.med_nm AS NAME,");
			bsqlsub.append("m_orca_medicine.unit_nm AS UNIT,");
			bsqlsub.append("m_orca_medicine.med_kind AS MED_KIND,");
			bsqlsub.append("m_orca_medicine.day_from AS KIKAN_STR,");
			bsqlsub.append("m_orca_medicine.day_to AS KIKAN_END,");
			bsqlsub.append("m_orca_medicine.med_price AS PRICE ");
			bsqlsub.append("FROM m_cont_item INNER JOIN m_orca_medicine ");
			bsqlsub.append("ON m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsqlsub.append("WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsqlsub.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsqlsub.append(" AND m_cont_item.cont_id = (SELECT MAX(m_cont_item.cont_id) ");
			bsqlsub.append(" from m_cont_item ");
			// bsqlsub.append("WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd )");
			bsqlsub.append("WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsqlsub.append(" AND m_cont_item.hospnum = '" + hospnum + "')");
			bsqlsub.append(" ORDER BY m_cont_item.item_no");

			// 文字列バッファを文字列化
			sql1 = bsqlsub.toString();
			// SQL 文をデータベースに送るための Statement オブジェクトを生成
			stmt1 = conn.createStatement();
			// SQL 文実行
			rs1 = stmt1.executeQuery(sql1);
			// 出力結果抽出
			while (rs1.next()) {

				MedicineSubInfo YakuzaiWs = new MedicineSubInfo();

				YakuzaiWs.setItemNo(rs1.getString("item_no"));
				YakuzaiWs.setMedName(rs1.getString("name"));
				YakuzaiWs.setMedUnit(rs1.getString("unit"));
				YakuzaiWs.setMedKind(rs1.getString("med_kind"));
				YakuzaiWs.setDayStart(rs1.getString("kikan_str"));
				YakuzaiWs.setDayEnd(rs1.getString("kikan_end"));
				YakuzaiWs.setMedPrice(rs1.getString("price"));
				aListSub.add(YakuzaiWs);
			}
			// MedicineSubInfoクラスの配列に変換
			cYaku = new MedicineSubInfo[aListSub.size()];
			cYaku = (MedicineSubInfo[])aListSub.toArray(cYaku);

			// 棚卸準備一覧表取得
			bsql.delete(0, bsql.length());
			bsql.append("SELECT t_stock.item_no as item_no,");
			bsql.append(" m_cont_item.med_nm as med_nm,");
			bsql.append(" t_stock.unit_price as unit_price,");
			bsql.append(" t_stock.stock_num as stock_num,");
			bsql.append(" t_stock.stocking_num as stocking_num, m_orca_medicine.unit_nm as unit_nm,");
			bsql.append(" m_cont_item.pack_unit3 as pack_unit3, m_cont_item.pack_unit2 as pack_unit2, t_stock.med_kind1 as med_kind1");
			bsql.append(" FROM (t_stock INNER JOIN m_cont_item ON t_stock.item_no = m_cont_item.item_no)");
			bsql.append(" INNER JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd");
			// bsql.append(" WHERE t_stock.yyyymm='" + yyyymm + "'");
			bsql.append(" WHERE t_stock.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock.yyyymm='" + yyyymm + "'");
			bsql.append(" AND m_cont_item.del_flg='0'");
			// drugKind="0"のとき：薬剤区分全体指定で条件を外す
			if (drugKind.equals("0") == false) {
				bsql.append(" AND m_cont_item.med_kind1='" + drugKind + "'");
			}
			// 複数業者薬剤の重複を避ける
			bsql.append(" AND m_cont_item.cont_id=");
			bsql.append("(SELECT MAX(cont_id) FROM m_cont_item");
			// bsql.append(" WHERE t_stock.item_no = m_cont_item.item_no)");
			bsql.append(" WHERE t_stock.item_no = m_cont_item.item_no ");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "')");
			// 2006.04.16 Hasegawa以下3行修正 絞り込みのため、有効期限が最終の情報を取得する
			bsql.append(" AND m_orca_medicine.day_to=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			// bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");
			bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "')");
			// drugKind="0"のとき：薬剤区分順に表示
			// if(drugKind.equals("0")==false){
			if (drugKind.equals("0")) {
				bsql.append(" ORDER BY m_cont_item.med_kind1,");
			} else {
				bsql.append(" ORDER BY");
			}
			// 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる)
			if (stock_med_order.equals("0")) {
				bsql.append(" t_stock.item_no");
			} else {
				bsql.append(" m_cont_item.med_kn");
			}
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StockListJunbiMdl wk = new StockListJunbiMdl();
				wk.setItemNum(rs.getString("item_no"));
				wk.setItemName(rs.getString("med_nm"));
				wk.setNowStock(rs.getString("stock_num")); // 当月在庫量
				wk.setUnitName(rs.getString("unit_nm"));
				wk.setBytesUnit3(rs.getString("pack_unit3"));
				wk.setBytesUnit2(rs.getString("pack_unit2"));
				wk.setMedKind(rs.getString("med_kind1"));
				// 単価
				dWork = rs.getDouble("unit_price");

				if (dWork == 0)
					wk.setStockPrice("-");
				else
					wk.setStockPrice(String.valueOf(dWork));

				sWork = rs.getString("stock_num");
				if ((sWork == null) || (sWork.equals("0") == true)) {
					sWork = "-";
					sZaiko = "-";
				} else if (dWork == 0) {
					sZaiko = "-";
				} else {
					dWork = dWork * Double.valueOf(sWork).doubleValue();
					sZaiko = Sprintf.format(8, 0, dWork);
				}
				wk.setUnit1Num(sZaiko);

				aList.add(wk);
			}
			cItem = new StockListJunbiMdl[aList.size()];
			cItem = (StockListJunbiMdl[])aList.toArray(cItem);

			// 2006.04.26 Hasegawa 正しい適用期間の名称と単位を入れる
			{
				Integer Iyymmdd = new Integer(nowDate);

				// 在庫情報レコード回数ループ
				for (int iLoop1 = 0; iLoop1 < aList.size(); iLoop1++) {
					// 薬剤情報レコード分ループ
					for (int iLoop2 = 0; iLoop2 < aListSub.size(); iLoop2++) {
						if (cItem[iLoop1].getStrItemNum().equals(
						        cYaku[iLoop2].getItemNo())) {
							if ((Iyymmdd.intValue() >= cYaku[iLoop2].getIntDayStart())
							        && (Iyymmdd.intValue() <= cYaku[iLoop2].getIntDayEnd())) {
								// □□□□ 有効期間内薬剤 □□□□
								// 薬剤名称セット
								cItem[iLoop1].setItemName(cYaku[iLoop2].getMedName());
								// 単位セット
								cItem[iLoop1].setUnitName(cYaku[iLoop2].getMedUnit());
								// 区分セット
								cItem[iLoop1].setMedKind(cYaku[iLoop2].getMedKind());
								break;
							}
						}
					}
				}
			}
		} catch (SQLException sqle) {
			System.out.println("BizStockListJunbi getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListJunbi getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}
}

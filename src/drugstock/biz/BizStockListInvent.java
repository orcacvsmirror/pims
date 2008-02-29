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
 * 更新履歴  :2004.XX.XX Onuki
 *              薬剤の有効期限を無視し、かつ薬剤の重複を避ける
 *            2004.07.14 onuki
 *              薬価基準が古くなるバグ修正
 *            2006.04.13 Hasegawa
 *              薬価変更があった薬剤が変更前の月の表示／印刷で抜ける現象を修正した。
 *              同一薬剤を複数業者で扱う場合のDB取込時の重複の排他処理をSQL文で吸収
 *              するように修正した。
 *            2006.04.26 Hasegawa
 *              有効期間が切れた薬剤に関しても、在庫として残っている場合は表示するように
 *              修正した。
 *
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
import drugstock.model.StockListInventMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「棚卸一覧表」DB処理
 */

public class BizStockListInvent {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
	String hospnum = null;

	public BizStockListInvent() {
	}

	/**
	 * 入力条件から、帳票：「棚卸一覧表」出力用のモデルを返します。
	 * 
	 * @param yyyymm
	 *            年月指定
	 * @param drugKind
	 *            薬剤区分指定
	 * @return 棚卸一覧表用モデルの配列
	 */
	public StockListInventMdl[] getListData(String yyyymm, String drugKind) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListInventMdl cItem[] = null;
		MedicineSubInfo cYaku[] = null;
		String sql = null;
		String sql1 = null;
		String nowDate = yyyymm + "01";
		double yakkakijyun = 0;
		double zaikotanka = 0;
		double tankaritu = 0;
		double dWork;

		ComDatabase db = new ComDatabase();

		// 単価、金額算出プロパティ取得
		String stock_unit_price_tana;
		PropRead prop = new PropRead();
		stock_unit_price_tana = prop.getProp("stock_unit_price_tana");
		if (stock_unit_price_tana == null)
			stock_unit_price_tana = "0";
		if (stock_unit_price_tana.equals("1") == false)
			stock_unit_price_tana = "0";
		// 薬剤出力並び順プロパティ取得
		String stock_med_order = prop.getProp("stock_med_order");
		if (stock_med_order == null)
			stock_med_order = "0";
		if (stock_med_order.equals("1") == false)
			stock_med_order = "0";

		try {

			// 整数表示
			String down_to_decimal = prop.getProp("down_to_decimal");
			if (down_to_decimal == null)
				down_to_decimal = "0";
			if (down_to_decimal.equals("1") == false)
				down_to_decimal = "0";

			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);// 棚卸一覧取得用ＳＱＬ文
			StringBuffer bsqlsub = new StringBuffer(256);// 薬剤情報取得用ＳＱＬ文

			ArrayList aList = new ArrayList(); // 棚卸一覧
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
			bsqlsub.append("m_orca_medicine.med_price AS PRICE,");
			bsqlsub.append("m_orca_medicine.hospnum AS HOSPNUM ");
			bsqlsub.append("FROM m_cont_item INNER JOIN m_orca_medicine ");
			bsqlsub.append("ON m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsqlsub.append("WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsqlsub.append("AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsqlsub.append("AND m_cont_item.cont_id = (SELECT MAX(m_cont_item.cont_id) ");
			bsqlsub.append("from m_cont_item ");
			bsqlsub.append("WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsqlsub.append("AND m_orca_medicine.hospnum = '" + hospnum + "')");
			bsqlsub.append(" ORDER BY m_cont_item.item_no");

			// System.out.println(bsqlsub.toString());
      //
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

			bsql.delete(0, bsql.length());

			bsql.append("SELECT t_stock_invent.item_no,");
			bsql.append(" m_cont_item.med_nm,");
			bsql.append(" m_cont_item.med_kind1,");
			bsql.append(" m_orca_medicine.unit_nm,");
			bsql.append(" t_stock.unit_price,");
			bsql.append(" t_stock.amount,");
			bsql.append(" t_stock_invent.stock_theory,");
			bsql.append(" t_stock_invent.stock_truth ");
			bsql.append("FROM (( t_stock_invent");
			bsql.append(" LEFT JOIN m_cont_item ON t_stock_invent.item_no = m_cont_item.item_no )");
			bsql.append(" LEFT JOIN t_stock ON t_stock.item_no = m_cont_item.item_no )");
			bsql.append(" LEFT JOIN m_orca_medicine ON m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsql.append("WHERE t_stock_invent.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock_invent.yyyymm = '" + yyyymm + "'");
			bsql.append(" AND t_stock.yyyymm = '" + yyyymm + "'");
			// 2006.04.16 Hasegawa以下3行修正 絞り込みのため、有効期限が最終の情報を取得する
			bsql.append(" AND m_orca_medicine.day_to=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");
			// 同じ薬剤を複数業者で扱っている場合、複数行表示することになるため、業者番号が最大の薬剤マスタを選択する
			bsql.append(" AND m_cont_item.cont_id=(SELECT MAX(cont_id) FROM m_cont_item WHERE t_stock.item_no = m_cont_item.item_no");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "')");

			bsql.append(" AND m_cont_item.del_flg='0'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			// 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる)
			if (stock_med_order.equals("0")) {
				bsql.append(" ORDER BY t_stock.item_no");
			} else {
				bsql.append(" ORDER BY m_cont_item.med_kn");
			}
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StockListInventMdl wk = new StockListInventMdl();
				wk.setNum(rs.getString("item_no"));
				wk.setName(rs.getString("med_nm"));
				wk.setUnit(rs.getString("unit_nm"));
				// 直接、薬剤種別を格納
				wk.setMedKind(rs.getString("med_kind1"));

				dWork = rs.getDouble("unit_price");
				if (dWork == 0)
					wk.setStock("-");
				else
					wk.setStock(String.valueOf(dWork));

        // old
				wk.setZaikoInvent(rs.getString("stock_truth")); // 棚卸在庫
				wk.setZaiko(rs.getString("stock_theory")); // 理論在庫

        // new test
				//wk.setZaikoInvent(rs.getString("stock_theory")); // 理論在庫
				//wk.setZaiko(rs.getString("stock_truth")); // 棚卸在庫

				// 差分＝棚卸−理論
				wk.setZaikoDif(String.valueOf(rs.getDouble("stock_truth")
						- rs.getDouble("stock_theory")));

				// 理論在庫金額
				wk.setKingaku(rs.getString("amount"));

				// 棚卸在庫金額＝単価×棚卸量
				double dTruth = rs.getDouble("stock_truth");
				if ((dWork == 0.0) && (rs.getDouble("stock_theory") != 0)) {
					dWork = rs.getDouble("amount")
					        / rs.getDouble("stock_theory");
				}
				double amountInventDouble = dWork * dTruth;
				// wk.setKingakuInvent(String.valueOf( amountInventDouble ));
				// 整数表示 04.02.12 onuki
				int intDownDecimal = 2;
				if (down_to_decimal.equals("1")) {
					intDownDecimal = 0;
				}
				wk.setKingakuInvent(Sprintf.format(13, intDownDecimal,
				        amountInventDouble));
				// 差分＝棚卸−理論
				wk.setKingakuDif(String.valueOf(amountInventDouble
				        - rs.getDouble("amount")));

				aList.add(wk);

			}
			cItem = new StockListInventMdl[aList.size()];
			cItem = (StockListInventMdl[])aList.toArray(cItem);

			// 2006.04.26 Hasegawa 正しい適用期間の名称と単位を入れる
			{
				Integer Iyymmdd = new Integer(nowDate);

				// 在庫情報レコード回数ループ
				for (int iLoop1 = 0; iLoop1 < aList.size(); iLoop1++) {
					// 薬剤情報レコード分ループ
					for (int iLoop2 = 0; iLoop2 < aListSub.size(); iLoop2++) {
						if (cItem[iLoop1].getStrNum().equals(
						        cYaku[iLoop2].getItemNo())) {
							if ((Iyymmdd.intValue() >= cYaku[iLoop2].getIntDayStart())
							        && (Iyymmdd.intValue() <= cYaku[iLoop2].getIntDayEnd())) {
								// □□□□ 有効期間内薬剤 □□□□
								// 薬剤名称セット
								cItem[iLoop1].setName(cYaku[iLoop2].getMedName());
								// 単位セット
								cItem[iLoop1].setUnit(cYaku[iLoop2].getMedUnit());
								// 区分セット
								cItem[iLoop1].setMedKind(cYaku[iLoop2].getMedKind());
								break;
							}
						}
					}
				}
			}
		} catch (SQLException sqle) {
			System.out.println("BizStockListInevnt getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListInevnt getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}
}

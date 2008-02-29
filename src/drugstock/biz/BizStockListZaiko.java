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
 * 更新履歴	: 2004.04.08 onuki
 *                 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる)
 *            2004.06.23 onuki
 *                 薬価基準が古くなるバグ修正 
 *            2004.07.14 onuki
 *                 薬剤の有効期限を無視し、かつ薬剤の重複を避ける
 *            2006.04.13 Hasegawa
 *                 薬価変更があった薬剤が変更前の月の表示／印刷で抜ける現象を修正した
 *                 同一薬剤を複数業者で扱う場合のDB取込時の重複の排他処理をSQL文で吸収
 *                 するように修正した
 *            2006.04.26 Hasegawa
 *                 有効期間が切れた薬剤についても在庫があれば、それを表示して、薬価と
 *                 単価率を「-」表示にする。
 *
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

import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;
import drugstock.db.ComDatabase;
import drugstock.model.MedicineSubInfo;
import drugstock.model.StockListZaikoMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「在庫一覧表」DB処理
 */

public class BizStockListZaiko {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
  String hospnum = null;

	public BizStockListZaiko() {
	}

	/**
	 * 入力条件から、帳票：「在庫一覧表」出力用のモデルを返します。
	 * 
	 * @param yyyymm
	 *            年月指定
	 * @param drugKind
	 *            薬剤指定
	 * @return 在庫一覧表用モデルの配列
	 * @see "2006.04.26 Hasegawa 更新"
	 */
	public StockListZaikoMdl[] getListData(String yyyymm, String drugKind) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListZaikoMdl cItem[] = null;
		MedicineSubInfo cYaku[] = null;
		String sql = null;
		String sql1 = null;
		String itemNo = "";
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
		if (stock_unit_price_tana == null || ! stock_unit_price_tana.equals("1")) {
			stock_unit_price_tana = "0";
		}
		// 薬剤出力並び順プロパティ取得
		String stock_med_order = prop.getProp("stock_med_order");
		if (stock_med_order == null || ! stock_med_order.equals("1")) {
			stock_med_order = "0";
		}

		try {

			// 整数表示 04.02.12 onuki
			String down_to_decimal = prop.getProp("down_to_decimal");
			if (down_to_decimal == null || ! down_to_decimal.equals("1")) {
				down_to_decimal = "0";
			}

			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);// 在庫情報取得用ＳＱＬ文
			StringBuffer bsqlsub = new StringBuffer(256);// 薬剤情報取得用ＳＱＬ文

			ArrayList aList = new ArrayList(); // 在庫一覧表
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
			bsqlsub.append("WHERE m_cont_item.hospnum ='" + hospnum + "'");
			bsqlsub.append(" AND m_orca_medicine.hospnum ='" + hospnum + "'");
			bsqlsub.append(" AND m_cont_item.cont_id = (SELECT MAX(m_cont_item.cont_id) ");
			bsqlsub.append("from m_cont_item ");
			// bsqlsub.append("WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd )");
			bsqlsub.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsqlsub.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsqlsub.append(" AND m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd )");
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

			// ＤＢ＞在庫情報取得
			bsql.delete(0, bsql.length());
			bsql.append(" SELECT t_stock.item_no as item_no, m_cont_item.med_nm as med_nm,");
			bsql.append(" m_cont_item.med_kind1 as med_kind1,");
			bsql.append(" m_orca_medicine.unit_nm as unit_nm, m_orca_medicine.med_price as med_price,");
			bsql.append(" t_stock.unit_price as unit_price,");
			bsql.append(" t_stock.before_stock as before_stock, t_stock.stocking_num as stocking_num,");
			bsql.append(" t_stock.expend_num as expend_num, t_stock.back_num as back_num,");
			bsql.append(" t_stock.adjust_num as adjust_num, t_stock.stock_num as stock_num,");
			bsql.append(" t_stock.amount as amount,");
			bsql.append(" t_stock_invent.stock_truth as stock_truth");
			bsql.append(" FROM ((t_stock LEFT JOIN m_cont_item ON t_stock.item_no = m_cont_item.item_no)");
			bsql.append(" LEFT JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd)");
			bsql.append(" LEFT JOIN t_stock_invent ON m_cont_item.item_no = t_stock_invent.item_no");
			//bsql.append(" WHERE t_stock.yyyymm='" + yyyymm + "'");
			bsql.append(" WHERE t_stock.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock_invent.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock.yyyymm='" + yyyymm + "'");
			bsql.append(" AND t_stock_invent.yyyymm='" + yyyymm + "'");
			bsql.append(" AND t_stock.del_flg='0'");
			// 2006.04.24 Hasegawa 以下3行修正
			bsql.append(" AND m_orca_medicine.day_to=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "')");
			// 同じ薬剤を複数業者で扱っている場合、複数行表示することになるため、業者番号が最大の薬剤マスタを選択する
			// bsql.append(" AND m_cont_item.cont_id=(SELECT MAX(cont_id) FROM m_cont_item WHERE t_stock.item_no = m_cont_item.item_no)");
			bsql.append(" AND m_cont_item.cont_id=(SELECT MAX(cont_id) FROM m_cont_item WHERE t_stock.item_no = m_cont_item.item_no");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "')");
			// 帳票出力；品番順を薬剤番号順／カナ名順に変更(設定ファイルによる) 04.04.08 onuki
			if (stock_med_order.equals("0")) {
				bsql.append(" ORDER BY t_stock.item_no");
			} else {
				bsql.append(" ORDER BY m_cont_item.med_kn");
			}
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				StockListZaikoMdl wk = new StockListZaikoMdl();
				wk.setNum(rs.getString("item_no"));
				wk.setName(rs.getString("med_nm"));
				wk.setUnit(rs.getString("unit_nm"));
				wk.setBase(rs.getString("med_price"));

				dWork = rs.getDouble("unit_price");
				if (dWork == 0)
					wk.setStock("-");
				else
					wk.setStock(String.valueOf(dWork));

				yakkakijyun = rs.getDouble("med_price");
				zaikotanka = rs.getDouble("unit_price");
				tankaritu = (zaikotanka / yakkakijyun) * 100;
				wk.setRate(Sprintf.format(5, 1, tankaritu));
				wk.setKurikosi(rs.getString("before_stock"));
				wk.setNyuko(rs.getString("stocking_num"));
				wk.setHarai(rs.getString("expend_num"));
				wk.setHenpin(rs.getString("back_num"));
				wk.setTyousei(rs.getString("adjust_num"));
				wk.setZaiko(rs.getString("stock_num"));

				// 直接、薬剤種別を格納
				wk.setMedKind(rs.getString("med_kind1"));

				// 整数表示 04.02.12 onuki
				int intDownDecimal = 2;
				if (down_to_decimal.equals("1")) {
					intDownDecimal = 0;
				}
				wk.setKingaku(Sprintf.format(13, intDownDecimal, rs.getDouble("amount")));

				aList.add(wk);
			}
			cItem = new StockListZaikoMdl[aList.size()];
			cItem = (StockListZaikoMdl[])aList.toArray(cItem);

			// 適切な薬剤名称、単位、薬価、単価率、区分をセットしていく 2006.04.25 Hasegawa
			{
				int iLoop1, iLoop2;
				Integer Iyymmdd = new Integer(nowDate);

				// 在庫情報レコード回数ループ
				for (iLoop1 = 0; iLoop1 < aList.size(); iLoop1++) {
					// 薬剤情報レコード分ループ
					for (iLoop2 = 0; iLoop2 < aListSub.size(); iLoop2++) {
						if (cItem[iLoop1].getStrNum().equals(
						        cYaku[iLoop2].getItemNo())) {
							if ((Iyymmdd.intValue() >= cYaku[iLoop2].getIntDayStart())
							        && (Iyymmdd.intValue() <= cYaku[iLoop2].getIntDayEnd())) {
								// □□□□ 有効期間内薬剤 □□□□
								// 薬剤名称セット
								cItem[iLoop1].setName(cYaku[iLoop2].getMedName());
								// 単位セット
								cItem[iLoop1].setUnit(cYaku[iLoop2].getMedUnit());
								// 薬価セット
								cItem[iLoop1].setBase(cYaku[iLoop2].getMedPrice());
								// 区分セット
								cItem[iLoop1].setMedKind(cYaku[iLoop2].getMedKind());
								// 単価率セット
								double dZaikoTanka = cItem[iLoop1].getDoubleStock();
								double dMedPrice = cYaku[iLoop2].getDoubleMedPrice();
								double dTankaritu = (dZaikoTanka / dMedPrice) * 100;
								cItem[iLoop1].setRate(Sprintf.format(5, 1,
								        dTankaritu));
								break;
							}
						}
					}
					// 有効期間範囲外の薬剤についての処理
					if (iLoop2 == aListSub.size()) {
						// 薬価に"-"をセット
						cItem[iLoop1].setBase("-");
						// 単価率に"-"をセット
						cItem[iLoop1].setRate("-");
					}
				}
			}
		} catch (SQLException sqle) {
			System.out.println("BizStockListZaiko getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListZaiko getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return cItem;
	}
}

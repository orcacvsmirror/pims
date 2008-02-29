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
 * 更新履歴  :  2004.02.24,25 onuki
 *              2004.02.26    onuki
 *              2004.07.14    onuki
 *                薬剤の有効期限を無視し、かつ薬剤の重複を避ける
 *              2006.04.13 Hasegawa
 *                薬価変更があった薬剤が変更前の月の表示／印刷で抜ける現象を修正した
 *              2006.04.26 Hasegawa
 *               有効期間が切れた薬剤に関しても、在庫として残っている場合は表示するように
 *               修正した。
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

import drugstock.cmn.Sprintf;
import drugstock.db.ComDatabase;
import drugstock.model.MedicineSubInfo;
import drugstock.model.StockListSaekiMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「品目別差益高分析表」DB処理
 */

public class BizStockListSaeki {

	Connection conn = null;
	Statement stmt = null;
	Statement stmt1 = null;
	ResultSet rs = null;
	ResultSet rs1 = null;

  String hospnum  = null;

	public BizStockListSaeki() {
	}

	/**
	 * 入力条件から、帳票：「品目別差益高分析表」出力用のモデルを返します。
	 * 
	 * @param yyyymm
	 *            年月指定
	 * @param drugKind
	 *            薬剤指定
	 * @param iPrintRank
	 *            印刷順番指定："shiyou"の場合は使用高順、"saeki"の場合は差益高順
	 * @return 品目別差益高分析表用モデルの配列
	 * @see "2006.04.26 Hasegawa 修正"
	 */
	public StockListSaekiMdl[] getListData(String yyyymm, String drugKind,
	        String iPrintRank) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		StockListSaekiMdl cItem[] = null;
		MedicineSubInfo cYaku[] = null;
		String sql = null;
		String itemNo = null;
		String nowDate = yyyymm + "01";
		String total_Price = null;
		String total_margin = null;
		double dbl_Total = 0.0;
		double dbl_Total_margin = 0.0;
		double dWork = 0.0;
		String tmp_item_no[] = null;
		double tmp_stc_price[] = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			ArrayList aList = new ArrayList();

			// ワークテーブル作成
			db.bigin();
			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_stock_list02");
			bsql.append(" WHERE hospnum ='" + hospnum + "'");
			db.execute(bsql.toString());

			bsql.delete(0, bsql.length());
			bsql.append("INSERT INTO t_stock_list02 ");
			bsql.append("SELECT t_stock.item_no as item_no,");
			bsql.append(" m_cont_item.med_nm as med_nm,");
			bsql.append(" 0 as expend_rank,");
			bsql.append(" 0 as margin_rank,");
			bsql.append(" m_orca_medicine.med_price as med_price,");
			bsql.append(" 0.0 as stc_price,");
			bsql.append(" 0.0 as margin,");
			bsql.append(" m_orca_medicine.unit_nm as unit_nm, t_stock.expend_num as expend_num,");
			bsql.append(" m_orca_medicine.med_price * t_stock.expend_num as expend_price, ");
			bsql.append(" 0.0 as margin_price,");
			bsql.append(" m_cont_item.med_kind1 as med_kind1,");
			bsql.append(" m_cont_item.hospnum as hospnum");
			bsql.append(" FROM (t_stock LEFT JOIN m_cont_item ON t_stock.item_no = m_cont_item.item_no)");
			bsql.append(" LEFT JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd");
			bsql.append(" WHERE t_stock.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_stock.yyyymm='" + yyyymm + "'");
			// drugKind="0"のとき：薬剤区分全体指定で条件を外す
			if (drugKind.equals("0") == false) {
				bsql.append(" AND m_cont_item.med_kind1='" + drugKind + "'");
			}
			bsql.append(" AND m_cont_item.del_flg='0'");
			// 2006.04.13 Hasegawa以下3行修正 集計月時の期間に適用されていた薬剤情報を取得する
			// bsql.append(" AND m_orca_medicine.day_from<='" + nowDate + "'");
			// bsql.append(" AND m_orca_medicine.day_to>'" + nowDate + "'");
			// bsql.append(" AND m_orca_medicine.day_to<=");
			bsql.append(" AND m_orca_medicine.day_to=");
			bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
			bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");
			//bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "')");

			bsql.append(" GROUP BY t_stock.item_no, m_cont_item.med_nm,  m_orca_medicine.med_price,m_orca_medicine.unit_nm,");
			bsql.append(" t_stock.expend_num ,m_orca_medicine.med_price * t_stock.expend_num,");
			bsql.append("m_cont_item.med_kind1,m_cont_item.hospnum");
			sql = bsql.toString();
			stmt = conn.createStatement();
			db.execute(sql);
			db.commit();
			stmt.close();

			// 適切な薬価をセットするために薬剤情報を取得する。
			// 2006.04.27 Hasegawa 更新

			ArrayList aListSub = new ArrayList(); // 薬剤情報

			// ＤＢ＞薬剤情報取得ＳＱＬ文作成
			bsql.delete(0, bsql.length());
			bsql.append("SELECT ");
			bsql.append("m_cont_item.item_no AS ITEM_NO,");
			bsql.append("m_orca_medicine.med_nm AS NAME,");
			bsql.append("m_orca_medicine.unit_nm AS UNIT,");
			bsql.append("m_orca_medicine.med_kind AS MED_KIND,");
			bsql.append("m_orca_medicine.day_from AS KIKAN_STR,");
			bsql.append("m_orca_medicine.day_to AS KIKAN_END,");
			bsql.append("m_orca_medicine.med_price AS PRICE,");
			bsql.append("m_orca_medicine.hospnum AS HOSPNUM ");
			bsql.append("FROM m_cont_item INNER JOIN m_orca_medicine ");
			bsql.append("ON m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsql.append("WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.cont_id = (SELECT MAX(m_cont_item.cont_id) ");
			bsql.append("from m_cont_item ");
			bsql.append("WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd ");
			bsql.append(" AND m_cont_item.hospnum = '" + hospnum +"')");
			bsql.append(" ORDER BY m_cont_item.item_no");

			// 文字列バッファを文字列化
			sql = bsql.toString();
			// SQL 文をデータベースに送るための Statement オブジェクトを生成
			stmt = conn.createStatement();
			// SQL 文実行
			rs = stmt.executeQuery(sql);
			// 出力結果抽出
			while (rs.next()) {

				MedicineSubInfo YakuzaiWs = new MedicineSubInfo();

				YakuzaiWs.setItemNo(rs.getString("item_no"));
				YakuzaiWs.setMedName(rs.getString("name"));
				YakuzaiWs.setMedUnit(rs.getString("unit"));
				YakuzaiWs.setMedKind(rs.getString("med_kind"));
				YakuzaiWs.setDayStart(rs.getString("kikan_str"));
				YakuzaiWs.setDayEnd(rs.getString("kikan_end"));
				YakuzaiWs.setMedPrice(rs.getString("price"));
				aListSub.add(YakuzaiWs);
			}
			// MedicineSubInfoクラスの配列に変換
			cYaku = new MedicineSubInfo[aListSub.size()];
			cYaku = (MedicineSubInfo[])aListSub.toArray(cYaku);
			rs.close();
			stmt.close();

			// ワークテーブルのレコード数を取得する。
			int iRecordCnt = 0;
			// 取得して処理する。
			bsql.delete(0, bsql.length());
			bsql.append("SELECT COUNT (*) FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			// レコード数を取得
			iRecordCnt = rs.getInt("count");
			rs.close();
			stmt.close();

			// ワークテーブル必要事項取得配列（薬剤番号、使用量）
			String[] SList02_MedNum = new String[iRecordCnt];
			double[] dList02_MedExp = new double[iRecordCnt];
			double[] dList02_MedPri = new double[iRecordCnt];

			// ワークテーブルから薬剤番号と使用量のペアを取得する。
			{
				int iLoop = 0;

				bsql.delete(0, bsql.length());
				bsql.append("SELECT item_no , expend_num , med_price FROM t_stock_list02");
			  bsql.append(" WHERE t_stock_list02.hospnum = '" + hospnum + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					SList02_MedNum[iLoop] = rs.getString("item_no");
					dList02_MedExp[iLoop] = rs.getDouble("expend_num");
					dList02_MedPri[iLoop] = rs.getDouble("med_price");
					iLoop++;
				}
				rs.close();
				stmt.close();
			}

			// ワークデータテーブルのアップデート
			{
				int iLoop1, iLoop2;

				// 集計年月を整数化
				Integer Iyymmdd = new Integer(nowDate);

				for (iLoop1 = 0; iLoop1 < iRecordCnt; iLoop1++) {
					for (iLoop2 = 0; iLoop2 < aListSub.size(); iLoop2++) {
						if (SList02_MedNum[iLoop1].equals(cYaku[iLoop2].getItemNo())) {
							if ((Iyymmdd.intValue() >= cYaku[iLoop2].getIntDayStart())
							        && (Iyymmdd.intValue() <= cYaku[iLoop2].getIntDayEnd())) {
								// □□□□ 有効期間内薬剤 □□□□

								// 薬価、薬剤名称、単位、使用高のアップデート
								bsql.delete(0, bsql.length());
								bsql.append("UPDATE t_stock_list02 ");
								bsql.append("SET ");
								// 薬剤名称
								bsql.append("med_nm ='"
								        + cYaku[iLoop2].getMedName() + "',");
								// 薬価
								dList02_MedPri[iLoop1] = cYaku[iLoop2].getDoubleMedPrice();
								bsql.append("med_price = '"
								        + cYaku[iLoop2].getMedPrice() + "',");
								// 単位
								bsql.append("unit_nm = '"
								        + cYaku[iLoop2].getMedUnit() + "',");
								// 使用量高
								Double DTmp = new Double(cYaku[iLoop2].getDoubleMedPrice()
								        * dList02_MedExp[iLoop1]);
								bsql.append("expend_price = '"
								        + DTmp.toString() + "',");
								// 薬剤区分
								bsql.append("med_kind1 = '"
								        + cYaku[iLoop2].getMedKind() + "'");
								bsql.append("WHERE hospnum = '" + hospnum + "'");
								bsql.append("AND item_no = '"
								        + cYaku[iLoop2].getItemNo() + "'");
								sql = bsql.toString();
								stmt = conn.createStatement();
								db.execute(sql);
								db.commit();
								stmt.close();
								break;
							}// end of if((Iyymmdd.intValue()
						}// end of if(SList02_MedNum[iLoop1]
					}// end of for(int iLoop2)
					// 有効期間範囲外の薬剤についての処理

					if (iLoop2 == aListSub.size()) {
						// 薬価に"-"をセット
						bsql.delete(0, bsql.length());
						bsql.append("UPDATE t_stock_list02 ");
						bsql.append("SET ");
						// 薬価
						dList02_MedPri[iLoop1] = 0.00;
						bsql.append("med_price = '" + 0.00 + "',");
						bsql.append("expend_price = '" + 0.00 + "'");
						bsql.append("WHERE hospnum = '" + hospnum + "'");
						bsql.append("AND item_no = '"
						        + SList02_MedNum[iLoop1] + "'");
						sql = bsql.toString();
						stmt = conn.createStatement();
						db.execute(sql);
						db.commit();
						stmt.close();
					}
				}// end of for(int iLoop1)
			}

			bsql.delete(0, bsql.length());
			bsql.append("SELECT item_no");
			bsql.append(" FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" GROUP BY item_no");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.last();
			// 最終行の行番号を取得
			int max_item_no = 0;
			max_item_no = rs.getRow();
			tmp_item_no = new String[max_item_no];
			tmp_stc_price = new double[max_item_no];
			rs.beforeFirst();

			int k = 0;
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("SELECT unit_price FROM t_stock");
			  bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND item_no = '" + rs.getString("item_no")
				        + "'");
				bsql.append(" AND yyyymm = '" + yyyymm + "'");
				bsql.append(" AND del_flg='0'");
				sql = bsql.toString();
				stmt1 = conn.createStatement();
				rs1 = stmt1.executeQuery(sql);
				dWork = 0.0;
				if (rs1.next() == true) {
					dWork = rs1.getDouble("unit_price");
				}
				tmp_stc_price[k] = dWork;

				rs1.close();
				stmt1.close();
				tmp_item_no[k] = rs.getString("item_no");
				k++;

			}
			rs.close();
			stmt.close();

			// 使用高の順位を取得 04.02.26 onuki
			bsql.delete(0, bsql.length());
			bsql.append("SELECT expend_price, item_no FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" ORDER BY expend_price DESC,item_no");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			int rank = 1;
			int tmp_rank = rank;
			double tmp_expend_price = 0.0;
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("UPDATE t_stock_list02");
				if (tmp_expend_price != rs.getDouble("expend_price")) {
					tmp_rank = rank;
				}
				bsql.append(" SET expend_rank = " + tmp_rank);
				bsql.append(" WHERE hospnum ='" + hospnum + "'");
				bsql.append(" AND item_no='" + rs.getString("item_no") + "'");
				sql = bsql.toString();
				stmt1 = conn.createStatement();
				db.execute(sql);
				db.commit();
				stmt1.close();

				tmp_expend_price = rs.getDouble("expend_price");
				rank++;
			}
			rs.close();
			stmt.close();

			// DBへ納入価、差益、差益高の反映 04.02.25 onuki
			// 薬価が０（有効期限が切れている薬の場合）も差益および差益高を０にする。
			for (k = 0; k < max_item_no; k++) {
				int int_stc_price = (int)(tmp_stc_price[k] * 1000);
				bsql.delete(0, bsql.length());
				bsql.append("UPDATE t_stock_list02");
				if ((tmp_stc_price[k] == 0.0) || (dList02_MedPri[k] == 0.0)) {
					bsql.append(" SET stc_price = 0.0 ");
					bsql.append(" , margin = 0.0 ");
					bsql.append(" , margin_price = 0.0 ");
				} else {
					bsql.append(" SET stc_price=" + tmp_stc_price[k]);
					bsql.append(" , margin = ( med_price*1000 - "
					        + int_stc_price + ") /1000 ");
					bsql.append(" , margin_price = ( med_price*1000 - "
					        + int_stc_price + ") * expend_num /1000");
				}
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND item_no='" + tmp_item_no[k] + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				db.execute(sql);
				db.commit();
				stmt.close();
			}

			// 差益高の順位を取得 04.02.26 onuki
			bsql.delete(0, bsql.length());
			bsql.append("SELECT margin_price, item_no FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" ORDER BY margin_price DESC,item_no");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			rank = 1;
			tmp_rank = rank;
			double tmp_margin_price = 0.0;
			while (rs.next()) {
				bsql.delete(0, bsql.length());
				bsql.append("UPDATE t_stock_list02");
				if (tmp_margin_price != rs.getDouble("margin_price")) {
					tmp_rank = rank;
				}
				bsql.append(" SET margin_rank = " + tmp_rank);
			  bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND item_no='" + rs.getString("item_no") + "'");
				sql = bsql.toString();
				stmt1 = conn.createStatement();
				db.execute(sql);
				db.commit();
				stmt1.close();

				tmp_margin_price = rs.getDouble("margin_price");
				rank++;
			}
			rs.close();
			stmt.close();

			// 使用高集計
			bsql.delete(0, bsql.length());
			bsql.append("SELECT COUNT(item_no) as cnt,");
			bsql.append(" SUM(med_price * expend_num) as total_price,");
			bsql.append(" SUM(margin * expend_num) as total_margin");
			bsql.append(" FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			String cnt = null;
			while (rs.next()) {
				StockListSaekiMdl wk = new StockListSaekiMdl();
				cnt = rs.getString("cnt");
				if (cnt.equals("0")) {
					total_Price = "0";
					total_margin = "0";
				} else {
					total_Price = rs.getString("total_price");
					total_margin = rs.getString("total_margin");
				}
				dbl_Total = Double.parseDouble(total_Price);
				dbl_Total_margin = Double.parseDouble(total_margin);
			}
			rs.close();
			stmt.close();

			// ワークテーブル読込み
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM t_stock_list02");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			if (iPrintRank == "shiyou") {
				bsql.append(" ORDER BY expend_price DESC, item_no");
			} else {
				bsql.append(" ORDER BY margin_price DESC, item_no");
			}
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {

				StockListSaekiMdl wk = new StockListSaekiMdl();

				wk.setExpend_Rank(rs.getString("expend_rank"));
				wk.setMargin_Rank(rs.getString("margin_rank"));

				wk.setItemNum(rs.getString("item_no"));
				wk.setItemName(rs.getString("med_nm"));
				wk.setMed_Price(rs.getString("med_price"));
				wk.setStc_Price(rs.getString("stc_price"));
				wk.setMargin(rs.getString("margin"));
				wk.setUnitName(rs.getString("unit_nm"));
				wk.setExpend_Num(rs.getString("expend_num"));
				wk.setExpend_Price(rs.getString("expend_price"));
				wk.setMargin_Price(rs.getString("margin_price"));
				// 薬剤種別を取得
				wk.setMedKind(rs.getString("med_kind1"));

				double rateD = 0.0;
				if (dbl_Total != 0.0) {
					rateD = Double.parseDouble(rs.getString("expend_price"))
					        * 100 / dbl_Total;
				}
				wk.setRate(Sprintf.formatCanma(2, 2, rateD));
				double rateMargin = 0.0;
				if (dbl_Total_margin != 0.0) {
					rateMargin = Double.parseDouble(rs.getString("margin_price"))
					        * 100 / dbl_Total_margin;
				}
				wk.setMargin_Rate(Sprintf.formatCanma(2, 2, rateMargin));
				aList.add(wk);
			}
			cItem = new StockListSaekiMdl[aList.size()];
			cItem = (StockListSaekiMdl[])aList.toArray(cItem);

			// 有効期間範囲外の薬剤の薬価と差益を「-」にする。 2006.04.25 Hasegawa
			{
				int iLoop1, iLoop2;

				Integer Iyymmdd = new Integer(nowDate);

				// 在庫情報レコード回数ループ
				for (iLoop1 = 0; iLoop1 < aList.size(); iLoop1++) {
					// 薬剤情報レコード分ループ
					for (iLoop2 = 0; iLoop2 < aListSub.size(); iLoop2++) {
						if (cItem[iLoop1].getStrItemNum().equals(
						        cYaku[iLoop2].getItemNo())) {
							if ((Iyymmdd.intValue() >= cYaku[iLoop2].getIntDayStart())
							        && (Iyymmdd.intValue() <= cYaku[iLoop2].getIntDayEnd())) {
								// □□□□ 有効期間内薬剤 □□□□
								break;
							}
						}
					}
					// 有効期間範囲外の薬剤についての処理
					if (iLoop2 == aListSub.size()) {
						// 薬価に"-"を表示
						cItem[iLoop1].setMed_Price("-");
						// 差益で"-"を表示
						cItem[iLoop1].setMargin("-");
						// 差益高で"-"を表示
						cItem[iLoop1].setMargin_Price("-");
						// 差益割合で"-"を表示
						cItem[iLoop1].setMargin_Rate("-");
					}
				}
			}

			rs.close();
			stmt.close();

		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizStockListSaeki getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizStockListSaeki getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}
}

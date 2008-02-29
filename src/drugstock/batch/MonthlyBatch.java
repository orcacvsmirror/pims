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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import drugstock.cl.MonthlyBatchDlg;
import drugstock.cmn.Common;
import drugstock.cmn.PropRead;
import drugstock.cmn.Sprintf;
import drugstock.cmn.WaitMsg;
import drugstock.db.ComDatabase;

import drugstock.batch.OrcaHospNumImport;

/**
 * 月次処理<BR>
 * <BR>
 * 以下の手順で月次処理を行います。<BR>
 * １)設定ファイルから、「開始日付」「単価種別(最新単価or平均単価)」<BR>
 * 「平均在庫単価の集計開始月」を読み込みます。<BR>
 * ２)在庫DBから、該当年月のデータを削除してリセットします。<BR>
 * ３)前月の在庫数量を棚卸DBから取得します。<BR>
 * 棚卸DBが存在しない場合：在庫DBから取得します。<BR>
 * 在庫DBが存在しない場合：0値を設定します。<BR>
 * ４)入庫／出庫／調整／返品のデータを、一時在庫DBから取得します。<BR>
 * ５)部門出庫のデータを、一時在庫DBから出庫DBへ移動します。<BR>
 * ６)在庫DBに計算した在庫データを書き込みます。<BR>
 * ７)平均単価設定のとき：平均単価を計算します。<BR>
 * ８)棚卸DBへ理論在庫を書き込みます。<BR>
 */

public class MonthlyBatch implements Runnable {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
	Statement stmt2 = null;
	ResultSet rs2 = null;
	MonthlyBatchDlg dlg = null;
  String hospnum = null;

	Thread thread;

	public MonthlyBatch() {
		dlg = new MonthlyBatchDlg();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dlg.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		dlg.setLocation((screenSize.width - frameSize.width) / 2,
		        (screenSize.height - frameSize.height) / 2);
		dlg.setModal(true);
		dlg.show();
		if (dlg.IsOK()) {
			thread = new Thread(this);
		}
	}

	public void start() {
		if (thread != null)
			thread.start();
	}

	public void stop() {
		thread = null;
	}

	public void run() {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		// int inen, ituki, ihi;
		String sql = null;
		String sFrom = null;
		String sTo = null;
		String sNengetu = null;
		String sBeforeNengetu = null;
		ComDatabase db = new ComDatabase();

		WaitMsg wait = new WaitMsg();
		wait.setMsg1("月次処理中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();

		// 対象日の取得
		PropRead prop = new PropRead();
		String sYear = dlg.getYear();
		String sMonth = dlg.getMonth();
		String closeDay = prop.getProp("stock_close_day");
		if (closeDay == null)
			closeDay = "99";

		String strStartMonth = prop.getProp("shukei_start_month");
		if (strStartMonth == null)
			strStartMonth = "1";

		String stock_unit_price_tana = prop.getProp("stock_unit_price_tana");
		if ((stock_unit_price_tana == null)
		        || (stock_unit_price_tana.equals("1") == false)) {
			stock_unit_price_tana = "0";
		}

		// 平均を計算する期間
		Common com = new Common();

		int tmpYear = Integer.parseInt(sYear);
		int tmpMonth = Integer.parseInt(sMonth);
		// Calendarでは0が１月なので、補正
		tmpMonth -= 1;

		sNengetu = com.setStrDate(tmpYear, tmpMonth, 1, "yyyyMM");
		sBeforeNengetu = com.setStrDate(tmpYear, tmpMonth - 1, 1, "yyyyMM");

		int intCloseDay = 1;
		if (closeDay.equals("99")) {
			intCloseDay = 1;
		} else {
			intCloseDay = Integer.parseInt(closeDay);
		}
		sFrom = com.setStrDate(tmpYear, tmpMonth, intCloseDay, "yyyyMMdd");
		sTo = com.setStrDate(tmpYear, tmpMonth + 1, intCloseDay, "yyyyMMdd");

		String fromNewNengetu = null;
		// Calendarでは0が１月なので、補正
		int intStartMonth = Integer.parseInt(strStartMonth) - 1;
		// 集計指定月が現在月より大きいとき、年をまたぐため、年-1
		if (tmpMonth < intStartMonth) {
			tmpYear -= 1;
		}
		fromNewNengetu = com.setStrDate(tmpYear, intStartMonth, 1, "yyyyMM");
		// System.out.println("fromNewNengetu ;"+ fromNewNengetu) ;

		// ここから、DB更新
		try {
			String medKind1 = null;
			String medKind2 = null;
			String medKind3 = null;
			String sBStockNum = "0";
			String sUnitPrice = "0";
			String sImpNum = "0";
			String sExpNum = "0";
			String sAdjNum = "0";
			String sBackNum = "0";
			String sStcAmount = "0";
			String sStcNum = "0";
			String sTotalAmount = "0";
			String sTotalNum = "0";
			String sOrcaMedCd = null;

			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			bsql.delete(0, bsql.length());
			bsql.append("VACUUM FULL");
			db.execute(bsql.toString());
			bsql.delete(0, bsql.length());
			bsql.append("VACUUM ANALYZE");
			db.execute(bsql.toString());

			db.bigin();
			// 在庫から、指定年月データを一端削除
			bsql.delete(0, bsql.length());
			// bsql.append("DELETE FROM t_stock WHERE yyyymm ='" + sNengetu + "'");
			bsql.append("DELETE FROM t_stock WHERE yyyymm ='" + sNengetu + "'");
			bsql.append(" AND hospnum ='" + hospnum + "'");
			db.execute(bsql.toString());

			// 全品番分取得して処理する。
			bsql.delete(0, bsql.length());
			bsql.append("SELECT count(item_no) AS cnt_item_no");
			bsql.append(" FROM m_cont_item");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg <> '1'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int cnt_item_no = 0;
			if (rs.next()) {
				cnt_item_no = Integer.parseInt(rs.getString("cnt_item_no"));
				// System.out.println(cnt_item_no);
			}
			String itemNoArr[] = new String[cnt_item_no];
			rs.close();
			stmt.close();

			// 全品番分取得して処理する。
			bsql.delete(0, bsql.length());
			bsql.append("SELECT item_no");
			bsql.append(" FROM m_cont_item");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg <> '1'");
			bsql.append(" GROUP BY item_no");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			int i = 0;
			while (rs.next()) {
				itemNoArr[i] = rs.getString("item_no");
				i++;
			}
			rs.close();
			stmt.close();

			cnt_item_no = i;
			// System.out.println(cnt_item_no);
			for (int j = 0; j < cnt_item_no; j++) {
				String itemNo = itemNoArr[j];

				if (medKind1 == null)
					medKind1 = "";
				if (medKind2 == null)
					medKind2 = "";
				if (medKind3 == null)
					medKind3 = "";

				sBStockNum = "0";
				sUnitPrice = "0";
				sImpNum = "0";
				sExpNum = "0";
				sAdjNum = "0";
				sBackNum = "0";
				sOrcaMedCd = null;
				boolean isStockingExist = false;

				double dUnitPrice = 0.0;

				// 最初に日レセ薬剤コードを取得
				bsql.delete(0, bsql.length());
				bsql.append("SELECT med_kind1, orca_med_cd");
				bsql.append(" FROM m_cont_item");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND item_no = '" + itemNo + "'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				if (rs2.next()) {
					sOrcaMedCd = rs2.getString("orca_med_cd");
					medKind1 = rs2.getString("med_kind1");
				}
				rs2.close();
				stmt2.close();

				// 前月の在庫数量を実在庫より取得
				// (実在庫がなければ、理論在庫が登録されている)
				bsql.delete(0, bsql.length());
				bsql.append("SELECT stock_truth");
				bsql.append(" FROM t_stock_invent");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND yyyymm = '" + sBeforeNengetu + "'");
				bsql.append(" AND   item_no = '" + itemNo + "'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				boolean isNotBeforeInvent = false;
				if (rs2.next()) {
					sBStockNum = rs2.getString("stock_truth");
				} else {
					isNotBeforeInvent = true;
				}
				rs2.close();
				stmt2.close();

				// 棚卸DBが存在しなかった場合は、前月在庫量をt_stockから取得
				if (isNotBeforeInvent) {
					bsql.delete(0, bsql.length());
					bsql.append("SELECT stock_num");
					bsql.append(" FROM t_stock");
					bsql.append(" WHERE hospnum ='" + hospnum + "'");
					bsql.append(" AND   yyyymm ='" + sBeforeNengetu + "'");
					bsql.append(" AND   item_no = '" + itemNo + "'");
					bsql.append(" AND   del_flg = '0'");
					sql = bsql.toString();
					stmt2 = conn.createStatement();
					rs2 = stmt2.executeQuery(sql);
					double stcAmountDouble = Double.parseDouble(sStcAmount); // 在庫金額
					if (rs2.next()) {
						sBStockNum = rs2.getString("stock_num");
					} else {
						sBStockNum = "0";
					}
				}

				// 入庫／出庫／調整／返品の各数量を取得
				String sExpNumTmp = "0"; // 部門出庫用文字列
				bsql.delete(0, bsql.length());
				bsql.append("SELECT stc_cd,");
				// bsql.append("SELECT stc_cd, stc_unit,");
				bsql.append("       sum(stc_num) as stc_total");
				bsql.append(" FROM t_stocking");
				bsql.append(" WHERE hospnum = '" + hospnum  + "'");
				bsql.append(" AND '" + sFrom + "' <= stc_date");
				bsql.append(" AND   stc_date < '" + sTo + "'");
				bsql.append(" AND   item_no = '" + itemNo + "'");
				bsql.append(" AND   del_flg = '0'");
				bsql.append(" GROUP BY stc_cd");
				// bsql.append(" GROUP BY stc_cd, stc_unit, stc_date");
				// bsql.append(" ORDER BY stc_date DESC");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				double sAdjDouble = 0.0;
				while (rs2.next()) {
					String tmpTotal = rs2.getString("stc_total");
					if (tmpTotal == null)
						tmpTotal = "0";

					if (rs2.getString("stc_cd").equals("1")) { // 仕入
						sImpNum = tmpTotal;
						isStockingExist = true;
					} else if ((rs2.getString("stc_cd").equals("8")) || // 調整
					        (rs2.getString("stc_cd").equals("3")) || // 部門返品
					        (rs2.getString("stc_cd").equals("4"))) { // 廃棄
						sAdjDouble += Double.parseDouble(tmpTotal);
						// }else
						// if(rs2.getString("stc_cd").equals("2")){ // 部門出庫
						// sExpNumTmp = tmpTotal;
					} else if (rs2.getString("stc_cd").equals("9")) { // 返品
						sBackNum = tmpTotal;
					}
				}
				sAdjNum = String.valueOf(sAdjDouble);
				rs2.close();
				stmt2.close();

				// 部門出庫を院内出庫に移動 04.06.09 onuki
				// まず、該当月の部門出庫分を消去
				bsql.delete(0, bsql.length());
				bsql.append("SELECT name ");
				bsql.append(" FROM t_expend");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND '" + sFrom + "' <= exp_date");
				bsql.append(" AND   exp_date < '" + sTo + "'");
				bsql.append(" AND   name ='＊部門出庫＊'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				boolean flagAutoExpend = rs2.next();
				rs2.close();
				stmt2.close();
				if (flagAutoExpend) {
					bsql.delete(0, bsql.length());
					bsql.append("DELETE FROM t_expend ");
					bsql.append(" WHERE hospnum = '" + hospnum + "'");
					bsql.append(" AND '" + sFrom + "' <= exp_date");
					bsql.append(" AND exp_date < '" + sTo + "'");
					bsql.append(" AND orca_med_cd ='" + sOrcaMedCd + "'");
					bsql.append(" AND name ='＊部門出庫＊'");
					db.execute(bsql.toString());
				}
				bsql.delete(0, bsql.length());
				bsql.append("SELECT sum(stc_num)as sum_stc, stc_date ");
				bsql.append(" FROM t_stocking");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND '" + sFrom + "' <= stc_date");
				bsql.append(" AND   stc_date < '" + sTo + "'");
				bsql.append(" AND   stc_cd = '2'");
				bsql.append(" AND   item_no = '" + itemNo + "'");
				bsql.append(" AND   del_flg = '0'");
				bsql.append(" GROUP BY stc_date");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);

				while (rs2.next()) {
					// System.out.println(rs2.getString("stc_date")) ;
					bsql.delete(0, bsql.length());
					bsql.append("INSERT INTO t_expend ( ");
					bsql.append("exp_date,orca_user,orca_med_cd,insurance,");
					bsql.append("expend_num,name,orca_user_no,hospnum");
					bsql.append(") VALUES (");
					bsql.append("'" + rs2.getString("stc_date") + "',");
					bsql.append("0,");
					bsql.append("'" + sOrcaMedCd + "',");
					bsql.append("'',");
					bsql.append(rs2.getDouble("sum_stc") * (-1) + ",");
					bsql.append("'＊部門出庫＊',");
					bsql.append("'',");
					bsql.append("'" + hospnum + "')");
					db.execute(bsql.toString());
				}
				rs2.close();
				stmt2.close();

				// 出庫の数量を取得
				if (sOrcaMedCd != null) {
					bsql.delete(0, bsql.length());
					bsql.append("SELECT Sum(t_expend.expend_num) AS expend_num_total");
					bsql.append(" FROM t_expend");
					bsql.append(" WHERE hospnum = '" + hospnum + "'");
					bsql.append(" AND '" + sFrom + "' <= t_expend.exp_date");
					bsql.append(" AND   t_expend.exp_date < '" + sTo + "'");
					bsql.append(" AND   orca_med_cd = '" + sOrcaMedCd + "'");
					sql = bsql.toString();
					stmt2 = conn.createStatement();
					rs2 = stmt2.executeQuery(sql);
					if (rs2.next()) {
						sExpNum = rs2.getString("expend_num_total");
					}
					if (sExpNum == null)
						sExpNum = "0";
					rs2.close();
					stmt2.close();
				}

				// 出庫／前月／仕入／返品の各数量をdouble型変数化
				double sExpDouble = Double.parseDouble(sExpNum);
				sExpNum = String.valueOf(sExpDouble);

				double sBStockDouble = Double.parseDouble(sBStockNum); // 前月在庫
				double sImpDouble = Double.parseDouble(sImpNum); // 仕入在庫
				double sBackDouble = Double.parseDouble(sBackNum); // 返品在庫

				double sStcDouble = sBStockDouble + sImpDouble + sAdjDouble
				        - sExpDouble + sBackDouble;
				sStcNum = String.valueOf(sStcDouble);

				// 単価計算用の金額／在庫を取得
				bsql.delete(0, bsql.length());
				bsql.append("SELECT sum(stc_amount) as sTotalAmount");
				bsql.append(", sum(stc_num) as sTotalNum");
				bsql.append(" FROM t_stocking");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND '" + sFrom + "' <= stc_date");
				bsql.append(" AND   stc_date < '" + sTo + "'");
				bsql.append(" AND  (stc_cd = '1'"); // 仕入
				bsql.append(" Or stc_cd='9'"); // 返品
				bsql.append(" Or stc_cd='5'"); // 値引
				bsql.append(" Or stc_cd='8')"); // 調整
				bsql.append(" AND   item_no = '" + itemNo + "'");
				bsql.append(" AND   del_flg = '0'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				if (rs2.next()) {
					sTotalAmount = rs2.getString("sTotalAmount");
					sTotalNum = rs2.getString("sTotalNum");
				}
				if (sTotalAmount == null)
					sTotalAmount = "0";
				if (sTotalNum == null)
					sTotalNum = "0";
				rs2.close();
				stmt2.close();

				// 前月分を加算
				bsql.delete(0, bsql.length());
				bsql.append("SELECT amount as beforeAmount");
				bsql.append(" FROM t_stock");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND yyyymm ='" + sBeforeNengetu + "'");
				bsql.append(" AND   item_no = '" + itemNo + "'");
				bsql.append(" AND   del_flg = '0'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				double stcAmountDouble = Double.parseDouble(sStcAmount); // 在庫金額
				if (rs2.next()) {
					stcAmountDouble += rs2.getDouble("beforeAmount");
				}
				sStcAmount = String.valueOf(stcAmountDouble);

				// 取り合えず、在庫を作成。
				bsql.delete(0, bsql.length());
				bsql.append("INSERT INTO t_stock (yyyymm,item_no,before_stock,unit_price,stocking_num,");
				bsql.append("adjust_num,expend_num,med_kind1,med_kind2,med_kind3,");
				bsql.append("amount,stock_num,del_flg,total_num,total_amount,back_num,");
				bsql.append("hospnum");
				bsql.append(") VALUES (");
				bsql.append("'" + sNengetu + "',");
				bsql.append("'" + itemNo + "',");
				bsql.append("" + sBStockNum + ",");
				// bsql.append("" + sUnitPrice + ",");
				bsql.append("0.0,");
				bsql.append("" + sImpNum + ",");
				bsql.append("" + sAdjNum + ",");
				bsql.append("" + sExpNum + ",");
				bsql.append("'" + medKind1 + "',");
				bsql.append("'" + medKind2 + "',");
				bsql.append("'" + medKind3 + "',");
				bsql.append("0,");
				bsql.append("" + sStcNum + ",");
				bsql.append("0,");
				bsql.append("" + sTotalNum + "," + sTotalAmount + ","
				        + sBackNum + ",");
				bsql.append("'" + hospnum + "'");
				bsql.append(")");
				db.execute(bsql.toString());

				double dWork = 0;
				// 平均単価・在庫金額の計算
				if (stock_unit_price_tana.equals("1")) {

					// 総合計額を総合計数で割る
					bsql.delete(0, bsql.length());
					bsql.append("SELECT SUM(total_amount) as total_amount, SUM(total_num) AS total_num FROM t_stock");
					bsql.append(" WHERE hospnum = '" + hospnum + "'");
					bsql.append(" AND item_no = '" + itemNo + "'");

					bsql.append(" AND yyyymm >= '" + fromNewNengetu + "'");
					// bsql.append(" AND yyyymm >= '" + sNengetu.substring(0, 4)
					// + "01" + "'");
					bsql.append(" AND yyyymm <= '" + sNengetu + "'");
					bsql.append(" AND del_flg='0'");
					sql = bsql.toString();
					stmt2 = conn.createStatement();
					rs2 = stmt2.executeQuery(sql);
					if (rs2.next()) {
						double d1, d2;
						d1 = rs2.getDouble("total_amount");
						d2 = rs2.getDouble("total_num");

						if (d1 == 0 || d2 == 0)
							dWork = 0;
						else
							dWork = d1 / d2;
					}
					rs2.close();
					stmt2.close();

					// 総合計がない＝今年度の仕入れがない場合＞現在(前年分)の最新納入単価
					if (dWork == 0.0) {
						bsql.delete(0, bsql.length());
						bsql.append("SELECT unit_price");
						bsql.append(" FROM m_cont_item");
						bsql.append(" WHERE hospnum = '" + hospnum + "'");
						bsql.append(" AND item_no = '" + itemNo + "'");
						bsql.append(" AND   del_flg = '0'");
						sql = bsql.toString();
						stmt2 = conn.createStatement();
						rs2 = stmt2.executeQuery(sql);
						if (rs2.next()) {
							// 入庫から最新納入単価を取得
							dWork = rs2.getDouble("unit_price");
						}
						rs2.close();
						stmt2.close();
					}
				}
				// 最新単価・在庫金額の計算 04.07.14 onuki
				else {
					bsql.delete(0, bsql.length());
					bsql.append("SELECT unit_price");
					bsql.append(" FROM m_cont_item");
					bsql.append(" WHERE hospnum = '" + hospnum + "'");
					bsql.append(" AND item_no = '" + itemNo + "'");
					bsql.append(" AND   del_flg = '0'");
					sql = bsql.toString();
					stmt2 = conn.createStatement();
					rs2 = stmt2.executeQuery(sql);
					if (rs2.next()) {
						// 入庫から最新納入単価を取得
						sUnitPrice = rs2.getString("unit_price");
						dWork = rs2.getDouble("unit_price");
					} else {
						// if (sUnitPrice == null){
						sUnitPrice = "0";
					}
					rs2.close();
					stmt2.close();
				}

				double dWorkZero = 0.0;
				// 在庫単価が0.0のとき：在庫金額＝薬価基準×在庫量 04.07.14 onuki
				if ((dWork == 0.0) && (sStcDouble != 0.0)) {
					bsql.delete(0, bsql.length());
					bsql.append("SELECT m_orca_medicine.med_price AS med_price");
					bsql.append(" FROM m_orca_medicine");
					bsql.append(" LEFT JOIN m_cont_item");
					bsql.append(" ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd");
					bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
					bsql.append(" AND m_cont_item.item_no = '" + itemNo
					                + "'");
					bsql.append(" AND m_orca_medicine.day_to = ");
					bsql.append("(SELECT MAX(day_to) FROM m_orca_medicine");
					// bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd)");
					bsql.append(" WHERE m_orca_medicine.orca_med_cd = m_cont_item.orca_med_cd");
					bsql.append(" AND m_orca_medicine.hospnum = '" + hospnum + "')");
					sql = bsql.toString();
					stmt2 = conn.createStatement();
					rs2 = stmt2.executeQuery(sql);
					if (rs2.next()) {
						dWorkZero = Double.parseDouble(rs2.getString("med_price"));
						// System.out.println(itemNo+";"+dWorkZero);
					}
					rs2.close();
					stmt2.close();
				}

				bsql.delete(0, bsql.length());
				bsql.append("UPDATE t_stock SET ");
				bsql.append("unit_price = " + String.valueOf(dWork));
				// 在庫金額＝単価×在庫数
				// 在庫単価が0.0のとき：在庫金額＝薬価基準×在庫量
				if (dWorkZero != 0.0) {
					dWork = dWorkZero;
				}
				dWork = Double.parseDouble(Sprintf.format(13, 2, dWork));
				sStcDouble = Double.parseDouble(Sprintf.format(13, 2,
				        sStcDouble));

				bsql.append(", amount = " + String.valueOf(dWork * sStcDouble));
				// System.out.println(j+";"+itemNo+";"+ String.valueOf(dWork *
				// sStcDouble));
				bsql.append("WHERE hospnum = '" + hospnum + "'");
				bsql.append("AND yyyymm = '" + sNengetu + "'");
				bsql.append("AND item_no='" + itemNo + "'");
				db.execute(bsql.toString());

				// [棚卸情報]全薬剤に対して、実在庫の初期値として理論在庫を入力 04.03.18 onuki
				bsql.delete(0, bsql.length());
				bsql.append("SELECT flag_truth AS flagTruth");
				bsql.append(" FROM t_stock_invent");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND yyyymm = '" + sNengetu + "'");
				bsql.append(" AND   item_no = '" + itemNo + "'");
				sql = bsql.toString();
				stmt2 = conn.createStatement();
				rs2 = stmt2.executeQuery(sql);
				String flagTruth = null;
				boolean flagInvent = rs2.next();
				if (flagInvent) {
					flagTruth = rs2.getString("flagTruth");
				}
				rs2.close();
				stmt2.close();

				// 在庫量を文字列化し、棚卸DBに格納
				if (sStcDouble != 0.0) {
					sStcNum = Double.toString(sStcDouble);
				} else {
					sStcNum = "0.0";
				}
				if (flagInvent) {// 既に存在するならUPDATE
					bsql.delete(0, bsql.length());
					bsql.append("UPDATE t_stock_invent SET ");
					bsql.append("stock_theory ='" + sStcNum + "'");
					if (flagTruth.equals("0")) {
						bsql.append(", stock_truth ='" + sStcNum + "'");
					}
					bsql.append("WHERE hospnum = '" + hospnum + "'");
					bsql.append("AND yyyymm = '" + sNengetu + "'");
					bsql.append("AND item_no='" + itemNo + "'");
					db.execute(bsql.toString());
				} else {// 存在しないならINSERT
					bsql.delete(0, bsql.length());
					bsql.append("INSERT INTO t_stock_invent ( ");
					bsql.append("yyyymm,item_no,stock_theory,stock_truth,");
          bsql.append("flag_truth,hospnum");
					bsql.append(") VALUES (");
					bsql.append("'" + sNengetu + "',");
					bsql.append("'" + itemNo + "',");
					bsql.append(sStcNum + ",");
					bsql.append(sStcNum + ",");
					bsql.append("'0'" + ",");
					bsql.append("'" + hospnum + "')");
					db.execute(bsql.toString());
				}
			}

			db.commit();

		} catch (SQLException sqle) {
			if (db != null) {
				db.rollback();
			}
			System.out.println("MonthlyBatch run SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			System.out.println("MonthlyBatch run Exception" + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
			wait.destroy();
		}
	}

}

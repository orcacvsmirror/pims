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

import drugstock.db.ComDatabase;
import drugstock.model.ItemName;
import drugstock.model.StockListKanjaMdl;

import drugstock.batch.OrcaHospNumImport;

/**
 * 帳票：「指定品目使用患者一覧表」DB処理
 */

public class BizStockListKanja {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  String hospnum = null;
	public ItemName iItemArr[] = null;

	public BizStockListKanja() {
	}

	/**
	 * 入力条件から、帳票：「指定品目使用患者一覧表」出力用のモデルを返します。
	 * 
	 * @param yyyymm
	 *            年月指定
	 * @param aList_item
	 *            薬剤品番指定
	 * @param AndOr_Flg
	 *            And,Or指定
	 * @return 指定品目使用患者一覧表用モデルの配列
	 */
	public StockListKanjaMdl[] getListData(String yyyymm, ArrayList aList_item,
	        String AndOr_Flg) {

		StockListKanjaMdl cItem[] = null;
		String sql = null;
		String itemNo = null;
		String nowDate = yyyymm + "01";
		String fromDate = yyyymm + "01";
		String toDate = yyyymm + "99";
		String item_No[] = new String[4];
		String sv_orca_user = null; // 患者ＩＤ
		String sv_orca_user_no = null; // 患者番号
		String wk_orca_user = null;
		String sv_name = null;
		String wk_insurance = null;
		String wk_expend_num = null;
		String wk_exp_date = null;
		String wk_item_no = null;
		String wk_use_med = "";
		boolean data_umu = false;

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		ComDatabase db = new ComDatabase();

		iItemArr = new ItemName[aList_item.size()];
		iItemArr = (ItemName[])aList_item.toArray(iItemArr);

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			ArrayList aList = new ArrayList();

			bsql.delete(0, bsql.length());
			bsql.append("SELECT t_expend.exp_date as exp_date,t_expend.orca_user as orca_user,");
			bsql.append(" t_expend.orca_user_no as orca_user_no, t_expend.name as name,");
			bsql.append(" t_expend.insurance as insurance,m_cont_item.item_no,t_expend.expend_num as expend_num");
			bsql.append(" FROM t_expend LEFT JOIN m_cont_item ON t_expend.orca_med_cd = m_cont_item.orca_med_cd");
			// bsql.append(" WHERE");
			bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND t_expend.hospnum = '" + hospnum + "'");
			bsql.append(" AND (");
			for (int i = 0; i < iItemArr.length; i++) {
				bsql.append(" m_cont_item.item_no='" + iItemArr[i].code
				        + "' OR");
			}
			bsql.delete(bsql.length() - 3, bsql.length());

			bsql.append(" ) AND t_expend.exp_date>='" + fromDate + "'");
			bsql.append(" AND t_expend.exp_date<='" + toDate + "'");

			bsql.append(" GROUP BY t_expend.exp_date, t_expend.orca_user, t_expend.orca_user_no,t_expend.name,");
			bsql.append(" t_expend.insurance,m_cont_item.item_no,t_expend.expend_num");
			bsql.append(" ORDER BY t_expend.orca_user, t_expend.exp_date, m_cont_item.item_no");
			sql = bsql.toString();
			// System.out.println(sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			int j = 0;
			int Day_ix = 0;
			String Set_Day = null;
			StringBuffer buse_day = new StringBuffer(62);
			String tuse_day[] = new String[31];
			String chk_Item[] = new String[iItemArr.length];
			String use_day = null;
			String Item_No = null;
			buse_day.delete(0, buse_day.length());

			for (int i = 0; i < 31; i++) {
				tuse_day[i] = "  ";
			}
			for (int i = 0; i < iItemArr.length; i++) {
				chk_Item[i] = "";
			}

			while (rs.next()) {
				data_umu = true;

				StockListKanjaMdl wk = new StockListKanjaMdl();

				// 初期値の設定
				if (sv_orca_user == null) {
					sv_orca_user = rs.getString("orca_user");
					sv_orca_user_no = rs.getString("orca_user_no");
					sv_name = rs.getString("name");
				}

				wk_orca_user = rs.getString("orca_user");
				wk_exp_date = rs.getString("exp_date");
				wk_item_no = rs.getString("item_no");

				// 患者ＩＤが変わった場合
				if (sv_orca_user.equals(wk_orca_user)) {
				} else {

					// ＡＮＤ条件チェック
					if (AndOr_Flg.equals("AND")) {
						int Match_Cnt = 0;
						for (int i = 0; i < iItemArr.length; i++) {
							if (chk_Item[i] == "1") {
								Match_Cnt++;
							}
						}
						// 条件と一致した場合
						if (Match_Cnt == iItemArr.length) {
							wk.setOrca_User_No(sv_orca_user_no);
							wk.setName(sv_name);
							wk.setInsurance(wk_insurance);
							// 選択薬剤が複数の場合、出庫数を空にする
							if (iItemArr.length > 1) {
								wk.setExpend_Num("");
							} else {
								wk.setExpend_Num(wk_expend_num);
							}

							// wk.setExpend_Num(wk_expend_num);
							j++;
							wk.setSeqNo(new String(new Long(j).toString()));
							for (int i = 0; i < 31; i++) {
								buse_day.append(tuse_day[i]);
							}
							use_day = buse_day.toString();
							wk.setUse_Day(use_day);
							aList.add(wk);
						}
						for (int i = 0; i < 31; i++) {
							tuse_day[i] = "  ";
						}
						buse_day.delete(0, buse_day.length());
						wk.setUse_Med("");

						// ＯＲ条件チェック
					} else {
						// 条件と一致した場合
						if (chk_Item.length > 0) {
							wk.setOrca_User_No(sv_orca_user_no);
							wk.setName(sv_name);
							wk.setInsurance(wk_insurance);
							// 選択薬剤が複数の場合、出庫数を空にする
							if (iItemArr.length > 1) {
								wk.setExpend_Num("");
							} else {
								wk.setExpend_Num(wk_expend_num);
							}

							// wk.setExpend_Num(wk_expend_num);
							j++;
							wk.setSeqNo(new String(new Long(j).toString()));
							for (int i = 0; i < 31; i++) {
								buse_day.append(tuse_day[i]);
							}
							use_day = buse_day.toString();
							wk.setUse_Day(use_day);

							for (int i = 0; i < iItemArr.length; i++) {
								if (chk_Item[i].equals("1")) {
									wk_use_med += i + 1;
								} else {
									wk_use_med += " ";
								}
							}
							wk.setUse_Med(wk_use_med);
							aList.add(wk);
						}
						for (int i = 0; i < 31; i++) {
							tuse_day[i] = "  ";
						}
						buse_day.delete(0, buse_day.length());
						wk_use_med = "";
					}

					// キーの再セット
					sv_orca_user = rs.getString("orca_user");
					sv_orca_user_no = rs.getString("orca_user_no");
					sv_name = rs.getString("name");

					// 条件チェックフラグのクリア
					for (int i = 0; i < iItemArr.length; i++) {
						chk_Item[i] = "";
					}
				}

				// 品目該当チェック
				for (int i = 0; i < iItemArr.length; i++) {
					if (wk_item_no.equals(iItemArr[i].code)) {
						chk_Item[i] = "1";
						break;
					}
				}

				// 日付エリア編集
				Set_Day = rs.getString("exp_date").substring(6, 8);
				Day_ix = Integer.parseInt(Set_Day) - 1;
				tuse_day[Day_ix] = Set_Day;

				wk_insurance = rs.getString("insurance");

				// 出庫数セット（03/02/22追加）
				wk_expend_num = rs.getString("expend_num");

			}
			StockListKanjaMdl wk = new StockListKanjaMdl();

			if (data_umu == true) {
				// ＡＮＤ条件チェック
				if (AndOr_Flg.equals("AND")) {
					int Match_Cnt = 0;
					for (int i = 0; i < iItemArr.length; i++) {
						if (chk_Item[i] == "1") {
							Match_Cnt++;
						}
					}
					// 条件と一致した場合
					if (Match_Cnt == iItemArr.length) {
						wk.setOrca_User_No(sv_orca_user_no);
						wk.setName(sv_name);
						wk.setInsurance(wk_insurance);

						// 選択薬剤が複数の場合、出庫数を空にする
						if (iItemArr.length > 1) {
							wk.setExpend_Num("");
						} else {
							wk.setExpend_Num(wk_expend_num);
						}

						j++;
						wk.setSeqNo(new String(new Long(j).toString()));
						for (int i = 0; i < 31; i++) {
							buse_day.append(tuse_day[i]);
						}
						use_day = buse_day.toString();
						wk.setUse_Day(use_day);
						aList.add(wk);
					}
					for (int i = 0; i < 31; i++) {
						tuse_day[i] = "  ";
					}
					buse_day.delete(0, buse_day.length());
					wk.setUse_Med("");

					// ＯＲ条件チェック
				} else {
					// 条件と一致した場合
					if (chk_Item.length > 0) {
						wk.setOrca_User_No(sv_orca_user_no);
						wk.setName(sv_name);
						wk.setInsurance(wk_insurance);

						// 選択薬剤が複数の場合、出庫数を空にする
						if (iItemArr.length > 1) {
							wk.setExpend_Num("");
						} else {
							wk.setExpend_Num(wk_expend_num);
						}

						// wk.setExpend_Num(wk_expend_num);
						j++;
						wk.setSeqNo(new String(new Long(j).toString()));
						for (int i = 0; i < 31; i++) {
							buse_day.append(tuse_day[i]);
						}
						use_day = buse_day.toString();
						wk.setUse_Day(use_day);

						for (int i = 0; i < iItemArr.length; i++) {
							if (chk_Item[i].equals("1")) {
								wk_use_med += i + 1;
							} else {
								wk_use_med += " ";
							}
						}
						wk.setUse_Med(wk_use_med);

						aList.add(wk);
					}
					for (int i = 0; i < 31; i++) {
						tuse_day[i] = "  ";
					}
					buse_day.delete(0, buse_day.length());
					wk_use_med = "";

				}
			}

			cItem = new StockListKanjaMdl[aList.size()];
			cItem = (StockListKanjaMdl[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizStockListKanja getListData SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizStockListKanja getListData Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}
}

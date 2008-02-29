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

import drugstock.db.OrcaDatabase;
import drugstock.cl.OrcaMedBatchDlg;
import drugstock.cmn.PropRead;
import drugstock.cmn.WaitMsg;
import drugstock.db.ComDatabase;

import drugstock.batch.OrcaHospNumImport;

/**
 * 日レセDBから薬剤使用量を在庫管理システムに取込む
 */

public class OrcaMedBatch implements Runnable {

	Connection conn_orca = null;
	Connection conn_drug = null;
	Statement stmt = null;
	ResultSet rs = null;
	Statement stmt2 = null;
	ResultSet rs2 = null;
	Statement stmt3 = null;
	ResultSet rs3 = null;
	OrcaMedBatchDlg dlg = null;

	Thread thread;

	public OrcaMedBatch() {
		dlg = new OrcaMedBatchDlg();
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
		String sql = null;
		String sFrom = null;
		String sTo = null;
    String hospnum;

		OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		OrcaDatabase db_orca = new OrcaDatabase();
		ComDatabase db_drug = new ComDatabase();

		WaitMsg wait = new WaitMsg();
		wait.setMsg1("日レセ診療データ取込中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();

		String sYear = dlg.getYear();
		String sMonth = dlg.getMonth();
		sFrom = sYear + sMonth + "01";
		sTo = sYear + sMonth + "31";

		// 院内、院外診療区分ファイルの読み込み
		String inSrykbnMst = null;
		String outSrykbnMst = null;
		PropRead prop = new PropRead();
		inSrykbnMst = prop.getProp("in_srykbn");
		outSrykbnMst = prop.getProp("out_srykbn");

		// ここから、DB更新
		try {
			String nyugaikbn, ptid, ptnum, sryka, zainum, srykbn, sryym;
			String srysyukbn; // 診療区分
			String srycd[] = new String[5];
			String srysuryo[] = new String[5];
			String day[] = new String[31];
			String hihknjaname, hknnum;

			conn_orca = db_orca.getConnection();
			conn_drug = db_drug.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			db_drug.bigin();
			// 出庫から、指定年月データを一端削除
			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM t_expend");
			bsql.append(" WHERE '" + sFrom + "' <= exp_date AND exp_date <= '"
			        + sTo + "'");
      bsql.append(" AND hospnum = '" + hospnum + "'");
			db_drug.execute(bsql.toString());

			sryym = sYear + sMonth;
			// 診療行為から使用薬剤と数量を保持する。
			bsql.delete(0, bsql.length());
			bsql.append("SELECT nyugaikbn, ptid, sryka, zainum, srykbn,");
			bsql.append("srycd1, srysuryo1, srycd2, srysuryo2, srycd3, srysuryo3, srycd4, srysuryo4, srycd5, srysuryo5");
			bsql.append(", srysyukbn");// 診療区分
			bsql.append(" FROM tbl_sryact");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND sryym = '" + sryym + "'");

			// System.out.println(bsql.toString());

			sql = bsql.toString();
			stmt = conn_orca.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				nyugaikbn = rs.getString("nyugaikbn");
				ptid = rs.getString("ptid");
				sryka = rs.getString("sryka");
				zainum = rs.getString("zainum");
				srykbn = rs.getString("srykbn");
				srysyukbn = rs.getString("srysyukbn");// 診療区分
				srycd[0] = rs.getString("srycd1");
				srysuryo[0] = rs.getString("srysuryo1");
				srycd[1] = rs.getString("srycd2");
				srysuryo[1] = rs.getString("srysuryo2");
				srycd[2] = rs.getString("srycd3");
				srysuryo[2] = rs.getString("srysuryo3");
				srycd[3] = rs.getString("srycd4");
				srysuryo[3] = rs.getString("srysuryo4");
				srycd[4] = rs.getString("srycd5");
				srysuryo[4] = rs.getString("srysuryo5");

				// 患者IDの取得
				ptnum = "";
				bsql.delete(0, bsql.length());
				bsql.append("SELECT ptnum FROM tbl_ptnum");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND ptid = " + ptid);
			  //System.out.println(bsql.toString());
				sql = bsql.toString();
				stmt2 = conn_orca.createStatement();
				rs2 = stmt2.executeQuery(sql);
				if (rs2.next()) {
					ptnum = rs2.getString("ptnum");
				}
				rs2.close();
				stmt2.close();

				// 患者名の取得
				hihknjaname = "";
				bsql.delete(0, bsql.length());
				bsql.append("SELECT name FROM tbl_ptinf");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND ptid = " + ptid);
				sql = bsql.toString();
				stmt2 = conn_orca.createStatement();
				rs2 = stmt2.executeQuery(sql);
				if (rs2.next()) {
					hihknjaname = rs2.getString("name");
				}
				rs2.close();
				stmt2.close();

				for (int j = 0; j < 5; j++) {
					if (srycd[j].substring(0, 1).equals("6")) {
						// srycd[j].substring(0,2).equals("62") ||

						// 診療行為から使用薬剤と数量を保持する。
						bsql.delete(0, bsql.length());
						bsql.append("SELECT day_1,day_2,day_3,day_4,day_5,day_6,day_7,day_8,day_9,day_10,");
						bsql.append("day_11,day_12,day_13,day_14,day_15,day_16,day_17,day_18,day_19,day_20,");
						bsql.append("day_21,day_22,day_23,day_24,day_25,day_26,day_27,day_28,day_29,day_30,day_31");
						bsql.append(" FROM tbl_sryacct");
						bsql.append(" WHERE sryym = '" + sryym + "'");
            bsql.append(" AND   hospnum = '" + hospnum + "'");
						bsql.append(" AND   nyugaikbn = '" + nyugaikbn + "'");
						bsql.append(" AND   ptid = " + ptid + "");
						bsql.append(" AND   sryka = '" + sryka + "'");
						bsql.append(" AND   srykbn = '" + srykbn + "'");
						bsql.append(" AND   zainum = " + zainum + "");
						sql = bsql.toString();
						stmt2 = conn_orca.createStatement();
						rs2 = stmt2.executeQuery(sql);
						if (rs2.next()) {
							day[0] = rs2.getString("day_1");
							day[1] = rs2.getString("day_2");
							day[2] = rs2.getString("day_3");
							day[3] = rs2.getString("day_4");
							day[4] = rs2.getString("day_5");
							day[5] = rs2.getString("day_6");
							day[6] = rs2.getString("day_7");
							day[7] = rs2.getString("day_8");
							day[8] = rs2.getString("day_9");
							day[9] = rs2.getString("day_10");
							day[10] = rs2.getString("day_11");
							day[11] = rs2.getString("day_12");
							day[12] = rs2.getString("day_13");
							day[13] = rs2.getString("day_14");
							day[14] = rs2.getString("day_15");
							day[15] = rs2.getString("day_16");
							day[16] = rs2.getString("day_17");
							day[17] = rs2.getString("day_18");
							day[18] = rs2.getString("day_19");
							day[19] = rs2.getString("day_20");
							day[20] = rs2.getString("day_21");
							day[21] = rs2.getString("day_22");
							day[22] = rs2.getString("day_23");
							day[23] = rs2.getString("day_24");
							day[24] = rs2.getString("day_25");
							day[25] = rs2.getString("day_26");
							day[26] = rs2.getString("day_27");
							day[27] = rs2.getString("day_28");
							day[28] = rs2.getString("day_29");
							day[29] = rs2.getString("day_30");
							day[30] = rs2.getString("day_31");
							for (int i = 0; i < 31; i++) {
								try {
									int cnt = Integer.parseInt(day[i]);
									if (0 < cnt) {
										// 診療行為を取り込むかどうか判別
										int flgGet = 1;

										// 日付文字列を生成
										double su = Double.parseDouble(srysuryo[j]);
										double num = cnt * su;
										String expend_num = String.valueOf(num);
										String procDate;
										if ((i + 1) < 10) {
											procDate = sryym + "0"
											        + String.valueOf(i + 1);
										} else {
											procDate = sryym
											        + String.valueOf(i + 1);
										}
										// 院外処方区分を取得
										String denpnum, ingaishohokbn;// 院外処方区分取得用変数
										denpnum = "";
										bsql.delete(0, bsql.length());
										bsql.append("SELECT denpnum");
										bsql.append(" FROM tbl_jyurrk");
										bsql.append(" WHERE hospnum = '"
										        + hospnum + "'");
										bsql.append(" AND   ptid = " + ptid
										        + "");
										bsql.append(" AND   sryymd = "
										        + procDate + "");
										bsql.append(" AND   nyugaikbn = "
										        + nyugaikbn + "");
										bsql.append(" AND  ((zainum1  = "
										        + zainum + ") OR");
										bsql.append("       (zainum2  = "
										        + zainum + ") OR");
										bsql.append("       (zainum3  = "
										        + zainum + ") OR");
										bsql.append("       (zainum4  = "
										        + zainum + ") OR");
										bsql.append("       (zainum5  = "
										        + zainum + ") OR");
										bsql.append("       (zainum6  = "
										        + zainum + ") OR");
										bsql.append("       (zainum7  = "
										        + zainum + ") OR");
										bsql.append("       (zainum8  = "
										        + zainum + ") OR");
										bsql.append("       (zainum9  = "
										        + zainum + ") OR");
										bsql.append("       (zainum10 = "
										        + zainum + ") OR");
										bsql.append("       (zainum11 = "
										        + zainum + ") OR");
										bsql.append("       (zainum12 = "
										        + zainum + ") OR");
										bsql.append("       (zainum13 = "
										        + zainum + ") OR");
										bsql.append("       (zainum14 = "
										        + zainum + ") OR");
										bsql.append("       (zainum15 = "
										        + zainum + "))");
										sql = bsql.toString();
										stmt3 = conn_orca.createStatement();
										rs3 = stmt3.executeQuery(sql);
										if (rs3.next()) {
											denpnum = rs3.getString("denpnum");
										}
										rs3.close();
										stmt3.close();

										ingaishohokbn = "";
										bsql.delete(0, bsql.length());
										bsql.append("SELECT ingaishohokbn");
										bsql.append(" FROM tbl_syunou");
										bsql.append(" WHERE hospnum = '"
										        + hospnum + "'");
										bsql.append(" AND   ptid = " + ptid
										        + "");
										bsql.append(" AND   denpnum = "
										        + denpnum + "");
										bsql.append(" AND   skystymd <='"
										        + procDate + "' AND '"
										        + procDate + "' <= skyedymd");
										sql = bsql.toString();
										stmt3 = conn_orca.createStatement();
										rs3 = stmt3.executeQuery(sql);
										if (rs3.next()) {
											ingaishohokbn = rs3.getString("ingaishohokbn");
										}
										rs3.close();
										stmt3.close();
										// 院外処方区分が１＝院外ならば、暫定院外処理
										if (ingaishohokbn.equals("1")) {
											flgGet = 0;
										}

										// 入外区分が１＝入院ならば、暫定院内処理
										if (nyugaikbn.equals("1")) {
											flgGet = 1;
										}

										// 院内診療区分により、-> 院内処理
										String inSrykbn = inSrykbnMst;
										String outSrykbn = outSrykbnMst;
										if (flgGet == 0) {
											if (srysyukbn.equals(inSrykbn.substring(0, 3))) {
												flgGet = 1;
											}
											while (inSrykbn.indexOf(",") != -1) {
												inSrykbn = inSrykbn.substring(
												        4, inSrykbn.length());
												if (srysyukbn.equals(inSrykbn.substring(0, 3))) {
													flgGet = 1;
												}
											}
										} else
										// 院外診療区分により、-> 院外処理
										if (flgGet == 1) {
											if (srysyukbn.equals(outSrykbn.substring(0, 3))) {
												flgGet = 0;
											}
											while (outSrykbn.indexOf(",") != -1) {
												outSrykbn = outSrykbn.substring(4, outSrykbn.length());
												if (srysyukbn.equals(outSrykbn.substring(0, 3))) {
													flgGet = 0;
												}
											}
										}

										// 自動出庫設定でない薬剤は取り込まない
										bsql.delete(0, bsql.length());
										bsql.append("SELECT med_kind2");
										bsql.append(" FROM m_cont_item");
										bsql.append(" WHERE orca_med_cd ='"
										        + srycd[j] + "'");
                    bsql.append(" AND   hospnum = '" + hospnum + "'");
										sql = bsql.toString();
										stmt3 = conn_drug.createStatement();
										rs3 = stmt3.executeQuery(sql);
										if (rs3.next()) {
											if (rs3.getString("med_kind2").equals("1")) {
												flgGet = 0;
											}
										}
										rs3.close();
										stmt3.close();

										if (flgGet == 1) {

											// 最初に患者情報を取得
											hknnum = "";
											bsql.delete(0, bsql.length());
											bsql.append("SELECT hknnum");
											bsql.append(" FROM tbl_pthkninf");
											bsql.append(" WHERE hospnum = '"
											        + hospnum + "'");
											bsql.append(" AND   ptid = " + ptid
											        + "");
											bsql.append(" AND   tekstymd <='"
											        + procDate + "' AND '"
											        + procDate
											        + "' <= tekedymd");
											sql = bsql.toString();
											stmt3 = conn_orca.createStatement();
											rs3 = stmt3.executeQuery(sql);
											if (rs3.next()) {
												hknnum = rs3.getString("hknnum");
											}
											rs3.close();
											stmt3.close();

											// 出庫を作成。
											bsql.delete(0, bsql.length());
											bsql.append("SELECT expend_num");
											bsql.append(" FROM t_expend");
											bsql.append(" WHERE exp_date = '"
											        + procDate + "'");
											bsql.append(" AND   orca_user = "
											        + ptid + "");
											bsql.append(" AND   orca_med_cd ='"
											        + srycd[j] + "'");
                      bsql.append(" AND hospnum = '" + hospnum + "'");
											//System.out.println(bsql.toString());
											sql = bsql.toString();
											stmt3 = conn_drug.createStatement();
											rs3 = stmt3.executeQuery(sql);
											if (rs3.next()) {
												// 既にあるから、出庫を足す
												// 出庫レコードを新規に作成
												String tot = rs3.getString("expend_num");
												double dExpNum = Double.parseDouble(tot)
												        + Double.parseDouble(expend_num);
												tot = String.valueOf(dExpNum);

												bsql.delete(0, bsql.length());
												bsql.append("UPDATE t_expend SET ");
												bsql.append(" expend_num = "
												        + tot);
												bsql.append(",");
                        bsql.append(" hospnum = '" + hospnum + "'");
												bsql.append(" WHERE exp_date = '"
												                + procDate
												                + "'");
												bsql.append(" AND   orca_user = "
												                + ptid + "");
												bsql.append(" AND   orca_med_cd ='"
												                + srycd[j]
												                + "'");
                        bsql.append(" AND hospnum = '" + hospnum + "'");
												//System.out.println(bsql.toString());
												db_drug.execute(bsql.toString());
											} else {
												// 出庫レコードを新規に作成
												bsql.delete(0, bsql.length());
												bsql.append("INSERT INTO t_expend (exp_date,orca_user,orca_med_cd,");
												bsql.append("insurance,expend_num,name,orca_user_no,hospnum");
												bsql.append(") VALUES (");
												bsql.append("'" + procDate
												        + "',");
												bsql.append("" + ptid + ",");

												bsql.append("'" + srycd[j]
												        + "',");
												bsql.append("'" + hknnum
												                + "',");
												bsql.append("" + expend_num
												        + ",");
												bsql.append("'" + hihknjaname
												        + "',");
												bsql.append("'" + ptnum
												        + "',");
												bsql.append("'" + hospnum + "'");
												bsql.append(")");
												//System.out.println(bsql.toString());
												db_drug.execute(bsql.toString());
											}
											rs3.close();
											stmt3.close();
										}
									}
								} catch (Exception e) {
								}
							}
						}
						rs2.close();
						stmt2.close();
					}
				}
			}

			db_drug.commit();
			conn_drug.close();
			db_drug.close();

		} catch (SQLException sqle) {
			if (db_drug != null) {
				db_drug.rollback();
			}
			System.out.println("OrcaMedBatch run SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			if (db_drug != null) {
				db_drug.rollback();
			}
			System.out.println("OrcaMedBatch run Exception" + e.toString());
		} finally {
			db_orca.closeAllResource(rs, stmt, conn_orca);
			wait.destroy();
		}
	}
}

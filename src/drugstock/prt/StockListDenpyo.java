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
package drugstock.prt;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import drugstock.biz.BizStockListDenpyo;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.Common;
import drugstock.cmn.WaitMsg;
import drugstock.model.StockListDenpyoDetailMdl;
import drugstock.model.StockListDenpyoSumMdl;
import drugstock.model.StockListHead;

/**
 * 「仕入先別仕入伝票一覧表」出力画面処理
 * </p>
 */

public class StockListDenpyo extends StockList {

	final int PAGE_MAX_ROW = 24; // １ページの明細数
	final boolean myDebug = false;

	public StockListDenpyo() {
		super(PRT_DENPYO, "＊＊＊仕入先別伝票一覧表＊＊＊");
	}

	public void run() {

		if (!bPrint) {
			return;
		}
		try {
			StockListDenpyoDetailMdl item[] = null;
			StockListDenpyoSumMdl itemSum[] = new StockListDenpyoSumMdl[iContArr.length];
			StockListHead head = new StockListHead();
			String printFlg = super.printFlg;
			int ps, pe, pcnt, kcnt;

			// 合計値設定用変数
			ArrayList aList = new ArrayList();

			// 合計ワーク
			double sum_stcnum, sum_amnt, sum_discnt, sum_taxamnt, sum_stcamnt;
			// 全業者合計ワーク初期化
			int sum_all_stcnum = 0;
			int sum_all_amnt = 0;
			int sum_all_discnt = 0;
			int sum_all_taxamnt = 0;
			int sum_all_stcamnt = 0;

			// 明細ワーク
			char chr0 = ' ';
			char chr1 = '/';
			char chr2 = '(';
			char chr3 = ')';
			String sumTitle = "　　　　　＊　＊　合　計　＊　＊　　　　";
			String w_stcdate = null;
			String prt_stcdate = null;

			// 画面からの受け取り項目
			String w_contid = "";
			String w_contnm = "";

			String fileName = dataDir + "stocklist5.dat";
			String formName = formDir + "stocklist5.red";
			String formNameSum = formDir + "stocklist5_sum.red";
			String pageNo = null;
			boolean bLoop = false;
			boolean sumLine = false; // 合計行印字判定SW

			// データ取得
			WaitMsg waitData = new WaitMsg();
			waitData.setMsg1("計算中...");
			waitData.setMsg2("しばらくお待ちください。");
			waitData.msgdsp();

			Common com = new Common();

			BizStockListDenpyo biz = new BizStockListDenpyo();
			item = biz.getListData(key_FromDate, key_ToDate, iContArr);
			waitData.destroy();

			// 画面表示
			if ((bTable) && (iDetailRank.equals("detail"))) {
				String[] columnHead = { "業者", "No.", "日付", "品番", "品名", "単位",
				        "バラ数", "金額", "値引", "消費税", "購入金額", "仕入区分" };
				int[] columnWidth = { 120, 100, 100, 100, 300, 80, 100, 100,
				        100, 100, 100, 80 };
				Hashtable ht = new Hashtable();
				ht.put("nengetu", "20000101");
				ht.put("fromDate", headFromymd);
				ht.put("toDate", headToymd);

				DispTableDlg dlg = new DispTableDlg(ht, columnHead,
				        columnWidth, PRT_DENPYO_DETAIL);
				dlg.setItemDenpyoDetail(item);
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
				printFlg = dlg.getPrintFlg();
				item = dlg.getItemDenpyoDetail();
			}

			if ((printFlg.equals("csv")) && (iDetailRank.equals("detail"))) {
				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("CSVファイル出力中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				String csvName = prop.getProp("CSV_output_path");
				if (csvName == null)
					csvName = "/tmp/";
				csvName += "stocklistDenpyo" + strNowDate + ".csv";
				FileWriter bw = new FileWriter(csvName, false);
				for (int i = 0; i < item.length; i++) {
					bw = getCSVContKind(bw, item[i].getStrCont(), "first");
					bw = getCSVEl(bw, item[i].getBytesStcNo(), "camma");
					// 明細日付編集(Hyy-mm-dd)
					w_stcdate = new String(item[i].getBytesStcDate());
					prt_stcdate = Common.getWareki2(Integer.parseInt(w_stcdate.substring(0, 4)))
					        + "-"
					        + w_stcdate.substring(4, 6)
					        + "-"
					        + w_stcdate.substring(6, 8);
					bw = getCSVEl(bw, prt_stcdate.toCharArray(), "camma");

					bw = getCSVEl(bw, item[i].getBytesItemNum(), "camma");
					bw = getCSVEl(bw, item[i].getBytesItemName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnitName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit3Num(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit3(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit2Num(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit2(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit1Num(), "camma");
					bw = getCSVEl(bw, item[i].getBytesStcNum(), "camma");
					bw = getCSVEl(bw, item[i].getBytesAmnt(), "camma");
					bw = getCSVEl(bw, item[i].getBytesDiscnt(), "camma");
					bw = getCSVEl(bw, item[i].getBytesTaxAmnt(), "camma");
					bw = getCSVEl(bw, item[i].getBytesCrdName(), "end");
				}
				bw.close();
				waitPrint.destroy();
			}

			// 印刷／CSV出力処理
			if ((printFlg.equals("print")) || (printFlg.equals("csv"))) {
				for (int i = 0; i < iContArr.length; i++) {
					kcnt = 0;
					pcnt = 1;
					w_contid = iContArr[i].getid();
					w_contnm = iContArr[i].getName();

					WaitMsg waitPrint = new WaitMsg();
					if (iDetailRank.equals("detail")) {
						waitPrint.setMsg1("印刷中：" + w_contnm + "...");
						waitPrint.setMsg2("しばらくお待ちください。");
						waitPrint.msgdsp();
					}

					// 合計ワーク初期化
					sum_stcnum = 0;
					sum_amnt = 0;
					sum_discnt = 0;
					sum_taxamnt = 0;
					sum_stcamnt = 0;

					bLoop = true;
					sumLine = true; // 合計行印字SW初期化 （未印字状態）
					while (bLoop) {
						if (1 <= pcnt && pcnt <= 9)
							pageNo = "  ";
						else if (10 <= pcnt && pcnt <= 99)
							pageNo = " ";
						pageNo += new String(new Long(pcnt).toString());

						// ヘッダー編集
						head.setYMDFrom(headFromymd);
						head.setYMDTo(headToymd);
						head.setPrtDate(headPrtDate);
						head.setPrtPage(pageNo);
						head.setPrtContId(new String(w_contid));
						head.setPrtContNm(new String(w_contnm));

						// ヘッダー出力
						String pageFileName = fileName
						        + new String(new Long(pcnt).toString());
						FileWriter bw = new FileWriter(pageFileName, false);
						bw.write(head.getBytesYMDFrom());
						bw.write(head.getBytesYMDTo());
						bw.write(head.getBytesPrtDate());
						bw.write(head.getBytesPrtPage());
						bw.write(head.getBytesPrtContId());
						bw.write(head.getBytesPrtContNm());

						// 該当業者の個数カウント
						int count = 0;
						for (int j = 0; j < item.length; j++) {
							// System.out.println(item[j].getStrCont()+";"+w_contid)
							// ;
							if (item[j].getStrCont().equals(w_contid)) {
								count++;
							}
						}

						// 該当薬剤の抽出
						StockListDenpyoDetailMdl itemTmp[] = new StockListDenpyoDetailMdl[count];
						int k = 0;
						for (int j = 0; j < item.length; j++) {
							if (item[j].getStrCont().equals(w_contid)) {
								itemTmp[k] = item[j];
								k++;
							}
						}

						ps = (kcnt * PAGE_MAX_ROW);
						pe = ps + PAGE_MAX_ROW;

						int j = 0;
						// 明細出力
						for (j = ps; j < pe; j++) {
							if (j < itemTmp.length) {
								bw.write(itemTmp[j].getBytesStcNo());

								// 明細日付編集(Hyy-mm-dd)
								w_stcdate = new String(itemTmp[j].getBytesStcDate());
								prt_stcdate = Common.getWareki2(Integer.parseInt(w_stcdate.substring(0, 4)))
								        + "-"
								        + w_stcdate.substring(4, 6)
								        + "-"
								        + w_stcdate.substring(6, 8);
								bw.write(prt_stcdate);

								bw.write(itemTmp[j].getBytesItemNum());
								bw.write(itemTmp[j].getBytesItemName());
								bw.write(itemTmp[j].getBytesUnitName());
								bw.write(itemTmp[j].getBytesUnit3Num());
								bw.write(chr1);
								bw.write(itemTmp[j].getBytesUnit3());
								bw.write(itemTmp[j].getBytesUnit2Num());
								bw.write(chr1);
								bw.write(itemTmp[j].getBytesUnit2());
								bw.write(itemTmp[j].getBytesUnit1Num());
								bw.write(itemTmp[j].getBytesStcNum());
								bw.write(itemTmp[j].getBytesAmnt());
								bw.write(chr2);
								bw.write(itemTmp[j].getBytesDiscnt());
								bw.write(chr3);
								bw.write(itemTmp[j].getBytesTaxAmnt());
								bw.write(itemTmp[j].getBytesStcAmnt());
								// コード番号消去 04.02.04 onuki
								// bw.write(item[j].getBytesStcCd());
								bw.write(itemTmp[j].getBytesCrdName());

								// 合計ワークへの加算
								sum_stcnum = sum_stcnum
								        + Double.parseDouble(new String(
								                itemTmp[j].getBytesStcNum2()));
								sum_amnt = sum_amnt
								        + Double.parseDouble(new String(
								                itemTmp[j].getBytesAmnt2()));
								sum_discnt = sum_discnt
								        + Double.parseDouble(new String(
								                itemTmp[j].getBytesDiscnt2()));
								sum_taxamnt = sum_taxamnt
								        + Double.parseDouble(new String(
								                itemTmp[j].getBytesTaxAmnt2()));
								sum_stcamnt = sum_amnt - sum_discnt
								        + sum_taxamnt;
							} else {
								StockListDenpyoDetailMdl wk = new StockListDenpyoDetailMdl();
								if (sumLine) {
									// 合計行の印字
									sumLine = false; // 合計行印字SWセット（印字済状態）
									StockListDenpyoSumMdl sum = new StockListDenpyoSumMdl();
									sum.setBytesSumStcNum(new String(
									        new Double(sum_stcnum).toString()));
									sum.setBytesSumAmnt(new String(new Double(
									        sum_amnt).toString()));
									sum.setBytesSumDiscnt(new String(
									        new Double(sum_discnt).toString()));
									sum.setBytesSumTaxAmnt(new String(
									                new Double(sum_taxamnt).toString()));
									sum.setBytesSumStcAmnt(new String(
									                new Double(sum_stcamnt).toString()));

									bw.write(wk.getBytesStcNo());
									bw.write(wk.getBytesStcDate());
									bw.write(wk.getBytesItemNum());
									bw.write(sumTitle);
									bw.write(wk.getBytesUnitName());
									bw.write(wk.getBytesUnit3Num());
									bw.write(chr0);
									bw.write(wk.getBytesUnit3());
									bw.write(wk.getBytesUnit2Num());
									bw.write(chr0);
									bw.write(wk.getBytesUnit2());
									bw.write(wk.getBytesUnit1Num());
									bw.write(sum.getBytesSumStcNum());
									bw.write(sum.getBytesSumAmnt());
									bw.write(chr2);
									bw.write(sum.getBytesSumDiscnt());
									bw.write(chr3);
									bw.write(sum.getBytesSumTaxAmnt());
									bw.write(sum.getBytesSumStcAmnt());
									// bw.write(wk.getBytesStcCd());
									bw.write(wk.getBytesCrdName());
								} else {
									bw.write(wk.getBytesStcNo());
									bw.write(wk.getBytesStcDate());
									bw.write(wk.getBytesItemNum());
									bw.write(wk.getBytesItemName());
									bw.write(wk.getBytesUnitName());
									bw.write(wk.getBytesUnit3Num());
									bw.write(chr0);
									bw.write(wk.getBytesUnit3());
									bw.write(wk.getBytesUnit2Num());
									bw.write(chr0);
									bw.write(wk.getBytesUnit2());
									bw.write(wk.getBytesUnit1Num());
									bw.write(wk.getBytesStcNum());
									bw.write(wk.getBytesAmnt());
									bw.write(chr0);
									bw.write(wk.getBytesDiscnt());
									bw.write(chr0);
									bw.write(wk.getBytesTaxAmnt());
									bw.write(wk.getBytesStcAmnt());
									// bw.write(wk.getBytesStcCd());
									bw.write(wk.getBytesCrdName());
								}
							}
						}
						StockListDenpyoDetailMdl wk = new StockListDenpyoDetailMdl();
						if (sumLine && j >= itemTmp.length) {
							// 合計行の印字
							sumLine = false; // 合計行印字SWセット（印字済状態）
							StockListDenpyoSumMdl sum = new StockListDenpyoSumMdl();
							sum.setBytesSumStcNum(new String(new Double(
							        sum_stcnum).toString()));
							sum.setBytesSumAmnt(new String(new Double(sum_amnt).toString()));
							sum.setBytesSumDiscnt(new String(new Double(
							        sum_discnt).toString()));
							sum.setBytesSumTaxAmnt(new String(new Double(
							        sum_taxamnt).toString()));
							sum.setBytesSumStcAmnt(new String(new Double(
							        sum_stcamnt).toString()));

							bw.write(wk.getBytesStcNo());
							bw.write(wk.getBytesStcDate());
							bw.write(wk.getBytesItemNum());
							bw.write(sumTitle);
							bw.write(wk.getBytesUnitName());
							bw.write(wk.getBytesUnit3Num());
							bw.write(chr0);
							bw.write(wk.getBytesUnit3());
							bw.write(wk.getBytesUnit2Num());
							bw.write(chr0);
							bw.write(wk.getBytesUnit2());
							bw.write(wk.getBytesUnit1Num());
							bw.write(sum.getBytesSumStcNum());
							bw.write(sum.getBytesSumAmnt());
							bw.write(chr2);
							bw.write(sum.getBytesSumDiscnt());
							bw.write(chr3);
							bw.write(sum.getBytesSumTaxAmnt());
							bw.write(sum.getBytesSumStcAmnt());
							// bw.write(wk.getBytesStcCd());
							bw.write(wk.getBytesCrdName());
						} else {
							bw.write(wk.getBytesStcNo());
							bw.write(wk.getBytesStcDate());
							bw.write(wk.getBytesItemNum());
							bw.write(wk.getBytesItemName());
							bw.write(wk.getBytesUnitName());
							bw.write(wk.getBytesUnit3Num());
							bw.write(chr0);
							bw.write(wk.getBytesUnit3());
							bw.write(wk.getBytesUnit2Num());
							bw.write(chr0);
							bw.write(wk.getBytesUnit2());
							bw.write(wk.getBytesUnit1Num());
							bw.write(wk.getBytesStcNum());
							bw.write(wk.getBytesAmnt());
							bw.write(chr0);
							bw.write(wk.getBytesDiscnt());
							bw.write(chr0);
							bw.write(wk.getBytesTaxAmnt());
							bw.write(wk.getBytesStcAmnt());
							// bw.write(wk.getBytesStcCd());
							bw.write(wk.getBytesCrdName());
						}
						bw.close();
						kcnt++;

						// monpe印刷処理
						if ((printFlg.equals("print"))
						        && (iDetailRank.equals("detail"))) {
							PrintUtil.printForm(formName, pageFileName);
						}
						if (itemTmp.length <= pe) {
							bLoop = false;
							break;
						}
						pcnt++;
					}
					if (iDetailRank.equals("detail"))
						waitPrint.destroy();

					// 詳細表示時に計算した値を、合計変数配列に代入
					StockListDenpyoSumMdl wk = new StockListDenpyoSumMdl();

					wk.setBytesCont(w_contid);
					wk.setBytesSumStcNum(new String(new Double(sum_stcnum).toString()));
					wk.setBytesSumAmnt(new String(new Double(sum_amnt).toString()));
					wk.setBytesSumDiscnt(new String(new Double(sum_discnt).toString()));
					wk.setBytesSumTaxAmnt(new String(new Double(sum_taxamnt).toString()));
					wk.setBytesSumStcAmnt(new String(new Double(sum_stcamnt).toString()));

					sum_all_stcnum += sum_stcnum;
					sum_all_amnt += sum_amnt;
					sum_all_discnt += sum_discnt;
					sum_all_taxamnt += sum_taxamnt;
					sum_all_stcamnt += sum_stcamnt;

					aList.add(wk);
				}
				// 全業者合計を代入
				StockListDenpyoSumMdl wk = new StockListDenpyoSumMdl();
				wk.setBytesCont("合計");
				wk.setBytesSumStcNum(new String(new Double(sum_all_stcnum).toString()));
				wk.setBytesSumAmnt(new String(new Double(sum_all_amnt).toString()));
				wk.setBytesSumDiscnt(new String(new Double(sum_all_discnt).toString()));
				wk.setBytesSumTaxAmnt(new String(new Double(sum_all_taxamnt).toString()));
				wk.setBytesSumStcAmnt(new String(new Double(sum_all_stcamnt).toString()));
				aList.add(wk);

				itemSum = new StockListDenpyoSumMdl[aList.size()];
				itemSum = (StockListDenpyoSumMdl[])aList.toArray(itemSum);

				// }else{
				// if(iDetailRank.equals("sum")) {
			}

			// 画面表示
			if ((bTable) && (iDetailRank.equals("sum"))) {
				String[] columnHead = { "業者", "合計量", "金額", "値引", "消費税", "購入金額" };
				int[] columnWidth = { 120, 120, 120, 120, 120, 120 };
				Hashtable ht = new Hashtable();
				ht.put("nengetu", "20000101");
				ht.put("fromDate", headFromymd);
				ht.put("toDate", headToymd);

				DispTableDlg dlg = new DispTableDlg(ht, columnHead,
				        columnWidth, PRT_DENPYO_SUM);
				dlg.setItemDenpyoSum(itemSum);
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
				printFlg = dlg.getPrintFlg();
				itemSum = dlg.getItemDenpyoSum();
			}

			if ((printFlg.equals("csv")) && iDetailRank.equals("sum")) {
				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("CSVファイル出力中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				String csvName = prop.getProp("CSV_output_path");
				if (csvName == null)
					csvName = "/tmp/";
				csvName += "stocklistDenpyoSum" + strNowDate + ".csv";
				FileWriter bwSum = new FileWriter(csvName, false);
				for (int i = 0; i < itemSum.length; i++) {
					bwSum = getCSVContKind(bwSum, itemSum[i].getStrCont(),
					        "first");
					bwSum = getCSVEl(bwSum, itemSum[i].getStrSumStcNum().toCharArray(), "camma");
					bwSum = getCSVEl(bwSum, itemSum[i].getStrSumAmnt().toCharArray(), "camma");
					bwSum = getCSVEl(bwSum, itemSum[i].getStrSumDiscnt().toCharArray(), "camma");
					bwSum = getCSVEl(bwSum, itemSum[i].getStrSumTaxAmnt().toCharArray(), "camma");
					bwSum = getCSVEl(bwSum, itemSum[i].getStrSumStcAmnt().toCharArray(), "end");
				}
				bwSum.close();
				waitPrint.destroy();
			}

			if ((printFlg.equals("print")) && iDetailRank.equals("sum")) {
				pcnt = 1;

				com = new Common();

				head = new StockListHead();

				sumLine = true; // 合計行印字SW初期化 （未印字状態）
				if (1 <= pcnt && pcnt <= 9)
					pageNo = "  ";
				else if (10 <= pcnt && pcnt <= 99)
					pageNo = " ";
				pageNo += new String(new Long(pcnt).toString());

				// ヘッダー編集
				head.setYMDFrom(headFromymd);
				head.setYMDTo(headToymd);
				head.setPrtDate(headPrtDate);
				head.setPrtPage(pageNo);

				// ヘッダー出力
				String pageFileName = fileName
				        + new String(new Long(pcnt).toString());
				FileWriter bw = new FileWriter(pageFileName, false);
				bw.write(head.getBytesYMDFrom());
				bw.write(head.getBytesYMDTo());
				bw.write(head.getBytesPrtDate());
				bw.write(head.getBytesPrtPage());

				StockListDenpyoDetailMdl wk = new StockListDenpyoDetailMdl();
				StockListDenpyoSumMdl sum = new StockListDenpyoSumMdl();

				for (int i = 0; i < iContArr.length; i++) {

					w_contid = iContArr[i].getid();
					w_contnm = iContArr[i].getName();
					head.setPrtContId(new String(w_contid));
					head.setPrtContNm(new String(w_contnm));

					// 該当業者の個数カウント
					int count = 0;
					for (int j = 0; j < itemSum.length; j++) {
						// System.out.println(item[j].getStrCont()+";"+w_contid)
						// ;
						if (itemSum[j].getStrCont().equals(w_contid)) {
							count++;
						}
					}
					// 該当業者の抽出
					StockListDenpyoSumMdl itemSumTmp[] = new StockListDenpyoSumMdl[count];
					int k = 0;
					for (int j = 0; j < itemSum.length; j++) {
						// System.out.println(itemSum[j].getStrCont()+";"+w_contid)
						// ;
						if (itemSum[j].getStrCont().equals(w_contid)) {
							itemSumTmp[k] = itemSum[j];
							k++;
						}
					}

					// 合計ワークへの加算
					for (int j = 0; j < itemSumTmp.length; j++) {
						bw.write(wk.getBytesStcNo());

						bw.write(head.getBytesPrtContNm());

						bw.write(itemSumTmp[j].getBytesSumStcNum());
						bw.write(itemSumTmp[j].getBytesSumAmnt());
						bw.write(chr2);
						bw.write(itemSumTmp[j].getBytesSumDiscnt());
						bw.write(chr3);
						bw.write(itemSumTmp[j].getBytesSumTaxAmnt());
						bw.write(itemSumTmp[j].getBytesSumStcAmnt());
					}

				}
				// 全業者合計
				sum.setBytesSumStcNum(new String(new Double(sum_all_stcnum).toString()));
				sum.setBytesSumAmnt(new String(new Double(sum_all_amnt).toString()));
				sum.setBytesSumDiscnt(new String(new Double(sum_all_discnt).toString()));
				sum.setBytesSumTaxAmnt(new String(new Double(sum_all_taxamnt).toString()));
				sum.setBytesSumStcAmnt(new String(new Double(sum_all_stcamnt).toString()));

				bw.write(wk.getBytesStcNo());
				bw.write(sumTitle);
				bw.write(sum.getBytesSumStcNum());
				bw.write(sum.getBytesSumAmnt());
				bw.write(chr2);
				bw.write(sum.getBytesSumDiscnt());
				bw.write(chr3);
				bw.write(sum.getBytesSumTaxAmnt());
				bw.write(sum.getBytesSumStcAmnt());

				for (int j = iContArr.length; j < PAGE_MAX_ROW; j++) {
					bw.write(wk.getBytesStcNo());
					bw.write(wk.getBytesItemName());
					bw.write(wk.getBytesStcNum());
					bw.write(wk.getBytesAmnt());
					bw.write(chr0);
					bw.write(wk.getBytesDiscnt());
					bw.write(chr0);
					bw.write(wk.getBytesTaxAmnt());
					bw.write(wk.getBytesStcAmnt());
				}
				bw.close();

				// monpe印刷処理
				if (!myDebug) {
					PrintUtil.printForm(formNameSum, pageFileName);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

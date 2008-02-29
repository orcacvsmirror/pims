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
import java.util.Vector;

import drugstock.biz.BizStockListZaiko;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.Common;
import drugstock.cmn.Sprintf;
import drugstock.cmn.WaitMsg;
import drugstock.model.StockListHead;
import drugstock.model.StockListSumMdl;
import drugstock.model.StockListZaikoMdl;

/**
 * 「在庫一覧表」出力画面処理
 * </p>
 */

public class StockListZaiko extends StockList {

	final int PAGE_MAX_ROW = 25; // １ページの明細数
	final boolean myDebug = false;

	public StockListZaiko() {
		super(PRT_ZAIKO, "＊＊＊在庫一覧表＊＊＊");
	}

	public void run() {

		if (!bPrint) {
			return;
		}

		// 合計金額用 04.06.08 onuki
		// Vector vTotal = new Vector();

		StockListZaikoMdl item[] = null;
		// StockListHead head = new StockListHead();
		String printFlg = super.printFlg;

		// データ取得
		WaitMsg waitData = new WaitMsg();
		// waitData.setMsg1("計算中...");
		waitData.setMsg1("計算中です...");
		waitData.setMsg2("しばらくお待ちください。");
		waitData.msgdsp();

		Common com = new Common();

		BizStockListZaiko biz = new BizStockListZaiko();
		item = biz.getListData(nengetu, iItemKind);
		waitData.destroy();

		if (iDetailRank.equals("detail")) {
			try {
				StockListHead head = new StockListHead();

				// 画面表示
				if (bTable) {
					String[] columnHead = { "種別", "品番", "品名", "単位", "薬価基準",
					        "在庫単価", "単価率", "繰越量", "入庫量", "払出・使用量", "返品量",
					        "調整量", "在庫量", "在庫金額" };
					int[] columnWidth = { 50, 100, 300, 80, 100, 100, 100, 100,
					        100, 100, 100, 100, 100, 100 };
					Hashtable ht = new Hashtable();
					ht.put("nengetu", nengetu);

					DispTableDlg dlg = new DispTableDlg(ht, columnHead,
					        columnWidth, PRT_ZAIKO);
					dlg.setItemZaiko(item);
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
					item = dlg.getItemZaiko();
				}

				if (printFlg.equals("csv")) {
					WaitMsg waitPrint = new WaitMsg();
					waitPrint.setMsg1("CSVファイル出力中...");
					waitPrint.setMsg2("しばらくお待ちください。");
					waitPrint.msgdsp();

					// String csvName = "/tmp/stocklistZaiko.csv";
					String csvName = prop.getProp("CSV_output_path");
					if (csvName == null)
						csvName = "/tmp/";
					csvName += "stocklistZaiko" + strNowDate + ".csv";
					FileWriter bw = new FileWriter(csvName, false);
					for (int i = 0; i < item.length; i++) {
						bw = getCSVMedKind(bw, item[i].getMedKind(), "first");
						bw = getCSVEl(bw, item[i].getBytesNum(), "camma");
						bw = getCSVEl(bw, item[i].getBytesName(), "camma");
						bw = getCSVEl(bw, item[i].getBytesUnit(), "camma");
						bw = getCSVEl(bw, item[i].getBytesBase(), "camma");
						bw = getCSVEl(bw, item[i].getBytesStock(), "camma");
						bw = getCSVEl(bw, item[i].getBytesRate(), "camma");
						bw = getCSVEl(bw, item[i].getBytesKurikosi(), "camma");
						bw = getCSVEl(bw, item[i].getBytesNyuko(), "camma");
						bw = getCSVEl(bw, item[i].getBytesHarai(), "camma");
						bw = getCSVEl(bw, item[i].getBytesHenpin(), "camma");
						bw = getCSVEl(bw, item[i].getBytesTyousei(), "camma");
						bw = getCSVEl(bw, item[i].getBytesZaiko(), "camma");
						bw = getCSVEl(bw, item[i].getBytesKingaku(), "end");
					}
					bw.close();
					waitPrint.destroy();
				}

				if (printFlg.equals("print")) {
					int ps, pe, pcnt, kcnt;
					String fileName = dataDir + "stocklist6.dat";
					String formName = formDir + "stocklist6.red";
					String drugKind = null;
					String pageNo = null;
					boolean bLoop = false;

					for (int i = 0; i < itemKindArr.length; i++) {
						// 合計金額用 04.06.08 onuki
						String swork = null;
						double dtotal = 0;
						drugKind = itemKindArr[i].code;
						// System.out.println(drugKind);

						WaitMsg waitPrint = new WaitMsg();
						waitPrint.setMsg1("印刷中：" + itemKindArr[i].name + "...");
						waitPrint.setMsg2("しばらくお待ちください。");
						waitPrint.msgdsp();

						pcnt = 1;
						kcnt = 0;

						bLoop = true;
						while (bLoop) {
							if (1 <= pcnt && pcnt <= 9)
								pageNo = "  ";
							else if (10 <= pcnt && pcnt <= 99)
								pageNo = " ";
							pageNo += new String(new Long(pcnt).toString());

							head.setDateFrom(headFromDate);
							head.setPrtDate(headPrtDate);
							head.setPrtPage(pageNo);
							head.setPrtDrugKind(itemKindArr[i].name);

							// ヘッダーの出力
							String pageFileName = fileName
							        + new String(new Long(pcnt).toString());
							FileWriter bw = new FileWriter(pageFileName, false);
							bw.write(head.getBytesDateFrom());
							bw.write(head.getBytesPrtDate());
							bw.write(head.getBytesPrtPage());
							bw.write(head.getBytesPrtDrugKind());

							// 該当薬剤の個数カウント
							int count = 0;
							for (int j = 0; j < item.length; j++) {
								if (item[j].getMedKind().equals(drugKind)) {
									count++;
								}
							}
							// System.out.println( drugKind + ";" + count );

							// 該当薬剤の抽出
							StockListZaikoMdl itemTmp[] = new StockListZaikoMdl[count];
							int k = 0;
							for (int j = 0; j < item.length; j++) {
								if (item[j].getMedKind().equals(drugKind)) {
									itemTmp[k] = item[j];
									k++;
								}
							}

							ps = (kcnt * PAGE_MAX_ROW);
							pe = ps + PAGE_MAX_ROW;
							// 明細の出力
							for (int j = ps; j < pe; j++) {
								if (j < itemTmp.length) {
									bw.write(itemTmp[j].getBytesNum());
									bw.write(itemTmp[j].getBytesName());
									bw.write(itemTmp[j].getBytesUnit());
									bw.write(itemTmp[j].getBytesBase());
									bw.write(itemTmp[j].getBytesStock());
									bw.write(itemTmp[j].getBytesRate());
									bw.write(itemTmp[j].getBytesKurikosi());
									bw.write(itemTmp[j].getBytesNyuko());
									bw.write(itemTmp[j].getBytesHarai());
									swork = String.valueOf(
									        itemTmp[j].getBytesHenpin()).trim();
									if (Double.parseDouble(swork) != 0) {
										swork = Sprintf.formatCanma(12, 3,
										        Double.parseDouble(swork) * -1);
										bw.write(com.getByteData(swork, 10));
									} else {
										bw.write(itemTmp[j].getBytesHenpin());
									}
									bw.write(itemTmp[j].getBytesTyousei());
									bw.write(itemTmp[j].getBytesZaiko());
									bw.write(itemTmp[j].getBytesKingaku());

									// 合計金額用 04.06.08 onuki
									// swork =
									// String.valueOf(itemTmp[j].getBytesKingaku());
									// if (swork.indexOf("- ") == -1)
									// dtotal = dtotal +
									// Double.valueOf(swork).doubleValue();

								} else {
									StockListZaikoMdl wk = new StockListZaikoMdl();
									bw.write(wk.getBytesNum());
									bw.write(wk.getBytesName());
									bw.write(wk.getBytesUnit());
									bw.write(wk.getBytesBase());
									bw.write(wk.getBytesStock());
									bw.write(wk.getBytesRate());
									bw.write(wk.getBytesKurikosi());
									bw.write(wk.getBytesNyuko());
									bw.write(wk.getBytesHarai());
									bw.write(wk.getBytesHenpin());
									bw.write(wk.getBytesTyousei());
									bw.write(wk.getBytesZaiko());
									bw.write(wk.getBytesKingaku());
								}
							}
							bw.close();
							kcnt++;

							// monpe印刷処理
							PrintUtil.printForm(formName, pageFileName);
							if (itemTmp.length <= pe) {
								bLoop = false;
								break;
							}
							pcnt++;
						}
						// 合計金額用 04.06.08 onuki
						waitPrint.destroy();
						// vTotal.add(String.valueOf(dtotal));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 薬剤区分毎 合計金額集計表 04.06.08 onuki
		if (iDetailRank.equals("sum")) {
			try {
				// 合計金額用
				Vector vTotal = new Vector();
				for (int i = 0; i < itemKindArr.length; i++) {

					String swork = null;
					double dtotal = 0;
					// 該当薬剤の個数カウント
					int count = 0;
					for (int j = 0; j < item.length; j++) {
						if (item[j].getMedKind().equals(itemKindArr[i].code)) {
							count++;
						}
					}

					// 該当薬剤の抽出
					StockListZaikoMdl itemTmp[] = new StockListZaikoMdl[count];
					int k = 0;
					for (int j = 0; j < item.length; j++) {
						if (item[j].getMedKind().equals(itemKindArr[i].code)) {
							itemTmp[k] = item[j];
							k++;
						}
					}

					for (int j = 0; j < itemTmp.length; j++) {
						swork = String.valueOf(itemTmp[j].getBytesKingaku());
						if (swork.indexOf("- ") == -1)
							dtotal = dtotal
							        + Double.valueOf(swork).doubleValue();
					}

					vTotal.add(String.valueOf(dtotal));
				}

				double dwork = 0;
				double dtotal = 0;
				String sum = "";

				String fileName = dataDir + "stocklist_sum.dat";
				String formName = formDir + "stocklist_sum.red";

				// Common com = new Common();
				StockListHead head = new StockListHead();

				head.setDateFrom(headFromDate);
				head.setPrtDate(headPrtDate);
				head.setPrtPage("1");

				// ヘッダーの出力
				String pageFileName = fileName + "1";
				FileWriter bw = new FileWriter(pageFileName, false);
				bw.write("    ＊＊＊在庫量合計金額一覧表＊＊＊    ");
				bw.write(head.getBytesDateFrom());
				bw.write(head.getBytesPrtDate());
				bw.write(head.getBytesPrtPage());

				String down_to_decimal = prop.getProp("down_to_decimal");
				if (down_to_decimal == null)
					down_to_decimal = "0";
				if (down_to_decimal.equals("1") == false)
					down_to_decimal = "0";

				int intDownDecimal = 3;
				if (down_to_decimal.equals("1")) {
					intDownDecimal = 0;
				}
				//
				int i = 0;
				ArrayList aList = new ArrayList();

				for (i = 0; i < 10; i++) {
					if (i < itemKindArr.length) {
						dwork = Double.valueOf((String)vTotal.elementAt(i)).doubleValue();
						sum = Sprintf.formatCanma(12, intDownDecimal, dwork);
						bw.write(com.getByteData(itemKindArr[i].name, 60));
						bw.write(com.getByteData(sum + " 円", 23));

						// 合計表示用クラスに格納
						StockListSumMdl wk = new StockListSumMdl();
						wk.setIndex(itemKindArr[i].name);
						wk.setPrice(sum);
						dtotal = dtotal + dwork;
						aList.add(wk);
					} else {
						bw.write(com.getByteData("", 60));
						bw.write(com.getByteData("", 23));
					}
				}
				// 総合計
				String stotal = Sprintf.formatCanma(12, intDownDecimal, dtotal);
				bw.write(com.getByteData(stotal + " 円", 23));
				// 合計表示用クラスに格納
				StockListSumMdl wk = new StockListSumMdl();
				wk.setIndex("合計");
				wk.setPrice(stotal);
				aList.add(wk);

				StockListSumMdl itemSum[] = new StockListSumMdl[aList.size()];
				itemSum = (StockListSumMdl[])aList.toArray(itemSum);

				bw.close();

				// String printFlg = "print" ;
				// 画面表示
				if (bTable) {
					String[] columnHead = { "項目", "金額" };
					int[] columnWidth = { 100, 200 };
					Hashtable ht = new Hashtable();
					ht.put("nengetu", nengetu);

					DispTableDlg dlg = new DispTableDlg(ht, columnHead,
					        columnWidth, PRT_SUM);
					dlg.setItemSum(itemSum);
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
					// itemSum = dlg.getItemSum() ; //合計は並べ換え必要なし
				}

				if (printFlg.equals("csv")) {
					WaitMsg waitPrint = new WaitMsg();
					waitPrint.setMsg1("CSVファイル出力中...");
					waitPrint.setMsg2("しばらくお待ちください。");
					waitPrint.msgdsp();

					// csvName += "stocklistZaikoSum.csv";
					String csvName = prop.getProp("CSV_output_path");
					if (csvName == null)
						csvName = "/tmp/";
					csvName += "stocklistZaikoSum" + strNowDate + ".csv";
					FileWriter bwSum = new FileWriter(csvName, false);
					for (i = 0; i < itemSum.length; i++) {
						bwSum = getCSVEl(bwSum, itemSum[i].getIndex().toCharArray(), "first");
						bwSum = getCSVEl(bwSum, itemSum[i].getPrice().toCharArray(), "end");
					}
					bwSum.close();
					waitPrint.destroy();
				}

				if (printFlg.equals("print")) {
					WaitMsg waitPrint = new WaitMsg();
					waitPrint.setMsg1("印刷中...");
					waitPrint.setMsg2("しばらくお待ちください。");
					waitPrint.msgdsp();

					// monpe印刷処理
					if (!myDebug) {
						PrintUtil.printForm(formName, pageFileName);
					}
					waitPrint.destroy();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}

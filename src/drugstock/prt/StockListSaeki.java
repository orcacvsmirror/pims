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

import drugstock.biz.BizStockListSaeki;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.Common;
import drugstock.cmn.Sprintf;
import drugstock.cmn.WaitMsg;
import drugstock.model.StockListHead;
import drugstock.model.StockListSaekiMdl;
import drugstock.model.StockListSumMdl;

/**
 * 「品目別差益高分析表」出力画面処理
 * </p>
 */

public class StockListSaeki extends StockList {

	// 差益高分析表対応 04.02.24 onuki
	// final int PAGE_MAX_ROW = 50; // １ページの明細数
	final int PAGE_MAX_ROW = 25; // １ページの明細数
	final boolean myDebug = false;

	public StockListSaeki() {
		super(PRT_SAEKI, "＊＊＊品目別差益高分析表＊＊＊");

	}

	public void run() {

		if (!bPrint) {
			return;
		}

		// 合計金額用 04.06.08 onuki
		// Vector vTotal = new Vector();

		StockListSaekiMdl item[] = null;
		String printFlg = super.printFlg;

		// データ取得
		WaitMsg waitData = new WaitMsg();
		waitData.setMsg1("計算中...");
		waitData.setMsg2("しばらくお待ちください。");
		waitData.msgdsp();

		BizStockListSaeki biz = new BizStockListSaeki();
		item = biz.getListData(nengetu, iItemKind, iPrintRank);

		waitData.destroy();

		if (iDetailRank.equals("detail")) {

			try {
				StockListHead head = new StockListHead();

				// 画面表示
				if (bTable) {
					String[] columnHead = { "種別", "A", "B", "品番", "薬剤品名", "薬価",
					        "納入価", "差益", "使用量", "使用高", "％", "差益高", "％" };
					int[] columnWidth = { 50, 40, 40, 100, 300, 100, 100, 100,
					        100, 100, 60, 100, 60 };
					Hashtable ht = new Hashtable();
					ht.put("nengetu", nengetu);
					// ht.put("width", "600") ;

					DispTableDlg dlg = new DispTableDlg(ht, columnHead,
					        columnWidth, PRT_SAEKI);
					dlg.setItemSaeki(item);
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
					item = dlg.getItemSaeki();
				}

				if (printFlg.equals("csv")) {
					WaitMsg waitPrint = new WaitMsg();
					waitPrint.setMsg1("CSVファイル出力中...");
					waitPrint.setMsg2("しばらくお待ちください。");
					waitPrint.msgdsp();

					String csvName = prop.getProp("CSV_output_path");
					if (csvName == null)
						csvName = "/tmp/";
					csvName += "stocklistSaeki" + strNowDate + ".csv";
					FileWriter bw = new FileWriter(csvName, false);
					for (int i = 0; i < item.length; i++) {
						bw = getCSVMedKind(bw, item[i].getMedKind(), "first");
						bw = getCSVEl(bw, item[i].getBytesExpend_Rank(),
						        "camma");
						bw = getCSVEl(bw, item[i].getBytesMargin_Rank(),
						        "camma");
						bw = getCSVEl(bw, item[i].getBytesItemNum(), "camma");
						bw = getCSVEl(bw, item[i].getBytesItemName(), "camma");
						bw = getCSVEl(bw, item[i].getBytesMed_Price(), "camma");
						bw = getCSVEl(bw, item[i].getBytesStc_Price(), "camma");
						bw = getCSVEl(bw, item[i].getBytesMargin(), "camma");
						bw = getCSVEl(bw, item[i].getBytesUnitName(), "camma");
						bw = getCSVEl(bw, item[i].getBytesExpend_num(), "camma");
						bw = getCSVEl(bw, item[i].getBytesExpend_Price(),
						        "camma");
						bw = getCSVEl(bw, item[i].getBytesRate(), "camma");
						bw = getCSVEl(bw, item[i].getBytesMargin_Rate(), "end");
					}
					bw.close();
					waitPrint.destroy();
				}

				if (printFlg.equals("print")) {
					int ps, pe, pcnt, kcnt;
					String fileName = dataDir + "stocklist2.dat";
					String formName = formDir + "stocklist2_1.red";
					String pageNo = null;
					boolean bLoop = false;

					for (int i = 0; i < itemKindArr.length; i++) {
						// 合計金額用 04.06.08 onuki
						String swork = null;
						double dtotal = 0;

						String drugKind = itemKindArr[i].code;
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
							if (iPrintRank == "shiyou") {
								head.setPrtOrder("使用高");
							} else {
								head.setPrtOrder("差益高");
							}

							// ヘッダーの出力
							String pageFileName = fileName
							        + new String(new Long(pcnt).toString());
							FileWriter bw = new FileWriter(pageFileName, false);
							bw.write(head.getBytesDateFrom());
							bw.write(head.getBytesPrtDate());
							bw.write(head.getBytesPrtPage());
							bw.write(head.getBytesPrtDrugKind());
							bw.write(head.getBytesPrtOrder());

							// 該当薬剤の個数カウント
							int count = 0;
							for (int j = 0; j < item.length; j++) {
								if (item[j].getMedKind().equals(drugKind)) {
									count++;
								}
							}
							// System.out.println( drugKind + ";" + count );

							// 該当薬剤の抽出
							StockListSaekiMdl itemTmp[] = new StockListSaekiMdl[count];
							int k = 0;
							for (int j = 0; j < item.length; j++) {
								if (item[j].getMedKind().equals(drugKind)) {
									itemTmp[k] = item[j];
									k++;
								}
							}

							ps = (kcnt * PAGE_MAX_ROW);
							pe = ps + PAGE_MAX_ROW;
							k = 0;

							// 明細の出力
							for (int j = ps; j < pe; j++) {
								if (j < itemTmp.length) {
									bw.write(itemTmp[j].getBytesExpend_Rank()); // 使用高順位
									bw.write(itemTmp[j].getBytesMargin_Rank()); // 差益高順位
									bw.write(itemTmp[j].getBytesItemNum()); // 品番
									bw.write(itemTmp[j].getBytesItemName()); // 品名
									bw.write(itemTmp[j].getBytesMed_Price()); // 薬価
									bw.write(itemTmp[j].getBytesStc_Price()); // 納入価
									bw.write(itemTmp[j].getBytesMargin()); // 差益
									bw.write(itemTmp[j].getBytesUnitName()); // 単位
									bw.write(itemTmp[j].getBytesExpend_num()); // 使用量
									bw.write(itemTmp[j].getBytesExpend_Price()); // 使用高
									bw.write(itemTmp[j].getBytesMargin_Price()); // 差益高
									bw.write(itemTmp[j].getBytesRate()); // ％
									bw.write(itemTmp[j].getBytesMargin_Rate()); // 差益％

									// 合計金額用 04.06.08 onuki
									swork = String.valueOf(itemTmp[j].getBytesExpend_Price_num());
									if (swork.indexOf("- ") == -1)
										dtotal = dtotal
										        + Double.valueOf(swork).doubleValue();
								} else {
									StockListSaekiMdl wk = new StockListSaekiMdl();
									bw.write(wk.getBytesExpend_Rank()); // 使用高順位
									bw.write(wk.getBytesMargin_Rank()); // 差益高順位
									bw.write(wk.getBytesItemNum()); // 品番
									bw.write(wk.getBytesItemName()); // 品名
									bw.write(wk.getBytesMed_Price()); // 薬価
									bw.write(wk.getBytesStc_Price()); // 納入価
									bw.write(wk.getBytesMargin()); // 差益
									bw.write(wk.getBytesUnitName()); // 単位
									bw.write(wk.getBytesExpend_num()); // 使用量
									bw.write(wk.getBytesExpend_Price()); // 使用高
									bw.write(wk.getBytesMargin_Price()); // 差益高
									bw.write(wk.getBytesRate()); // ％
									bw.write(wk.getBytesMargin_Rate()); // 差益％
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
						waitPrint.destroy();
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
					StockListSaekiMdl itemTmp[] = new StockListSaekiMdl[count];
					int k = 0;
					for (int j = 0; j < item.length; j++) {
						if (item[j].getMedKind().equals(itemKindArr[i].code)) {
							itemTmp[k] = item[j];
							k++;
						}
					}

					for (int j = 0; j < itemTmp.length; j++) {
						swork = String.valueOf(itemTmp[j].getBytesExpend_Price_num());
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

				Common com = new Common();
				StockListHead head = new StockListHead();

				head.setDateFrom(headFromDate);
				head.setPrtDate(headPrtDate);
				head.setPrtPage("1");

				// ヘッダーの出力
				String pageFileName = fileName + "1";
				FileWriter bw = new FileWriter(pageFileName, false);
				bw.write("    ＊＊＊使用高合計金額一覧表＊＊＊    ");
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

					String csvName = prop.getProp("CSV_output_path");
					if (csvName == null)
						csvName = "/tmp/";
					csvName += "stocklistSaekiSum" + strNowDate + ".csv";
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
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import drugstock.biz.BizDeadStockList;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.Common;
import drugstock.cmn.PropRead;
import drugstock.cmn.WaitMsg;
import drugstock.model.DeadStockListMdl;
import drugstock.model.StockListHead;

/**
 * 「デッドストックリスト」出力画面処理
 * </p>
 */

public class DeadStockList extends StockList {

	final int PAGE_MAX_ROW = 55; // １ページの明細数
	final boolean myDebug = false;
	public static String TITLE_DEAD_STOCK = "＊＊デッドストックリスト＊＊";

	public DeadStockList() {
		super(PRT_DEAD_STOCK, TITLE_DEAD_STOCK);
	}

	public void run() {
		Vector vTotal = new Vector();

		// 薬剤区分毎在庫金額集計表
		try {
			DeadStockListMdl item[] = null;
			StockListHead head = new StockListHead();
			String printFlg = super.printFlg;

			// データ取得
			WaitMsg waitData = new WaitMsg();
			waitData.setMsg1("計算中...");
			waitData.setMsg2("しばらくお待ちください。");
			waitData.msgdsp();

			Common com = new Common();

			// 現在の日付と設定ファイルから基準日付を計算
			PropRead prop = new PropRead();
			String dead_stock_month = prop.getProp("dead_stock_month");
			if (dead_stock_month == null)
				dead_stock_month = "0";

			SimpleDateFormat tmpDateFormat = new SimpleDateFormat("MMdd");
			String strTmpDate = "20000101";
			String strDeadDate = "20000101";
			Date tmpDate = new Date();
			Calendar calendar = Calendar.getInstance();
			strTmpDate = calendar.get(Calendar.YEAR)
			        + tmpDateFormat.format(tmpDate);
			calendar.add(Calendar.MONTH, -1
			        * Integer.parseInt(dead_stock_month));
			tmpDate = calendar.getTime();
			strDeadDate = calendar.get(Calendar.YEAR)
			        + tmpDateFormat.format(tmpDate);
			// System.out.println("Dead："+ calendar.get(Calendar.YEAR)
			// + tmpDateFormat.format(tmpDate)) ;

			BizDeadStockList biz = new BizDeadStockList();
			item = biz.getListData(strNowDate, iContArr, itemKindArr,
			        strDeadDate);
			waitData.destroy();

			// 画面表示
			if (bTable) {
				String[] columnHead = { "業者名", "区分", "品番", "薬剤名", "最終出庫日",
				        "在庫量", "在庫金額" };
				int[] columnWidth = { 100, 80, 100, 300, 120, 120, 120 };
				Hashtable ht = new Hashtable();
				ht.put("nengetu", strNowDate);
				ht.put("deadDate", strDeadDate);

				DispTableDlg dlg = new DispTableDlg(ht, columnHead,
				        columnWidth, PRT_DEAD_STOCK);
				dlg.setItemDead(item);
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
				item = dlg.getItemDead();
			}

			if (printFlg.equals("csv")) {
				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("CSVファイル出力中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				String csvName = prop.getProp("CSV_output_path");
				if (csvName == null)
					csvName = "/tmp/";
				csvName += "stocklistDeadStock" + strTmpDate + ".csv";
				FileWriter bw = new FileWriter(csvName, false);
				for (int i = 0; i < item.length; i++) {
					bw = getCSVContKind(bw, item[i].getStrCont(), "first");
					bw = getCSVMedKind(bw, item[i].getMedKind(), "camma");
					bw = getCSVEl(bw, item[i].getBytesNum(), "camma");
					bw = getCSVEl(bw, item[i].getBytesName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesDate(), "camma");
					bw = getCSVEl(bw, item[i].getBytesStockNum(), "camma");
					bw = getCSVEl(bw, item[i].getBytesStockPrice(), "end");
				}
				bw.close();
				waitPrint.destroy();
			}

			if (printFlg.equals("print")) {

				// 画面からの受け取り項目
				String drugKind = null;
				String contKind = null;
				String w_contnm = null;
				String sum = "";

				int ps, pe, pcnt, kcnt;
				String fileName = dataDir + "stocklist_dead.dat";
				String formName = formDir + "stocklist_dead.red";
				String pageNo = null;
				boolean bLoop = false;

				for (int i = 0; i < iContArr.length; i++) {
					// drugKind = itemKindArr[0].code;
					contKind = iContArr[i].getid();
					w_contnm = iContArr[i].getName();

					WaitMsg waitPrint = new WaitMsg();
					waitPrint.setMsg1("印刷中：" + w_contnm + "...");
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
						head.setPrtContNm(new String(w_contnm));
						head.setPrtPage(pageNo);

						// ヘッダーの出力
						String pageFileName = fileName + "1";
						FileWriter bw = new FileWriter(pageFileName, false);
						bw.write(head.getBytesPrtDate());
						bw.write(head.getBytesPrtPage());
						bw.write(head.getBytesPrtContNm());
						bw.write(com.getByteData(TITLE_DEAD_STOCK, 40));

						// 凡例の出力
						bw.write(com.getByteData("区分", 8));
						bw.write(com.getByteData("品番", 10));
						bw.write(com.getByteData("薬剤名", 40));
						bw.write(com.getByteData("最終出庫日付", 12));
						bw.write(com.getByteData("薬剤量", 12));
						bw.write(com.getByteData("在庫金額", 12));

						// 該当業者の個数カウント
						int count = 0;
						for (int j = 0; j < item.length; j++) {
							if (item[j].getStrCont().equals(contKind)) {
								count++;
							}
						}

						// 該当薬剤の抽出
						DeadStockListMdl itemTmp[] = new DeadStockListMdl[count];
						int k = 0;
						for (int j = 0; j < item.length; j++) {
							if (item[j].getStrCont().equals(contKind)) {
								itemTmp[k] = item[j];
								k++;
							}
						}

						ps = (kcnt * PAGE_MAX_ROW);
						pe = ps + PAGE_MAX_ROW;
						for (int j = ps; j < pe; j++) {

							if (j < itemTmp.length) {
								// bw.write(com.getByteData(item[j].getMedKind(),
								// 8)) ;// 薬剤区分; 8
								for (k = 0; k < itemKindArr.length; k++) {
									if (itemKindArr[k].code.equals(itemTmp[j].getMedKind())) {
										bw.write(com.getByteData(
										        itemKindArr[k].name, 8)); // 薬剤区分;
										// 8
									}
								}
								bw.write(itemTmp[j].getBytesNum()); // 品番 ;10
								bw.write(itemTmp[j].getBytesName()); // 薬剤名
								// ;40
								bw.write(itemTmp[j].getBytesDate()); // 最終出庫日付
								// ;12
								bw.write(itemTmp[j].getBytesStockNum()); // 薬剤量
								// ;12
								bw.write(itemTmp[j].getBytesStockPrice()); // 在庫金額
								// ;12
							} else {
								bw.write(com.getByteData("", 94)); // 空白
							}
						}
						bw.close();
						kcnt++;

						// monpe印刷処理
						if (!myDebug) {
							PrintUtil.printForm(formName, pageFileName);
						}
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
}
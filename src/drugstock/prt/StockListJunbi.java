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
import java.util.Hashtable;

import drugstock.biz.BizStockListJunbi;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.WaitMsg;
import drugstock.model.StockListHead;
import drugstock.model.StockListJunbiMdl;

/**
 * 「棚卸準備一覧表」出力画面処理
 * </p>
 */

public class StockListJunbi extends StockList {

	final int PAGE_MAX_ROW = 25; // １ページの明細数
	final boolean myDebug = false;

	public StockListJunbi() {
		super(PRT_JUNBI, "＊＊＊棚卸準備一覧表＊＊＊");
	}

	public void run() {

		if (!bPrint) {
			return;
		}

		try {
			int[] itemLen = new int[itemKindArr.length];
			StockListJunbiMdl item[] = null;
			StockListHead head = new StockListHead();
			String printFlg = super.printFlg;

			// データ取得
			WaitMsg waitData = new WaitMsg();
			waitData.setMsg1("計算中...");
			waitData.setMsg2("しばらくお待ちください。");
			waitData.msgdsp();

			BizStockListJunbi biz = new BizStockListJunbi();
			item = biz.getListData(nengetu, iItemKind);

			waitData.destroy();

			// 画面表示
			if (bTable) {
				String[] columnHead = { "種別", "品番", "薬剤品名", "在庫単価", "当月在庫量",
				        "単位", "在庫金額" };
				int[] columnWidth = { 40, 100, 300, 100, 100, 80, 100 };
				Hashtable ht = new Hashtable();
				ht.put("nengetu", nengetu);

				DispTableDlg dlg = new DispTableDlg(ht, columnHead,
				        columnWidth, PRT_JUNBI);
				dlg.setItemJunbi(item);
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
				item = dlg.getItemJunbi();
			}

			if (printFlg.equals("csv")) {
				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("CSVファイル出力中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				String csvName = prop.getProp("CSV_output_path");
				if (csvName == null)
					csvName = "/tmp/";
				csvName += "stocklistJunbi" + strNowDate + ".csv";
				FileWriter bw = new FileWriter(csvName, false);
				for (int i = 0; i < item.length; i++) {
					bw = getCSVMedKind(bw, item[i].getMedKind(), "first");
					bw = getCSVEl(bw, item[i].getBytesItemNum(), "camma");
					bw = getCSVEl(bw, item[i].getBytesItemName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesStockPrice(), "camma");
					bw = getCSVEl(bw, item[i].getBytesNowStock(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit3Num(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit3(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit2Num(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit2(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnitName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUnit1Num(), "end");
				}
				bw.close();
				waitPrint.destroy();
			}
			if (printFlg.equals("print")) {
				int ps, pe, pcnt, kcnt;
				String fileName = dataDir + "stocklist1.dat";
				String formName = formDir + "stocklist1.red";
				String pageNo = null;
				boolean bLoop = false;

				// 印刷処理
				for (int i = 0; i < itemKindArr.length; i++) {
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
						// head.setPrtDrugKind(iItemKind);

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
						StockListJunbiMdl itemTmp[] = new StockListJunbiMdl[count];
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
								bw.write(itemTmp[j].getBytesItemNum());
								bw.write(itemTmp[j].getBytesItemName());
								bw.write(itemTmp[j].getBytesStockPrice());
								bw.write(itemTmp[j].getBytesNowStock());
								bw.write(itemTmp[j].getBytesUnit3Num());
								bw.write(itemTmp[j].getBytesUnit3());
								bw.write(itemTmp[j].getBytesUnit2Num());
								bw.write(itemTmp[j].getBytesUnit2());
								bw.write(itemTmp[j].getBytesUnitName());
								bw.write(itemTmp[j].getBytesUnit1Num());

							} else {
								StockListJunbiMdl wk = new StockListJunbiMdl();
								bw.write(wk.getBytesItemNum());
								bw.write(wk.getBytesItemName());
								bw.write(wk.getBytesStockPrice());
								bw.write(wk.getBytesNowStock());
								bw.write(wk.getBytesUnit3Num());
								bw.write(wk.getBytesUnit3());
								bw.write(wk.getBytesUnit2Num());
								bw.write(wk.getBytesUnit2());
								bw.write(wk.getBytesUnitName());
								bw.write(wk.getBytesUnit1Num());
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
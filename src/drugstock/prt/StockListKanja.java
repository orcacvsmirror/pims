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

import drugstock.biz.BizStockListKanja;
import drugstock.cl.DispTableDlg;
import drugstock.cmn.WaitMsg;
import drugstock.model.StockListHead;
import drugstock.model.StockListKanjaMdl;

/**
 * 「指定品目使用患者一覧表」出力画面処理
 * </p>
 */

public class StockListKanja extends StockList {

	final int PAGE_MAX_ROW = 24; // １ページの明細数
	final boolean myDebug = false;

	public StockListKanja() {
		super(PRT_KANJA, "＊＊＊指定品目使用患者一覧表＊＊＊");
	}

	public void run() {

		if (!bPrint) {
			return;
		}
		try {
			StockListKanjaMdl item[] = null;
			StockListHead head = new StockListHead();
			// boolean isPrintable = false ;
			String printFlg = super.printFlg;

			WaitMsg waitData = new WaitMsg();
			waitData.setMsg1("計算中...");
			waitData.setMsg2("しばらくお待ちください。");
			waitData.msgdsp();

			BizStockListKanja biz = new BizStockListKanja();
			item = biz.getListData(nengetu, aList_item, AndOr_flg);

			waitData.destroy();

			// 画面表示
			if (bTable) {
				String[] columnHead = { "連番", "患者番号", "氏名", "保険", "薬剤", "使用日",
				        "払出量" };
				int[] columnWidth = { 40, 160, 160, 40, 40, 400, 100 };
				Hashtable ht = new Hashtable();
				ht.put("nengetu", nengetu);
				for (int i = 0; i < 5; i++) {
					if (iItemArr.length <= i) {
						ht.put("med_code_name_" + i, "  ");
					} else {
						ht.put("med_code_name_" + i, iItemArr[i].code + "    "
						        + iItemArr[i].name);
					}
				}

				DispTableDlg dlg = new DispTableDlg(ht, columnHead,
				        columnWidth, PRT_KANJA);
				dlg.setItemKanja(item);
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
				item = dlg.getItemKanja();
			}

			if (printFlg.equals("csv")) {
				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("CSVファイル出力中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				String csvName = prop.getProp("CSV_output_path");
				if (csvName == null)
					csvName = "/tmp/";
				csvName += "stocklistKanja" + strNowDate + ".csv";
				FileWriter bw = new FileWriter(csvName, false);
				for (int i = 0; i < item.length; i++) {
					bw = getCSVEl(bw, item[i].getBytesOrca_User_No(), "first");
					bw = getCSVEl(bw, item[i].getBytesName(), "camma");
					bw = getCSVEl(bw, item[i].getBytesInsurance(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUse_Med(), "camma");
					bw = getCSVEl(bw, item[i].getBytesUse_Day(), "camma");
					bw = getCSVEl(bw, item[i].getBytesExpend_Num(), "end");
				}
				bw.close();
				waitPrint.destroy();
			}

			if (printFlg.equals("print")) {
				int ps, pe, pcnt, kcnt;
				String fileName = dataDir + "stocklist4.dat";
				String formName = formDir + "stocklist4.red";
				String drugKind = null;
				String pageNo = null;
				boolean bLoop = false;

				WaitMsg waitPrint = new WaitMsg();
				waitPrint.setMsg1("印刷中...");
				waitPrint.setMsg2("しばらくお待ちください。");
				waitPrint.msgdsp();

				// ソート後のデータ取得
				StockListKanjaMdl itemTmp[] = new StockListKanjaMdl[item.length];
				for (int j = 0; j < item.length; j++) {
					itemTmp[j] = item[j];
				}

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

					// ヘッダーの出力
					String pageFileName = fileName
					        + new String(new Long(pcnt).toString());
					FileWriter bw = new FileWriter(pageFileName, false);
					bw.write(head.getBytesDateFrom());
					bw.write(head.getBytesPrtDate());
					bw.write(head.getBytesPrtPage());
					if (AndOr_flg.equals("AND")) {
						head.setPrtAndOr("ＡＮＤ");
						bw.write(head.getBytesPrtAndOr());
					} else {
						head.setPrtAndOr("ＯＲ");
						bw.write(head.getBytesPrtAndOr());
					}

					for (int i = 0; i < iItemArr.length; i++) {
						head.setPrtItemNo(iItemArr[i].code);
						head.setPrtItemNm(iItemArr[i].name);
						bw.write(head.getBytesPrtItemNo());
						bw.write(head.getBytesPrtItemNm());
					}
					for (int i = iItemArr.length; i < 5; i++) {
						head.setPrtItemNo("");
						head.setPrtItemNm("");
						bw.write(head.getBytesPrtItemNo());
						bw.write(head.getBytesPrtItemNm());
					}

					ps = (kcnt * PAGE_MAX_ROW);
					pe = ps + PAGE_MAX_ROW;
					// 明細の出力
					for (int j = ps; j < pe; j++) {
						if (j < itemTmp.length) {
							bw.write(itemTmp[j].getBytesSeqNo()); // 連番
							bw.write(itemTmp[j].getBytesOrca_User_No()); // 患者番号
							bw.write(itemTmp[j].getBytesName()); // 氏名
							bw.write(itemTmp[j].getBytesInsurance()); // 保険
							bw.write(itemTmp[j].getBytesUse_Med()); // 品目
							bw.write(itemTmp[j].getBytesUse_Day()); // 日付
							bw.write(itemTmp[j].getBytesExpend_Num()); // 出庫数

						} else {
							StockListKanjaMdl wk = new StockListKanjaMdl();
							bw.write(wk.getBytesSeqNo()); // 連番
							bw.write(wk.getBytesOrca_User_No()); // 患者番号
							bw.write(wk.getBytesName()); // 氏名
							bw.write(wk.getBytesInsurance()); // 保険
							bw.write(wk.getBytesUse_Med()); // 品目
							bw.write(wk.getBytesUse_Day()); // 日付
							bw.write(wk.getBytesExpend_Num()); // 出庫数

						}
					}

					bw.close();
					kcnt++;

					// monpe印刷処理
					if (!myDebug) {
						PrintUtil.printForm(formName, pageFileName);
					}
					if (item.length <= pe) {
						bLoop = false;
						break;
					}
					pcnt++;
				}
				waitPrint.destroy();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
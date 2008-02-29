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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import drugstock.cl.PrintSettingDlg;
import drugstock.cmn.Common;
import drugstock.cmn.PropRead;
import drugstock.model.CodeName;
import drugstock.model.ItemName;
import drugstock.model.SyuruiCdNm;

/**
 * 出力画面処理のスーパークラス
 */

public abstract class StockList implements Runnable, Cloneable {

	public PrintSettingDlg dlg = null;
	protected String nengetu = null; // 画面入力年月（クエリ用 yyyymm形式）
	protected String key_FromDate = null; // 画面入力From年月日（クエリ用 yyyymmdd形式）
	protected String key_ToDate = null; // 画面入力To年月日（クエリ用 yyyymmdd形式）
	protected String headFromDate = null; // 画面入力年月（FROM)
	protected String headToDate = null; // 画面入力年月（TO)
	protected String headFromymd = null; // 画面入力年月日（FROM)
	protected String headToymd = null; // 画面入力年月日（TO)
	protected String headPrtDate = null; // 印刷日付
	protected String strNowDate = null; // 薬剤マスタ用日付
	protected boolean bPrint = false; // 印刷するか？
	protected boolean bTable = false; // テーブル表示するか？
	protected String iContCD = "0"; // 業者指定ID 0:全指定
	protected String iItemKind = "0"; // 品番 0:全指定
	protected String iPrintRank = "0"; // 印刷順 0:使用高
	protected String iDetailRank = "0"; // 印刷順 0:合計
	protected SyuruiCdNm itemKindArr[] = null;
	protected CodeName iContArr[] = null;
	protected ItemName iItemArr[] = null;
	protected String scrTitle = null;
	protected int syori_Mode;
	protected String AndOr_flg = null;
	protected ArrayList aList_item = new ArrayList();
	protected String printFlg = null;

	public final String homeDir = System.getProperty("user.home");
	public final String formDir = homeDir + "/stock/frm/";
	public final String dataDir = homeDir + "/stock/data/";
	PropRead prop = new PropRead();
	// 薬剤印刷モード
	public static int PRT_JUNBI = 1; // 棚卸準備一覧表
	public static int PRT_SAEKI = 2; // 差益高分析表
	public static int PRT_DEAD_STOCK = 3; // デッドストックリスト
	public static int PRT_HACCHU = 4; // 発注薬剤一覧
	public static int PRT_KANJA = 5; // 患者別使用量一覧
	public static int PRT_DENPYO = 6; // 伝票一覧
	public static int PRT_DENPYO_DETAIL = 61; // 伝票一覧
	public static int PRT_DENPYO_SUM = 62; // 伝票一覧
	public static int PRT_ZAIKO = 8; // 在庫一覧表
	public static int PRT_INVENT = 9; // 棚卸一覧表
	public static int PRT_MED_MASTER = 10; // 薬剤マスタ一覧表
	public static int PRT_SUM = 20; // 薬剤マスタ一覧表
	public static int PRT_SHIYO = 99; // 使用高分析表

	Thread thread = null;

	public void start() {
		if (thread != null) {
			printFlg = dlg.getPrintFlg();
			// System.out.println(printFlg) ;

			thread.start();
		}
	}

	public void stop() {
		thread = null;
	}

	public StockList(int mode, String printTitle) {
		dlg = new PrintSettingDlg();

		if (mode == PRT_JUNBI) {
			dlg.disableFromTo();
			dlg.disableConter();
			dlg.disableItemNo();
			dlg.disablePrintRank();
			dlg.disableDetailSum();
		} else if (mode == PRT_SAEKI) {
			dlg.disableFromTo();
			dlg.disableConter();
			dlg.disableItemNo();
		} else if (mode == PRT_SHIYO) {
			dlg.disableFromTo();
			dlg.disableConter();
			dlg.disableDrugKind();
			dlg.disableItemNo();
			dlg.disablePrintRank();
			dlg.disableDetailSum();
		} else if (mode == PRT_KANJA) {
			dlg.disableFromTo();
			dlg.disableConter();
			dlg.disableDrugKind();
			dlg.disablePrintRank();
			dlg.disableDetailSum();
		} else if (mode == PRT_DENPYO) {
			dlg.disableyyyymm();
			dlg.disableDrugKind();
			dlg.disableItemNo();
			dlg.disablePrintRank();
		} else if ((mode == PRT_ZAIKO) || (mode == PRT_INVENT)) {
			dlg.disableFromTo();
			dlg.disableConter();
			dlg.disableDrugKind();
			dlg.disableItemNo();
			dlg.disablePrintRank();
		} else if ((mode == PRT_MED_MASTER) || (mode == PRT_DEAD_STOCK)
		        || (mode == PRT_HACCHU)) {
			dlg.disableyyyymm();
			dlg.disableFromTo();
			dlg.disableItemNo();
			dlg.disablePrintRank();
			dlg.disableDetailSum();
		}

		syori_Mode = mode;

		dlg.SetSyoriMode(mode);
		dlg.Init_Scr_Disp();
		scrTitle = printTitle;
		StockListScr();
	}

	public StockList() {
		dlg = new PrintSettingDlg();
		StockListScr();
	}

	private void StockListScr() {
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
		dlg.setPaneTitle(scrTitle);
		dlg.show();
		if (dlg.IsOK()) {
			bTable = dlg.IsTable();

			String iMonth;
			if (dlg.getMonth().length() != 2)
				iMonth = "0" + dlg.getMonth();
			else
				iMonth = dlg.getMonth();

			// 抽出条件(年月）
			nengetu = dlg.getYear() + iMonth;
			if (syori_Mode != PRT_DENPYO && syori_Mode != PRT_MED_MASTER
			        && syori_Mode != PRT_DEAD_STOCK && syori_Mode != PRT_HACCHU) {
				headFromDate = Common.getWareki(Integer.parseInt(dlg.getYear()))
				        + "年" + iMonth + "月分";
			}

			// プリント日付
			Date nowDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(nowDate);
			SimpleDateFormat format = new SimpleDateFormat("年MM月dd日作成");
			headPrtDate = Common.getWareki(calendar.get(Calendar.YEAR))
			        + format.format(nowDate);

			// 日付(マスタ印刷用)
			SimpleDateFormat nowDateFormat = new SimpleDateFormat("MMdd");
			strNowDate = calendar.get(Calendar.YEAR)
			        + nowDateFormat.format(nowDate);

			// 画面入力From年月日 編集
			String iFromMonth;
			if (dlg.getFromMonth().length() != 2)
				iFromMonth = "0" + dlg.getFromMonth();
			else
				iFromMonth = dlg.getFromMonth();

			String iFromDay;
			if (dlg.getFromDay().length() != 2)
				iFromDay = "0" + dlg.getFromDay();
			else
				iFromDay = dlg.getFromDay();

			key_FromDate = dlg.getFromYear() + iFromMonth + iFromDay;
			if (syori_Mode == PRT_DENPYO) {
				headFromymd = Common.getWareki(Integer.parseInt(dlg.getFromYear()))
				        + "年" + iFromMonth + "月" + iFromDay + "日";
			}

			// 画面入力To年月日 編集
			String iToMonth;
			if (dlg.getToMonth().length() != 2)
				iToMonth = "0" + dlg.getToMonth();
			else
				iToMonth = dlg.getToMonth();

			String iToDay;
			if (dlg.getToDay().length() != 2)
				iToDay = "0" + dlg.getToDay();
			else
				iToDay = dlg.getToDay();

			key_ToDate = dlg.getToYear() + iToMonth + iToDay;
			if (syori_Mode == PRT_DENPYO) {
				headToymd = Common.getWareki(Integer.parseInt(dlg.getToYear()))
				        + "年" + iToMonth + "月" + iToDay + "日";
			}

			int iKindSel = 0;
			int iContSel = 0;
			bPrint = true;

			// 指定業者設定
			if (syori_Mode == PRT_DENPYO || syori_Mode == PRT_MED_MASTER
			        || syori_Mode == PRT_DEAD_STOCK || syori_Mode == PRT_HACCHU) {
				iContCD = dlg.getContractor();
				iContSel = dlg.getContSel();
				ArrayList aList_cont = new ArrayList();
				if (iContCD.equals("0")) {
					for (int i = 0; i < dlg.contItem.length; i++) {
						aList_cont.add(dlg.contItem[i]);

					}
				} else {
					aList_cont.add(dlg.contItem[iContSel - 1]);

				}
				iContArr = new CodeName[aList_cont.size()];
				iContArr = (CodeName[])aList_cont.toArray(iContArr);
			}

			// 詳細／合計設定 04.03.24 & 06.03
			if (syori_Mode == PRT_DENPYO || syori_Mode == PRT_SAEKI
			        || syori_Mode == PRT_ZAIKO || syori_Mode == PRT_INVENT) {

				iDetailRank = dlg.getDetail();
			}

			// 指定薬剤種別の取得
			if (syori_Mode == PRT_JUNBI || syori_Mode == PRT_SAEKI
			        || syori_Mode == PRT_ZAIKO || syori_Mode == PRT_INVENT
			        || syori_Mode == PRT_MED_MASTER
			        || syori_Mode == PRT_DEAD_STOCK || syori_Mode == PRT_HACCHU) {
				iItemKind = dlg.getDrug();
				iKindSel = dlg.getiKindSel();
				ArrayList aList_kind = new ArrayList();
				if (iItemKind.equals("0")) {
					for (int i = 0; i < dlg.syuruiCdNm.length; i++) {
						aList_kind.add(dlg.syuruiCdNm[i]);
					}
				} else {
					aList_kind.add(dlg.syuruiCdNm[iKindSel - 1]);
				}
				itemKindArr = new SyuruiCdNm[aList_kind.size()];
				itemKindArr = (SyuruiCdNm[])aList_kind.toArray(itemKindArr);
			}

			// 品番情報の取得
			if (syori_Mode == PRT_KANJA) {

				AndOr_flg = dlg.getAndOr();

				// ArrayList aList_item = new ArrayList();
				ItemName item = new ItemName();
				for (int i = 1; i < 6; i++) {
					item = dlg.getItemNo(i);
					if (item.code.equals("") || item.code.equals(null)) {
					} else {
						aList_item.add(item);
					}
				}
				iItemArr = new ItemName[aList_item.size()];
				iItemArr = (ItemName[])aList_item.toArray(iItemArr);

			}

			// 印刷順設定 04.02.28
			if (syori_Mode == PRT_SAEKI) {
				iPrintRank = dlg.getRank();
			}

			thread = new Thread(this);
		}
	}

	protected FileWriter getCSVEl(FileWriter inbw, char[] inBytes, String flg) {
		FileWriter bw = inbw;
		String str = "";

		// 先頭の要素でなければ、区切り文字","を付加
		if (flg.equals("first") == false) {
			str += ',';
		}

		// スペースを消去し、"で囲む
		str += '"';
		for (int i = 0; i < inBytes.length; i++) {
			if (inBytes[i] != ' ') {
				str += inBytes[i];
				// "が値に含まれていたら、""とする
				if (inBytes[i] == '"') {
					str += '"';
				}
			}
		}
		str += '"';
		// 最後の要素のとき、改行文字"\n"を付加
		if (flg.equals("end")) {
			str += '\n';
		}

		char[] outCh = new char[str.length()];
		outCh = str.toCharArray();

		try {
			bw.write(outCh);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bw;
	}

	protected FileWriter getCSVMedKind(FileWriter inbw, String code, String flg) {
		String drugName = null;
		for (int j = 0; j < itemKindArr.length; j++) {
			if (code.equals(itemKindArr[j].code)) {
				drugName = itemKindArr[j].name;
				break;
			}
		}
		if (drugName == null) {
			drugName = "不明";
		}
		char[] medKindCh = new char[drugName.length()];
		medKindCh = drugName.toCharArray();

		FileWriter bw = inbw;
		bw = this.getCSVEl(bw, medKindCh, flg);
		return bw;
	}

	protected FileWriter getCSVContKind(FileWriter inbw, String code, String flg) {
		String contName = null;
		for (int j = 0; j < iContArr.length; j++) {
			if (code.equals(iContArr[j].getid())) {
				contName = iContArr[j].getName();
				break;
			} else if (code.equals("合計")) {
				contName = "合計";
				break;
			}
		}
		if (contName == null) {
			contName = "不明";
		}
		char[] contKindCh = new char[contName.length()];
		contKindCh = contName.toCharArray();

		FileWriter bw = inbw;
		bw = this.getCSVEl(bw, contKindCh, flg);
		return bw;
	}

}
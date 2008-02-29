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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.regex.Pattern;

import drugstock.biz.BizContractor;
import drugstock.biz.BizContradrug;
import drugstock.biz.BizInventStock;
import drugstock.cl.OrcaDrugImportInitDlg;
import drugstock.cmn.CSVElementException;
import drugstock.cmn.Common;
import drugstock.cmn.PropRead;
import drugstock.cmn.MsgDlg;
import drugstock.cmn.WaitMsg;
import drugstock.db.ComDatabase;
import drugstock.model.CodeName;
import drugstock.model.ContItem;
import drugstock.model.OrcaMedicine;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * CSVファイルから初期薬剤マスタ、初期在庫を設定する
 */

public class OrcaDrugImportInit implements Runnable {

  String hospnum = null;
	OrcaDrugImportInitDlg dlg = null;
	Thread thread;

	private boolean isError = false;
	private StringBuffer strError = new StringBuffer(256);
	private StringBuffer strLog = new StringBuffer(256);

	private static final int MAX_CSV_EL_NUM = 13;
	private int maxCSVLine = 2;

	public OrcaDrugImportInit() {
		dlg = new OrcaDrugImportInitDlg();
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
		String[] strEl = new String[MAX_CSV_EL_NUM];
		ContItem item[] = null;
		WaitMsg wait = new WaitMsg();

		wait.setMsg1("CSVファイルを取込中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();
		maxCSVLine = getCSVLine();
		item = this.ctrlCSV();
		wait.destroy();

		wait.setMsg1("在庫管理システムに取込中です。");
		wait.setMsg2("しばらくお待ちください。");
		wait.msgdsp();
		int inputLine = 0;
		for (int i = 0; i < maxCSVLine; i++) {
			if (item[i] != null) {
				inputLine++;
				this.setContItemDB(item[i]);
				this.setInventDB(i, item[i]);
			}
		}
		wait.destroy();
		strLog.append(strError + "\n");
		strLog.append(maxCSVLine + "行のCSVファイルを解析しました。\n");
		strLog.append(inputLine + "行の薬剤を設定しました。\n");

		// ログ出力
		try {
			PropRead prop = new PropRead();
			String csvName = prop.getProp("CSV_init_input_log");
			if (csvName == null)
				csvName = "/tmp/";
			csvName += "CSV_init_input_log.txt";
			FileWriter bw = new FileWriter(csvName, false);

			char[] outCh = new char[strLog.toString().length()];
			outCh = strLog.toString().toCharArray();
			bw.write(outCh);
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * CSVファイルの行数を検出
	 */
	private int getCSVLine() {
		String fileName = dlg.getPath();
		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			int line = 0;
			while (bf.readLine() != null) {
				line++;
			}
			bf.close();
			return line;

		} catch (IOException e) {
			System.out.println("ファイルが見つかりません\n" + e.toString());
			return 0;
		}
	}

	/**
	 * CSVファイルを読み込む
	 */
	private ContItem[] ctrlCSV() {
		String fileName = dlg.getPath();
		ContItem item[] = new ContItem[maxCSVLine];
		MsgDlg msgdlg = new MsgDlg();

		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			String lineBuffer = null;
			boolean isTmpError = false;
			int line = 0;
			while ((lineBuffer = bf.readLine()) != null) {

				item[line] = analyseCSV(line + 1, getCSVEl(lineBuffer));
				line++;
			}
			bf.close();

		} catch (IOException e) {
			strLog.append("ファイルが見つかりません\n" + e.toString() + "\n");
			msgdlg.msgdsp("ファイルが見つかりません", MsgDlg.ERROR_MESSAGE);
			return null;
		}
		return item;
	}

	/**
	 * CSVの要素で、"(ダブルクォーテーション)で括られた文字列を抽出する
	 */
	private String[] getCSVEl(String str) {
		// Pattern pat = Pattern.compile( "(\"[^\"]*(?:\"\"[^\"]*)*\"|[^,]*)," )
		// ;

		try {
			if (getCountChar(str, '"') % 2 != 0) {
				throw new CSVElementException("行目：CSVのダブルクォーテーション関連のエラーです");
			}

			StringBuffer strNoDC = new StringBuffer(256);

			// '"'がない場合は、コンマを削除しない
			if (getCountChar(str, '"') == 0) {
				strNoDC.append(str);
			} else {
				// ダブルクォーテーションで文字列を分割する
				Pattern patDC = Pattern.compile("\"");
				String[] strDivDC = patDC.split(str);

				for (int i = 0; i < strDivDC.length; i++) {
					// ダブルクォーテーションで括られている＝要素の最初と最後が','でなかったなら、
					// 要素内のコンマ全削除
					if ((strDivDC[i].startsWith(",") == false)
					        && (strDivDC[i].endsWith(",") == false)) {
						strNoDC.append(delCharInStr(strDivDC[i], ','));
					} else {
						strNoDC.append(strDivDC[i]);
					}
				}
			}

			Pattern patComma = Pattern.compile(",");
			String[] strEl = patComma.split(strNoDC.toString());

			return strEl;
		} catch (Exception e) {
			strLog.append("CSV要素検出のエラーです" + e.toString() + "\n");
			return null;
		}

	}

	// 特定の文字が、文字列内に含まれる数を返す
	private int getCountChar(String str, char ch) {
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == ch) {
				count++;
			}
		}
		return count;
	}

	// 文字列から、特定の文字をすべて消去する
	private String delCharInStr(String str, char ch) {
		StringBuffer sb = new StringBuffer(256);
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) != ch) {
				sb.append(str.charAt(i));
			}
		}
		return sb.toString();
	}

	/**
	 * 読み込んだCSVファイルの一行を解析する
	 */
	private ContItem analyseCSV(int line, String[] strEl) {

		try {
			if ((strEl == null) || (strEl.length != MAX_CSV_EL_NUM)) {
				throw new CSVElementException(line + "行目：CSVフォーマットエラーです");
			}

			strError.delete(0, strError.length());
			BizContradrug biz = new BizContradrug();

			String orca_med_cd = "";
			String item_no = "";
			String med_nm = "";
			String med_kn = "";
			String cont_id = getContId(biz, strEl[2]);
			String med_kind1 = getSyuruiCd(biz, strEl[3]);
			String med_kind3 = "";
			String unit_price = getDoubleStr(strEl[4]);
			String pack_unit3 = getDoubleStr(strEl[5]);
			String pack_unit2 = getDoubleStr(strEl[6]);
			String pack_unit1 = "1.000";
			String hacchu_p = getDoubleStr(strEl[10]);
			String del_flg = "0";
			String discount = getDiscount(strEl[11]);
			String med_kind2 = getShukkoFlg(strEl[12]);

			OrcaMedicine tmpOrcaMedicine = getOrcaMedicine(biz, strEl[0]);

			if (tmpOrcaMedicine == null) {
				strNullSetError("ORCA薬剤番号が存在しません\n");
			} else {
				orca_med_cd = getStrElseNull(tmpOrcaMedicine.orca_med_cd);
				item_no = getStrElseNull(strEl[1], tmpOrcaMedicine.item_no);
				med_nm = getStrElseNull(tmpOrcaMedicine.med_nm);
				med_kn = getStrElseNull(tmpOrcaMedicine.med_kn);
				med_kind1 = getStrElseNull(med_kind1, tmpOrcaMedicine.med_kind);
				unit_price = getStrElseZero(unit_price,
				        tmpOrcaMedicine.med_price);
			}
			// 初期在庫量を計算
			String initMedNum = this.getInitMedNum(pack_unit3, pack_unit2,
			        getDoubleStr(strEl[7]), getDoubleStr(strEl[8]),
			        getDoubleStr(strEl[9]));

			// 該当行にエラーがあり、DB登録しない場合
			if (isError) {
				strLog.append(strError.toString());
				strLog.append(line + "行目：該当行にエラーがあるため、DB登録できませんでした。\n");
				isError = false;
				return null;
			} else {
				return new ContItem(cont_id, // 業者ＩＤ
				        orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
				        item_no, // 品番
				        med_nm, // 薬剤名称
				        med_kn, // 薬剤名称カナ
				        med_kind1, // 薬剤種類１
				        initMedNum, // 薬剤種類１名称 -> 初期バラ数を格納
				        med_kind2, // 薬剤種類２
				        med_kind3, // 薬剤種類３
				        pack_unit3, // 梱包単位
				        pack_unit2, // 包装単位
				        pack_unit1, // バラ単位
				        unit_price, // 最新納入単価
				        discount, // 単品値引率
				        "", // 単位名
				        "", // 薬価
				        hacchu_p); // 発注用P点
			}
		} catch (CSVElementException e) {
			System.out.println(e.toString());
			return null;
		}
	}

	/**
	 * CSV解析データを薬剤DB"m_cont_item"に書き込む
	 */
	private void setContItemDB(ContItem item) {
		OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("INSERT INTO m_cont_item ( ");
			bsql.append(" cont_id, orca_med_cd, item_no, ");
			bsql.append(" med_nm, med_kn, ");
			bsql.append(" med_kind1, med_kind2, med_kind3,");
			bsql.append(" pack_unit3, pack_unit2, pack_unit1,");
			bsql.append(" unit_price, discount, ");
			bsql.append(" del_flg, hacchu_p, hospnum");
			bsql.append(" ) VALUES (");
			bsql.append(item.cont_id + ",");
			bsql.append("'" + item.orca_med_cd + "',");
			bsql.append("'" + item.item_no + "',");
			bsql.append("'" + item.med_nm + "',");
			bsql.append("'" + item.med_kn + "',");
			bsql.append("'" + item.med_kind1 + "',");
			bsql.append("'" + item.med_kind2 + "',");
			bsql.append("'" + item.med_kind3 + "',");
			bsql.append(item.pack_unit3 + ",");
			bsql.append(item.pack_unit2 + ",");
			bsql.append(item.pack_unit1 + ",");
			bsql.append(item.unit_price + ",");
			bsql.append(item.discount + ",");
			bsql.append("'0',");
			bsql.append(item.hacchu_p + ",");
			bsql.append("'" + hospnum + "')");

			db.execute(bsql.toString());
			db.commit();
			conn.close();
			db.close();

		} catch (Exception e) {
			if (db != null) {
				db.rollback();
			}
			System.out.println("OrcaDrugImportInit run Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

	}

	/**
	 * ORCA薬剤番号(短縮番号)から名称、カナ名(在庫単価が空だった場合は薬価)を取得
	 */
	private OrcaMedicine getOrcaMedicine(BizContradrug biz, String orca_med_cd) {
		try {
			if (orca_med_cd == null) {
				strNullSetError("該当するORCA薬剤番号がありません\n");
				return null;
			}
			OrcaMedicine cItem = new BizContradrug().getOrca_medicine(orca_med_cd);
			String orca_med_tmp = null;
			if (cItem != null) {
				orca_med_tmp = cItem.orca_med_cd;
			} else {
				strNullSetError("該当するORCA薬剤番号がありません\n");
				return null;
			}

			if (orca_med_tmp == null) {
				orca_med_tmp = biz.get_orcaMedCd_from_orcaMedCd(orca_med_cd);
				if (orca_med_tmp == null) {
					strNullSetError(orca_med_cd + "該当するORCA薬剤番号、短縮番号が存在しません\n");
					return null;
				}
			} else {
				orca_med_cd = orca_med_tmp;
			}

			OrcaMedicine orcaMed = biz.getOrca_medicine(orca_med_cd);
			// 品番として、短縮番号を格納
			// orcaMed.setContId( biz.get_ORCADB_tanshuku(orca_med_cd)[0] ) ;
			return orcaMed;
		} catch (Exception e) {
			System.out.println("該当するORCA薬剤番号がありません\n" + e.toString());
			return null;
		}

	}

	/**
	 * cont_id;業者名が正常かどうかを判別する。
	 */
	private String getContId(BizContradrug biz, String cont_id) {

		if (cont_id == null) {
			return strNullSetError("業者名が空です\n");
		} else {
			boolean isTmpError = true;
			BizContractor bizCont = new BizContractor();
			CodeName[] codeName = bizCont.getCodeName();
			for (int i = 0; i < codeName.length; i++) {
				if (codeName[i].getName().equals(cont_id)) {
					cont_id = codeName[i].getid();
					isTmpError = false;
				}
			}
			if (isTmpError) {
				return strNullSetError("業者名が正常ではありません\n");
			} else {
				return cont_id;
			}
		}
	}

	/**
	 * med_kind1;薬剤区分名が正常かどうかを判別する。
	 */
	private String getSyuruiCd(BizContradrug biz, String med_kind1) {

		if (med_kind1 == null) {
			return strNullSetError("薬剤区分名が空です\n");
		} else if (med_kind1.equals("")) {
			return null;
		} else {
			boolean isTmpError = true;
			SyuruiCdNm[] syuruiCdNm = biz.getMed_kind_list();
			for (int i = 0; i < syuruiCdNm.length; i++) {
				if (syuruiCdNm[i].getName().equals(med_kind1)) {
					med_kind1 = syuruiCdNm[i].getCode();
					isTmpError = false;
				}
			}
			if (isTmpError) {
				return strNullSetError("薬剤区分名が正常ではありません\n");
			} else {
				return med_kind1;
			}
		}
	}

	/**
	 * 値引き率が0〜100の間にあるか判別、範囲外の場合は"0"値を返す。
	 */
	private String getDiscount(String str) {
		str = getDoubleStr(str);

		double tmpDiscount = 0.0;
		tmpDiscount = Double.parseDouble(str);
		if ((tmpDiscount > 100) || (tmpDiscount < 0)) {
			tmpDiscount = 0.0;
		}
		return Double.toString(tmpDiscount);
	}

	/**
	 * 自動出庫の設定判別
	 */
	private String getShukkoFlg(String str) {

		if ((str.equals("なし")) || (str.equals("無"))) {
			return "1";
		} else {
			return "0";
		}
	}

	/**
	 * 基本文字列と予備文字列のうち、nullでないものを返す。ただし、基本文字列を優先する。
	 */
	private String getStrElseNull(String str) {
		return getStrElseNull(null, str);
	}

	private String getStrElseNull(String str, String preStr) {
		if ((str == null) || (str.equals(""))) {
			if (preStr == null) {
				return strNullSetError("orca薬剤コード関連のエラーです\n");
			} else {
				return preStr;
			}
		} else {
			return str;
		}
	}

	/**
	 * 基本文字列と予備文字列のうち、値"0.0"でないものを返す。ただし、基本文字列を優先する。
	 */
	private String getStrElseZero(String str, String preStr) {
		if (str.equals("0.0")) {
			if (preStr == null) {
				return "0.0";
			} else {
				return preStr;
			}
		} else {
			return str;
		}
	}

	/**
	 * エラー文字列を設定し、無効な文字列の戻り値として空文字列を返す。
	 */
	private String strNullSetError(String str) {
		strError.append(str);
		isError = true;
		return "";
	}

	/**
	 * 配列が実数値文字列かどうかを判別する。 実数値文字列だった場合、入力文字列自身を返す。
	 * 数字以外の文字や、nullだった場合、実数値"0.0"の文字として値を返す。
	 */
	private String getDoubleStr(String strEl) {
		double tmpDouble = 0.0;
		try {
			tmpDouble = Double.parseDouble(strEl);
		} catch (NumberFormatException e) {
			System.out.println("OrcaDrugImportInit run Exception"
			        + e.toString());
		} catch (Exception e) {
			System.out.println("OrcaDrugImportInit run Exception"
			        + e.toString());
		}
		return Double.toString(tmpDouble);
	}

	/**
	 * 入力された年月文字列を取得する。
	 */
	private String getYearMonth() {
		String sYear = dlg.getYear();
		String sMonth = dlg.getMonth();

		int tmpYear = Integer.parseInt(sYear);
		int tmpMonth = Integer.parseInt(sMonth);
		// Calendarでは0が１月なので、補正
		tmpMonth -= 1;

		Common com = new Common();
		return com.setStrDate(tmpYear, tmpMonth, 1, "yyyyMM");
	}

	/**
	 * 梱包数と包装数からバラ数を計算する。
	 */
	private String getInitMedNum(String packKonpou, String packHousou,
	        String konpou, String housou, String Bara) {

		double doubleHousou = Double.parseDouble(konpou)
		        * Double.parseDouble(packKonpou);
		double doubleBara = (doubleHousou + Double.parseDouble(housou))
		        * Double.parseDouble(packHousou);
		doubleBara += Double.parseDouble(Bara);
		return Double.toString(doubleBara);
	}

	/**
	 * 初期バラ数を棚卸DBに登録する。
	 */
	private void setInventDB(int line, ContItem item) {
		String strBara = item.med_kind_name;
		String strItemNo = item.item_no;
		String errorLog = null;

		BizInventStock bizI = new BizInventStock();
		errorLog = bizI.newInventStock(strItemNo, strBara, getYearMonth());
		strError.append(line + "行目：" + errorLog);
	}
}

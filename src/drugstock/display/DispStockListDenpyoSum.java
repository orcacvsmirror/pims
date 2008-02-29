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
package drugstock.display;

import java.util.Vector;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import drugstock.biz.BizContractor;
import drugstock.biz.BizContradrug;
import drugstock.cmn.Sort;
import drugstock.model.CodeName;
import drugstock.model.StockListDenpyoSumMdl;
import drugstock.model.SyuruiCdNm;

/**
 * 「伝票一覧表」(合計)画面処理／表内部データ制御
 */

public class DispStockListDenpyoSum {

	// index値の項目が「数字」であることを示す配列
	private int[] numFlag = new int[6];
	private final int INDEX_NUM = 0;
	private final int INDEX_STR = 1;
	private final int INDEX_DEF = 2;

	/**
	 * コンストラクタ 要素が数字か文字列かの指定をします。
	 */
	public DispStockListDenpyoSum() {
		for (int i = 0; i < numFlag.length; i++) {
			switch (i) {
			case 0:
				numFlag[i] = INDEX_STR;
				break;
			default:
				numFlag[i] = INDEX_NUM;
				break;
			}
		}
	}

	/**
	 * 画面表示の情報を取得し、モデルを返します。
	 * 
	 * @param item
	 *            出力項目の配列
	 * @param isOrderSmall
	 *            降順：true、昇順：false
	 * @param comboSelect
	 *            選択された項目のindex値
	 * @return モデルの配列を返します。
	 */
	public StockListDenpyoSumMdl[] getTmpItem(StockListDenpyoSumMdl[] item,
	        boolean isOrderSmall, int comboSelect) {

		Sort sort = new Sort(isOrderSmall);
		int[] sortArr = null;

		String[] strArr = new String[item.length];
		int[] intArr = new int[item.length];

		for (int i = 0; i < item.length; i++) {
			switch (comboSelect) {

			case 0:
				strArr[i] = item[i].getStrCont();
				break; // 業者：文字列
			case 1:
				strArr[i] = item[i].getStrSumStcNum();
				break; // 合計量：実数
			case 2:
				strArr[i] = item[i].getStrSumAmnt();
				break; // 金額：実数
			case 3:
				strArr[i] = item[i].getStrSumDiscnt();
				break; // 値引：実数
			case 4:
				strArr[i] = item[i].getStrSumTaxAmnt();
				break; // 消費税額：実数
			case 5:
				strArr[i] = item[i].getStrSumStcAmnt();
				break; // 購入金額：実数
			default:
				break;
			}
		}

		StockListDenpyoSumMdl[] tmpItem = new StockListDenpyoSumMdl[item.length];

		switch (numFlag[comboSelect]) {
		case INDEX_NUM:
			sortArr = sort.intSort(strArr);
			break;
		case INDEX_STR:
			sortArr = sort.strSort(strArr);
			break;
		default:
			sortArr = sort.defSort(item.length);
			break;
		}

		for (int i = 0; i < item.length; i++) {
			tmpItem[i] = item[sortArr[i]];
		}

		return tmpItem;
	}

	/**
	 * モデルを取得し、項目セットを返します。
	 * 
	 * @param tmpItem
	 *            モデルの配列
	 * @return Vector型の項目セットを返します。
	 */
	public Vector getVdata(StockListDenpyoSumMdl[] tmpItem) {

		Vector vdata = new Vector();

		BizContradrug bizsyurui = new BizContradrug();
		SyuruiCdNm[] ssyuruiCdNm = bizsyurui.getMed_kind_list();
		BizContractor bizCont = new BizContractor();
		CodeName[] contCdNm = bizCont.getCodeName();

		for (int i = 0; i < tmpItem.length; i++) {
			Vector vcdata = new Vector();

			// 業者名を取得
			String contStr = "";
			for (int j = 0; j < contCdNm.length; j++) {
				// System.out.println(tmpItem[i].getStrCont()+";"+
				// contCdNm[j].getid()) ;
				if (tmpItem[i].getStrCont().equals(contCdNm[j].getid())) {
					contStr = contCdNm[j].getName();
				} else if (tmpItem[i].getStrCont().equals("合計")) {
					contStr = "合計";
				}
			}
			vcdata.addElement(contStr); // 業者：文字列

			vcdata.addElement(tmpItem[i].getStrSumStcNum()); // 合計量：実数
			vcdata.addElement(tmpItem[i].getStrSumAmnt()); // 金額：実数
			vcdata.addElement(tmpItem[i].getStrSumDiscnt()); // 値引：実数
			vcdata.addElement(tmpItem[i].getStrSumTaxAmnt()); // 消費税額：実数
			vcdata.addElement(tmpItem[i].getStrSumStcAmnt()); // 購入金額：実数

			vcdata.addElement(""); // 実在庫入力
			vdata.addElement(vcdata);
		}
		return vdata;
	}

	public JTable getCellRenderer(JTable inSchTable) {
		JTable schTable = inSchTable;

		// テーブル右詰項目の定義
		DefaultTableCellRenderer rrend = new DefaultTableCellRenderer();
		DefaultTableCellRenderer crend = new DefaultTableCellRenderer();
		rrend.setHorizontalAlignment(SwingConstants.RIGHT);
		crend.setHorizontalAlignment(SwingConstants.CENTER);

		for (int i = 0; i < numFlag.length; i++) {
			if (numFlag[i] == INDEX_NUM) {
				schTable.getColumnModel().getColumn(i).setCellRenderer(rrend);
			}
		}

		return schTable;
	}

}

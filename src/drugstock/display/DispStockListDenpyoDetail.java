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
import drugstock.cmn.Common;
import drugstock.cmn.Sort;
import drugstock.model.CodeName;
import drugstock.model.StockListDenpyoDetailMdl;
import drugstock.model.SyuruiCdNm;

/**
 * 「伝票一覧表」(詳細)画面処理／表内部データ制御
 */

public class DispStockListDenpyoDetail {

	// index値の項目が「数字」であることを示す配列
	private int[] numFlag = new int[13];
	private final int INDEX_NUM = 0;
	private final int INDEX_STR = 1;
	private final int INDEX_DEF = 2;

	/**
	 * コンストラクタ 要素が数字か文字列かの指定をします。
	 */
	public DispStockListDenpyoDetail() {
		for (int i = 0; i < numFlag.length; i++) {
			switch (i) {
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
				numFlag[i] = INDEX_NUM;
				break;
			default:
				numFlag[i] = INDEX_STR;
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
	public StockListDenpyoDetailMdl[] getTmpItem(
	        StockListDenpyoDetailMdl[] item, boolean isOrderSmall,
	        int comboSelect) {

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
				strArr[i] = item[i].getStrStcNo();
				break; // 伝票：文字列
			case 2:
				strArr[i] = item[i].getStrStcDate();
				break; // 日付：文字列
			case 3:
				strArr[i] = item[i].getStrItemNum();
				break; // 品番：文字列
			case 4:
				strArr[i] = item[i].getStrItemName();
				break; // 品名：文字列
			case 5:
				strArr[i] = item[i].getStrUnitName();
				break; // 単位：文字列
			// case 6 : strArr[i] = item[i].getStrUnit1Num() ; break ; // バラ数：実数
			case 6:
				strArr[i] = item[i].getStrStcNum();
				break; // 合計量：実数
			case 7:
				strArr[i] = item[i].getStrAmnt();
				break; // 金額：実数
			case 8:
				strArr[i] = item[i].getStrDiscnt();
				break; // 値引：実数
			case 9:
				strArr[i] = item[i].getStrTaxAmnt();
				break; // 消費税額：実数
			case 10:
				strArr[i] = item[i].getStrStcAmnt();
				break; // 購入金額：実数
			case 11:
				strArr[i] = item[i].getStrCrdName();
				break; // 仕入区分：文字列
			default:
				break;
			}
		}

		StockListDenpyoDetailMdl[] tmpItem = new StockListDenpyoDetailMdl[item.length];
		// for(int i=0; i<numFlag.length; i++){
		// if(numFlag[i]){
		// sortArr = sort.intSort(strArr) ;
		// }else

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
	public Vector getVdata(StockListDenpyoDetailMdl[] tmpItem) {

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
				if (tmpItem[i].getStrCont().equals(contCdNm[j].getid())) {
					contStr = contCdNm[j].getName();
				}
			}
			vcdata.addElement(contStr); // 業者：文字列

			vcdata.addElement(tmpItem[i].getStrStcNo()); // 伝票：文字列
			String stcdate = tmpItem[i].getStrStcDate();
			stcdate = Common.getWareki2(Integer.parseInt(stcdate.substring(0, 4)))
			        + "-"
			        + stcdate.substring(4, 6)
			        + "-"
			        + stcdate.substring(6, 8);
			vcdata.addElement(stcdate); // 日付：文字列
			vcdata.addElement(tmpItem[i].getStrItemNum()); // 品番：文字列
			vcdata.addElement(tmpItem[i].getStrItemName()); // 品名：文字列
			vcdata.addElement(tmpItem[i].getStrUnitName()); // 単位：文字列
			// vcdata.addElement(tmpItem[i].getStrUnit1Num()); // バラ数：実数
			vcdata.addElement(tmpItem[i].getStrStcNum()); // 合計量：実数
			vcdata.addElement(tmpItem[i].getStrAmnt()); // 金額：実数
			vcdata.addElement(tmpItem[i].getStrDiscnt()); // 値引：実数
			vcdata.addElement(tmpItem[i].getStrTaxAmnt()); // 消費税額：実数
			vcdata.addElement(tmpItem[i].getStrStcAmnt()); // 購入金額：実数
			vcdata.addElement(tmpItem[i].getStrCrdName()); // 仕入区分：文字列

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

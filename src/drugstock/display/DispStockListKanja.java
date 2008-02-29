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

import drugstock.biz.BizContradrug;
import drugstock.cmn.Sort;
import drugstock.model.StockListKanjaMdl;
import drugstock.model.SyuruiCdNm;

/**
 * 「薬剤使用患者一覧表」画面処理／表内部データ制御
 */

public class DispStockListKanja {

	// index値の項目が「数字」であることを示す配列
	private int[] numFlag = new int[7];
	private final int INDEX_NUM = 0;
	private final int INDEX_STR = 1;
	private final int INDEX_DEF = 2;

	/**
	 * コンストラクタ 要素が数字か文字列かの指定をします。
	 */
	public DispStockListKanja() {
		for (int i = 0; i < numFlag.length; i++) {
			switch (i) {
			case 0:
			case 6:
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
	public StockListKanjaMdl[] getTmpItem(StockListKanjaMdl[] item,
	        boolean isOrderSmall, int comboSelect) {

		Sort sort = new Sort(isOrderSmall);
		int[] sortArr = null;

		String[] strArr = new String[item.length];
		int[] intArr = new int[item.length];

		for (int i = 0; i < item.length; i++) {
			switch (comboSelect) {
			case 0:
				strArr[i] = item[i].getStrSeqNo();
				break; // 連番：整数
			case 1:
				strArr[i] = item[i].getStrOrca_User_No();
				break; // 患者番号：文字列
			case 2:
				strArr[i] = item[i].getStrName();
				break; // 氏名：文字列
			case 3:
				strArr[i] = item[i].getStrInsurance();
				break; // 保険：文字列
			case 4:
				strArr[i] = item[i].getStrUse_Med();
				break; // 使用品目：文字列
			case 5:
				strArr[i] = item[i].getStrUse_Day();
				break; // 使用日：文字列
			case 6:
				strArr[i] = item[i].getStrExpend_Num();
				break; // 払出量：実数
			default:
				break;
			}
		}

		StockListKanjaMdl[] tmpItem = new StockListKanjaMdl[item.length];
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
	public Vector getVdata(StockListKanjaMdl[] tmpItem) {

		Vector vdata = new Vector();

		BizContradrug bizsyurui = new BizContradrug();
		SyuruiCdNm[] ssyuruiCdNm = bizsyurui.getMed_kind_list();

		for (int i = 0; i < tmpItem.length; i++) {
			Vector vcdata = new Vector();

			vcdata.addElement(tmpItem[i].getStrSeqNo()); // 連番：整数
			vcdata.addElement(tmpItem[i].getStrOrca_User_No()); // 患者番号：文字列
			vcdata.addElement(tmpItem[i].getStrName()); // 氏名：文字列
			vcdata.addElement(tmpItem[i].getStrInsurance()); // 保険：文字列
			vcdata.addElement(tmpItem[i].getStrUse_Med()); // 使用品目：整数
			vcdata.addElement(tmpItem[i].getStrUse_Day()); // 使用日：文字列
			vcdata.addElement(tmpItem[i].getStrExpend_Num()); // 払出量：実数

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

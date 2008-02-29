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
import drugstock.model.StockListSaekiMdl;
import drugstock.model.SyuruiCdNm;

/**
 * 「差益高分析表」画面処理／表内部データ制御
 */

public class DispStockListSaeki {

	// index値の項目が「数字」であることを示す配列
	private int[] numFlag = new int[13];
	private final int INDEX_NUM = 0;
	private final int INDEX_STR = 1;
	private final int INDEX_DEF = 2;

	/**
	 * コンストラクタ 要素が数字か文字列かの指定をします。
	 */
	public DispStockListSaeki() {
		for (int i = 0; i < numFlag.length; i++) {
			switch (i) {
			case 1:
			case 2:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
				numFlag[i] = INDEX_NUM;
				break;
			case 3:
			case 4:
				numFlag[i] = INDEX_STR;
				break;
			default:
				numFlag[i] = INDEX_DEF;
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
	public StockListSaekiMdl[] getTmpItem(StockListSaekiMdl[] item,
	        boolean isOrderSmall, int comboSelect) {

		Sort sort = new Sort(isOrderSmall);
		int[] sortArr = null;

		String[] strArr = new String[item.length];
		int[] intArr = new int[item.length];

		for (int i = 0; i < item.length; i++) {
			switch (comboSelect) {
			case 1:
				strArr[i] = item[i].getStrExpend_Rank();
				break; // 使用高順位：整数
			case 2:
				strArr[i] = item[i].getStrMargin_Rank();
				break; // 差益高順位：整数
			case 3:
				strArr[i] = item[i].getStrItemNum();
				break; // 品番：整数
			case 4:
				strArr[i] = item[i].getStrItemName();
				break; // 品名：文字列
			case 5:
				strArr[i] = item[i].getStrMed_Price();
				break; // 薬価：実数
			case 6:
				strArr[i] = item[i].getStrStc_Price();
				break; // 納入価：実数
			case 7:
				strArr[i] = item[i].getStrMargin();
				break; // 差益：実数
			case 8:
				strArr[i] = item[i].getStrExpend_num();
				break; // 使用量：実数
			case 9:
				strArr[i] = item[i].getStrExpend_Price();
				break; // 使用高：実数
			case 10:
				strArr[i] = item[i].getStrRate();
				break; // 使用高％：実数
			case 11:
				strArr[i] = item[i].getStrMargin_Price();
				break; // 差益高：実数
			case 12:
				strArr[i] = item[i].getStrMargin_Rate();
				break; // 差益高％：実数
			default:
				break;
			}
		}

		StockListSaekiMdl[] tmpItem = new StockListSaekiMdl[item.length];
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
	public Vector getVdata(StockListSaekiMdl[] tmpItem) {

		Vector vdata = new Vector();

		BizContradrug bizsyurui = new BizContradrug();
		SyuruiCdNm[] ssyuruiCdNm = bizsyurui.getMed_kind_list();

		for (int i = 0; i < tmpItem.length; i++) {
			Vector vcdata = new Vector();

			// 薬剤種別の名前を取得
			for (int j = 0; j < ssyuruiCdNm.length; j++) {
				if (tmpItem[i].getMedKind().equals(ssyuruiCdNm[j].code)) {
					vcdata.addElement(ssyuruiCdNm[j].name);
				}
			}
			vcdata.addElement(tmpItem[i].getStrExpend_Rank()); // 使用高順位
			vcdata.addElement(tmpItem[i].getStrMargin_Rank()); // 差益高順位
			vcdata.addElement(tmpItem[i].getStrItemNum()); // 品番
			vcdata.addElement(tmpItem[i].getStrItemName()); // 品名
			vcdata.addElement(tmpItem[i].getStrMed_Price()); // 薬価
			vcdata.addElement(tmpItem[i].getStrStc_Price()); // 納入価
			vcdata.addElement(tmpItem[i].getStrMargin()); // 差益
			vcdata.addElement(tmpItem[i].getStrExpend_num()); // 使用量
			vcdata.addElement(tmpItem[i].getStrExpend_Price()); // 使用高
			vcdata.addElement(tmpItem[i].getStrRate()); // 使用高％
			vcdata.addElement(tmpItem[i].getStrMargin_Price()); // 差益高
			vcdata.addElement(tmpItem[i].getStrMargin_Rate()); // 差益高％

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

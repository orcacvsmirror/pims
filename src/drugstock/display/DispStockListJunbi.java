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
import drugstock.model.StockListJunbiMdl;
import drugstock.model.SyuruiCdNm;

/**
 * 「棚卸準備一覧表」画面処理／表内部データ制御
 */

public class DispStockListJunbi {

	/**
	 * コンストラクタ
	 */
	public DispStockListJunbi() {
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
	public StockListJunbiMdl[] getTmpItem(StockListJunbiMdl[] item,
	        boolean isOrderSmall, int comboSelect) {

		Sort sort = new Sort(isOrderSmall);
		int[] sortArr = null;

		String[] strArr = new String[item.length];
		int[] intArr = new int[item.length];

		for (int i = 0; i < item.length; i++) {
			switch (comboSelect) {
			case 1:
				strArr[i] = item[i].getStrItemNum();
				break; // 整数
			case 2:
				strArr[i] = item[i].getStrItemName();
				break; // 文字列
			case 3:
				strArr[i] = item[i].getStrStockPrice();
				break; // 実数
			case 4:
				strArr[i] = item[i].getStrNowStock();
				break; // 実数
			case 5:
				strArr[i] = item[i].getStrUnitName();
				break; // 文字列
			case 6:
				strArr[i] = item[i].getStrUnit1Num();
				break; // 実数
			default:
				break;
			}
		}

		StockListJunbiMdl[] tmpItem = new StockListJunbiMdl[item.length];
		switch (comboSelect) {
		case 1:
		case 3:
		case 4:
		case 6:
			sortArr = sort.intSort(strArr);
			break;
		case 2:
		case 5:
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
	public Vector getVdata(StockListJunbiMdl[] tmpItem) {

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
			vcdata.addElement(tmpItem[i].getStrItemNum()); // 薬剤番号
			vcdata.addElement(tmpItem[i].getStrItemName()); // 薬剤名称
			vcdata.addElement(tmpItem[i].getStrStockPrice()); // 在庫単価
			vcdata.addElement(tmpItem[i].getStrNowStock()); // 現在庫量
			vcdata.addElement(tmpItem[i].getStrUnitName()); // 単位
			vcdata.addElement(tmpItem[i].getStrUnit1Num()); // 在庫金額

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

		schTable.getColumnModel().getColumn(3).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(4).setCellRenderer(rrend);
		schTable.getColumnModel().getColumn(6).setCellRenderer(rrend);

		return schTable;
	}

}

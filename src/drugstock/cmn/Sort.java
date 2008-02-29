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
package drugstock.cmn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ソート用クラス
 */

public class Sort {

	private boolean isOrderSmall = true;

	public Sort() {
	}

	public Sort(boolean inIsOrderSmall) {
		isOrderSmall = inIsOrderSmall;
	}

	/*
	 * 文字列のソートを行い、ソート後のindex配列を返します。 例){bb,a,b,aa,c}を昇順でソートした場合
	 * index値：[0,1,2,3,4] ソート後の配列(返り値でない)：{a,aa,b,bb,c}
	 * ソート後のindex配列(返り値)：[1,3,2,0,4] @param 入力文字列の配列 @return ソート後のindex配列
	 */
	public int[] strSort(String[] inStrArr) {
		int len = inStrArr.length;
		int[] sortArr = new int[len];
		String[] strArr = new String[len];
		boolean[] checkArr = new boolean[len];

		for (int i = 0; i < len; i++)
			checkArr[i] = true;

		// ArrayList を生成
		List list = new ArrayList();
		// 数値をListに設定
		for (int i = 0; i < len; i++) {
			list.add(inStrArr[i]);
		}

		Comparator c = new StrCompare();
		// ソート処理を行う
		if (isOrderSmall) {
			Collections.sort(list);
		} else {
			Collections.sort(list, c);
		}
		// System.out.println("ソートパート１後 : ");
		for (int i = 0; i < len; i++) {
			strArr[i] = (String)list.get(i);
		}

		// ソート後のindex配列を取得
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				if ((inStrArr[i].equals(strArr[j])) && (checkArr[j])) {
					sortArr[j] = i;
					checkArr[j] = false;
					break;
				}
			}
		}
		// for(int i=0; i<len; i++) System.out.println(sortArr[i] + ";" +
		// checkArr[i]);

		return sortArr;
	}

	/*
	 * 文字列(整数)のソートを行い、ソート後のindex配列を返します。 例){3,-1,14,0,5}を昇順でソートした場合
	 * index値：[0,1,2,3,4] ソート後の配列(返り値でない)：{-1,0,3,5,14}
	 * ソート後のindex配列(返り値)：[2,3,1,0,4] @param 入力文字列の配列 @return ソート後のindex配列
	 */
	public int[] intSort(String[] inStrArr) {
		int len = inStrArr.length;
		int[] sortArr = new int[len];
		Integer[] inIntArr = new Integer[len];
		Integer[] intArr = new Integer[len];
		boolean[] checkArr = new boolean[len];

		for (int i = 0; i < len; i++)
			checkArr[i] = true;
		// ArrayList を生成
		List list = new ArrayList();

		// Integer型に変換、Listに設定
		for (int i = 0; i < len; i++) {
			Integer integerTmp = null;
			inIntArr[i] = Integer.decode(delChar(inStrArr[i]));
			list.add(inIntArr[i]);
		}

		Comparator c = new IntCompare();
		// ソート処理を行う
		if (isOrderSmall) {
			Collections.sort(list);
		} else {
			Collections.sort(list, c);
		}
		// System.out.println("ソートパート１後 : "+list);
		for (int i = 0; i < len; i++) {
			intArr[i] = (Integer)list.get(i);
		}

		// ソート後のindex配列を取得
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < len; j++) {
				if ((inIntArr[i] == intArr[j]) && (checkArr[j])) {
					sortArr[j] = i;
					checkArr[j] = false;
					break;
				}
			}
		}

		return sortArr;
	}

	/*
	 * 指定長の、0から１ずつインクリメントされたindex配列を返します。 例)指定長が5の場合 index配列(返り値)：[1、２、３、４、５]
	 * @param 整数の指定長 @return 指定長のindex配列
	 */
	public int[] defSort(int len) {
		int[] intArr = new int[len];

		for (int i = 0; i < len; i++)
			intArr[i] = i;
		return intArr;
	}

	/*
	 * ","と"."を削除します。
	 */
	private String delChar(String inStr) {
		String str = null;

		try {
			StringBuffer sb = new StringBuffer(inStr);
			sb = delCharSub(sb, ".");
			sb = delCharSub(sb, ",");

			str = sb.toString();
			if (str.equals("-"))
				str = "0";

			// 一度整数化
			Integer integer = null;
			int tmpInt = Integer.parseInt(str);
			str = String.valueOf(tmpInt);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/*
	 * 文字列バッファから、特定の文字列がなくなるまで消去する。 @param String文字列バッファ @param str 消去する文字列
	 * @return 消去済のStringBuffer文字列
	 */
	private StringBuffer delCharSub(StringBuffer inSb, String str) {
		StringBuffer sb = inSb;
		int index = sb.indexOf(str);

		while (index != -1) {
			sb.deleteCharAt(index);
			index = sb.indexOf(str);
		}
		return sb;
	}

	/*
	 * Stringのソートで降順設定にする
	 */
	private class StrCompare implements Comparator {

		public StrCompare() {
			super();
		}

		public boolean equals(Object obj) {
			return (super.equals(obj));
		}

		public int compare(Object obj1, Object obj2) {
			int v = ((String)obj1).compareTo((String)obj2);
			return -(v);
		}
	}

	/*
	 * Integerのソートで降順設定にする
	 */
	private class IntCompare implements Comparator {

		public IntCompare() {
			super();
		}

		public boolean equals(Object obj) {
			return (super.equals(obj));
		}

		public int compare(Object obj1, Object obj2) {
			int v1 = ((Integer)obj1).intValue();
			int v2 = ((Integer)obj2).intValue();
			return -(v1 - v2);
		}
	}

}

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
package drugstock.biz;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import drugstock.db.OrcaDatabase;
import drugstock.db.ComDatabase;
import drugstock.model.CodeName;
import drugstock.model.ContItem;
import drugstock.model.OrcaMedicine;
import drugstock.model.SyuruiCdNm;

import drugstock.batch.OrcaHospNumImport;

/**
 * 「薬剤設定」DB処理
 */

public class BizContradrug {

	Connection conn = null;
	Statement stmt = null;
	ResultSet rs = null;
  OrcaHospNumImport hosp = null;
  String hospnum = null;

	// 薬剤リスト取得件数 １００件より多い場合 "OVER", １００件以下は"OK"
	private String sSarch_over_flg = "";

	public BizContradrug() {
	}

	/**
	 * 薬剤リストを取得し、１００件以上の明細があるか確認します。
	 * 
	 * @return １００件より多い場合 "OVER", １００件以下は"OK"を返す。
	 */
	public String getSarcOverSts() {
		return sSarch_over_flg;
	}

	/**
	 * 薬剤区分マスタの内容を取得します。
	 * 
	 * @return 薬剤区分マスタの配列
	 */
	public SyuruiCdNm[] getMed_kind_list() {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		SyuruiCdNm cItem[] = null;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_med_kind");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" ORDER BY stock_med_kind");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			String stock_cd;
			String orca_cd;
			String nm;
			while (rs.next()) {
				stock_cd = rs.getString("stock_med_kind");
				orca_cd = rs.getString("orca_med_kind");
				nm = rs.getString("med_kind_name");

				SyuruiCdNm wk = new SyuruiCdNm(stock_cd, orca_cd, nm);
				aList.add(wk);
			}
			cItem = new SyuruiCdNm[aList.size()];
			cItem = (SyuruiCdNm[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getMed_kind_list SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getMed_kind_list Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 日レセ薬剤マスタの内容を取得します。
	 * 
	 * @param kananm
	 *            業者カナ名称
	 * @param orca_medcd
	 *            日レセ診療コード
	 * @param kind
	 *            薬剤区分
	 * @return 日レセ薬剤マスタの配列
	 */
	public OrcaMedicine[] getOrca_medicine_list(String kananm,
	        String orca_medcd, String kind) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		OrcaMedicine cItem[] = null;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			// ORCADBから短縮番号を参照、orca_med_cdを取得
			String tmp_orca_medcd = null;
			tmp_orca_medcd = get_orcaMedCd_from_orcaMedCd(orca_medcd);
			if (tmp_orca_medcd != null) {
				orca_medcd = tmp_orca_medcd;
			}

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT orca_med_cd, Max(day_to) AS day_to");
			bsql.append(" FROM m_orca_medicine");
			bsql.append(" WHERE orca_med_cd IS NOT NULL");
			bsql.append(" AND hospnum = '" + hospnum +"'");
			if (kananm.equals("") == false) {
				bsql.append(" AND med_nm Like '%" + kananm + "%'");
			}
			if (orca_medcd.equals("") == false) {
				bsql.append(" AND orca_med_cd Like '%" + orca_medcd + "%'");
			}
			if (kind.equals("") == false) {
				bsql.append(" AND med_kind = '" + kind + "'");
			}
			// 値引き検索を除外
			bsql.append(" AND orca_med_cd != '' ");
			bsql.append(" GROUP BY orca_med_cd");
			bsql.append(" ORDER BY orca_med_cd");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			int i = 0;
			while (rs.next()) {
				String orca_med_cd = rs.getString("orca_med_cd"); // ＯＲＣＡ薬剤ＣＤ
				String day_from = ""; // 有効開始日
				String day_to = rs.getString("day_to"); // 有効終了日
				String med_nm = ""; // 薬剤名称
				String med_kn = ""; // 薬剤名称カナ
				String unit_nm = ""; // 単位名
				String med_kind = ""; // 薬剤種類
				String med_kind_name = ""; // 薬剤種類名称
				String med_price = ""; // 最新納入単価
				String discount = ""; // 単品値引率
				String item_no = ""; // 品番

				OrcaMedicine wk = new OrcaMedicine(orca_med_cd, day_from,
				        day_to, med_nm, med_kn, unit_nm, med_kind,
				        med_kind_name, med_price, discount, item_no);
				aList.add(wk);
				i = i + 1;
				if (i >= 100)
					break;
			}
			if (rs.next())
				sSarch_over_flg = "OVER";
			rs.close();
			stmt.close();
			cItem = new OrcaMedicine[aList.size()];
			cItem = (OrcaMedicine[])aList.toArray(cItem);

			for (i = 0; i < cItem.length; i++) {
				bsql.delete(0, bsql.length());
				bsql.append("SELECT m_orca_medicine.*");
				bsql.append(" FROM m_orca_medicine");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND orca_med_cd = '" + cItem[i].orca_med_cd
				        + "'");
				bsql.append(" AND day_to = '" + cItem[i].day_to + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					cItem[i].day_from = rs.getString("day_from"); // 有効開始日
					cItem[i].med_nm = rs.getString("med_nm"); // 薬剤名称
					cItem[i].med_kn = rs.getString("med_kn"); // 薬剤名称カナ
					cItem[i].unit_nm = rs.getString("unit_nm"); // 単位名
					cItem[i].med_kind = rs.getString("med_kind"); // 薬剤種類
					cItem[i].med_price = rs.getString("med_price"); // 最新納入単価
				}
				stmt.close();
				rs.close();
			}
			// 薬剤種類名を設定
			SyuruiCdNm syuruicdnm[] = getMed_kind_list();
			for (i = 0; i < cItem.length; i++) {
				String syuruicd = cItem[i].med_kind;
				String syuruinm = "";
				for (int j = 0; j < syuruicdnm.length; j++) {
					if (syuruicdnm[j].orcacd.equals(syuruicd) == true) {
						syuruinm = syuruicdnm[j].name;
						break;
					}
				}
				cItem[i].med_kind_name = syuruinm;
			}

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getOrca_medicine_list SQLException"
			                + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getOrca_medicine_list Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 日レセ薬剤マスタの内容を取得します。
	 * 
	 * @param orca_medcd
	 *            日レセ診療コード
	 * @return 日レセ薬剤マスタ
	 */
	public OrcaMedicine getOrca_medicine(String orca_medcd) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		OrcaMedicine cItem = null;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);

			bsql.delete(0, bsql.length());
			bsql.append("SELECT m_orca_medicine.*");
			bsql.append(" FROM m_orca_medicine");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND orca_med_cd = '" + orca_medcd + "'");
			bsql.append(" ORDER BY day_to DESC");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String orca_med_cd = rs.getString("orca_med_cd"); // ＯＲＣＡ薬剤ＣＤ
				String day_from = rs.getString("day_from"); // 有効開始日
				String day_to = rs.getString("day_to"); // 有効終了日
				String med_nm = rs.getString("med_nm"); // 薬剤名称
				String med_kn = rs.getString("med_kn"); // 薬剤名称カナ
				String unit_nm = rs.getString("unit_nm"); // 単位名
				String med_kind = rs.getString("med_kind"); // 薬剤種類
				String med_kind_name = ""; // 薬剤種類名称
				String med_price = rs.getString("med_price"); // 最新納入単価
				String discount = ""; // 単品値引率
				String item_no = ""; // 品番

				cItem = new OrcaMedicine(orca_med_cd, day_from, day_to, med_nm,
				        med_kn, unit_nm, med_kind, med_kind_name, med_price,
				        discount, item_no);
			}
			rs.close();
			stmt.close();

			if (cItem != null) {
				// 品番を設定
				bsql.delete(0, bsql.length());
				bsql.append("SELECT item_no");
				bsql.append(" FROM m_cont_item");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND orca_med_cd = '" + orca_medcd + "'");
				bsql.append(" AND del_flg='0'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					cItem.item_no = rs.getString("item_no");
				}
				rs.close();
				stmt.close();
			}
		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getOrca_medicine SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getOrca_medicine Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 指定条件の薬剤情報リストを取得します。
	 * 
	 * @param gyouid
	 *            業者ID
	 * @param kananm
	 *            業者カナ名称
	 * @param itemno
	 *            薬剤品番
	 * @param kind
	 *            薬剤区分
	 * @param isOrg
	 *            独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse
	 * @return 薬剤情報マスタの配列
	 */
	public ContItem[] getCont_item_list(String gyouid, String kananm,
	        String itemno, String kind, boolean isOrg) {
		return getCont_item_list(gyouid, kananm, itemno, kind, isOrg, false);
	}

	/**
	 * 指定条件の薬剤情報リストを取得します。
	 * 
	 * @param gyouid
	 *            業者ID
	 * @param kananm
	 *            業者カナ名称
	 * @param itemno
	 *            薬剤品番
	 * @param kind
	 *            薬剤区分
	 * @param isOrg
	 *            独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse
	 * @param isDenpyo
	 *            伝票入力フラグ：伝票入力、院内処理ならtrue、それ以外はfalse
	 * @return 薬剤情報マスタの配列
	 */
	public ContItem[] getCont_item_list(String gyouid, String kananm,
	        String itemno, String kind, boolean isOrg, boolean isDenpyo) {
		ContItem cItem[] = null;
		String sql = null;

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);
			String orcaMedCd = null;

			// ORCADBから短縮番号を参照、orca_med_cdを取得
			orcaMedCd = get_orcaMedCd_from_itemNo(itemno);

			bsql.delete(0, bsql.length());
			bsql.append("SELECT m_cont_item.*");
			bsql.append(" FROM m_cont_item");
			bsql.append(" INNER JOIN m_orca_medicine ON m_cont_item.orca_med_cd = m_orca_medicine.orca_med_cd");
			bsql.append(" WHERE m_cont_item.hospnum ='" + hospnum + "'");
			bsql.append(" AND m_orca_medicine.hospnum ='" + hospnum + "'");
			bsql.append(" AND del_flg='0' ");
			if (gyouid.equals("NONE") == false) {
				bsql.append(" AND m_cont_item.cont_id = " + gyouid);
			}
			if (kananm.equals("") == false) {
				bsql.append(" AND m_cont_item.med_nm Like '%" + kananm + "%'");
			}
			if (kind.equals("") == false) {
				bsql.append(" AND m_cont_item.med_kind1 = '" + kind + "'");
			}
			// 値引き検索を除外
			bsql.append(" AND m_cont_item.orca_med_cd != '' ");

			// ORCADBから短縮番号をもとにorca_med_cdを取得したとき
			if (orcaMedCd == null) {
				if (itemno.equals("") == false) {
					bsql.append(" AND m_cont_item.item_no = '" + itemno + "'");
				}
			} else {
				bsql.append(" AND m_cont_item.orca_med_cd = '" + orcaMedCd
				        + "' ");
			}
			// 独自薬剤除外／限定
			if (!isDenpyo) {
				if (isOrg) {
					bsql.append(" AND m_cont_item.orca_med_cd >= 'XXX' ");
				} else {
					bsql.append(" AND m_cont_item.orca_med_cd < 'XXX' ");
				}
			}
			//
			bsql.append(" ORDER BY m_cont_item.item_no");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			// System.out.println( sql ) ;
			ArrayList aList = new ArrayList();

			int i = 0;
			String tmpContItem = "";
			while (rs.next()) {
				String cont_id = rs.getString("cont_id"); // 業者ＩＤ
				String orca_med_cd = rs.getString("orca_med_cd"); // ＯＲＣＡ薬剤ＣＤ
				String item_no = rs.getString("item_no"); // 品番
				String med_nm = rs.getString("med_nm"); // 薬剤名称
				String med_kn = rs.getString("med_kn"); // 薬剤名称カナ
				String med_kind1 = rs.getString("med_kind1"); // 薬剤種類１
				String med_kind_name = ""; // 薬剤種類１名称
				String med_kind2 = rs.getString("med_kind2"); // 薬剤種類２
				String med_kind3 = rs.getString("med_kind3"); // 薬剤種類３
				String pack_unit3 = rs.getString("pack_unit3"); // 梱包単位
				String pack_unit2 = rs.getString("pack_unit2"); // 包装単位
				String pack_unit1 = rs.getString("pack_unit1"); // バラ単位
				String unit_price = rs.getString("unit_price"); // 最新納入単価
				String discount = rs.getString("discount"); // 単品値引率
				String unit_nm = ""; // 単位名
				String med_price = ""; // 薬価
				String hacchu_p = rs.getString("hacchu_p"); // 発注用P点

				// 複数業者薬剤の重複を避ける
				if (tmpContItem.equals(item_no) == false) {

					ContItem wk = new ContItem(cont_id, // 業者ＩＤ
					        orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
					        item_no, // 品番
					        med_nm, // 薬剤名称
					        med_kn, // 薬剤名称カナ
					        med_kind1, // 薬剤種類１
					        med_kind_name, // 薬剤種類１名称
					        med_kind2, // 薬剤種類２
					        med_kind3, // 薬剤種類３
					        pack_unit3, // 梱包単位
					        pack_unit2, // 包装単位
					        pack_unit1, // バラ単位
					        unit_price, // 最新納入単価
					        discount, // 単品値引率
					        unit_nm, // 単位名
					        med_price, // 薬価
					        hacchu_p); // 発注用P点
					aList.add(wk);
					i = i + 1;
				}
				tmpContItem = item_no;
				if (i >= 100)
					break;
			}
			if (rs.next())
				sSarch_over_flg = "OVER";
			rs.close();
			stmt.close();
			cItem = new ContItem[aList.size()];
			cItem = (ContItem[])aList.toArray(cItem);
			// 薬剤種類名を設定
			SyuruiCdNm syuruicdnm[] = getMed_kind_list();
			for (i = 0; i < cItem.length; i++) {
				String syuruicd = cItem[i].med_kind1;
				String syuruinm = "";
				for (int j = 0; j < syuruicdnm.length; j++) {
					if (syuruicdnm[j].code.equals(syuruicd) == true) {
						syuruinm = syuruicdnm[j].name;
						break;
					}
				}
				cItem[i].med_kind_name = syuruinm;
			}

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getCont_item_list SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getCont_item_list Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return cItem;
	}

	/**
	 * 指定条件の薬剤情報を取得します。
	 * 
	 * @param gyouid
	 *            業者ID
	 * @param itemno
	 *            薬剤品番
	 * @return 薬剤情報マスタ
	 */
	public ContItem getCont_item(String gyouid, String itemno) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		ContItem cItem = null;
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());

			bsql.append("SELECT m_cont_item.*");
			bsql.append(" FROM m_cont_item");
			bsql.append(" WHERE hospnum ='" + hospnum + "'");
			bsql.append(" AND del_flg='0' ");
			if (gyouid.equals("NONE") == false) {
				bsql.append(" AND cont_id = " + gyouid);
			}
			bsql.append(" AND item_no = '" + itemno + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				String cont_id = rs.getString("cont_id"); // 業者ＩＤ
				String orca_med_cd = rs.getString("orca_med_cd"); // ＯＲＣＡ薬剤ＣＤ
				String item_no = rs.getString("item_no"); // 品番
				String med_nm = rs.getString("med_nm"); // 薬剤名称
				String med_kn = rs.getString("med_kn"); // 薬剤名称カナ
				String med_kind1 = rs.getString("med_kind1"); // 薬剤種類１
				String med_kind_name = ""; // 薬剤種類１名称
				String med_kind2 = rs.getString("med_kind2"); // 薬剤種類２
				String med_kind3 = rs.getString("med_kind3"); // 薬剤種類３
				String pack_unit3 = rs.getString("pack_unit3"); // 梱包単位
				String pack_unit2 = rs.getString("pack_unit2"); // 包装単位
				String pack_unit1 = rs.getString("pack_unit1"); // バラ単位
				String unit_price = rs.getString("unit_price"); // 最新納入単価
				String discount = rs.getString("discount"); // 単品値引率
				String unit_nm = ""; // 単位名
				String med_price = ""; // 薬価
				String hacchu_p = rs.getString("hacchu_p"); // 発注用P点

				cItem = new ContItem(cont_id, // 業者ＩＤ
				        orca_med_cd, // ＯＲＣＡ薬剤ＣＤ
				        item_no, // 品番
				        med_nm, // 薬剤名称
				        med_kn, // 薬剤名称カナ
				        med_kind1, // 薬剤種類１
				        med_kind_name, // 薬剤種類１名称
				        med_kind2, // 薬剤種類２
				        med_kind3, // 薬剤種類３
				        pack_unit3, // 梱包単位
				        pack_unit2, // 包装単位
				        pack_unit1, // バラ単位
				        unit_price, // 最新納入単価
				        discount, // 単品値引率
				        unit_nm, // 単位名
				        med_price, // 薬価
				        hacchu_p); // 発注用P点
			}
			rs.close();
			stmt.close();

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug getCont_item SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug getCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return cItem;
	}

	/**
	 * 複数の薬剤情報を登録します。
	 * 
	 * @param cItem
	 *            薬剤情報マスタの配列<BR>
	 *            isOrg 独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse<BR>
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String insCont_item(ContItem[] cItem, boolean isOrg) {
		String rets = isFoundItem(cItem[0]);
		for (int i = 0; i < cItem.length; i++) {
			if (rets.equals("OK")) {
				rets = insCont_item(cItem[i], isOrg);
			}
		}
		return rets;
	}

	/**
	 * 薬剤情報を登録します。
	 * 
	 * @param cItem
	 *            薬剤情報マスタ
	 * @param isOrg
	 *            独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String insCont_item(ContItem item, boolean isOrg) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			// 薬剤の新規登録
			db.bigin();
			bsql.delete(0, bsql.length());
			bsql.append("INSERT INTO m_cont_item VALUES (");
			bsql.append(item.cont_id + ",");
			// 独自薬剤の場合：日レセ番号"XXX+ 品番"04.04.02 onuki
			if (isOrg) {
				bsql.append("'XXX" + item.item_no + "',");
			} else {
				bsql.append("'" + item.orca_med_cd + "',");
			}
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
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
			// } catch (SQLException sqle) {
			// db.rollback();
			// System.out.println("BizContrdrug insCont_item SQLException" +
			// sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug insCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;
	}

	/**
	 * 薬剤DBに薬剤情報が存在するかどうかを確認します。
	 * 
	 * @param item
	 *            薬剤情報マスタ
	 * @return 指定薬剤が未登録の場合は"OK"、既に登録されていた場合は"FOUND"を返します。
	 */
	public String isFoundItem(ContItem item) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "OK";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			bsql.append("SELECT orca_med_cd");
			bsql.append(" FROM m_cont_item");
			bsql.append(" WHERE hospnum='" + hospnum + "'");
			bsql.append(" AND item_no='" + item.item_no + "'");
			bsql.append(" AND del_flg = '0'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rets = "FOUND";
			}
		} catch (SQLException sqle) {
			// db.rollback();
			System.out.println("BizContrdrug isFoundItem SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug isFoundItem Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return rets;
	}

	/**
	 * 薬剤情報を日レセ薬剤DBに登録します。
	 * 
	 * @param item
	 *            薬剤情報マスタ<BR>
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String insOrca_med(ContItem item) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);

			// 薬剤品名の重複チェック
			bsql.append("SELECT orca_med_cd");
			bsql.append(" FROM m_orca_medicine");
			bsql.append(" WHERE hospnum='" + hospnum + "'");
			bsql.append(" AND med_nm='" + item.med_nm + "'");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rets = "NAME_FOUND";
			} else {
				// 薬剤品番の重複チェック
				bsql.delete(0, bsql.length());
				bsql.append("SELECT orca_med_cd");
				bsql.append(" FROM m_cont_item");
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND ( item_no='" + item.item_no + "'");
				bsql.append(" OR orca_med_cd = 'XXX" + item.item_no + "') ");
				bsql.append(" AND del_flg = '0'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					rets = "HINBAN_FOUND";
				} else {
					// 薬剤の新規登録
					db.bigin();
					bsql.delete(0, bsql.length());
					bsql.append("INSERT INTO m_orca_medicine (");
					bsql.append("orca_med_cd, ");
					bsql.append("day_from, ");
					bsql.append("day_to, ");
					bsql.append("med_nm, ");
					bsql.append("med_kn, ");
					bsql.append("unit_nm, ");
					bsql.append("med_kind, ");
					bsql.append("med_price, ");
					bsql.append("kousei_cd, ");
					bsql.append("hospnum ");
					bsql.append(") VALUES (");
					bsql.append("'XXX" + item.item_no + "',");
					bsql.append("'00000000',");
					bsql.append("'99999999',");
					bsql.append("'" + item.med_nm + "',");
					bsql.append("'" + item.med_kn + "',");
					bsql.append("'" + item.unit_nm + "',");
					bsql.append("'" + item.med_kind1 + "',");
					bsql.append(item.med_price + ",");
					bsql.append("'XXX',");
					bsql.append("'" + hospnum + "')");
					int stats = db.execute(bsql.toString());
					if (stats != 0) {
						db.rollback();
					} else {
						db.commit();
						rets = "OK";
					}
				}
			}
		} catch (SQLException sqle) {
			// db.rollback();
			System.out.println("BizContrdrug insOrca_med SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug insOrca_med Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 複数の薬剤情報を修正します。
	 * 
	 * @param item
	 *            薬剤情報マスタ
	 * @param tmp_item_no
	 *            修正前の薬剤品番
	 * @param isOrg
	 *            独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String updtCont_item(ContItem[] cItem, String tmp_item_no,
	        boolean isOrg) {
		// String rets = isFoundItem(cItem[0]) ;
		String rets = "OK";
		for (int i = 0; i < cItem.length; i++) {
			if (rets.equals("OK")) {
				rets = updtCont_item(cItem[i], tmp_item_no, isOrg);
			}
		}
		return rets;
	}

	/**
	 * 薬剤情報を修正します。
	 * 
	 * @param item
	 *            薬剤情報マスタ
	 * @param tmp_item_no
	 *            修正前の薬剤品番
	 * @param isOrg
	 *            独自薬剤フラグ：独自薬剤ならtrue、それ以外はfalse
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String updtCont_item(ContItem item, String tmp_item_no, boolean isOrg) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			// 薬剤品番の重複チェック
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());

			if (item.item_no.equals(tmp_item_no) == false) {
				bsql.append("SELECT orca_med_cd");
				bsql.append(" FROM m_cont_item");
				// 変更後の品番が既にあった場合
				bsql.append(" WHERE hospnum'" + hospnum + "'");
				bsql.append(" AND item_no='" + item.item_no + "'");
				bsql.append(" AND del_flg = '0'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				// 旧品番と新品番が異なるとき
				if (rs.next()) {
					rets = "FOUND";
				}
			}
			if (rets != "FOUND") {
				db.bigin();
				bsql.delete(0, bsql.length());
				bsql.append("UPDATE m_cont_item");
				bsql.append(" SET med_nm='" + item.med_nm + "',");
				bsql.append(" med_kn='" + item.med_kn + "',");
				bsql.append(" orca_med_cd= ");
				// 独自薬剤の場合：日レセ番号"XXX+ 品番"04.04.02 onuki
				if (isOrg) {
					bsql.append("'XXX" + item.item_no + "',");
				} else {
					bsql.append("'" + item.orca_med_cd + "',");
				}
				// 変更前の品番データに上書き 04.04.05 onuki
				bsql.append(" item_no='" + item.item_no + "',");
				bsql.append(" med_kind1='" + item.med_kind1 + "',");
				bsql.append(" med_kind2='" + item.med_kind2 + "',");
				bsql.append(" med_kind3='" + item.med_kind3 + "',");
				bsql.append(" pack_unit3=" + item.pack_unit3 + ",");
				bsql.append(" pack_unit2=" + item.pack_unit2 + ",");
				bsql.append(" pack_unit1=" + item.pack_unit1 + ",");
				bsql.append(" unit_price=" + item.unit_price + ",");
				bsql.append(" discount=" + item.discount + ",");
				bsql.append(" hacchu_p=" + item.hacchu_p + ",");
				bsql.append(" hospnum='" + hospnum + "'");
				bsql.append(" WHERE del_flg = '0'");
				// 04.07.26 onuki
				// bsql.append(" AND cont_id = " + item.cont_id );
				bsql.append(" AND item_no = '" + tmp_item_no + "'");
				bsql.append(" AND hospnum'" + hospnum + "'");
				int stats = db.execute(bsql.toString());
				if (stats != 0) {
					db.rollback();
				} else {
					db.commit();
					rets = "OK";
				}
			}
		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizContrdrug updtCont_item SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug updtCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 独自薬剤情報を修正します。
	 * 
	 * @param item
	 *            薬剤情報マスタ
	 * @param tmp_item_no
	 *            修正前の薬剤品番
	 * @return 処理が正常終了したなら"OK"、既に登録されていた場合は"FOUND"、他の異常があった場合には"NG"を返します。
	 */
	public String updtOrca_med(ContItem item, String tmp_item_no) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			// 薬剤品番の重複チェック
			conn = db.getConnection();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());

			if (item.item_no.equals(tmp_item_no) == false) {
				bsql.append("SELECT orca_med_cd");
				bsql.append(" FROM m_cont_item");
				// 変更後の品番が既にあった場合
				bsql.append(" WHERE hospnum = '" + hospnum + "'");
				bsql.append(" AND item_no='" + item.item_no + "'");
				bsql.append(" AND del_flg = '0'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				if (rs.next()) {
					rets = "FOUND";
				}
			}
			if (rets != "FOUND") {

				db.bigin();
				bsql.delete(0, bsql.length());
				bsql.append("UPDATE m_orca_medicine");
				bsql.append(" SET");
				bsql.append(" orca_med_cd='XXX" + item.item_no + "',");
				bsql.append(" med_nm='" + item.med_nm + "',");
				bsql.append(" med_kn='" + item.med_kn + "',");
				bsql.append(" unit_nm='" + item.unit_nm + "',");
				bsql.append(" med_kind='" + item.med_kind1 + "',");
				bsql.append(" med_price=" + item.med_price + ",");
				bsql.append(" hospnum = '" + hospnum + "'");
				bsql.append(" WHERE orca_med_cd='XXX" + tmp_item_no + "'");
				bsql.append(" AND hospnum = '" + hospnum + "'");
				int stats = db.execute(bsql.toString());
				if (stats != 0) {
					db.rollback();
				} else {
					db.commit();
					rets = "OK";
				}
			}
		} catch (SQLException sqle) {
			db.rollback();
			System.out.println("BizContrdrug updtCont_item SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug updtCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 薬剤情報を削除します。
	 * 
	 * @param item
	 *            薬剤情報マスタ
	 * @return 処理が正常終了したなら"OK"、異常があった場合には"NG"を返します。
	 */
	public String delCont_item(String itemno) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			db.bigin();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_cont_item");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND del_flg = '0'");
			bsql.append(" AND item_no = '" + itemno + "'");
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}

		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug delCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 日レセ薬剤DBの薬剤情報を削除します。
	 * 
	 * @param orca_med_cd
	 *            日レセ診療コード
	 * @return 処理が正常終了したなら"OK"、異常があった場合には"NG"を返します。
	 */
	public String delOrca_med(String orca_med_cd) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String rets = "NG";
		String sql = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			db.bigin();
			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("DELETE FROM m_orca_medicine");
			bsql.append(" WHERE hospnum = '" + hospnum + "'");
			bsql.append(" AND orca_med_cd = '" + orca_med_cd + "'");
			int stats = db.execute(bsql.toString());
			if (stats != 0) {
				db.rollback();
			} else {
				db.commit();
				rets = "OK";
			}
		} catch (Exception e) {
			db.rollback();
			System.out.println("BizContrdrug delCont_item Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return rets;
	}

	/**
	 * 薬剤品番から薬剤に対応する、すべての業者を取得します。
	 * 
	 * @param itemno
	 *            薬剤品番
	 * @return 業者情報の配列を返します。
	 */
	public CodeName[] getCodeName(String item_no) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		CodeName cItem[] = null;
		String sql = null;
		String id = null;
		String cd = null;
		String nm = null;
		String nmkn = null;
		String nbkrt = null;
		String zeikbn = null;

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT * FROM m_contractor");
			bsql.append(" LEFT JOIN m_cont_item");
			bsql.append(" ON m_cont_item.cont_id = m_contractor.cont_id");
			bsql.append(" WHERE m_cont_item.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_contractor.hospnum = '" + hospnum + "'");
			bsql.append(" AND m_cont_item.item_no = '" + item_no + "'");
			bsql.append(" ORDER BY m_cont_item.cont_id ");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			ArrayList aList = new ArrayList();

			while (rs.next()) {
				id = rs.getString("cont_id");
				cd = rs.getString("cont_cd");
				nm = rs.getString("cont_nm");
				nmkn = rs.getString("short_nm");
				nbkrt = rs.getString("discount");
				zeikbn = rs.getString("tax_flg");

				CodeName wk = new CodeName(id, cd, nm, nmkn, nbkrt, zeikbn);
				aList.add(wk);
			}
			cItem = new CodeName[aList.size()];
			cItem = (CodeName[])aList.toArray(cItem);

		} catch (SQLException sqle) {
			System.out.println("BizContractorInItem GetCodeName SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContractorInItem GetCodeName Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}

		return cItem;
	}

	/**
	 * 薬剤品番から薬剤名称を取得します。
	 * 
	 * @param itemno
	 *            薬剤品番
	 * @return 薬剤名称を返します。
	 */
	public String get_item_nm(String itemno) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String sql = null;
		String med_nm = "未登録";

		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			StringBuffer bsql = new StringBuffer(256);
			bsql.delete(0, bsql.length());
			bsql.append("SELECT m_cont_item.item_no, m_cont_item.med_nm");
			bsql.append(" FROM m_cont_item");
			bsql.append(" WHERE hospnum ='" + hospnum + "'");
			bsql.append(" AND m_cont_item.del_flg='0' ");
			bsql.append(" AND m_cont_item.item_no = '" + itemno + "'");
			bsql.append(" GROUP BY m_cont_item.item_no, m_cont_item.med_nm");
			sql = bsql.toString();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs.next()) {
				med_nm = rs.getString("med_nm"); // 薬剤名称
			}
			rs.close();
			stmt.close();

		} catch (SQLException sqle) {
			System.out.println("BizContrdrug get_item_nm SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug get_item_nm Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return med_nm;
	}

	/**
	 * 日レセDBから短縮番号を検索し、該当する日レセ診療番号を取得します。
	 * 
	 * @param orcaMedCd
	 *            日レセ診療番号
	 * @return 日レセ診療番号を返します。該当する短縮番号が存在しない場合には、nullを返します。
	 */
	public String get_orcaMedCd_from_orcaMedCd(String orcaMedCd) {
		return get_ORCADB_orcaMedCd(orcaMedCd, "orca_med_cd");
	}

	/**
	 * 日レセDBから短縮番号を検索し、該当する日レセ診療番号を取得します。
	 * 
	 * @param itemno
	 *            薬剤品番
	 * @return 日レセ診療番号を返します。該当する短縮番号が存在しない場合には、nullを返します。
	 */
	public String get_orcaMedCd_from_itemNo(String itemno) {
		return get_ORCADB_orcaMedCd(itemno, "item_no");
	}

	// ORCADBから短縮番号を検索、orca_med_cdを取得
	private String get_ORCADB_orcaMedCd(String str, String flg) {
		Connection conn_orca = null;
		OrcaDatabase db_orca = new OrcaDatabase();
		ComDatabase db = new ComDatabase();
		conn_orca = db_orca.getConnection();
		conn = db.getConnection();
		String orcaMedCd = null;
		StringBuffer bsql = new StringBuffer(256);
		String sql = null;

		try {

			// 引数item_noが指定され、かつその品番がm_cont_itemに存在しないとき
			if (str.equals("") == false) {
				int count = 0;
				bsql.delete(0, bsql.length());
				bsql.append("SELECT count(*) as count");
				if (flg.equals("item_no")) {
					bsql.append(" FROM m_cont_item ");
					bsql.append(" WHERE item_no='" + str + "'");
				} else if (flg.equals("orca_med_cd")) {
					bsql.append(" FROM m_orca_medicine ");
					bsql.append(" WHERE orca_med_cd='" + str + "'");
				}
				bsql.append(" AND hospnum = '" + hospnum + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					count = Integer.parseInt(rs.getString("count"));
				}
				rs.close();
				stmt.close();

				// ORCADBから短縮番号を参照、orca_med_cdを取得
				if (count == 0) {

					bsql.delete(0, bsql.length());
					bsql.append("SELECT srycd as srycd");
					bsql.append(" FROM tbl_inputcd ");
					bsql.append(" WHERE hospnum='" + hospnum + "'");
					bsql.append(" AND inputcd='" + str + "'");
					sql = bsql.toString();
					stmt = conn_orca.createStatement();
					rs = stmt.executeQuery(sql);
					while (rs.next()) {
						orcaMedCd = rs.getString("srycd");
					}
					rs.close();
					stmt.close();
				}
			}
		} catch (SQLException sqle) {
			System.out.println("BizContrdrug get_ORCADB_orcaMedCd SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug get_ORCADB_orcaMedCd Exception"
			        + e.toString());
		} finally {
			db_orca.closeAllResource(rs, stmt, conn_orca);
		}
		return orcaMedCd;
	}

	/**
	 * 日レセ診療番号をもとに、日レセDBから短縮番号を検索します。
	 * 
	 * @param orcaMedCd
	 *            日レセ薬剤コード
	 * @return 短縮番号を格納した文字列の配列を返します。該当する短縮番号が存在しない場合には、nullを返します。
	 */
	public String[] get_ORCADB_tanshuku(String orcaMedCd) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String tanshuku[] = null;
		Connection conn_orca = null;
		OrcaDatabase db_orca = new OrcaDatabase();
		conn_orca = db_orca.getConnection();
		StringBuffer bsql = new StringBuffer(256);
		String sql = null;

		try {
			bsql.delete(0, bsql.length());
			bsql.append("SELECT ");
			bsql.append(" count(inputcd) as count");
			bsql.append(" FROM tbl_inputcd ");
			bsql.append(" WHERE hospnum ='" + hospnum + "'");
			bsql.append(" AND srycd='" + orcaMedCd + "'");
			sql = bsql.toString();
			stmt = conn_orca.createStatement();
			rs = stmt.executeQuery(sql);
			int count = 0;
			if (rs.next()) {
				count = Integer.parseInt(rs.getString("count"));
			}
			String tmp[] = null;
			rs.close();
			stmt.close();
			if (count != 0) {
				tmp = new String[count];
				bsql.delete(0, bsql.length());
				bsql.append("SELECT ");
				bsql.append(" inputcd as inputcd");
				bsql.append(" FROM tbl_inputcd ");
				bsql.append(" WHERE hospnum='" + hospnum + "'");
				bsql.append(" AND srycd='" + orcaMedCd + "'");
				sql = bsql.toString();
				stmt = conn_orca.createStatement();
				rs = stmt.executeQuery(sql);
				int i = 0;
				while (rs.next()) {
					tmp[i] = rs.getString("inputcd");
					i++;
				}
				rs.close();
				stmt.close();
			}
			tanshuku = tmp;
		} catch (SQLException sqle) {
			System.out.println("BizContrdrug get_ORCADB_tanshuku SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug get_ORCADB_tanshuku Exception"
			        + e.toString());
		} finally {
			db_orca.closeAllResource(rs, stmt, conn_orca);
		}
		return tanshuku;
	}

	/**
	 * 日レセ薬剤コードから薬剤品番を取得します。
	 * 
	 * @param orcaMedCd
	 *            日レセ薬剤コード
	 * @return 薬剤品番を返します。
	 */
	public String get_itemNo_orcaMedCd(String orcaMedCd) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String itemNo = null;
		StringBuffer bsql = new StringBuffer(256);
		String sql = null;
		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			// orcaMedCdがnullでも空でもないとき
			if ((orcaMedCd == null) || (orcaMedCd.equals("") == false)) {
				int count = 0;
				bsql.delete(0, bsql.length());
				bsql.append("SELECT item_no as item_no");
				bsql.append(" FROM m_cont_item ");
				bsql.append(" WHERE hospnum='" + hospnum + "'");
				bsql.append(" AND orca_med_cd='" + orcaMedCd + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					itemNo = rs.getString("item_no");
				}
				rs.close();
				stmt.close();
			}
		} catch (SQLException sqle) {
			System.out.println("BizContrdrug get_itemNo_orcaMedCd SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug get_itemNo_orcaMedCd Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return itemNo;
	}

	/**
	 * 薬剤品番から日レセ薬剤コードを取得します。
	 * 
	 * @param itemNo
	 *            薬剤品番
	 * @return 日レセ薬剤コードを返します。
	 */
	// item_noからorca_med_cdを取得
	public String get_orcaMedCd_itemNo(String itemNo) {

    OrcaHospNumImport hosp = new OrcaHospNumImport();
    hospnum = hosp.getHospNum();

		String orcaMedCd = null;
		StringBuffer bsql = new StringBuffer(256);
		String sql = null;
		ComDatabase db = new ComDatabase();

		try {
			conn = db.getConnection();

			// orcaMedCdがnullでも空でもないとき
			if ((itemNo == null) || (itemNo.equals("") == false)) {
				int count = 0;
				bsql.delete(0, bsql.length());
				bsql.append("SELECT orca_med_cd as orca_med_cd");
				bsql.append(" FROM m_cont_item ");
				bsql.append(" WHERE hospnum ='" + hospnum + "'");
				bsql.append(" AND item_no='" + itemNo + "'");
				sql = bsql.toString();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				while (rs.next()) {
					orcaMedCd = rs.getString("orca_med_cd");
				}
				rs.close();
				stmt.close();
			}
		} catch (SQLException sqle) {
			System.out.println("BizContrdrug get_orcaMedCd_itemNo SQLException"
			        + sqle.toString());
		} catch (Exception e) {
			System.out.println("BizContrdrug get_orcaMedCd_itemNo Exception"
			        + e.toString());
		} finally {
			db.closeAllResource(rs, stmt, conn);
		}
		return orcaMedCd;
	}

}

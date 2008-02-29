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
package drugstock.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.JDialog;

import drugstock.cmn.PropRead;
import drugstock.cmn.MsgDlg;

/**
 * DBアクセス基本クラス
 */

public class ComDatabase extends JDialog {

	// DBコネクション
	private Connection con = null;
	private ResultSet rs = null;
	private Statement stm = null;

	private MsgDlg msgdlg = new MsgDlg(this);

	static {
		try {
	        Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
        	throw new ExceptionInInitializerError(e);
        }
	}
	/**
	 * コンストラクタ
	 */
	public ComDatabase() {
		String userName = null;
		String password = null;
		String ipaddres = null;

		try {
			PropRead prop = new PropRead();
			userName = prop.getProp("stock_user");
			password = prop.getProp("stock_pass");
			ipaddres = prop.getProp("stock_ip");

			Properties info = new Properties();
			info.put("user", userName);
			info.put("password", password);

			String url = "jdbc:postgresql://" + ipaddres + "/" + userName;
			con = DriverManager.getConnection(url, info);

		} catch (Exception e) {
			System.out.println("ComDatabase Exception" + e.toString());
			msgdlg.msgdsp("DBアクセスエラー：強制終了します。", MsgDlg.ERROR_MESSAGE);
			System.exit(-1);
		}

	}

	/**
	 * バックアップモードのコンストラクタ
	 * 
	 * @param backupMode
	 *            引数が渡されたらバックアップモードとみなす
	 */
	public ComDatabase(int backupMode) {
		String userName = null;
		String password = null;
		String ipaddres = null;
		try {
			PropRead prop = new PropRead();
			userName = prop.getProp("stock_bk_user");
			password = prop.getProp("stock_bk_pass");
			ipaddres = prop.getProp("stock_bk_ip");

			Properties info = new Properties();
			info.put("user", userName);
			info.put("password", password);

			String url = "jdbc:postgresql://" + ipaddres + "/" + userName;
			con = DriverManager.getConnection(url, info);

		} catch (SQLException sqle) {
			System.out.println("ComDatabase Exception" + sqle.toString());
		} catch (Exception e) {
			System.out.println("ComDatabase Exception" + e.toString());
		}
	}

	/**
	 * データベースとの接続 (セッション) を返します。
	 */
	public Connection getConnection() {
		return con;
	}

	/**
	 * データベースとの接続 (セッション) を閉じます。 同時に、引数で渡された接続に関するインスタンスをすべて終了します。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int closeAllResource(ResultSet rs, Statement stm, Connection con) {
		int stats = -1;
		try {
			if (rs != null)
				rs.close();
			if (stm != null)
				stm.close();
			if (con != null)
				close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase closeRecordset Exception"
			        + e.toString());
		}
		return stats;
	}

	/**
	 * SQL文の実行のみを行います。実行結果を取得する事はできません。
	 * 
	 * @param szSQL
	 *            SQLの命令文
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int execute(String szSQL) {
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.setQueryTimeout(10);
			stm.executeUpdate(szSQL);
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase execute Exception" + e.toString());
		}
		return stats;
	}

	/**
	 * SQL文を実行し、実行結果セットを返します。
	 * 
	 * @param szSQL
	 *            SQLの命令文
	 * @return ResultSetを返します。
	 */
	public ResultSet openRecordset(String szSQL) {
		try {
			if (stm == null || rs == null) {
				closeRecordset();
			}
			stm = con.createStatement();
			stm.setQueryTimeout(10);
			rs = stm.executeQuery(szSQL);
		} catch (Exception e) {
			System.out.println("ComDatabase openRecordset Exception"
			        + e.toString());
		}
		return rs;
	}

	/**
	 * openRecordsetを強制終了します。 同時に、接続に関する広域変数をすべて終了します。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	private int closeRecordset() {
		int stats = -1;
		try {
			if (rs != null) {
				rs.close();
			}
			if (stm != null) {
				stm.close();
			}
			rs = null;
			stm = null;
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase closeRecordset Exception"
			        + e.toString());
		}
		return stats;
	}

	/**
	 * 接続(セッション)を閉じます。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int close() {
		int stats = -1;
		try {
			con.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase Close Exception" + e.toString());
		}
		return stats;
	}

	/**
	 * 接続(セッション)を開始します。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int bigin() {
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("BEGIN");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase Bigin Exception" + e.toString());
		}
		return stats;
	}

	/**
	 * 接続(セッション)をコミットします。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int commit() {
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("COMMIT");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase Commit Exception" + e.toString());
		}
		return stats;
	}

	/**
	 * 接続(セッション)をロールバックします。
	 * 
	 * @return 正常ならば"0"、異常ならば"-1"を返します。
	 */
	public int rollback() {
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("ROLLBACK");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("ComDatabase Rollback Exception" + e.toString());
		}
		return stats;
	}

}

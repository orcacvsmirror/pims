package drugstock.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import drugstock.cmn.PropRead;

/**
 * データベース操作用クラス
 */

public class OrcaDatabase {

	// DBコネクション
	private static Connection con = null;
	private static ResultSet rs = null;
	private static Statement stm = null;

	static {
		try {
	        Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
	        throw new ExceptionInInitializerError(e);
        }
	}
	public OrcaDatabase() {
		String userName = null;
		String password = null;
		String ipaddres = null;
		try {
			PropRead prop = new PropRead();
			userName = prop.getProp("orca_user");
			password = prop.getProp("orca_pass");
			ipaddres = prop.getProp("orca_ip");

			Properties info = new Properties();
			info.put("user", userName);
			info.put("password", password);

			String url = "jdbc:postgresql://" + ipaddres + "/orca";

      if (ipaddres == null || ipaddres.equals("")) {
			  url = "jdbc:postgresql:orca";
      }

			con = DriverManager.getConnection(url, info);

		} catch (Exception e) {
			System.out.println("OrcaDatabase Exception" + e.toString());
		}
	}

	public Connection getConnection() {
		return con;
	}

	public int closeAllResource(ResultSet rs, Statement stm, Connection con) { // 0:ok
		// -1:err
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
			System.out.println("OrcaDatabase closeRecordset Exception"
			        + e.toString());
		}
		return stats;
	}

	public int execute(String szSQL) { // 0:ok -1:err
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.setQueryTimeout(10);
			stm.executeUpdate(szSQL);
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("OrcaDatabase Close Exception" + e.toString());
		}
		return stats;
	}

	public ResultSet openRecordset(String szSQL) {
		try {
			if (stm == null || rs == null) {
				closeRecordset();
			}
			stm = con.createStatement();
			stm.setQueryTimeout(10);
			rs = stm.executeQuery(szSQL);
		} catch (Exception e) {
			System.out.println("OrcaDatabase openRecordset Exception"
			        + e.toString());
		}
		return rs;
	}

	public int closeRecordset() { // 0:ok -1:err
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
			System.out.println("OrcaDatabase closeRecordset Exception"
			        + e.toString());
		}
		return stats;
	}

	public int close() { // 0:ok -1:err
		int stats = -1;
		try {
			con.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("OrcaDatabase Close Exception" + e.toString());
		}
		return stats;
	}

	public int bigin() { // 0:ok -1:err
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("BEGIN");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("OrcaDatabase Bigin Exception" + e.toString());
		}
		return stats;
	}

	public int commit() { // 0:ok -1:err
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("COMMIT");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("OrcaDatabase Commit Exception" + e.toString());
		}
		return stats;
	}

	public int rollback() { // 0:ok -1:err
		int stats = -1;
		try {
			stm = con.createStatement();
			stm.executeUpdate("ROLLBACK");
			stm.close();
			stats = 0;
		} catch (Exception e) {
			System.out.println("OrcaDatabase Rollback Exception" + e.toString());
		}
		return stats;
	}
}

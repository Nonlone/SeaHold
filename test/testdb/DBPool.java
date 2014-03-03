package testdb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

public class DBPool {
	private final static String dbConfigName = "dblayer";
	private static BoneCP boneCP = null;

	private static BoneCP configBoneCP() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		BoneCPConfig config = null;
		try {
			config = new BoneCPConfig(DBPool.class.getResourceAsStream("/test_bonecp-config.xml"), dbConfigName);
			return new BoneCP(config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		if (null == boneCP) {
			synchronized (DBPool.class) {
				if (null == boneCP) {
					boneCP = configBoneCP();
				}
			}
		}
		return boneCP.getConnection();
	}

	public static void close(Connection conn) throws SQLException {
		conn.close();
	}

	public static void close(PreparedStatement pstmt) throws SQLException {
		pstmt.close();
	}

	public static void close(ResultSet rs) throws SQLException {
		rs.close();
	}

	public static void close() {
		boneCP.close();
	}
}

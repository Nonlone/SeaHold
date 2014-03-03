package testDao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import com.seahold.dao.filler.VoFiller;
import com.seahold.dao.filler.impl.DefaultVoFiller;

import testDao.model.User;
import testdb.DBPool;

public class TestVoFiller {
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String testSql = "insert into testuser(nick,sessionKey) values ( @nick ,@sessionKey )";
		List<User> userList = new ArrayList<User>();
		int minSize = 5;
		int maxSize = 10;
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setNick(getRandomString(minSize, maxSize));
			user.setSessionKey(getRandomString(30, 50));
			userList.add(user);
			System.out.println(new JSONObject(user));
		}
		VoFiller vf = new DefaultVoFiller();
		Connection conn = DBPool.getConnection();
		List<PreparedStatement> pstmtList = vf.getPreparedStatement(conn, testSql, userList);
		List<int[]> resultList = new ArrayList<int[]>();
		for(PreparedStatement pstmt : pstmtList){
			int[] resultNode = pstmt.executeBatch();
			resultList.add(resultNode);
		}
		int result = 0;
		for(int[] resultNode:resultList){
			for(int i:resultNode){
				result += i;
			}
		}
		System.out.println(result);
	}

	private static String getRandomString(int minSize, int maxSize) {
		String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		int length = 0;
		while (length < minSize) {
			length = random.nextInt(maxSize);
		}
		StringBuffer sbString = new StringBuffer(length);
		for (int i = 0; i < sbString.capacity(); i++) {
			sbString = sbString.append(base.charAt(random.nextInt(base.length())));
		}
		return sbString.toString();
	}

	private static long getRandomLong(int minSize, int maxSize) {
		Random random = new Random();
		int length = 0;
		while (length < minSize) {
			length = random.nextInt(maxSize);
		}
		StringBuffer sbString = new StringBuffer(length);
		for (int i = 0; i < sbString.capacity(); i++) {
			sbString = sbString.append(random.nextInt(10));
		}
		return Long.parseLong(sbString.toString());
	}
}

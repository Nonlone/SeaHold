/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testdb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 *
 * @author Thunderbird
 */
public class TestInsertXWUser {

	public static void main(String[] args) throws IOException{
		int singeLength = 10000;
		int totalTimes = 200;
		List<Thread> threadList = new ArrayList<Thread>();
		//insert 100w
		for (int i = 0; i < totalTimes; i++) {
			List<User> userList = getRandomUserList(singeLength);
			Thread iurThread = new Thread(new InsertUserRunner(userList));
			iurThread.setDaemon(true);
			iurThread.setName("iur-" + (threadList.size() + 1));
			threadList.add(iurThread);
			iurThread.start();
		}
		boolean runFlag = true;
		while (runFlag) {
			for (Thread thread : threadList) {
				if (thread.getState() == Thread.State.TERMINATED) {
					runFlag = false;
				} else {
					runFlag = true;
					break;
				}
			}
		}
		DBPool.close();
	}
	
	private static List<User> getRandomUserList(int Length) {
		int userIdMinSize = 10;
		int userIdMaxSize = 15;
		int nickMinSize = 10;
		int nickMaxSize = 50;
		List<User> userList = new ArrayList<User>(Length);
		for (int i = 0; i < Length; i++) {
			User user = new User();
			user.setUserId(getRandomLong(userIdMinSize, userIdMaxSize));
			user.setNick(getRandomString(nickMinSize, nickMaxSize));
			userList.add(user);
		}
		return userList;
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

class InsertUserRunner implements Runnable {

	private final static int bufferSize = 200;
	private List<User> userList;

	public InsertUserRunner(List<User> userList) {
		this.userList = userList;
	}

	@Override
	public void run() {
		try {
			UserDao uDao = new UserDao();
			int result = uDao.insertBatch(userList, bufferSize);
//			int result = uDao.insertBatchNoSafe(userList);
			System.out.println("batch Insert : "+result);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

	

class UserDao {

	private final static Logger logger = Logger.getLogger(UserDao.class);

	public int insert(User user) throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder("insert user(userid,nick)values(?,?)");
		try {
			conn = DBPool.getConnection();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, user.getUserId());
			pstmt.setString(2, user.getNick());
			System.out.println(user.toJSONObject());
			result = pstmt.executeUpdate();
		} catch (ClassNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (SQLException ex) {
			logger.error(ex.getSQLState(), ex);
			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			DBPool.close(pstmt);
			DBPool.close(conn);
		}
		return result;
	}

	public int insertBatch(List<User> userList, int bufferSize) throws SQLException {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sql = new StringBuilder("insert user(userid,nick)values(?,?)");
		try {
			conn = DBPool.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql.toString());
			int count = 0;
			for (User user : userList) {
				try {
					pstmt.setLong(1, user.getUserId());
					pstmt.setString(2, user.getNick());
					pstmt.addBatch();
					count++;
					if (count % bufferSize == 0) {
						//buffer insert;
						try {
							int[] resultList = pstmt.executeBatch();
							conn.commit();
							for (int k : resultList) {
								result += k;
							}
							System.out.println(" buffer insert : "+result);
						} catch (SQLException ex) {
							logger.info(ex.getMessage(), ex);
							//single insert
							List<User> userListBlock = userList.subList(count - bufferSize - 1, count - 1);
							for (User blockUser : userListBlock) {
								try {
									insert(blockUser);
								} catch (Exception iex) {
									logger.error(ex.getMessage(), iex);
								}
							}
						}
					}
				} catch (SQLException ex) {
					logger.error(ex.getMessage(), ex);
				}
				//break;
			}
			//buffer insert;
			try {
				int[] resultList = pstmt.executeBatch();
				conn.commit();
				for (int k : resultList) {
					result += k;
				}
			} catch (SQLException ex) {
				logger.info(ex.getMessage(), ex);
				//single insert
				List<User> userListBlock = userList.subList(count - bufferSize - 1, count - 1);
				for (User blockUser : userListBlock) {
					try {
						insert(blockUser);
					} catch (Exception iex) {
						logger.error(ex.getMessage(), iex);
					}
				}
			}
		} catch (ClassNotFoundException ex) {
			logger.error(ex.getMessage(), ex);
		} catch (SQLException ex) {
			logger.error(ex.getSQLState(), ex);
			throw ex;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			conn.setAutoCommit(true);
			DBPool.close(pstmt);
			DBPool.close(conn);
		}
		return result;
	}
	
	public int insertBatchNoSafe(List<User> userList){
		int result =0;
		for(User user:userList){
			try{
				insert(user);
				result++;
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return result;
	}
}

/**
 * dtjo
 *
 * @author Thunderbird
 */
class User {

	private Long id;
	private Long userId;
	private String nick;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("userId", userId);
		json.put("nick", nick);
		return json;
	}
}
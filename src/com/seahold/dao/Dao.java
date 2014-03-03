package com.seahold.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.seahold.dao.annotation.Table;
import com.seahold.dao.connection.ConnectionPooling;
import com.seahold.dao.connection.impl.DefaultConnectionFactory;
import com.seahold.dao.filler.VoFiller;
import com.seahold.dao.filler.impl.DefaultVoFiller;
import com.seahold.dao.sql.SqlMaker;
import com.seahold.dao.sql.impl.Pager;
import com.seahold.dao.sql.impl.SqlC;
import com.seahold.dao.utils.StringUtils;
import com.seahold.dao.wrapper.VoWrapper;
import com.seahold.dao.wrapper.impl.DefaultVoWrapper;

/**
 * Dao 默认入口类，用静态方法建立Dao对象
 * 
 * @author Ezir
 * 
 */
public class Dao implements DaoFunc {

	private final static Logger logger = Logger.getLogger(Dao.class);
	/**
	 * 连接池接口
	 */
	private static ConnectionPooling connPool = new DefaultConnectionFactory();
	/**
	 * 核心接口，可替换
	 */
	private static DaoFunc daoFunc = null;

	/**
	 * 初始化函数
	 * 
	 * @param connPool
	 */
	public static void init(ConnectionPooling connPool) {
		if (connPool != null) {
			Dao.connPool = connPool;
		}
	}

	public static void init(DaoFunc daoFunc) {
		if (daoFunc != null) {
			Dao.daoFunc = daoFunc;
		}
	}

	/**
	 * 初始化函数
	 * 
	 * @param connPool
	 * @param daoFunc
	 */
	public static void init(ConnectionPooling connPool, DaoFunc daoFunc) {
		init(connPool);
		init(daoFunc);
	}

	/**
	 * Dao 构造器 ，默认从池中获得连接conn
	 * 
	 * @param classOfT
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Dao getInstance(Class<?> classOfT) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Dao dao = new Dao();
		dao.voClass = classOfT;
		Table table = dao.voClass.getAnnotation(Table.class);
		if (table != null) {
			if (!StringUtils.isEmpty(table.voWrapper())) {
				dao.voWrapper = (VoWrapper) Class.forName(table.voWrapper()).newInstance();
			}
			if (!StringUtils.isEmpty(table.voFiller())) {
				dao.voFiller = (VoFiller) Class.forName(table.voFiller()).newInstance();
			}
		}
		dao.conn = connPool.getConnection();
		return dao;
	}

	/**
	 * Dao 构造器 ，外部放入连接
	 * 
	 * @param classOfT
	 * @param conn
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static Dao getInstance(Class<?> classOfT, Connection conn) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Dao dao = new Dao();
		dao.voClass = classOfT;
		Table table = dao.voClass.getAnnotation(Table.class);
		if (table != null) {
			if (!StringUtils.isEmpty(table.voWrapper())) {
				dao.voWrapper = (VoWrapper) Class.forName(table.voWrapper()).newInstance();
			}
			if (!StringUtils.isEmpty(table.voFiller())) {
				dao.voFiller = (VoFiller) Class.forName(table.voFiller()).newInstance();
			}
		}
		dao.conn = conn;
		return dao;
	}

	/**
	 * 数据表vo类
	 */
	private Class<?> voClass;
	/**
	 * 数据库连接
	 */
	private Connection conn;
	/**
	 * vo类到PreparedStatement填充器
	 */
	private VoFiller voFiller = new DefaultVoFiller();
	/**
	 * ResultSet到vo类包装器
	 */
	private VoWrapper voWrapper = new DefaultVoWrapper();

	public Class<?> getVoClass() {
		return voClass;
	}

	public Connection getConn() {
		return conn;
	}

	@Override
	public int insert(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException{
		if (entity == null) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.insert(entity);
		} else {
			SqlC sqlC = SqlC.insert(voClass);
			PreparedStatement pstmt = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			int result = pstmt.executeUpdate();
			pstmt.close();
			return result;
		}
	}

	@Override
	public int insert(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.insert(entity);
		} else {

			SqlC sqlC = SqlC.insert(voClass);
			conn.setAutoCommit(false);
			List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			List<int[]> resultList = new ArrayList<int[]>();
			for(PreparedStatement pstmt:pstmtList){
				int[] resultNode = pstmt.executeBatch();
				resultList.add(resultNode);
			}
			int result = 0;
			for(int[] resultNode:resultList){
				for (int i : resultNode) {
					result += i;
				}
			}
			conn.setAutoCommit(true);
			for(PreparedStatement pstms:pstmtList){
				pstms.close();
			}
			return result;
		}
	}

	@Override
	public int update(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException {
		if (entity == null) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.update(entity);
		} else {
			SqlC sqlC = SqlC.update(voClass);
			PreparedStatement pstmt = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			int result = pstmt.executeUpdate();
			pstmt.close();
			return result;
		}
	}

	@Override
	public int update(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.update(entity);
		} else {
			SqlC sqlC = SqlC.update(voClass);
			conn.setAutoCommit(false);
			List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			List<int[]> resultList = new ArrayList<int[]>();
			for(PreparedStatement pstmt:pstmtList){
				int[] resultNode = pstmt.executeBatch();
				resultList.add(resultNode);
			}
			int result = 0;
			for(int[] resultNode:resultList){
				for (int i : resultNode) {
					result += i;
				}
			}
			conn.setAutoCommit(true);
			for(PreparedStatement pstmt:pstmtList){
				pstmt.close();
			}
			return result;
		}
	}

	@Override
	public int delete(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException {
		if (entity == null) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.delete(entity);
		} else {
			SqlC sqlC = SqlC.delete(voClass);
			PreparedStatement pstmt = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			int result = pstmt.executeUpdate();
			pstmt.close();
			return result;
		}
	}

	@Override
	public int delete(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		if (daoFunc != null) {
			return daoFunc.delete(entity);
		} else {
			SqlC sqlC = SqlC.delete(voClass);
			conn.setAutoCommit(false);
			List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(conn, sqlC.getSql(), entity);
			List<int[]> resultList = new ArrayList<int[]>();
			for(PreparedStatement pstmt:pstmtList){
				int[] resultNode = pstmt.executeBatch();
				resultList.add(resultNode);
			}
			int result = 0;
			for(int[] resultNode:resultList){
				for (int i : resultNode) {
					result += i;
				}
			}
			conn.setAutoCommit(true);
			for(PreparedStatement pstmt:pstmtList){
				pstmt.close();
			}
			return result;
			
		}
	}

	@Override
	public <T> T querySingle(SqlMaker sqlMaker) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sqlMaker);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sqlMaker.getSql());
				rs = pstmt.executeQuery();
				if(rs != null ){
					return voWrapper.wrapResultSetSingle(rs, voClass);
				}else{
					return null;
				}				
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> T querySingle(String sql) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sql);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if(rs!=null){
					return voWrapper.wrapResultSetSingle(rs, voClass);
				}else{
					return null;
				}				
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> List<T> query(SqlMaker sqlMaker) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sqlMaker);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sqlMaker.getSql());
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSet(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> List<T> query(String sql) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sql);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSet(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> T querySingle(SqlMaker sqlMaker, JSONObject confjson) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sqlMaker);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(voFiller.getFilledSql(sqlMaker.getSql(), confjson));
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSetSingle(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> T querySingle(String sql, JSONObject confjson) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sql);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(voFiller.getFilledSql(sql, confjson));
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSetSingle(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> List<T> query(SqlMaker sqlMaker, JSONObject confjson) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sqlMaker);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(voFiller.getFilledSql(sqlMaker.getSql(), confjson));
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSet(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public <T> List<T> query(String sql, JSONObject confjson) {
		if (daoFunc != null) {
			return daoFunc.querySingle(sql);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(voFiller.getFilledSql(sql, confjson));
				rs = pstmt.executeQuery();
				return voWrapper.wrapResultSet(rs, voClass);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return null;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public int execute(String sql) {
		if (daoFunc != null) {
			return daoFunc.execute(sql);
		} else {
			PreparedStatement pstmt = null;
			try {
				pstmt = conn.prepareStatement(sql);
				return pstmt.executeUpdate();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0;
			} finally {
				try {
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public long count() {
		if (daoFunc != null) {
			return daoFunc.count();
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(SqlC.count(voClass).getSql());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getLong(1);
				}
				return 0L;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0L;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public long count(String sql) {
		if (daoFunc != null) {
			return daoFunc.count();
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getLong(1);
				}
				return 0L;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0L;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public long count(SqlMaker sql) {
		if (daoFunc != null) {
			return daoFunc.count();
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(sql.toString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getLong(1);
				}
				return 0L;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0L;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public long fuzzyCount() {
		if (daoFunc != null) {
			return daoFunc.fuzzyCount();
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				pstmt = conn.prepareStatement(SqlC.fuzzyCount(voClass).getSql());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					return rs.getLong(1);
				}
				return 0L;
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0L;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public long fastCount(int scanSpan, int countOut) {
		if (daoFunc != null) {
			return daoFunc.fastCount(scanSpan, countOut);
		} else {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				SqlC sqlC = SqlC.select(voClass);
				int count = 1;
				sqlC.limit(new Pager(count, scanSpan));
				while (true) {
					pstmt = conn.prepareStatement(sqlC.getSql());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						rs.last();
						int result = rs.getRow();
logger.debug("debug in fastCount middle count and result : " + count + "," + result);
						if (result != scanSpan) {
							return (count - 1) * scanSpan + result;
						} else {
							count++;
							sqlC.limit(new Pager(count, scanSpan));
						}
					}
					if (count > countOut) {
						return 0L;
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
				return 0L;
			} finally {
				try {
					rs.close();
					pstmt.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 注销对象时，关闭连接
	 */
	@Override
	public void finalize() throws Throwable {
		if (conn != null && !conn.isClosed()) {
			conn.close();
		}
		super.finalize();
	}

	public String wrapSqlString(String sql, JSONObject confjson) {
		return voFiller.getFilledSql(sql, confjson);
	}

	public String wrapSqlString(SqlMaker sqlMaker, JSONObject confjson) {
		return voFiller.getFilledSql(sqlMaker.toString(), confjson);
	}
}
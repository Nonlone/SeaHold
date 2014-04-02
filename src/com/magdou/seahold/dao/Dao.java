package com.magdou.seahold.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.magdou.seahold.annotation.DBTable;
import com.magdou.seahold.exception.DataSourceNullException;
import com.magdou.seahold.filler.VoFiller;
import com.magdou.seahold.filler.impl.DefaultVoFiller;
import com.magdou.seahold.sql.DynamicSqlFiller;
import com.magdou.seahold.sql.SqlMaker;
import com.magdou.seahold.sql.impl.Pager;
import com.magdou.seahold.sql.impl.SqlC;
import com.magdou.seahold.utils.StringUtils;
import com.magdou.seahold.wrapper.VoWrapper;
import com.magdou.seahold.wrapper.impl.DefaultVoWrapper;

/**
 * Dao 默认入口类，用静态方法建立Dao对象
 * 
 * @author Ezir
 * 
 */
public class Dao<T> implements DaoFunc {

	/**
	 * 数据表vo类
	 */
	private Class<?> voClass;
	/**
	 * 数据池连接
	 */
	private DataSource dataSource;
	/**
	 * vo类到PreparedStatement填充器
	 */
	private VoFiller voFiller = new DefaultVoFiller();
	/**
	 * ResultSet到vo类包装器
	 */
	private VoWrapper voWrapper = new DefaultVoWrapper();

	/**
	 * 构造方法，传入connection
	 * 
	 * @param conn
	 * @throws DataSourceNullException
	 */
	public Dao(DataSource dataSource) throws DataSourceNullException {
		voClass = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (dataSource == null)
			throw new DataSourceNullException();
		this.dataSource = dataSource;
	}

	public Class<?> getVoClass() {
		return voClass;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int insert(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException {
		if (entity == null) {
			return 0;
		}
		Connection connection = dataSource.getConnection();
		SqlC sqlC = SqlC.insert(voClass);
		PreparedStatement pstmt = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		int result = pstmt.executeUpdate();
		pstmt.close();
		connection.close();
		return result;
	}

	@Override
	public int insert(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		SqlC sqlC = SqlC.insert(voClass);
		Connection connection = dataSource.getConnection();
		connection.setAutoCommit(false);
		List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		List<int[]> resultList = new ArrayList<int[]>();
		for (PreparedStatement pstmt : pstmtList) {
			int[] resultNode = pstmt.executeBatch();
			resultList.add(resultNode);
		}
		int result = 0;
		for (int[] resultNode : resultList) {
			for (int i : resultNode) {
				result += i;
			}
		}
		connection.setAutoCommit(true);
		for (PreparedStatement pstms : pstmtList) {
			pstms.close();
		}
		connection.close();
		return result;
	}

	@Override
	public int update(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException {
		if (entity == null) {
			return 0;
		}
		Connection connection = dataSource.getConnection();
		SqlC sqlC = SqlC.update(voClass);
		PreparedStatement pstmt = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		int result = pstmt.executeUpdate();
		pstmt.close();
		connection.close();
		return result;
	}

	@Override
	public int update(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		Connection connection = dataSource.getConnection();
		SqlC sqlC = SqlC.update(voClass);
		connection.setAutoCommit(false);
		List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		List<int[]> resultList = new ArrayList<int[]>();
		for (PreparedStatement pstmt : pstmtList) {
			int[] resultNode = pstmt.executeBatch();
			resultList.add(resultNode);
		}
		int result = 0;
		for (int[] resultNode : resultList) {
			for (int i : resultNode) {
				result += i;
			}
		}
		connection.setAutoCommit(true);
		for (PreparedStatement pstmt : pstmtList) {
			pstmt.close();
		}
		connection.close();
		return result;
	}

	@Override
	public int delete(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException {
		if (entity == null) {
			return 0;
		}
		Connection connection = dataSource.getConnection();
		SqlC sqlC = SqlC.delete(voClass);
		PreparedStatement pstmt = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		int result = pstmt.executeUpdate();
		pstmt.close();
		connection.close();
		return result;
	}

	@Override
	public int delete(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException {
		if (entity == null || entity.isEmpty()) {
			return 0;
		}
		Connection connection = dataSource.getConnection();
		SqlC sqlC = SqlC.delete(voClass);
		connection.setAutoCommit(false);
		List<PreparedStatement> pstmtList = voFiller.getPreparedStatement(connection, sqlC.getSql(), entity);
		List<int[]> resultList = new ArrayList<int[]>();
		for (PreparedStatement pstmt : pstmtList) {
			int[] resultNode = pstmt.executeBatch();
			resultList.add(resultNode);
		}
		int result = 0;
		for (int[] resultNode : resultList) {
			for (int i : resultNode) {
				result += i;
			}
		}
		connection.setAutoCommit(true);
		for (PreparedStatement pstmt : pstmtList) {
			pstmt.close();
		}
		connection.close();
		return result;
	}

	@Override
	public <T> T querySingle(SqlMaker sqlMaker) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlMaker.getSql());
			rs = pstmt.executeQuery();
			if (rs != null) {
				return voWrapper.wrapResultSetSingle(rs, voClass);
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> T querySingle(String sql) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs != null) {
				return voWrapper.wrapResultSetSingle(rs, voClass);
			} else {
				return null;
			}
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> List<T> query(SqlMaker sqlMaker) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlMaker.getSql());
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSet(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> List<T> query(String sql) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSet(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> T querySingle(SqlMaker sqlMaker, Map<String, String> condMap) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(DynamicSqlFiller.getTableFilledSql(sqlMaker.getSql(), condMap));
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSetSingle(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> T querySingle(String sql, Map<String, String> condMap) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(DynamicSqlFiller.getTableFilledSql(sql, condMap));
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSetSingle(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> List<T> query(SqlMaker sqlMaker, Map<String, String> condMap) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(DynamicSqlFiller.getTableFilledSql(sqlMaker.getSql(), condMap));
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSet(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public <T> List<T> query(String sql, Map<String, String> condMap) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(DynamicSqlFiller.getTableFilledSql(sql, condMap));
			rs = pstmt.executeQuery();
			return voWrapper.wrapResultSet(rs, voClass);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public ResultSet execute(String sql) throws SQLException {
		Connection conn = dataSource.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			return pstmt.executeQuery();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public long count() throws SQLException {
		Connection conn = dataSource.getConnection();
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
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public long count(String sql) throws SQLException {
		Connection conn = dataSource.getConnection();
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
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public long count(SqlMaker sql) throws SQLException {
		Connection conn = dataSource.getConnection();
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
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public long count(String sql, Map<String, String> condMap) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long count(SqlMaker sql, Map<String, String> condMap) throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public long fuzzyCount() throws SQLException {
		Connection conn = dataSource.getConnection();
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
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}

	@Override
	public long fastCount(int scanSpan, int countOut) throws SQLException {
		Connection conn = dataSource.getConnection();
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
			throw new SQLException(e);
		} finally {
			try {
				rs.close();
				pstmt.close();
				conn.close();
			} catch (SQLException e) {
				throw new SQLException(e);
			}
		}
	}
}
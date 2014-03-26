package com.magdou.seahold.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.magdou.seahold.sql.SqlMaker;

/**
 * Dao 对应功能函数
 * 
 * @author Ezir
 * 
 */
public interface DaoFunc {
	/**
	 * 插入单个实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 */
	public int insert(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException;

	/**
	 * 插入批量实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public int insert(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException;

	/**
	 * 更新单个实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 */
	public int update(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException;

	/**
	 * 更新批量实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public int update(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException;

	/**
	 * 删除单个实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 */
	public int delete(Object entity) throws IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException;

	/**
	 * 删除批量实体
	 * 
	 * @param entity
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws SQLException
	 */
	public int delete(Collection<?> entity) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SQLException;

	/**
	 * 搜索单个结果
	 * 
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> T querySingle(SqlMaker sqlMaker) throws SQLException;

	/**
	 * 搜索单个结果
	 * 
	 * @param sql sql语句
	 * @return
	 */
	public <T> T querySingle(String sql) throws SQLException;

	/**
	 * 返回多个结果
	 * 
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> List<T> query(SqlMaker sqlMaker) throws SQLException;

	/**
	 * 返回多个结果
	 * 
	 * @param sql sql语句
	 * @return
	 */
	public <T> List<T> query(String sql) throws SQLException;

	/**
	 * 搜索单个结果，按照json配置表名
	 * 
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> T querySingle(SqlMaker sqlMaker, Map<String, String> condMap) throws SQLException;

	/**
	 * 搜索单个结，按照json配置表名
	 * 
	 * @param sql sql语句
	 * @return
	 */
	public <T> T querySingle(String sql, Map<String, String> condMap) throws SQLException;

	/**
	 * 返回多个结果，按照json配置表名
	 * 
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> List<T> query(SqlMaker sqlMaker, Map<String, String> condMap) throws SQLException;

	/**
	 * 返回多个结果，按照json配置表名
	 * 
	 * @param sql sql语句
	 * @return
	 */
	public <T> List<T> query(String sql, Map<String, String> condMap) throws SQLException;

	/**
	 * 执行sql
	 * 
	 * @param sql sql语句
	 * @return
	 */
	public ResultSet execute(String sql) throws SQLException;

	/**
	 * 完整搜索
	 * 
	 * @param classOfT
	 * @return
	 */
	public long count() throws SQLException;

	/**
	 * 完整搜索
	 * 
	 * @param classOfT
	 * @return
	 */
	public long count(String sql) throws SQLException;

	/**
	 * 带条件的完整搜索
	 * 
	 * @param sql
	 * @param condMap
	 * @return
	 */
	public long count(String sql, Map<String, String> condMap) throws SQLException;

	/**
	 * 完整搜索
	 * 
	 * @param classOfT
	 * @return
	 */
	public long count(SqlMaker sql) throws SQLException;

	/**
	 * 带条件的完整搜索
	 * 
	 * @param sql
	 * @param condMap
	 * @return
	 */
	public long count(SqlMaker sql, Map<String, String> condMap) throws SQLException;

	/**
	 * 按照id模糊搜索
	 * 
	 * @param classOfT
	 * @return
	 */
	public long fuzzyCount() throws SQLException;

	/**
	 * limit 快速搜索
	 * 
	 * @param classOfT
	 * @return
	 */
	public long fastCount(int scanSpan, int countOut) throws SQLException;
}

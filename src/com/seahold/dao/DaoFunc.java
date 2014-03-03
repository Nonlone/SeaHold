package com.seahold.dao;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;

import com.seahold.dao.sql.SqlMaker;
/**
 * Dao 对应功能函数
 * @author Ezir
 *
 */
public interface DaoFunc {
	/**
	 * 插入单个实体
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
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> T querySingle(SqlMaker sqlMaker);
	/**
	 * 搜索单个结果
	 * @param sql sql语句
	 * @return
	 */
	public <T> T querySingle(String sql);
	
	/**
	 * 返回多个结果
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> List<T> query(SqlMaker sqlMaker);
	/**
	 * 返回多个结果
	 * @param sql sql语句
	 * @return
	 */
	public <T> List<T> query(String sql);
	/**
	 * 搜索单个结果，按照json配置表名
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> T querySingle(SqlMaker sqlMaker,JSONObject confjson);
	/**
	 * 搜索单个结，按照json配置表名
	 * @param sql sql语句
	 * @return
	 */
	public <T> T querySingle(String sql,JSONObject confjson);
	
	/**
	 * 返回多个结果，按照json配置表名
	 * @param sqlMaker sql制造器
	 * @return
	 */
	public <T> List<T> query(SqlMaker sqlMaker,JSONObject confjson);
	/**
	 * 返回多个结果，按照json配置表名
	 * @param sql sql语句
	 * @return
	 */
	public <T> List<T> query(String sql,JSONObject confjson);
	/**
	 * 执行sql
	 * @param sql sql语句
	 * @return
	 */
	public int execute(String sql);
	/**
	 * 完整搜索
	 * @param classOfT
	 * @return
	 */
	public long count();
	/**
	 * 完整搜索
	 * @param classOfT
	 * @return
	 */
	public long count(String sql);
	/**
	 * 完整搜索
	 * @param classOfT
	 * @return
	 */
	public long count(SqlMaker sql);
	/**
	 * id模糊搜索
	 * @param classOfT
	 * @return
	 */
	public long fuzzyCount();
	/**
	 * limit 快速搜索
	 * @param classOfT
	 * @return
	 */
	public long fastCount(int scanSpan,int countOut);
}

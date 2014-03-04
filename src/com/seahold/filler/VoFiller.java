package com.seahold.filler;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * vo类到PreparedStatement填充器接口
 * @author Ezir
 *
 */
public interface VoFiller {
	
	public Connection connection = null;
	
	/**
	 * 实例填充器，填充集合到sql语句中
	 * @param sqlMaker
	 * @param entity
	 * @return
	 * @throws SQLException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public List<PreparedStatement> getPreparedStatement(String sql,Collection<?> entity) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	/**
	 * 实例填充器，填充单个实体到sql语句中
	 * @param sqlMaker
	 * @param entity
	 * @return
	 * @throws SQLException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public PreparedStatement getPreparedStatement(String sql,Object entity) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
	/**
	 * Sql 包装器，包装Configuration Json到表名
	 * @param sql
	 * @param confjson
	 * @return
	 */
	public String getFilledSql(String sql,Map<String,String> configMap);
}

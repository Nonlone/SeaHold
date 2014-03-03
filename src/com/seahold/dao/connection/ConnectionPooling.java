package com.seahold.dao.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * 连接接口，项目用无论是连接池还是简单jdbc连接都是用这个接口实现一下
 * @author Ezir
 *
 */
public interface ConnectionPooling {
	/**
	 * 得到数据连接
	 * @return
	 */
	public Connection getConnection();
	/**
	 * 关闭连接
	 * @param conn
	 */
	public void close(Connection conn);
	/**
	 * 关闭Statement语句
	 * @param stmt
	 */
	public void close(Statement stmt);
	/**
	 * 关闭PreparedStatement语句
	 * @param pstmt
	 */
	public void close(PreparedStatement pstmt);
	/**
	 * 关闭结果集
	 * @param rs
	 */
	public void close(ResultSet rs);
	/**
	 * 关闭连接池
	 */
	public void close();
}

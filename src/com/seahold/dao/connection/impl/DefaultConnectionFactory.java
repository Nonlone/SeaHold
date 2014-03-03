package com.seahold.dao.connection.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.seahold.dao.connection.ConnectionPooling;
/**
 * 测试用连接池
 * @author Ezir
 *
 */
public class DefaultConnectionFactory implements ConnectionPooling {

	private String driver = "com.mysql.jdbc.Driver";
	private String jdbcUrlPerfix = "jdbc:mysql://";
	private String url = "127.0.0.1";
	private Integer port = 3306;
	private String dbName = "test";
	private String username = "root";
	private String password = "1123581321";
	
	public DefaultConnectionFactory(){}
	
	public DefaultConnectionFactory(String url,String dbName,Integer port,String username,String password){
		this.url = url;
		this.dbName = dbName;
		this.port = port;
		this.username = username;
		this.password = password;
	}

	private String  getJdbcUrl(){
		StringBuffer sbUrl = new StringBuffer(jdbcUrlPerfix);
		sbUrl.append(url);
		sbUrl.append(":"+port+"/");
		sbUrl.append(dbName);
		return sbUrl.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(getJdbcUrl(), username, password);
		} catch (ClassNotFoundException e) {
			return null;
		} catch (SQLException e) {
			return null;
		}
	}

	@Override
	public void close(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			//TODO Logger
		}
	}

	@Override
	public void close(Statement stmt) {
		try{
			if(stmt!=null){
				stmt.close();
			}
		}catch (SQLException e) {
		}

	}

	@Override
	public void close(PreparedStatement pstmt) {
		try{
			if(pstmt!=null){
				pstmt.close();
			}
		}catch (SQLException e) {
		}
	}

	@Override
	public void close(ResultSet rs) {
		try{
			if(rs!=null){
				rs.close();
			}
		}catch (SQLException e) {
		}

	}

	@Override
	public void close() {
	}

}

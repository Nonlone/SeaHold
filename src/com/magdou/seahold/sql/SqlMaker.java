package com.magdou.seahold.sql;
/**
 * Sql制造器，制造Sql
 * @author Ezir
 *
 */
public interface SqlMaker {
	/**
	 * return sql to execute
	 * @return
	 */
	public String getSql();
	/**
	 * 判断为select语句
	 * @return
	 */
	public boolean isSelect();
	/**
	 * 判断为Insert语句
	 * @return
	 */
	public boolean isInsert();
	/**
	 * 判断为update语句
	 * @return
	 */
	public boolean isUpdate();
	/**
	 * 判断为delete语句
	 * @return
	 */
	public boolean isDelete();
	
}

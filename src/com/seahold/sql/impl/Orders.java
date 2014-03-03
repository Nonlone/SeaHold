package com.seahold.sql.impl;

import java.util.HashMap;
import java.util.Map;
/**
 * Sql排序
 * @author Ezir
 *
 */
public class Orders {
	/**
	 * 排序类型枚举
	 * @author Ezir
	 *
	 */
	private enum SortOrder {
		asc, desc
	}

	/**
	 * 排序Map
	 */
	private Map<String, SortOrder> ordersMap = new HashMap<String, SortOrder>();
	/**
	 * 私有构造器
	 */
	private Orders(){}

	/**
	 * 升序构造
	 * @param columnName
	 * @return
	 */
	public static Orders sortAsc(String columnName) {
		Orders orders = new Orders();
		orders.ordersMap.put(columnName, SortOrder.asc);
		return orders;
	}

	/**
	 * 降序构造
	 * @param columnName
	 * @return
	 */
	public static Orders sortDesc(String columnName) {
		Orders orders = new Orders();
		orders.ordersMap.put(columnName, SortOrder.desc);
		return orders;
	}
	
	/**
	 * 附加升序条件
	 * @param columnName
	 * @return
	 */
	public Orders asc(String columnName){
		this.ordersMap.put(columnName,SortOrder.asc);
		return this;
	}
	
	/**
	 * 附加降序条件
	 * @param columnName
	 * @return
	 */
	public Orders desc(String columnName){
		this.ordersMap.put(columnName, SortOrder.desc);
		return this;
	}
	
	/**
	 * 获得Sql
	 * @return
	 */
	public String getSql(){
		StringBuffer sbSql = new StringBuffer(" order by");
		for(String columnName:this.ordersMap.keySet()){
			SortOrder so = ordersMap.get(columnName);
			sbSql.append(" "+columnName+" "+so+",");
		}
		return sbSql.substring(0, sbSql.length()-1);
	}
	
	@Override
	public String toString() {
		return this.getSql();
	}
}

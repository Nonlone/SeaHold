package com.seahold.dao.dynamic;

import org.json.JSONObject;
/**
 * 动态表名接口
 * @author Ezir
 *
 */
public interface DynamicTable {
	/**
	 * vo实现这个接口，按照实体生成动态配置json，会自动替换 表名中${x}，x为json的key，替换为对应json值
	 * @return
	 */
	public JSONObject getDynamicConfig();
}

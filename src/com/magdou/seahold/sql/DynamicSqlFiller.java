package com.magdou.seahold.sql;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicSqlFiller {
	
	public String getFilledSql(String sql,Map<String,String> configMap){
		//修正sql，替换动态表名
		String tableRegExp = "\\$\\{[^}]+\\}";
		Pattern pattern = Pattern.compile(tableRegExp);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String keyName = getElementForTableName(matcher.group());			
			if(configMap.containsKey(keyName)){
				sql = sql.replace(matcher.group(), configMap.get(keyName));
				matcher = pattern.matcher(sql);
			}
		}
		return sql;
	 }
	
	/**
	 * 提取动态表名值
	 * @param dynName
	 * @return
	 */
	private static String getElementForTableName(String dynName) {
		if (dynName.length() > 3) {
			int beginIndex = dynName.indexOf("{")+1;
			int endIndex = dynName.lastIndexOf("}");
			return dynName.substring(beginIndex, endIndex);
		}
		return null;
	}
}

package com.magdou.seahold.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicSqlFiller {
	
	//动态表名替换器，按照Map的key替换表名对应的${key}位置
	public static String getTableFilledSql(String sql,Map<String,String> configMap){
		String tableRegExp = "\\$\\{[^}]+\\}";
		Pattern pattern = Pattern.compile(tableRegExp);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String keyName = getKeyForTableName(matcher.group());			
			if(configMap.containsKey(keyName)){
				sql = sql.replace(matcher.group(), configMap.get(keyName));
				matcher = pattern.matcher(sql);
			}
		}
		return sql;
	 }
	
	public static List<String> getValueFilledSql(String sql,Map<String,String> configMap){
		List<String> keyList = new ArrayList<String>();
		String fieldRegExp = "@[\\S]*\\b";
		Pattern pattern = Pattern.compile(fieldRegExp);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String keyName = getKeyForValueName(matcher.group());
			if(configMap.containsKey(keyName)){
				sql = sql.replaceFirst(matcher.group(), "?");
				keyList.add(keyName);
				matcher = pattern.matcher(sql);
			}
		}
		return keyList;
	}
	
	/**
	 * 提取动态表名值
	 * @param dynKey
	 * @return
	 */
	private static String getKeyForTableName(String dynKey) {
		if (dynKey.length() > 3) {
			int beginIndex = dynKey.indexOf("{")+1;
			int endIndex = dynKey.lastIndexOf("}");
			return dynKey.substring(beginIndex, endIndex);
		}
		return null;
	}
	
	/**
	 * 提取动态值名
	 * @param dynKey
	 * @return
	 */
	private static String getKeyForValueName(String dynKey){
		if(dynKey.length()>1){
			return dynKey.substring(1);
		}
		return null;
	}
}

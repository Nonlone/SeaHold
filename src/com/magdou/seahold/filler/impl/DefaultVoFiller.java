package com.magdou.seahold.filler.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.magdou.seahold.dao.dynamic.DynamicTable;
import com.magdou.seahold.filler.VoFiller;
import com.magdou.seahold.utils.StringUtils;
/**
 * 默认PreparedStatement填充器
 * @author Ezir
 *
 */
public class DefaultVoFiller implements VoFiller {


	@Override
	public List<PreparedStatement> getPreparedStatement(Connection conn, String sql, Collection<?> entity) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		List<String> fieldList = new ArrayList<String>();
		Map<String,PreparedStatement> pstmtMap = new HashMap<String, PreparedStatement>();
		List<PreparedStatement> pstmtList = new ArrayList<PreparedStatement>();
		PreparedStatement pstmt = null;
		if (!StringUtils.isEmpty(sql)&&entity!=null&&!entity.isEmpty()) {
			//修正sql，替换field
			String fieldRegExp = "@[\\S]*\\b";
			Pattern pattern = Pattern.compile(fieldRegExp);
			Matcher matcher = pattern.matcher(sql);
			while (matcher.find()) {
				fieldList.add(matcher.group().substring(1));
				sql = sql.replaceFirst(matcher.group(), "?");
			}
			for (Object c : entity) {
				int i = 1;
				//动态表名接口填充
				if (c instanceof DynamicTable) {
					JSONObject json = ((DynamicTable) c).getDynamicConfig();
					if(pstmtMap.containsKey(json.toString())){
						//获取pstmt
						pstmt = pstmtMap.get(json.toString());
					}else{
						//制造sql，封入map
						String operaSql = getFilledSql(sql, json);
						pstmt = conn.prepareStatement(operaSql);
						pstmtMap.put(json.toString(), pstmt);
					}
				}else{
					if(pstmt==null){
						pstmt = conn.prepareStatement(sql);
					}
				}
				//反射方法填充field
				Map<String, Method> methodMap = new HashMap<String, Method>();
				for (Method method : c.getClass().getDeclaredMethods()) {
					methodMap.put(method.getName(), method);
				}
				for (String value : fieldList) {
					String methodName = "get" + StringUtils.upperFirst(value);
					if (methodMap.containsKey(methodName)) {
						Method method = methodMap.get(methodName);
						pstmt.setObject(i++, method.invoke(c, null));
					}
				}
				pstmt.addBatch();
			}
			if(pstmtMap.isEmpty()){
				pstmtList.add(pstmt);
			}else{
				pstmtList = new ArrayList<PreparedStatement>(pstmtMap.values());
			}
		}
		return pstmtList;
	}

	@Override
	public PreparedStatement getPreparedStatement(Connection conn, String sql, Object entity) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		List<String> fieldList = new ArrayList<String>();
		PreparedStatement pstmt = null;
		if (!StringUtils.isEmpty(sql)&&entity!=null) {
			//修正sql，替换field
			String fieldRegExp = "@[\\S]*\\b";
			Pattern pattern = Pattern.compile(fieldRegExp);
			Matcher matcher = pattern.matcher(sql);
			while (matcher.find()) {
				fieldList.add(matcher.group().substring(1));
				sql = sql.replaceFirst(matcher.group(), "?");
			}
			int i = 1;
			//动态表名接口填充
			if (entity instanceof DynamicTable) {
				JSONObject json = ((DynamicTable) entity).getDynamicConfig();
				String operaSql = getFilledSql(sql, json);
				pstmt = conn.prepareStatement(operaSql);
			}else{
				pstmt = conn.prepareStatement(sql);
			}
			//反射方法填充field
			Map<String, Method> methodMap = new HashMap<String, Method>();
			for (Method method : entity.getClass().getDeclaredMethods()) {
				methodMap.put(method.getName(), method);
			}
			for (String value : fieldList) {
				String methodName = "get" + StringUtils.upperFirst(value);
				if (methodMap.containsKey(methodName)) {
					Method method = methodMap.get(methodName);
					pstmt.setObject(i++, method.invoke(entity, null));
				}
			}
			pstmt.addBatch();

		}
		return pstmt;
	}

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

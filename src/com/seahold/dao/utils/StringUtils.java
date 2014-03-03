package com.seahold.dao.utils;

/**
 * 字符串工具类
 * 
 * @author Ezir
 * 
 */
public class StringUtils {
	/**
	 * 首字母大写
	 * 
	 * @param str
	 * @return
	 */
	public static String upperFirst(String str) {
		if (null == str || str.length() < 1) {
			return null;
		}
		StringBuffer sb = new StringBuffer(str.substring(0, 1).toUpperCase());
		sb.append(str.substring(1));
		return sb.toString();
	}

	/**
	 * 首字母小写
	 * 
	 * @param str
	 * @return
	 */
	public static String lowerFirst(String str) {
		if (null == str || str.length() < 1) {
			return null;
		}
		StringBuffer sb = new StringBuffer(str.substring(0, 1).toLowerCase());
		sb.append(str.substring(1));
		return sb.toString();
	}

	public static boolean isEmpty(String str) {
		if (str != null && str.length() > 0) {
			return false;
		}
		return true;
	}
}

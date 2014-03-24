package com.magdou.seahold.wrapper;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
/**
 * ResultSet 包装器接口
 * @author Ezir
 *
 */
public interface VoWrapper {
	/**
	 * ResultSet 包装器，包装成单个Vo返回
	 * @param rs
	 * @param classOfT 单个类
	 * @return
	 */
	public <T> T  wrapResultSetSingle(ResultSet rs,Class<?> classOfT);
	/**
	 * ResultSet 包装器，包装成Vo的List 返回
	 * @param rs
	 * @param classOfT 单个类
	 * @return
	 */
	public <T> List<T> wrapResultSet(ResultSet rs,Class<?> classOfT);
	/**
	 * ResultSet 包装器，包装成Vo的List 返回
	 * @param rs
	 * @param classes 多个类
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public <T> Map<Class,List<T>> wrapResultSet(ResultSet rs,Class<?>...classes);
}

package com.magdou.seahold.wrapper.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.magdou.seahold.annotation.DBField;
import com.magdou.seahold.annotation.DBTable;
import com.magdou.seahold.utils.StringUtils;
import com.magdou.seahold.wrapper.MultiFieldConventer;
import com.magdou.seahold.wrapper.VoWrapper;

/**
 * 默认ResultSet包装器
 * @author Ezir
 *
 */
public class DefaultVoWrapper implements VoWrapper {


	@SuppressWarnings("unchecked")
	public <T> List<T> wrapResultSet(ResultSet rs,String className) {
		return (List<T>) this.wrapResultSet(rs, Class.forName(className));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T wrapResultSetSingle(ResultSet rs, Class<?> classOfT) {
		// 处理vo
		Map<String, Method> methodMap = new HashMap<String, Method>();
		for (Method method : classOfT.getMethods()) {
			methodMap.put(method.getName(), method);
		}
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> colnums = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				colnums.add(rsmd.getColumnName(i));
			}
			//json值Map
			Map<String, Object> colMap = new HashMap<String, Object>();
			T entity = (T) classOfT.newInstance();
			if (rs.next()) {
				// fill entity
				for (String colnum : colnums) {
					colMap.put(colnum, rs.getObject(colnum));
				}
				DBTable table = classOfT.getAnnotation(DBTable.class);
				Set<Field> fieldSet = new HashSet<Field>();
				for(Field field:classOfT.getFields()){
					fieldSet.add(field);
				}
				for(Field field:classOfT.getDeclaredFields()){
					fieldSet.add(field);
				}
				if (table != null && !table.isAutoFill()) {
					//表注解
					for (Field field : fieldSet) {
						DBField dbf = field.getAnnotation(DBField.class);
						if (dbf != null) {
							//列注解
							if (dbf.multiColnum().length > 0) {
								//多值json填充
								JSONObject json = new JSONObject();
								for (String colnum : dbf.multiColnum()) {
									json.put(colnum, colMap.get(colnum));
								}
								String methodName = "set" + StringUtils.upperFirst(field.getName());
								if (methodMap.containsKey(methodName)) {
									//填充field
									Method method = methodMap.get(methodName);
									Class<?> paramsType = method.getParameterTypes()[0];
									if (paramsType == String.class) {
										method.invoke(entity, json.toString());
									} else if (paramsType == JSONObject.class) {
										method.invoke(entity, json);
									} else {
										//转换json，获得转换器
										if (dbf.multiConventer().length() > 0) {
											MultiFieldConventer<T> mfc = (MultiFieldConventer<T>) Class.forName(dbf.multiConventer()).newInstance();
											method.invoke(entity, mfc.multiFieldConvent(json));
										}
									}
								}
							} else {
								//单个column
								if (dbf.isExt()) {
									//延伸字段
									JSONObject json = new JSONObject(colMap.get(dbf.fieldName()));
									if (json.has(field.getName())) {
										String methodName = "set" + StringUtils.upperFirst(field.getName());
										if (methodMap.containsKey(methodName)) {
											Method method = methodMap.get(methodName);
											if(colMap.get(field.getName())!=null){
												method.invoke(entity, colMap.get(field.getName()));
											}
										}
									}
								} else {
									//普通字段
									String methodName= "set" + StringUtils.upperFirst(field.getName());
									String colnumName=dbf.fieldName();
									if(colnumName.length()==0){
										colnumName = field.getName();
									}
									if (methodMap.containsKey(methodName)) {
										Method method = methodMap.get(methodName);
										if(colMap.get(colnumName)!=null){
											method.invoke(entity, colMap.get(colnumName));
										}
									}
								}
							}
						}
					}
				} else {
					//自动填充
					for (Field field : fieldSet) {
						String methodName = "set" + StringUtils.upperFirst(field.getName());
						if (methodMap.containsKey(methodName)) {
							Method method = methodMap.get(methodName);
							if(colMap.get(field.getName())!=null){
								method.invoke(entity, colMap.get(field.getName()));
							}
						}
					}
				}
				return entity;
			}else{
				return null;
			}			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> wrapResultSet(ResultSet rs, Class<?> classOfT) {
		// 处理vo
		List<T> resultList = new ArrayList<T>();
		Map<String, Method> methodMap = new HashMap<String, Method>();
		for (Method method : classOfT.getMethods()) {
			methodMap.put(method.getName(), method);
		}
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> colnums = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				colnums.add(rsmd.getColumnName(i));
			}
			//json值Map
			Map<String, Object> colMap = new HashMap<String, Object>();
			//field 集合
			Set<Field> fieldSet = new HashSet<Field>();
			for(Field field:classOfT.getFields()){
				fieldSet.add(field);
			}
			for(Field field:classOfT.getDeclaredFields()){
				fieldSet.add(field);
			}
			while (rs.next()) {
				T entity = (T) classOfT.newInstance();
				//填充实体
				for (String colnum : colnums) {
					colMap.put(colnum, rs.getObject(colnum));
				}
				DBTable table = classOfT.getAnnotation(DBTable.class);
				if (table != null && !table.isAutoFill()) {
					//表注解
					for (Field field : fieldSet) {
						DBField dbf = field.getAnnotation(DBField.class);
						if (dbf != null) {
							//列注解
							if (dbf.multiColnum().length > 0) {
								//填充json多值
								JSONObject json = new JSONObject();
								for (String colnum : dbf.multiColnum()) {
									json.put(colnum, colMap.get(colnum));
								}
								String methodName = "set" + StringUtils.upperFirst(field.getName());
								if (methodMap.containsKey(methodName)) {
									//填充field
									Method method = methodMap.get(methodName);
									Class<?> paramsType = method.getParameterTypes()[0];
									if (paramsType == String.class) {
										method.invoke(entity, json.toString());
									} else if (paramsType == JSONObject.class) {
										method.invoke(entity, json);
									} else {
										//转换json，获得转换器
										if (dbf.multiConventer().length() > 0) {
											MultiFieldConventer<T> mfc = (MultiFieldConventer<T>) Class.forName(dbf.multiConventer()).newInstance();
											method.invoke(entity, mfc.multiFieldConvent(json));
										}
									}
								}
							} else {
								//单个column
								if (dbf.isExt()) {
									//延伸字段
									JSONObject json = new JSONObject(colMap.get(dbf.fieldName()));
									if (json.has(field.getName())) {
										String methodName = "set" + StringUtils.upperFirst(field.getName());
										if (methodMap.containsKey(methodName)) {
											Method method = methodMap.get(methodName);
											if(colMap.get(field.getName())!=null){
												method.invoke(entity, colMap.get(field.getName()));
											}
										}
									}
								} else {
									//填充普通列
									String methodName= "set" + StringUtils.upperFirst(field.getName());
									String colnumName=dbf.fieldName();
									if(colnumName.length()==0){
										colnumName = field.getName();
									}
									if (methodMap.containsKey(methodName)) {
										Method method = methodMap.get(methodName);
										if(colMap.get(colnumName)!=null){
											method.invoke(entity, colMap.get(colnumName));
										}
									}
								}
							}
						}
					}
				} else {
					//自动填充
					for (Field field : fieldSet) {
						String methodName = "set" + StringUtils.upperFirst(field.getName());
						if (methodMap.containsKey(methodName)) {
							Method method = methodMap.get(methodName);
							if(colMap.get(field.getName())!=null){
								method.invoke(entity, colMap.get(field.getName()));
							}
						}
					}
				}
				resultList.add(entity);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			return resultList;
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return resultList;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> Map<Class, List<T>> wrapResultSet(ResultSet rs, Class<?>... classes) {
		Map<Class, List<T>> resultMap = new HashMap<Class, List<T>>();
		Map<Class, Map<String, Method>> classMethodMap = new HashMap<Class, Map<String, Method>>();
		for (Class classOfT : classes) {
			Map<String, Method> methodMap = new HashMap<String, Method>();
			for (Method method : classOfT.getMethods()) {
				methodMap.put(method.getName(), method);
			}
			classMethodMap.put(classOfT, methodMap);
		}
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> colnums = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				colnums.add(rsmd.getColumnName(i));
			}
			//json值Map
			Map<String, Object> colMap = new HashMap<String, Object>();
			while (rs.next()) {
				colMap.clear();
				for (String colnum : colnums) {
					colMap.put(colnum, rs.getObject(colnum));
				}
				for (Class<?> classOfT : classes) {
					Map<String, Method> methodMap = classMethodMap.get(classOfT);
					T entity = (T) classOfT.newInstance();
					Set<Field> fieldSet = new HashSet<Field>();
					for(Field field:classOfT.getFields()){
						fieldSet.add(field);
					}
					for(Field field:classOfT.getDeclaredFields()){
						fieldSet.add(field);
					}
					//表注解
					DBTable table = (DBTable) classOfT.getAnnotation(DBTable.class);
					if (table != null && !table.isAutoFill()) {
						//列注解
						for (Field field : fieldSet) {
							DBField dbf = field.getAnnotation(DBField.class);
							if (dbf != null) {
								//检查注解
								if (dbf.multiColnum().length > 0) {
									//多个colnum
									JSONObject json = new JSONObject();
									for (String colnum : dbf.multiColnum()) {
										json.put(colnum, colMap.get(colnum));
									}
									String methodName = "set" + StringUtils.upperFirst(field.getName());
									if (methodMap.containsKey(methodName)) {
										//填充field
										Method method = methodMap.get(methodName);
										Class paramsType = method.getParameterTypes()[0];
										if (paramsType == String.class) {
											method.invoke(entity, json.toString());
										} else if (paramsType == JSONObject.class) {
											method.invoke(entity, json);
										} else {
											//转换json，获得转换器
											if (dbf.multiConventer().length() > 0) {
												MultiFieldConventer<T> mfc = (MultiFieldConventer<T>) Class.forName(dbf.multiConventer()).newInstance();
												method.invoke(entity, mfc.multiFieldConvent(json));
											}
										}
									}
								} else {
									//单个column
									if (dbf.isExt()) {
										//延伸字段
										JSONObject json = new JSONObject(colMap.get(dbf.fieldName()));
										if (json.has(field.getName())) {
											String methodName = "set" + StringUtils.upperFirst(field.getName());
											if (methodMap.containsKey(methodName)) {
												Method method = methodMap.get(methodName);
												method.invoke(entity, json.get(field.getName()));
											}
										}
									} else {
										//普通字段
										String methodName= "set" + StringUtils.upperFirst(field.getName());
										String colnumName=dbf.fieldName();
										if(colnumName.length()==0){
											colnumName = field.getName();
										}
										if (methodMap.containsKey(methodName)) {
											Method method = methodMap.get(methodName);
											if(colMap.get(colnumName)!=null){
												method.invoke(entity, colMap.get(colnumName));
											}
										}
									}
								}
							}
						}
					} else {
						//自动填充
						for (Field field : fieldSet) {
							String methodName = "set" + StringUtils.upperFirst(field.getName());
							if (methodMap.containsKey(methodName)) {
								Method method = methodMap.get(methodName);
								method.invoke(entity, colMap.get(field.getName()));
							}
						}
					}
					if (resultMap.containsKey(classOfT)) {
						resultMap.get(classOfT).add(entity);
					} else {
						List<T> resultList = new ArrayList<T>();
						resultList.add(entity);
						resultMap.put(classOfT, resultList);
					}
				}
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e);
			return null;
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return resultMap;
	}
}
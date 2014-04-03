package com.magdou.seahold.config.colunm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.magdou.seahold.utils.StringUtils;

public class NormalConfig {
	
	public static Map<String,Field> getColunmConfig(Class<?> classOfT){
		Map<String,Method> methodMap = new HashMap<String,Method>();
		Map<String,Field> fieldMap = new HashMap<String,Field>();
		Class<?> clazz = classOfT;
		//递归获得类方法
		while(clazz!=Object.class){
			for(Method method:classOfT.getDeclaredMethods()){
				if(!methodMap.containsKey(method.getName())){
					methodMap.put(method.getName(),method);
				}
			}
			clazz=classOfT.getClass().getSuperclass();
		}
		clazz = classOfT;
		// 递归获得类属性
		while(clazz!=Object.class){
			for(Field field:classOfT.getDeclaredFields()){
				if(!fieldMap.containsKey(field.getName())){
					fieldMap.put(field.getName(),field);
				}
			}
			clazz = clazz.getClass().getSuperclass();
		}
		
	}
}

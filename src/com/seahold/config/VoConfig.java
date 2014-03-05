package com.seahold.config;

import java.util.Map;
import java.util.Set;

import com.seahold.annotation.DBTable;
import com.seahold.annotation.DBTable.TableType;
import com.seahold.exception.DBTableAnnotationException;

public class VoConfig {
	private String tableName;//表名
	private TableType tableType;//表类型
	private Map<String,String> colunmMap;//列Map
	private Set<String> keyColunm;//键Set
	private String fillerPath;//填充器包路径
	private String wrapperPath;//包装器包路径
	
	public VoConfig(Class<?> clazz) throws DBTableAnnotationException{
		DBTable dbTable = clazz.getAnnotation(DBTable.class);
		if(dbTable!=null){
			if(dbTable.tableName().length()>0){
				//获得表名
				tableName = dbTable.tableName();
			}else{
				//使用类名
				tableName = clazz.getSimpleName();
			}
		}else{
			throw new DBTableAnnotationException();
		}
	}
	
}

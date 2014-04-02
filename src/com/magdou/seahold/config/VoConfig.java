package com.magdou.seahold.config;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import com.magdou.seahold.annotation.DBTable;
import com.magdou.seahold.annotation.DBTable.TableType;
import com.magdou.seahold.exception.DBTableAnnotationException;

public class VoConfig {
	private String tableName;//表名
	private TableType tableType;//表类型
	private Map<String,Field> colunmMap;//列Map
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
			tableType = dbTable.tableType();
			switch (tableType) {
			case NORMAL:
				
				break;

			default:
				break;
			}
		}else{
			throw new DBTableAnnotationException();
		}
	}	
}

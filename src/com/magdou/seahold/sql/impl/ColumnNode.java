package com.magdou.seahold.sql.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.magdou.seahold.annotation.DBField;
import com.magdou.seahold.annotation.DBTable;
/**
 * Sql列构造器
 * @author Ezir
 *
 */
public class ColumnNode {
	/**
	 * 全部列
	 */
	public final static int ALLCOLUMN = 1;
	/**
	 * 自动生成列
	 */
	public final static int GENERATECOLUMN = 2;
	/**
	 * 关键列，key列
	 */
	public final static int KEYCOLUMN = 3;
	/**
	 * 实体持有列，不含有自动生成列
	 */
	public final static int ENTITYCOLUMN = 4;

	private final static String count = "count(";
	private final static String sum = "sum(";
	private final static String avg = "avg(";
	/**
	 * 列名
	 */
	private String fieldName;
	/**
	 * 表名
	 */
	private String tableName;

	public ColumnNode() {
	}

	public ColumnNode(String fieldName) {
		this.fieldName = fieldName;
		this.tableName = "";
	}

	public ColumnNode(String fieldName, String tableName) {
		this.fieldName = fieldName;
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public static ColumnNode put(String fieldName) {
		return put(fieldName, "");
	}

	public static ColumnNode put(String fieldName, String tableName) {
		return new ColumnNode(fieldName, tableName);
	}

	public static ColumnNode count(String fieldName) {
		return count(fieldName, "");
	}

	public static ColumnNode count(String fieldName, String tableName) {
		return new ColumnNode(count + fieldName + ")", tableName);
	}

	public static ColumnNode sum(String fieldName) {
		return sum(fieldName, "");
	}

	public static ColumnNode sum(String fieldName, String tableName) {
		return new ColumnNode(sum + fieldName + ")", tableName);
	}

	public static ColumnNode avg(String fieldName) {
		return avg(fieldName, "");
	}

	public static ColumnNode avg(String fieldName, String tableName) {
		return new ColumnNode(avg + fieldName + ")", tableName);
	}
	/**
	 * 按照类型和实体类生成列list
	 * @param columnType
	 * @param classes
	 * @return
	 */
	public static List<ColumnNode> getColumn(int columnType, Class<?>... classes) {
		List<ColumnNode> resultList = new ArrayList<ColumnNode>();
		for (Class<?> classOfT : classes) {
			DBTable table = classOfT.getAnnotation(DBTable.class);
			Set<Field> fieldSet = new HashSet<Field>();
			for(Field field:classOfT.getFields()){
				fieldSet.add(field);
			}
			for(Field field:classOfT.getDeclaredFields()){
				fieldSet.add(field);
			}
			if (table != null && !table.isAutoFill()) {
				//不是自动类型，解析注解
				String tableName = table.tableName();
				for (Field field : fieldSet) {
					if (!Modifier.isFinal(field.getModifiers())) {
						DBField dbf = field.getAnnotation(DBField.class);
						if (dbf != null && dbf.multiColnum().length < 1) {
							if (columnType == ENTITYCOLUMN) {
								if (!dbf.isAutoGenerate()) {
									//实例列
									ColumnNode sn;
									if(dbf.fieldName().length()!=0){
										sn = new ColumnNode(dbf.fieldName(), tableName);
									}else{
										sn = new ColumnNode(field.getName(), tableName);
									}
									resultList.add(sn);
								}
							} else if (columnType == GENERATECOLUMN) {
								if (dbf.isAutoGenerate()) {
									//db系统生成列
									ColumnNode sn;
									if(dbf.fieldName().length()!=0){
										sn = new ColumnNode(dbf.fieldName(), tableName);
									}else{
										sn = new ColumnNode(field.getName(), tableName);
									}
									resultList.add(sn);
								}
							} else if (columnType == KEYCOLUMN) {
								if (dbf.isKey()) {
									//db系统生成列
									ColumnNode sn;
									if(dbf.fieldName().length()!=0){
										sn = new ColumnNode(dbf.fieldName(), tableName);
									}else{
										sn = new ColumnNode(field.getName(), tableName);
									}
									resultList.add(sn);
								}
							} else {
								//全部列
								ColumnNode sn;
								if(dbf.fieldName().length()!=0){
									sn = new ColumnNode(dbf.fieldName(), tableName);
								}else{
									sn = new ColumnNode(field.getName(), tableName);
								}
								resultList.add(sn);
							}
						}
					}
				}
			} else if (columnType == ALLCOLUMN) {
				//获得非注解状态下的全部列
				for (Field field : fieldSet) {
					if (!Modifier.isFinal(field.getModifiers())) {
						ColumnNode sn = new ColumnNode(field.getName());
						resultList.add(sn);
					}
				}
			} else {
				//其他列类型需要注解支持，返回空值
				resultList = null;
			}
		}
		return resultList;
	}
}

package com.seahold.dao.sql.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;
import com.seahold.dao.sql.SqlMaker;
import com.seahold.dao.utils.StringUtils;

/**
 * Sql 构造器
 * 
 * @author Ezir
 * 
 */
public class SqlC implements SqlMaker {
	/**
	 * SQL 操作枚举类
	 * 
	 * @author Ezir
	 * 
	 */
	private enum Operator {
		Select, Insert, Update, Delete
	}
	/**
	 * 映射类
	 */
	private Class<?> voClass;
	/**
	 * Sql操作符
	 */
	private Operator operator;
	/**
	 * Sql操作列
	 */
	private List<ColumnNode> colNode;
	/**
	 * Sql操作表
	 */
	private TableNode tableNode;
	/**
	 * Sql操作条件
	 */
	private Conditions whereConditions;
	/**
	 * Sql操作排序
	 */
	private Orders orders;
	/**
	 * Sql操作限制（分页）
	 */
	private Pager pager;
	
	/**
	 * 私有化构造器，从静态类实现
	 */
	private SqlC() {
	}

	/**
	 * 构造Select，*， 所有列
	 * @return
	 */
	public static SqlC select() {
		SqlC sqlC = new SqlC();
		sqlC.operator = Operator.Select;
		sqlC.colNode = new ArrayList<ColumnNode>();
		ColumnNode colNode = new ColumnNode("*");
		sqlC.colNode.add(colNode);
		return sqlC;
	}

	/**
	 * 构造Select，类中所有列
	 * @param classOfT
	 * @return
	 */
	public static SqlC select(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Select;
		sqlC.colNode = ColumnNode.getColumn(ColumnNode.ALLCOLUMN, classOfT);
		sqlC.tableNode = TableNode.getTable(classOfT);
		return sqlC;
	}

	/**
	 * 构造Select 多个类
	 * @param classes
	 * @return
	 */
	public static SqlC select(Class<?>... classes) {
		SqlC sqlC = new SqlC();
		sqlC.operator = Operator.Select;
		sqlC.colNode = ColumnNode.getColumn(ColumnNode.ALLCOLUMN, classes);
		sqlC.tableNode = TableNode.getTable(classes[0]);
		int i = 1;
		TableNode tn = sqlC.tableNode;
		while (i < classes.length) {
			TableNode tableNode = TableNode.getTable(classes[i]);
			tn.crossJoin(tableNode);
			tn = tn.getTableNode();
			i++;
		}
		return sqlC;
	}

	/**
	 * 构造Insert
	 * @param classOfT
	 * @return
	 */
	public static SqlC insert(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Insert;
		sqlC.colNode = ColumnNode.getColumn(ColumnNode.ENTITYCOLUMN, classOfT);
		sqlC.tableNode = TableNode.getTable(classOfT);
		return sqlC;
	}

	/**
	 * 构造Update
	 * @param classOfT
	 * @return
	 */
	public static SqlC update(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Update;
		sqlC.colNode = ColumnNode.getColumn(ColumnNode.ENTITYCOLUMN, classOfT);
		sqlC.tableNode = TableNode.getTable(classOfT);
		List<ColumnNode> keyCol = ColumnNode.getColumn(ColumnNode.KEYCOLUMN, classOfT);
		if (keyCol != null && keyCol.size() > 0) {
			ColumnNode key = keyCol.get(0);
			Conditions conditions = Conditions.start(key.getFieldName()).eq("@" + key.getFieldName());
			sqlC.whereConditions = conditions;
			for (int i = 1; i < keyCol.size(); i++) {
				key = keyCol.get(i);
				Conditions next = Conditions.start(key.getFieldName()).eq("@" + key.getFieldName());
				conditions.and(next);
			}
		}
		return sqlC;
	}

	/**
	 * 构造Update 按照条件
	 * @param classOfT
	 * @param conditions
	 * @return
	 */
	public static SqlC update(Class<?> classOfT, Conditions conditions) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Update;
		sqlC.colNode = ColumnNode.getColumn(ColumnNode.ENTITYCOLUMN, classOfT);
		sqlC.tableNode = TableNode.getTable(classOfT);
		sqlC.whereConditions = conditions;
		return sqlC;
	}

	/**
	 * 构造Delete
	 * @param classOfT
	 * @return
	 */
	public static SqlC delete(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Delete;
		sqlC.tableNode = TableNode.getTable(classOfT);
		List<ColumnNode> keyCol = ColumnNode.getColumn(ColumnNode.KEYCOLUMN, classOfT);
		if (keyCol != null && keyCol.size() > 0) {
			ColumnNode key = keyCol.get(0);
			Conditions conditions = Conditions.start(key.getFieldName()).eq("@" + key.getFieldName());
			sqlC.whereConditions = conditions;
			for (int i = 1; i < keyCol.size(); i++) {
				key = keyCol.get(i);
				Conditions next = Conditions.start(key.getFieldName()).eq("@" + key.getFieldName());
				conditions.and(next);
			}
		}
		return sqlC;
	}

	/**
	 * 构造Delete 按照条件
	 * @param classOfT
	 * @param conditions
	 * @return
	 */
	public static SqlC delete(Class<?> classOfT, Conditions conditions) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Delete;
		sqlC.tableNode = TableNode.getTable(classOfT);
		sqlC.whereConditions = conditions;
		return sqlC;
	}

	/**
	 * 构造count 本体为select
	 * @param classOfT
	 * @return
	 */
	public static SqlC count(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Select;
		List<ColumnNode> colNode = ColumnNode.getColumn(ColumnNode.KEYCOLUMN, classOfT);
		if (!colNode.isEmpty()) {
			ColumnNode cn = colNode.get(0);
			sqlC.colNode = new ArrayList<ColumnNode>();
			if (!StringUtils.isEmpty(cn.getTableName())) {
				sqlC.colNode.add(ColumnNode.count(cn.getTableName() + "." + cn.getFieldName()));
			} else {
				sqlC.colNode.add(ColumnNode.count(cn.getFieldName()));
			}

		}
		sqlC.tableNode = TableNode.getTable(classOfT);
		return sqlC;
	}
	
	/**
	 * 构造模糊count 本体为按照id逆序的select，返回自增id值
	 * @param classOfT
	 * @return
	 */
	public static SqlC fuzzyCount(Class<?> classOfT) {
		SqlC sqlC = new SqlC();
		sqlC.voClass = classOfT;
		sqlC.operator = Operator.Select;
		List<ColumnNode> colNode = ColumnNode.getColumn(ColumnNode.KEYCOLUMN, classOfT);
		if (!colNode.isEmpty()) {
			sqlC.colNode = new ArrayList<ColumnNode>();
			String columnName = null;
			for(ColumnNode cn :colNode){
				//查找id字段
				if(cn.getFieldName().contains("id")){
					columnName = cn.getFieldName();
					sqlC.colNode.add(cn);
					break;
				}
			}
			if(!sqlC.colNode.isEmpty()&&columnName!=null){
				//添加倒叙条件
				sqlC.orders = Orders.sortDesc(columnName);
				sqlC.pager = new Pager(1, 1);
			}

		}
		sqlC.tableNode = TableNode.getTable(classOfT);
		return sqlC;
	}

	/**
	 * from 子句
	 * 
	 * @param tableNode
	 * @return this
	 */
	public SqlC from(TableNode tableNode) {
		if (tableNode != null && operator == Operator.Select) {
			this.tableNode = tableNode;
		}
		return this;
	}

	/**
	 * where子句
	 * 
	 * @param conditions
	 * @return this
	 */
	public SqlC where(Conditions conditions) {
		if (conditions != null) {
			this.whereConditions = conditions;
		}
		return this;
	}
	/**
	 * order 子句
	 * @param orders
	 * @return
	 */
	public SqlC orderBy(Orders orders){
		if(orders!=null){
			this.orders = orders;
		}
		return this;
	}
	
	/**
	 * limit 子句
	 * 
	 * @param pager
	 * @return this
	 */
	public SqlC limit(Pager pager) {
		if (pager != null) {
			this.pager = pager;
		}
		return this;
	}

	/**
	 * 拼装Insert Sql
	 * 
	 * @return Sql String
	 */
	private String getInsertSql() {
		StringBuffer sbSql = new StringBuffer();
		Map<String,Field> colFieldMap = new HashMap<String,Field>();
		Table table = this.voClass.getAnnotation(Table.class);
		if(table!=null&&table.isAutoFill()!=true){
			for(Field field:voClass.getDeclaredFields()){
				DBField dbf = field.getAnnotation(DBField.class);
				if(dbf!=null){
					if(dbf.fieldName().length()>0){
						colFieldMap.put(dbf.fieldName(), field);
					}else{
						colFieldMap.put(field.getName(), field);
					}
				}
			}
		}else{
			for(Field field:voClass.getDeclaredFields()){
				DBField dbf = field.getAnnotation(DBField.class);
				if(dbf!=null){
					if(dbf.fieldName().length()>0){
						colFieldMap.put(dbf.fieldName(), field);
					}else{
						colFieldMap.put(field.getName(), field);
					}
				}else{
					colFieldMap.put(field.getName(), field);
				}
			}
		}
		sbSql.append(operator + " into ");
		sbSql.append(tableNode.getTableName());
		sbSql.append("( ");
		for (int i = 0; i < colNode.size() - 1; i++) {
			ColumnNode cn = colNode.get(i);
			sbSql.append(cn.getFieldName() + ", ");
		}
		sbSql.append(colNode.get(colNode.size() - 1).getFieldName());
		sbSql.append(" ) values ( ");
		for (int i = 0; i < colNode.size() - 1; i++) {
			ColumnNode cn = colNode.get(i);
			if(!colFieldMap.isEmpty()&&colFieldMap.containsKey(cn.getFieldName())){
				//替换Field
				Field field = colFieldMap.get(cn.getFieldName());
				sbSql.append(" @" + field.getName() + " , ");
			}else{
				//替换Column
				sbSql.append(" @" + cn.getFieldName() + " , ");
			}
		}
		ColumnNode cn = colNode.get(colNode.size()-1);
		if(!colFieldMap.isEmpty()&&colFieldMap.containsKey(cn.getFieldName())){
			//替换Field
			Field field = colFieldMap.get(cn.getFieldName());
			sbSql.append(" @" + field.getName() + " ) ");
		}else{
			//替换Column
			sbSql.append(" @" + cn.getFieldName() + " ) ");
		}
		return sbSql.toString();
	}

	/**
	 * 拼装Delete Sql
	 * 
	 * @return Sql String
	 */
	private String getDeleteSql() {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(operator + " from ");
		sbSql.append(tableNode.getTableName());
		if (whereConditions != null) {
			sbSql.append(" where ");
			sbSql.append(whereConditions.getSql());
		}
		return sbSql.toString();
	}

	/**
	 * 拼装 Update Sql
	 * 
	 * @return Sql String
	 */
	private String getUpdateSql() {
		StringBuffer sbSql = new StringBuffer();
		Map<String,Field> colFieldMap = new HashMap<String,Field>();
		Table table = this.voClass.getAnnotation(Table.class);
		if(table!=null&&table.isAutoFill()!=true){
			for(Field field:voClass.getDeclaredFields()){
				DBField dbf = field.getAnnotation(DBField.class);
				if(dbf!=null){
					if(dbf.fieldName().length()>0){
						colFieldMap.put(dbf.fieldName(), field);
					}else{
						colFieldMap.put(field.getName(), field);
					}
				}
			}
		}
		sbSql.append(operator + " ");
		sbSql.append(tableNode.getTableName() + " ");
		sbSql.append("set ");
		for (int i = 0; i < colNode.size() - 1; i++) {
			ColumnNode cn = colNode.get(i);
			if(!colFieldMap.isEmpty()&&colFieldMap.containsKey(cn.getFieldName())){
				//替换Field
				Field field = colFieldMap.get(cn.getFieldName());
				sbSql.append(cn.getFieldName() +" =@" + field.getName() + " , ");
			}else{
				//替换Column
				sbSql.append(cn.getFieldName() +" =@" + cn.getFieldName() + " , ");
			}
		}
		ColumnNode cn = colNode.get(colNode.size()-1);
		if(!colFieldMap.isEmpty()&&colFieldMap.containsKey(cn.getFieldName())){
			//替换Field
			Field field = colFieldMap.get(cn.getFieldName());
			sbSql.append(cn.getFieldName() +" =@" + field.getName() );
		}else{
			//替换Column
			sbSql.append(cn.getFieldName() +" =@" + cn.getFieldName() );
		}
		if (whereConditions != null) {
			sbSql.append(" where " + whereConditions.getSql());
		}
		return sbSql.toString();
	}

	/**
	 * 拼装Select Sql
	 * 
	 * @return Sql String
	 */
	private String getSelectSql() {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(operator + " ");
		for (int i = 0; i < colNode.size() - 1; i++) {
			ColumnNode cn = colNode.get(i);
			if (cn.getTableName() != null && cn.getTableName().length() > 0) {
				sbSql.append(cn.getTableName() + "." + cn.getFieldName() + ", ");
			} else {
				sbSql.append(cn.getFieldName() + ", ");
			}
		}
		ColumnNode cn = colNode.get(colNode.size() - 1);
		if (cn.getTableName() != null && cn.getTableName().length() > 0) {
			sbSql.append(cn.getTableName() + "." + cn.getFieldName());
		} else {
			sbSql.append(cn.getFieldName());
		}
		if (this.tableNode != null) {
			sbSql.append(" from " + tableNode.getSql());
		}
		if (this.whereConditions != null) {
			sbSql.append(" where " + whereConditions.getSql());
		}
		if(this.orders!=null){
			sbSql.append(orders.getSql());
		}
		if (this.pager != null) {
			sbSql.append(pager.getLimitSql());
		}
		return sbSql.toString();
	}

	@Override
	public String getSql() {
		switch (operator) {
		case Select:
			return getSelectSql();
		case Insert:
			return getInsertSql();
		case Update:
			return getUpdateSql();
		case Delete:
			return getDeleteSql();
		}
		return null;
	}

	@Override
	public String toString() {
		return this.getSql();
	}

	@Override
	public boolean isSelect() {
		if (this.operator == Operator.Select) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isUpdate() {
		if (this.operator == Operator.Update) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isInsert() {
		if (this.operator == Operator.Insert) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isDelete() {
		if (this.operator == Operator.Delete) {
			return true;
		}
		return false;
	}
}

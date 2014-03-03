package com.seahold.dao.sql.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.seahold.dao.annotation.DBField;
import com.seahold.dao.annotation.Table;
import com.seahold.dao.utils.StringUtils;
/**
 * Sql 条件类
 * @author Ezir
 *
 */
public class Conditions {

	/**
	 * 条件操作枚举
	 * @author Ezir
	 *
	 */
	private enum Operator {
		eq, neq, lt, gt, let, get, like,isNotNull,isNull,findInSet
	}

	/**
	 * 条件连接枚举
	 * @author Ezir
	 *
	 */
	private enum Connector {
		and, or
	}

	/**
	 * 条件因子1
	 */
	private String operator1;
	/**
	 * 操作
	 */
	private Operator operator;
	/**
	 * 条件因子2
	 */
	private String operator2;
	/**
	 * 连接list
	 */
	private List<Connector> connector = new ArrayList<Conditions.Connector>();
	/**
	 * 连接条件
	 */
	private List<Conditions> next = new ArrayList<Conditions>();

	private Conditions() {
	};

	/**
	 * 构造条件
	 * @param fieldName
	 * @return
	 */
	public static Conditions start(String fieldName) {
		Conditions con = new Conditions();
		con.operator1 = fieldName;
		return con;
	}

	/**
	 * 按照类构造条件
	 * @param classOfT
	 * @param fieldName
	 * @return
	 */
	public Conditions start(Class<?> classOfT, String fieldName) {
		Conditions con = new Conditions();
		Table table = classOfT.getAnnotation(Table.class);
		boolean checkFlag = false;
		if (table != null && !table.isAutoFill()) {
			for (Field field : classOfT.getDeclaredFields()) {
				DBField dbf = (DBField) field.getAnnotation(DBField.class);
				if (dbf != null && dbf.fieldName().length() > 0) {
					if (dbf.fieldName().equals(fieldName)) {
						checkFlag = true;
						break;
					}
				} else {
					if (field.getName().equals(fieldName)) {
						checkFlag = true;
						break;
					}
				}
			}
		} else {
			for (Field field : classOfT.getDeclaredFields()) {
				if (field.getName().equals(fieldName)) {
					checkFlag = true;
					break;
				}
			}
		}
		if (checkFlag) {
			if (table != null && table.tableName().length() > 0) {
				this.operator1 = table.tableName() + "." + fieldName;
			} else {
				this.operator1 = StringUtils.lowerFirst(classOfT.getName()) + "." + fieldName;
			}
		} else {
			this.operator1 = null;
		}
		return con;
	}

	/**
	 * 私有操作器
	 * @param opera
	 * @param fieldName
	 * @return
	 */
	private Conditions opera(Operator opera, Object value) {
		this.operator = opera;
		if(value!=null){
			this.operator2 = value.toString();
		}else{
			this.operator2 = null;
		}
		return this;
	}

	private Conditions opera(Operator opera, Class<?> classOfT, String fieldName) {
		Table table = classOfT.getAnnotation(Table.class);
		boolean checkFlag = false;
		if (table != null && !table.isAutoFill()) {
			for (Field field : classOfT.getDeclaredFields()) {
				DBField dbf = (DBField) field.getAnnotation(DBField.class);
				if (dbf != null && dbf.fieldName().length() > 0) {
					if (dbf.fieldName().equals(fieldName)) {
						checkFlag = true;
						break;
					}
				} else {
					if (field.getName().equals(fieldName)) {
						checkFlag = true;
						break;
					}
				}
			}
		} else {
			for (Field field : classOfT.getDeclaredFields()) {
				if (field.getName().equals(fieldName)) {
					checkFlag = true;
					break;
				}
			}
		}
		if (checkFlag) {
			if (table != null && table.tableName().length() > 0) {
				this.operator2 = table.tableName() + "." + fieldName;
			} else {
				this.operator2 = StringUtils.lowerFirst(classOfT.getName()) + "." + fieldName;
			}
		} else {
			this.operator2 = null;
		}
		this.operator = opera;
		return this;
	}

	//操作列表
	//=
	public Conditions eq(String value) {
		if(value.startsWith("@")){
			return opera(Operator.eq, value);
		}else{
			return opera(Operator.eq, "\""+value+"\"");
		}
	}
	
	public Conditions eq(Object value) {
		return opera(Operator.eq, value);
	}

	public Conditions eq(Class<?> classOfT, String fieldName) {
		return opera(Operator.eq, classOfT, fieldName);
	}
	
	
	//<>
	public Conditions neq(String value) {
		if(value.startsWith("@")){
			return opera(Operator.neq, value);
		}else{
			return opera(Operator.neq, "\""+value+"\"");
		}
	}
	
	public Conditions neq(Object value) {
		return opera(Operator.neq, value);
	}

	public Conditions neq(Class<?> classOfT, String fieldName) {
		return opera(Operator.neq, classOfT, fieldName);
	}
	
	
	//<
	public Conditions lt(String value) {
		if(value.startsWith("@")){
			return opera(Operator.lt, value);
		}else{
			return opera(Operator.lt, "\""+value+"\"");
		}
	}
	
	public Conditions lt(Object value) {
		return opera(Operator.lt, value);
	}

	public Conditions lt(Class<?> classOfT, String fieldName) {
		return opera(Operator.lt, classOfT, fieldName);
	}

	//<=
	public Conditions lte(String value) {
		if(value.startsWith("@")){
			return opera(Operator.let, value);
		}else{
			return opera(Operator.let, "\""+value+"\"");
		}
	}
	
	public Conditions lte(Object value) {
		return opera(Operator.let, value);
	}

	public Conditions lte(Class<?> classOfT, String fieldName) {
		return opera(Operator.let, classOfT, fieldName);
	}

	//>
	public Conditions gt(String value) {
		if(value.startsWith("@")){
			return opera(Operator.gt, value);
		}else{
			return opera(Operator.gt, "\""+value+"\"");
		}
	}
	
	public Conditions gt(Object value) {
		return opera(Operator.gt, value);
	}

	public Conditions gt(Class<?> classOfT, String fieldName) {
		return opera(Operator.gt, classOfT, fieldName);
	}

	//>=
	public Conditions gte(String value) {
		if(value.startsWith("@")){
			return opera(Operator.get, value);
		}else{
			return opera(Operator.get, "\""+value+"\"");
		}
	}
	
	public Conditions gte(Object value) {
		return opera(Operator.get, value);
	}

	public Conditions gte(Class<?> classOfT, String fieldName) {
		return opera(Operator.get, classOfT, fieldName);
	}

	//Not Null
	public Conditions isNotNull(){
		return opera(Operator.isNotNull, null);
	}
	
	// Null
	public Conditions isNull(){
		return opera(Operator.isNull, null);
	}
	
	//find_in_set
	public Conditions findInSet(String value){
		return opera(Operator.findInSet, value);
	}
	
	
	//like
	public Conditions like(String likeWord) {
		if(likeWord.startsWith("@")){
			return opera(Operator.like, likeWord);
		}else{
			return opera(Operator.like, "\""+likeWord+"\"");
		}
	}

	//条件操作
	//and
	public Conditions and(Conditions next) {
		if (next != null) {
			this.connector.add(Connector.and);
			this.next.add(next);
		}
		return this;
	}

	//or
	public Conditions or(Conditions next) {
		if (next != null) {
			this.connector.add(Connector.or);
			this.next.add(next);
		}
		return this;
	}

	/**
	 * Sql生成
	 * @return
	 */
	public String getSql() {
		StringBuffer sbSql = new StringBuffer();
		switch (operator) {
		case eq:
			sbSql.append(this.operator1 + " ");
			sbSql.append("= ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case neq:
			sbSql.append(this.operator1 + " ");
			sbSql.append("<> ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case lt:
			sbSql.append(this.operator1 + " ");
			sbSql.append("< ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case let:
			sbSql.append(this.operator1 + " ");
			sbSql.append("<= ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case gt:
			sbSql.append(this.operator1 + " ");
			sbSql.append("> ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case get:
			sbSql.append(this.operator1 + " ");
			sbSql.append(">= ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case like:
			sbSql.append(this.operator1 + " ");
			sbSql.append("like ");
			if(operator2!=null){
				sbSql.append(operator2 + " ");
			}else{
				sbSql.append(" null ");
			}
			break;
		case isNotNull:
			sbSql.append(this.operator1 + " ");
			sbSql.append(" is not null ");
			break;
		case isNull:
			sbSql.append(this.operator1 + " ");
			sbSql.append(" is null ");
			break;
		case findInSet:
			sbSql.append(" FIND_IN_SET( ");
			if(this.operator2!=null){
				sbSql.append(this.operator2);
			}else{
				sbSql.append(" null ");
			}
			sbSql.append(" , "+this.operator1+" ) ");
			break;
		}
		for (int i = 0; i < this.connector.size(); i++) {
			Connector connector = this.connector.get(i);
			switch (connector) {
			case and:
				sbSql.append(" and ( ");
				break;
			case or:
				sbSql.append(" or ( ");
				break;
			}
			Conditions conditions = this.next.get(i);
			sbSql.append(conditions.getSql());
			sbSql.append(" ) ");
		}
		return sbSql.toString();
	}

	@Override
	public String toString() {
		return getSql();
	}
}

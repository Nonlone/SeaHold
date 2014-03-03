package com.seahold.dao.sql.impl;

import com.seahold.dao.annotation.Table;
import com.seahold.dao.utils.StringUtils;
/**
 * 表名构造器
 * @author Ezir
 *
 */
public class TableNode {

	/**
	 * 表连接枚举，笛卡尔集用CrossJoin
	 * @author Ezir
	 *
	 */
	private enum JoinOperate {
		LeftJoin, RightJoin, InnerJoin, CrossJoin
	}
	/**
	 * 表名
	 */
	private String tableName;
	/**
	 * 连接符
	 */
	private JoinOperate joinOperate;
	/**
	 * 连接表
	 */
	private TableNode tableNode;
	/**
	 * 条件
	 */
	private Conditions onConditions;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public JoinOperate getJoinOperate() {
		return joinOperate;
	}

	public void setJoinOperate(JoinOperate joinOperate) {
		this.joinOperate = joinOperate;
	}

	public TableNode getTableNode() {
		return tableNode;
	}

	public void setTableNode(TableNode tableNode) {
		this.tableNode = tableNode;
	}

	public Conditions getOnConditions() {
		return onConditions;
	}

	public void setOnConditions(Conditions onConditions) {
		this.onConditions = onConditions;
	}

	private TableNode() {
	};
	
	/**
	 * 按照类型构造表
	 * @param classOfT
	 * @return
	 */
	public static TableNode getTable(Class<?> classOfT) {
		TableNode tn = new TableNode();
		Table table = classOfT.getAnnotation(Table.class);
		if (table != null) {
			String tableName = table.tableName();
			if (tableName.length() > 0) {
				tn.tableName = tableName;
			} else {
				tn.tableName = classOfT.getName();
			}
		} else {
			tn.tableName = classOfT.getName();
		}
		tn.tableName = StringUtils.lowerFirst(tn.tableName);
		return tn;
	}

	public TableNode leftJoin(TableNode tableNode) {
		if (tableNode != null) {
			this.joinOperate = JoinOperate.LeftJoin;
			this.tableNode = tableNode;
		}
		return this;
	}

	public TableNode rightJoin(TableNode tableNode) {
		if (tableNode != null) {
			this.joinOperate = JoinOperate.RightJoin;
			this.tableNode = tableNode;
		}
		return this;
	}

	public TableNode innerJoin(TableNode tableNode) {
		if (tableNode != null) {
			this.joinOperate = JoinOperate.InnerJoin;
			this.tableNode = tableNode;
		}
		return this;
	}

	public TableNode crossJoin(TableNode tableNode) {
		if (tableNode != null) {
			this.joinOperate = JoinOperate.CrossJoin;
			this.tableNode = tableNode;
		}
		return this;
	}

	public TableNode on(Conditions conditions) {
		if (conditions != null) {
			this.onConditions = conditions;
		}
		return this;
	}

	//构造sql
	public String getSql() {
		StringBuffer sbSql = new StringBuffer();
		if (tableName != null && tableName.length() > 0) {
			sbSql.append(" " + tableName);
			if (tableNode != null) {
				switch (joinOperate) {
				case LeftJoin:
					sbSql.append(" left join ");
					break;
				case RightJoin:
					sbSql.append(" right join ");
					break;
				case CrossJoin:
					sbSql.append(" cross join ");
					break;
				case InnerJoin:
					sbSql.append(" inner join ");
					break;
				}
				sbSql.append(tableNode.getSql());
				if (onConditions != null) {
					sbSql.append(" on " + onConditions.getSql());
				}
			}
		}
		return sbSql.toString();
	}

	@Override
	public String toString() {
		return getSql();
	}
}

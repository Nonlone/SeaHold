package com.seahold.sql.impl;

public class Pager {

	private int curPage;
	private int pageSize;
	private int totalRecords;
	private int totalPages;

	public Pager(int curPage, int pageSize) {
		this.curPage = curPage;
		this.pageSize = pageSize;
	}

	public static Pager getPager(int curPage, int pageSize) {
		Pager pager = new Pager(curPage, pageSize);
		return pager;
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public String getLimitSql() {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" limit ");
		if (curPage < 1) {
			curPage = 1;
		}
		sbSql.append((curPage - 1) * pageSize);
		sbSql.append("," + pageSize + " ");
		return sbSql.toString();
	}

	@Override
	public String toString() {
		return getLimitSql();
	}
}

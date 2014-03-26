package com.magdou.seahold.exception;

public class DataSourceNullException extends Exception{
	
	private static final String exptionMsg = "DataSource is null";
	
	public DataSourceNullException(){
		super(exptionMsg);
	}
}

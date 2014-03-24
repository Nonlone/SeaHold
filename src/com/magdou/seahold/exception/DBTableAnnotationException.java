package com.magdou.seahold.exception;

public class DBTableAnnotationException extends Exception{
	
	private static final String exptionMsg = "DBTable Annotation is null";
	
	public DBTableAnnotationException(){
		super(exptionMsg);
	}
}

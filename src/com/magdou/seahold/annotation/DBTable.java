package com.magdou.seahold.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 表 注解，只能注解在类上
 * @author Ezir
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DBTable {
	
	public static enum TableType  {
		STRICT,//严格模式，全部根据注解注入
		NORMAL,//正常模式，有注解根据注解注入，没有注解按照变量名注入，按照变量名根据Setter方法注入
		LOOSE//松散模式，跟住Setter注入
	}
	
	/**
	 * 表名 ，可以用${}方式进行动态表名，要实现DynamicTable接口
	 * @return
	 */
	public String tableName() default "";
	/**
	 * 是否为自动填充标志，等于不注解
	 * @return
	 */
	public TableType tableType() default TableType.NORMAL;
	/**
	 * 自己实现的Vo包装器，实现VoWrapper接口即可
	 * @return
	 */
	public String voWrapper() default "com.seahold.dao.wrapper.impl.DefaultVoWrapper";
	/**
	 * 自己实现的Vo填充器，实现VoFiller接口即可
	 * @return
	 */
	public String voFiller() default "com.seahold.dao.filler.impl.DefaultVoFiller";
}

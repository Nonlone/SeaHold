package com.seahold.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 数据表 列注解，只能注解在属性上
 * @author Ezir
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBField {
	/**
	 * 对应列，空为默认属性名
	 * @return
	 */
	public String fieldName() default "";
	/**
	 * 是否自动生成，true时，插入和更新不修改对应列数据
	 * @return
	 */
	public boolean isAutoGenerate() default false;
	/**
	 * 是否为key，更新和删除是会自动产生条件
	 * @return
	 */
	public boolean isKey() default false;
	/**
	 * 多个属性值对应一个列值，列为json字符串，可以从提取对应属性
	 * @return
	 */
	public boolean isExt() default false;
	/**
	 * 多个列值对应一个属性值，属性为json，或者String，其他类型需要多值转换器
	 * @return
	 */
	public String[] multiColnum() default {};
	/**
	 * 多列值转换器，写上类名（含包路径），会按照类型急性自动转换
	 * @return
	 */
	public String multiConventer() default "";
}

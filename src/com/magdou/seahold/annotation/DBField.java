package com.magdou.seahold.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 列注解
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

package com.magdou.seahold.wrapper;

import org.json.JSONObject;
/**
 * 多列转换器接口，实现多个数据列转换成一个vo属性
 * @author Ezir
 *
 * @param <T>
 */
public interface MultiFieldConventer<T> {
	public T multiFieldConvent(JSONObject json);

}

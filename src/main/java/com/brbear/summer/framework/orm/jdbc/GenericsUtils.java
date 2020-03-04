package com.brbear.summer.framework.orm.jdbc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {

	public static Class getSuperClassGenericType(Class clazz, int index) {
		Type genericSuperclass = clazz.getGenericSuperclass();
		if (genericSuperclass == null) {
			return null;
		}
		if ((genericSuperclass instanceof ParameterizedType) == false) {
			return null;
		}
		ParameterizedType paramterizedType = (ParameterizedType) genericSuperclass;
		Type[] actualTypeArguments = paramterizedType.getActualTypeArguments();
		if (actualTypeArguments.length < (index - 1) || actualTypeArguments.length == 0) {
			return null;
		}
		return (Class) actualTypeArguments[0];
	}
}

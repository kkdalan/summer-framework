package com.brbear.summer.framework.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Id;

public class PropertyMapping{
	final boolean insertable;
	final boolean updatable;
	final String columnName;
	final boolean id;
	public final Method getter;
	public final Method setter;
	final Class enumClass;
	final String fieldName;
	
	public PropertyMapping(Method getter,Method setter, Field field) {
		this.getter = getter;
		this.setter = setter;
		this.enumClass = getter.getReturnType().isEnum() ? getter.getReturnType() : null;
		Column column = field.getAnnotation(Column.class);
		this.insertable = column == null || column.insertable();
		this.updatable = column == null || column.updatable();
		this.columnName = getColumnName(getter, column);
		this.id = field.isAnnotationPresent(Id.class);
		this.fieldName = field.getName();
	}
	
	private String getColumnName(Method getter, Column column) {
		boolean emptyColumn = column == null || "".equals(column.name());
		return emptyColumn ? ClassMappings.getGetterName(getter) : column.name();
	}
	
	@SuppressWarnings("unchecked")
	Object get(Object target) throws Exception{
		Object value = getter.invoke(target);
		return enumClass == null ? value : Enum.valueOf(enumClass, (String)value);
	}
	
	@SuppressWarnings("unchecked")
	void set(Object target, Object value) throws Exception{
		if(enumClass != null && value != null) {
			value = Enum.valueOf(enumClass, (String)value);
		}
		try {
			if(value != null) {
				setter.invoke(target, setter.getParameterTypes()[0].cast(value));
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.err.println(fieldName + "--" + value);
		}
	}
}

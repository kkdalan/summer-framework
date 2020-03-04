package com.brbear.summer.framework.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.jdbc.core.RowMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityOperation<T> {

	public Class<T> entityClass = null;
	public final Map<String, PropertyMapping> mappings;
	public final RowMapper<T> rowMapper;
	
	public final String tableName;
	public String allColumn = "*";
	public Field pkField;
	
	public EntityOperation(Class<T> clazz, String pk)throws Exception {
		if(!clazz.isAnnotationPresent(Entity.class)) {
			throw new Exception("ORM failed. No Entity annotation found in " + clazz.getName());
		}
		this.entityClass = clazz;
		Table table = entityClass.getAnnotation(Table.class);
		this.tableName = (table != null) ? table.name() : entityClass.getSimpleName();

		Map<String,Method> getters = ClassMappings.findPublicGetters(entityClass);
		Map<String,Method> setters = ClassMappings.findPublicSetters(entityClass);
		Field[] fields = ClassMappings.findFields(entityClass);
		fillPkFieldAndAllColumn(pk, fields);
		
		this.mappings = getPropertyMappings(getters, setters, fields);
		this.allColumn = this.mappings.keySet().toString().replace("[", "").replace("]", "").replace(" ", "");
		this.rowMapper = createRowMapper();
	}
	
	protected Map<String,PropertyMapping> getPropertyMappings(Map<String,Method> getters,Map<String,Method> setters, Field[] fields){
		Map<String,PropertyMapping> mappings = new HashMap<String,PropertyMapping>();
		String name;
		for(Field field : fields) {
			if(field.isAnnotationPresent(Transient.class)) { continue; }
			name = field.getName();
			if(name.startsWith("is")) {
				name = name.substring(2);
			}
			name = Character.toLowerCase(name.charAt(0))+name.substring(1);
			Method setter = setters.get(name);
			Method getter = getters.get(name);
			if(setter == null || getter == null) { continue; }
			
			Column column = field.getAnnotation(Column.class);
			if(column == null) {
				mappings.put(field.getName(), new PropertyMapping(getter, setter, field));
			}else {
				mappings.put(column.name(), new PropertyMapping(getter, setter, field));
			}
		}
		return mappings;
	}
	
	private void fillPkFieldAndAllColumn(String pk, Field[] fields) {
		boolean pkEmpty = ( pk == null || pk.isEmpty());
		try {
			if (!pkEmpty) {
				pkField = entityClass.getDeclaredField(pk);
				pkField.setAccessible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (pkEmpty) {
				Id id = f.getAnnotation(Id.class);
				if(id != null) {
					pkField = f;
					break;
				}
			}
		}
	}
	
	protected RowMapper<T> createRowMapper(){
		return new RowMapper<T>() {
			public T mapRow(ResultSet rs, int rowNum) throws SQLException {
				try {
					T t = entityClass.newInstance();
					ResultSetMetaData meta = rs.getMetaData();
					int columnCount = meta.getColumnCount();
					for(int i = 1; i <= columnCount; i++) {
						Object value = rs.getObject(i);
						String columnName = meta.getColumnName(i);
						fillBeanFieldValue(t, columnName, value);
					}
					return t;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	protected void fillBeanFieldValue(T t, String columnName, Object value) {
		if(value != null) {
			PropertyMapping pm = mappings.get(columnName);
			if(pm != null) {
				try {
					pm.set(t, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public T parse(ResultSet rs) {
		T t = null;
		if(rs == null) { return null; }

		Object value = null;
		try {
			t = (T)entityClass.newInstance();
			for(String columnName : mappings.keySet()) {
				try {
					value = rs.getObject(columnName);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				fillBeanFieldValue(t, columnName, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public Map<String,Object> parse(T t) {
		Map<String,Object> _map = new TreeMap<String,Object>();
		try {
			for(String columnName : mappings.keySet()) {
				Object value = mappings.get(columnName).getter.invoke(t);
				if(value == null) { continue; }
				_map.put(columnName, value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return _map;
	}
	
	public void println(T t) {
		try {
			for(String columnName : mappings.keySet()) {
				Object value = mappings.get(columnName).getter.invoke(t);
				if(value == null) { continue; }
				System.out.println(columnName + " = " + value);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}

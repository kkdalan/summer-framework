package com.brbear.summer.framework.orm.jdbc.datasource;

import com.brbear.summer.framework.aop.aspect.BRJoinPoint;

public class DynamicDataSourceEntry {

	public static final String DEFAULT_SOURCE = null;

	private static final ThreadLocal<String> local = new ThreadLocal<String>();

	public void clear() {
		local.remove();
	}

	public String get() {
		return local.get();
	}

	public void restore(BRJoinPoint joinPoint) {
		local.set(DEFAULT_SOURCE);
	}

	public void restore() {
		local.set(DEFAULT_SOURCE);
	}

	public void set(String source) {
		local.set(source);
	}

	public void set(int year) {
		local.set("DB_" + year);
	}

}

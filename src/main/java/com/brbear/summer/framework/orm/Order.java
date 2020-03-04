package com.brbear.summer.framework.orm;

public class Order {
	
	private String porpertyName;
	private boolean ascending;
	
	public String toString() {
		return porpertyName + ' ' + (ascending? "asc" : "desc");
	}

	protected Order(String porpertyName, boolean ascending) {
		this.porpertyName = porpertyName;
		this.ascending = ascending;
	}

	public static Order asc(String porpertyName) {
		return new Order(porpertyName, true);
	}
	
	public static Order desc(String porpertyName) {
		return new Order(porpertyName, false);
	}
	
}

package com.brbear.summer.framework.beans;

public class BRBeanWrapper {
	
	private Object wrapperedInstance;
	private Class<?> wrapperedClass;

	public BRBeanWrapper(Object wrapperedInstance) {
		this.wrapperedInstance = wrapperedInstance;
	}

	public Object getWrapperedInstance() {
		return this.wrapperedInstance;
	}

	public Class<?> getWrapperedClass() {
		return this.wrapperedInstance.getClass();
	}

}

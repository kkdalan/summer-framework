package com.brbear.summer.framework.core;

public interface BRBeanFactory {

	Object getBean(String beanName) throws Exception;

	public Object getBean(Class<?> beanClass) throws Exception;

}

package com.brbear.summer.framework.beans.config;

public class BRBeanPostProcessor {

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception{
		return bean;
	}
}

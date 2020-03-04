package com.brbear.summer.framework.context.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.brbear.summer.framework.beans.config.BRBeanDefinition;

public class BRDefaultListableBeanFactory extends BRAbstractApplicationContext {

	protected final Map<String, BRBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BRBeanDefinition>();

	public void refresh() throws Exception {
	}

}

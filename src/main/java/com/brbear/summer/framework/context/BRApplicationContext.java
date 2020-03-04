package com.brbear.summer.framework.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.brbear.summer.framework.annotation.BRAutowired;
import com.brbear.summer.framework.annotation.BRController;
import com.brbear.summer.framework.annotation.BRService;
import com.brbear.summer.framework.aop.BRAopConfig;
import com.brbear.summer.framework.aop.BRAopProxy;
import com.brbear.summer.framework.aop.BRCglibAopProxy;
import com.brbear.summer.framework.aop.BRJdkDynamicAopProxy;
import com.brbear.summer.framework.aop.support.BRAdvisedSupport;
import com.brbear.summer.framework.beans.BRBeanWrapper;
import com.brbear.summer.framework.beans.config.BRBeanDefinition;
import com.brbear.summer.framework.beans.config.BRBeanPostProcessor;
import com.brbear.summer.framework.context.support.BRBeanDefinitionReader;
import com.brbear.summer.framework.context.support.BRDefaultListableBeanFactory;
import com.brbear.summer.framework.core.BRBeanFactory;

public class BRApplicationContext extends BRDefaultListableBeanFactory implements BRBeanFactory {

	private String[] configLocations;
	private BRBeanDefinitionReader reader;

	private Map<String, Object> factoryBeanObjectCache = new HashMap<String, Object>();
	private Map<String, BRBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, BRBeanWrapper>();

	public BRApplicationContext(String ... configLocations) {
		this.configLocations = configLocations;
		try {
			refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void refresh() throws Exception {
		
		reader = new BRBeanDefinitionReader(this.configLocations);
		
		List<BRBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
		
		doRegisterBeanDefinition(beanDefinitions);
		
		doAutowired();
	}
	
	private void doAutowired() {
		for (Map.Entry<String, BRBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
			String beanName = beanDefinitionEntry.getKey();
			if(!beanDefinitionEntry.getValue().isLazyInit()) {
				try {
					getBean(beanName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void doRegisterBeanDefinition(List<BRBeanDefinition> beanDefinitions) throws Exception{
		for(BRBeanDefinition beanDefinition : beanDefinitions) {
			if(super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
				throw new Exception("The '" + beanDefinition.getFactoryBeanName() + "' already exists! ");
			}
			super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
			
		}
	}
	
	public Object getBean(Class<?> beanClass) throws Exception {
		return getBean(beanClass.getName());
	}
	
	public Object getBean(String beanName) throws Exception {
		BRBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
		
		try {
			BRBeanPostProcessor beanPostProcessor = new BRBeanPostProcessor();
			
			Object instance = instantiateBean(beanDefinition);
			if(null == instance) { return null; }
			 
			beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
			
			BRBeanWrapper beanWrapper = new BRBeanWrapper(instance);
			this.factoryBeanInstanceCache.put(beanName, beanWrapper);
			
			beanPostProcessor.postProcessAfterInitialization(instance, beanName);
			
			populateBean(beanName, instance);
			return this.factoryBeanInstanceCache.get(beanName).getWrapperedInstance();

		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	private void populateBean(String beanName, Object instance) {
		Class clazz = instance.getClass();
		
		boolean hasControllerAnnotation = clazz.isAnnotationPresent(BRController.class);
		boolean hasServiceAnnotation = clazz.isAnnotationPresent(BRService.class);
		if (!(hasControllerAnnotation || hasServiceAnnotation)) {
			return;
		}
		
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			boolean hasAutowiredAnnotation = field.isAnnotationPresent(BRAutowired.class);
			if(!hasAutowiredAnnotation) { continue; }
			
			BRAutowired autowired = field.getAnnotation(BRAutowired.class);
			String autowiredBeanName = autowired.value().trim();
			
			if("".equals(autowiredBeanName)) {
				autowiredBeanName = field.getType().getName();
			}
			
			if(!this.factoryBeanInstanceCache.containsKey(autowiredBeanName)) {
				BRBeanDefinition autowiredBeanDefinition = super.beanDefinitionMap.get(autowiredBeanName);
				Object autowiredInstance = instantiateBean(autowiredBeanDefinition);
				if(null == autowiredInstance) { return; }
				BRBeanWrapper autowiredBeanWrapper = new BRBeanWrapper(autowiredInstance);
				this.factoryBeanInstanceCache.put(autowiredBeanName, autowiredBeanWrapper);
			}
			
			field.setAccessible(true);
			try {
				field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrapperedInstance());
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private Object instantiateBean(BRBeanDefinition beanDefinition) {
		Object instance = null;
		String className = beanDefinition.getBeanClassName();
		try {
			if(this.factoryBeanObjectCache.containsKey(className)) {
				instance = this.factoryBeanObjectCache.get(className);
			}else {
				Class<?> clazz = Class.forName(className);
				instance = clazz.newInstance();
				
				//AOP
				BRAdvisedSupport config = instantiateAopConfig(beanDefinition);
				config.setTargetClass(clazz);	
				config.setTarget(instance);
				if(config.pointCutMatch()) {
					instance = createProxy(config).getProxy();
				}
				this.factoryBeanObjectCache.put(className, instance);
				this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
			}
			return instance;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private BRAdvisedSupport instantiateAopConfig(BRBeanDefinition beanDefinition) throws Exception{
		BRAopConfig config = new BRAopConfig();
		config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
		config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
		config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
		config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
		config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
		config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
		return new BRAdvisedSupport(config);
	}
	
	private BRAopProxy createProxy(BRAdvisedSupport config) {
		Class targetClass = config.getTargetClass();
		if(targetClass.getInterfaces().length > 0 ) {
			return new BRJdkDynamicAopProxy(config);
		}
		return new BRCglibAopProxy(config);
	}
	
	
	public String[] getBeanDefinitionNames() {
		int beanDefinitionCount = this.beanDefinitionMap.size();
		return this.beanDefinitionMap.keySet().toArray(new String[beanDefinitionCount]);
	}
	
	public int getBeanDefinitionNameCount() {
		return this.beanDefinitionMap.size();
	}
	
	public Properties getConfig() {
		return this.reader.getConfig();
	}

}

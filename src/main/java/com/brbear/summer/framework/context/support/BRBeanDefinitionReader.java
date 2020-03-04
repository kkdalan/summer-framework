package com.brbear.summer.framework.context.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.brbear.summer.framework.beans.config.BRBeanDefinition;

public class BRBeanDefinitionReader {

	private static final String SCAN_PACKAGE = "scanPackage";

	private List<String> registerBeanClasses = new ArrayList<String>();
	private Properties config = new Properties();

	public BRBeanDefinitionReader(String... locations) {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
		try {
			config.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		doScanner(config.getProperty(SCAN_PACKAGE));
		
	}
	
	private void doScanner(String scanPackage) {
		URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
		File classPath = new File(url.getFile());
		for(File file : classPath.listFiles()) {
			if(file.isDirectory()) {
				doScanner(scanPackage + "." + file.getName());
			}else {
				if(!file.getName().endsWith(".class")) { continue; }
				String className =  scanPackage + "." + file.getName().replace(".class", "");
				registerBeanClasses.add(className);
			}
		}
	}

	public Properties getConfig() {
		return config;
	}
	
	public List<BRBeanDefinition> loadBeanDefinitions(){
		
		List<BRBeanDefinition> result = new ArrayList<BRBeanDefinition>();
		try {
			for (String className : registerBeanClasses) {
				Class<?> beanClass = Class.forName(className);
				if(beanClass.isInterface()) {continue;}
				result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
				
				Class<?>[] interfaces = beanClass.getInterfaces();
				for(Class<?> intfClass : interfaces) {
					result.add(doCreateBeanDefinition(intfClass.getName(), beanClass.getName()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private BRBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
		BRBeanDefinition beanDefinition = new BRBeanDefinition();
		beanDefinition.setBeanClassName(beanClassName);
		beanDefinition.setFactoryBeanName(factoryBeanName);
		return beanDefinition;
	}
	
	private String toLowerFirstCase(String simpleName) {
		char[] chars = simpleName.toCharArray();
		chars[0] += 32;
		return String.copyValueOf(chars);
	}

}

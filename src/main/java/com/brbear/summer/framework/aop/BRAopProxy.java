package com.brbear.summer.framework.aop;

public interface BRAopProxy {

	Object getProxy();
	
	Object getProxy(ClassLoader classLoader);
	
}

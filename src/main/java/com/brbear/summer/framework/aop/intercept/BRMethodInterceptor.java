package com.brbear.summer.framework.aop.intercept;

public interface BRMethodInterceptor {
	
	Object invoke(BRMethodInvocation mi) throws Throwable;
	
}

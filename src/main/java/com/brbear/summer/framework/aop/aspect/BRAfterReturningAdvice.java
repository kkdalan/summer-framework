package com.brbear.summer.framework.aop.aspect;

import java.lang.reflect.Method;

import com.brbear.summer.framework.aop.intercept.BRMethodInterceptor;
import com.brbear.summer.framework.aop.intercept.BRMethodInvocation;

public class BRAfterReturningAdvice extends BRAbstractAspectAdvice implements BRAdvice, BRMethodInterceptor{

	private BRJoinPoint joinPoint;
	
	public BRAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}

	public Object invoke(BRMethodInvocation mi) throws Throwable {
		Object retValue = mi.proceed();
		this.joinPoint = mi;
		this.afterReturning(retValue, mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}
	
	public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
		invokeAdviceMethod(this.joinPoint, returnValue, null);
	}

}

package com.brbear.summer.framework.aop.aspect;

import java.lang.reflect.Method;

import com.brbear.summer.framework.aop.intercept.BRMethodInterceptor;
import com.brbear.summer.framework.aop.intercept.BRMethodInvocation;

public class BRMethodBeforeAdvice extends BRAbstractAspectAdvice implements BRAdvice, BRMethodInterceptor{

	private BRJoinPoint joinPoint;
	
	public BRMethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}

	public void before(Method method, Object[] args, Object target) throws Throwable {
		invokeAdviceMethod(this.joinPoint, null, null);
	}
	
	public Object invoke(BRMethodInvocation mi) throws Throwable {
		this.joinPoint = mi;
		this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
		return mi.proceed();
	}

}

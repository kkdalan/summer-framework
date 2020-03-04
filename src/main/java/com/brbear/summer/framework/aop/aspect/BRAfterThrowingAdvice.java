package com.brbear.summer.framework.aop.aspect;

import java.lang.reflect.Method;

import com.brbear.summer.framework.aop.intercept.BRMethodInterceptor;
import com.brbear.summer.framework.aop.intercept.BRMethodInvocation;

public class BRAfterThrowingAdvice extends BRAbstractAspectAdvice implements BRAdvice, BRMethodInterceptor{

	private String throwingName;
	private BRMethodInvocation mi;
	
	public BRAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
		super(aspectMethod, aspectTarget);
	}
	
	public void setThrowingName(String name) {
		this.throwingName = name;
	}

	public Object invoke(BRMethodInvocation mi) throws Throwable {
		try {
			return mi.proceed();
		}catch(Throwable ex) {
			invokeAdviceMethod(mi, null, ex.getCause());
			throw ex;
		}
	}

}

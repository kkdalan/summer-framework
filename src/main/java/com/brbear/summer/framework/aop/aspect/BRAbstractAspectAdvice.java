package com.brbear.summer.framework.aop.aspect;

import java.lang.reflect.Method;

public abstract class BRAbstractAspectAdvice implements BRAdvice{

	private Method aspectMethod;
	private Object aspectTarget;
	
	public BRAbstractAspectAdvice(Method aspectMethod, Object aspectTarget) {
		this.aspectMethod = aspectMethod;
		this.aspectTarget = aspectTarget;
	}
	
	protected Object invokeAdviceMethod(BRJoinPoint joinPoint, Object returnValue, Throwable ex) throws Throwable{
		Class<?>[] paramTypes = this.aspectMethod.getParameterTypes();
		if(null == paramTypes || paramTypes.length == 0) {
			return this.aspectMethod.invoke(aspectTarget);
		}else {
			Object[] args = new Object[paramTypes.length];
			for(int i = 0; i < paramTypes.length ; i++) {
				if(paramTypes[i] == BRJoinPoint.class) {
					args[i] = joinPoint;
				}else if(paramTypes[i] == Throwable.class) {
					args[i] = ex;
				}else if(paramTypes[i] == Object.class) {
					args[i] = returnValue;
				}
			}
			return this.aspectMethod.invoke(aspectTarget,args);
		}
	}
	
	
}

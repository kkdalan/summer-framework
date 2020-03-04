package com.brbear.summer.framework.aop.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brbear.summer.framework.aop.aspect.BRJoinPoint;

public class BRMethodInvocation implements BRJoinPoint{

	private Object proxy;
	private Method method;
	private Object target;
	private Class<?> targetClass;
	private Object[] arguments;
	private List<Object> interceptorsAndDynamicMethodMatchers;
	
	private Map<String,Object> userAttributers;
	
	private int currentInterceptorIndex = -1;
	
	public BRMethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments,
			List<Object> interceptorsAndDynamicMethodMatchers) {
		this.proxy = proxy;
		this.method = method;
		this.target = target;
		this.targetClass = targetClass;
		this.arguments = arguments;
		this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
	}

	public Object proceed() throws Throwable{
		if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1) {
			return this.method.invoke(this.target, this.arguments);
		}
		Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
		if(interceptorOrInterceptionAdvice instanceof BRMethodInterceptor) {
			BRMethodInterceptor mi = (BRMethodInterceptor) interceptorOrInterceptionAdvice;
			return mi.invoke(this);
		}else {
			return proceed();
		}
	}
	
	public Method getMethod() {
		return this.method;
	}

	public Object[] getArguments() {
		return this.arguments;
	}

	public Object getThis() {
		return this.target;
	}

	public void setUserAttribute(String key, Object value) {
		if(value != null) {
			if(this.userAttributers == null) {
				this.userAttributers = new HashMap<String,Object>();
			}
			this.userAttributers.put(key, value);
		}else {
			if(this.userAttributers != null) {
				this.userAttributers.remove(key);
			}
		}
	}

	public Object getUserAttribute(String key) {
		return (this.userAttributers != null) ? this.userAttributers.get(key) : null;
	}
 
}

package com.brbear.summer.framework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import com.brbear.summer.framework.aop.intercept.BRMethodInvocation;
import com.brbear.summer.framework.aop.support.BRAdvisedSupport;

public class BRJdkDynamicAopProxy implements BRAopProxy, InvocationHandler {

	private BRAdvisedSupport config;

	public BRJdkDynamicAopProxy(BRAdvisedSupport config) {
		this.config = config;
	}

	public Object getProxy() {
		return getProxy(this.config.getTargetClass().getClassLoader());
	}

	public Object getProxy(ClassLoader classLoader) {
		return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		List<Object> interceptorsAndDynamicMethodMatchers = 
				config.getInterceptorsAndDynamicInterceptionAdvice(method, this.config.getTargetClass());
		BRMethodInvocation invocation = new BRMethodInvocation(proxy, method, this.config.getTarget(),
				this.config.getTargetClass(), args, interceptorsAndDynamicMethodMatchers);
		return invocation.proceed();
	}

}

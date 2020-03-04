package com.brbear.summer.framework.aop.aspect;

import java.lang.reflect.Method;

public interface BRJoinPoint {

	Method getMethod();

	Object[] getArguments();

	Object getThis();

	void setUserAttribute(String key, Object value);

	Object getUserAttribute(String key);

}

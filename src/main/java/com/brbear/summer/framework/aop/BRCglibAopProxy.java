package com.brbear.summer.framework.aop;

import com.brbear.summer.framework.aop.support.BRAdvisedSupport;

public class BRCglibAopProxy implements BRAopProxy {

	private BRAdvisedSupport config;

	public BRCglibAopProxy(BRAdvisedSupport config) {
		this.config = config;
	}

	public Object getProxy() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getProxy(ClassLoader classLoader) {
		// TODO Auto-generated method stub
		return null;
	}

}

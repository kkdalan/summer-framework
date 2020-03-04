package com.brbear.summer.framework.demo.service.impl;

import com.brbear.summer.framework.demo.service.IModifyService;

public class ModifyService implements IModifyService{

	public String add(String name, String addr) throws Exception {
		throw new Exception("AOP testing exception!");
//		return "modifyService add, name=" + name + ", addr=" + addr;
	}

	public String edit(Integer id, String name) {
		return "modifyService edit, id=" + id + ", name=" + name;
	}

	public String remove(Integer id) {
		return "modifyService remove, id=" + id ;
	}
	 
}

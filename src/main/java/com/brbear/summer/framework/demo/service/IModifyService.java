package com.brbear.summer.framework.demo.service;

public interface IModifyService {
	
	public String add(String name, String addr) throws Exception;
	public String edit(Integer id, String name);
	public String remove(Integer id);
	
}

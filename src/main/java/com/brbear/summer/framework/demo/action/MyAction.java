package com.brbear.summer.framework.demo.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brbear.summer.framework.annotation.BRAutowired;
import com.brbear.summer.framework.annotation.BRController;
import com.brbear.summer.framework.annotation.BRRequestMapping;
import com.brbear.summer.framework.annotation.BRRequestParam;
import com.brbear.summer.framework.demo.service.IModifyService;
import com.brbear.summer.framework.demo.service.IQueryService;
import com.brbear.summer.framework.webmvc.BRModelAndView;

@BRController
@BRRequestMapping("/web")
public class MyAction {

	@BRAutowired IQueryService queryService;
	@BRAutowired IModifyService modifyService;
	
	@BRRequestMapping("/query.json")
	public BRModelAndView query(HttpServletRequest req, HttpServletResponse resp, @BRRequestParam("name") String name) {
		String result = queryService.query(name);
		return out(resp, result);
	}
	
	@BRRequestMapping("/add.json")
	public BRModelAndView add(HttpServletRequest req, HttpServletResponse resp, 
			@BRRequestParam("name") String name, @BRRequestParam("addr") String addr) throws Exception {
		String result = modifyService.add(name, addr);
		return out(resp, result);
	}
	
	@BRRequestMapping("/remove.json")
	public BRModelAndView remove(HttpServletRequest req, HttpServletResponse resp, 
			@BRRequestParam("id") Integer id) {
		String result = modifyService.remove(id);
		return out(resp, result);
	}
	
	@BRRequestMapping("/edit.json")
	public BRModelAndView edit(HttpServletRequest req, HttpServletResponse resp, 
			@BRRequestParam("id") Integer id, @BRRequestParam("name") String name) {
		String result = modifyService.edit(id, name);
		return out(resp, result);
	}

	private BRModelAndView out(HttpServletResponse resp,String str) {
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

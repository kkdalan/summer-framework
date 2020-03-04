package com.brbear.summer.framework.demo.action;

import java.util.HashMap;
import java.util.Map;

import com.brbear.summer.framework.annotation.BRAutowired;
import com.brbear.summer.framework.annotation.BRController;
import com.brbear.summer.framework.annotation.BRRequestMapping;
import com.brbear.summer.framework.annotation.BRRequestParam;
import com.brbear.summer.framework.demo.service.IQueryService;
import com.brbear.summer.framework.webmvc.BRModelAndView;

@BRController
@BRRequestMapping("/web")
public class PageAction {

	@BRAutowired IQueryService queryService;
	
	@BRRequestMapping("/first.html")
	public BRModelAndView query(@BRRequestParam("teacher") String teacher) {
		String result = queryService.query(teacher);

		Map<String,Object> model = new HashMap<String,Object>();
		model.put("teacher", teacher);
		model.put("data", result);
		model.put("token", "123456");
		
		return new BRModelAndView("first.html", model);
	}
	
}

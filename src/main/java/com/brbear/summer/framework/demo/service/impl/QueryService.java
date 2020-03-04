package com.brbear.summer.framework.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.brbear.summer.framework.annotation.BRService;
import com.brbear.summer.framework.demo.service.IQueryService;

import lombok.extern.slf4j.Slf4j;

@BRService
@Slf4j
public class QueryService implements IQueryService{

	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "}";
		log.info("這是在業務方法中印出的資訊: " + json);
		return json;
	}

}

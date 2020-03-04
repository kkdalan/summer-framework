package com.brbear.summer.framework.webmvc;

import java.util.Map;

public class BRModelAndView {

	private String viewName;
	private Map<String, ?> model;

	public BRModelAndView(String viewName) {
		this.viewName = viewName;
	}

	public BRModelAndView(String viewName, Map<String, ?> model) {
		this.viewName = viewName;
		this.model = model;
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public Map<String, ?> getModel() {
		return model;
	}

	public void setModel(Map<String, ?> model) {
		this.model = model;
	}

}

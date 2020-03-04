package com.brbear.summer.framework.webmvc;

import java.io.File;
import java.util.Locale;

public class BRViewResolver {

	private static final String DEFAULT_TEMPLATE_SUFFIX = ".html";

	private File templateRootDir;
	private String viewName;

	public BRViewResolver(String templateRootPath) {
		this.templateRootDir = new File(templateRootPath);
	}

	public BRView resolveViewName(String viewName, Locale locale) throws Exception{
		this.viewName = viewName;
		if(null == viewName || "".equals(viewName.trim())) {return null;}
		viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
		
		String templateFilePath = templateRootDir.getPath() + "/" + viewName.replaceAll("/+", "/");
		File templateFile = new File(templateFilePath);
		return new BRView(templateFile);
	}
	
	
	public String getViewName() {
		return viewName;
	}

}

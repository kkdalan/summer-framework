package com.brbear.summer.framework.webmvc.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brbear.summer.framework.annotation.BRController;
import com.brbear.summer.framework.annotation.BRRequestMapping;
import com.brbear.summer.framework.context.BRApplicationContext;
import com.brbear.summer.framework.webmvc.BRHandlerAdapter;
import com.brbear.summer.framework.webmvc.BRHandlerMapping;
import com.brbear.summer.framework.webmvc.BRModelAndView;
import com.brbear.summer.framework.webmvc.BRView;
import com.brbear.summer.framework.webmvc.BRViewResolver;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BRDispatcherServlet extends HttpServlet {

	private static final String LOCATION = "contextConfigLocation";
	
	private List<BRHandlerMapping> handlerMappings = new ArrayList<BRHandlerMapping>();
	private Map<BRHandlerMapping,BRHandlerAdapter> handlerAdapters = new HashMap<BRHandlerMapping,BRHandlerAdapter>();
	private List<BRViewResolver> viewResolvers = new ArrayList<BRViewResolver>();
	private BRApplicationContext context;

	@Override
	public void init(ServletConfig config) throws ServletException {
		context = new BRApplicationContext(config.getInitParameter(LOCATION));
		initStrategies(context);
	}
	
	protected void initStrategies(BRApplicationContext context) {
		initMultipartResolver(context);
		initLocateResolver(context);
		initThemeResolver(context);
		initHandlerMappings(context);
		initHandlerAdapters(context);
		initHandlerExceptionResolver(context);
		initRequestToViewNameTranslator(context);
		initViewResolvers(context);
		initFlashMapManager(context);
	}
	
	private void initFlashMapManager(BRApplicationContext context) {}
	private void initRequestToViewNameTranslator(BRApplicationContext context) {}
	private void initHandlerExceptionResolver(BRApplicationContext context) {}
	private void initThemeResolver(BRApplicationContext context) {}
	private void initLocateResolver(BRApplicationContext context) {}
	private void initMultipartResolver(BRApplicationContext context) {}

	private void initHandlerMappings(BRApplicationContext context) {
		String[] beanNames = context.getBeanDefinitionNames();
		try {
			for (String beanName : beanNames) {
				Object controller = context.getBean(beanName);
				Class<?> clazz = controller.getClass();
				
				if(!clazz.isAnnotationPresent(BRController.class)) {
					continue;
				}
				
				String baseUrl = "";
				if(clazz.isAnnotationPresent(BRRequestMapping.class)) {
					BRRequestMapping requestMapping = clazz.getAnnotation(BRRequestMapping.class);
					baseUrl = requestMapping.value();
				}
				
				Method[] methods = clazz.getMethods();
				for(Method method : methods) {
					if(!method.isAnnotationPresent(BRRequestMapping.class)) {
						continue;
					}
					BRRequestMapping requestMapping = method.getAnnotation(BRRequestMapping.class);
					String regex = ( baseUrl + requestMapping.value().replace("\\*", ".*")).replace("/+", "/");
					
					Pattern pattern = Pattern.compile(regex);
					this.handlerMappings.add(new BRHandlerMapping(controller, method, pattern));
					log.info("Mapping: " + regex + " , " + method);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initHandlerAdapters(BRApplicationContext context) {
		for(BRHandlerMapping handlerMapping: this.handlerMappings) {
			this.handlerAdapters.put(handlerMapping, new BRHandlerAdapter());
		}
	}
	
	private void initViewResolvers(BRApplicationContext context) {
		String templateRoot = context.getConfig().getProperty("templateRoot");
		String templateRootPath = this.getClass().getClassLoader().getResource("../"+templateRoot).getFile();
		
		File templateRootDir = new File(templateRootPath);
		for(File template: templateRootDir.listFiles()) {
			this.viewResolvers.add(new BRViewResolver(templateRootPath));
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			doDispatch(req, resp);
		} catch (Exception e) {
			try {
				BRModelAndView mv = new BRModelAndView("500");
				Map<String,Object> model = new HashMap<String,Object>();
				String detail = (e.getMessage()==null)? "" : e.getMessage();
				model.put("detail", detail);
				model.put("stackTrace", convertStackTraceString(e));
				mv.setModel(model);
				
				processDispatchResult(req, resp, mv);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}
	}
	
	private String convertStackTraceString(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		return exceptionAsString;
	}

	private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		BRHandlerMapping handler = getHandler(req);
		if(handler == null) {
			processDispatchResult(req, resp, new BRModelAndView("404"));
			return;
		}
		
		BRHandlerAdapter adapter = getHandlerAdapter(handler);
		BRModelAndView mv = adapter.handle(req, resp, handler);
		
		processDispatchResult(req, resp, mv);
	}
	
	private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, BRModelAndView mv) throws Exception {
		if(null == mv) {return;}
		if(this.viewResolvers != null) {
			for(BRViewResolver viewResolver : this.viewResolvers) {
				BRView view = viewResolver.resolveViewName(mv.getViewName(), null);
				if(view != null) {
					view.render(mv.getModel(), req, resp);
					return;
				}
			}
		}
	}
	
	private BRHandlerAdapter getHandlerAdapter(BRHandlerMapping handler) {
		if(this.handlerAdapters.isEmpty()) {return null;}
		BRHandlerAdapter adapter = this.handlerAdapters.get(handler);
		if(adapter.supports(handler)){
			return adapter;
		}
		return null;
	}
	
	private BRHandlerMapping getHandler(HttpServletRequest req) {
		if(this.handlerMappings.isEmpty()) {return null;}
		
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		url = url.replace(contextPath, "").replaceAll("/+", "/");
		
		for(BRHandlerMapping handler : this.handlerMappings) {
			Matcher matcher = handler.getPattern().matcher(url);
			if(!matcher.matches()) { continue; }
			return handler;
		}
		
		return null;
	}
}

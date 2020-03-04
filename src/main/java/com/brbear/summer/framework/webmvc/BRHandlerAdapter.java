package com.brbear.summer.framework.webmvc;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.brbear.summer.framework.annotation.BRRequestParam;

public class BRHandlerAdapter {

	public boolean supports(Object hander) {
		return hander instanceof BRHandlerMapping;
	}
	
	public BRModelAndView handle(HttpServletRequest req, HttpServletResponse resp, Object handler ) throws Exception{
		BRHandlerMapping handlerMapping = (BRHandlerMapping)handler;
		
		Map<String,Integer> paramMapping = new HashMap<String,Integer>();
	
		Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
		for(int i = 0 ; i < pa.length ; i++) {
			for(Annotation a :  pa[i]) {
				if(a instanceof BRRequestParam) {
					String paramName = ((BRRequestParam)a).value();
					if(!"".equals(paramName.trim())) {
						paramMapping.put(paramName, i);
					}
				}
			}
		}
		
		Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
		for(int i = 0 ; i < paramTypes.length ; i++) {
			Class<?> type = paramTypes[i];
			if(type == HttpServletRequest.class || type == HttpServletResponse.class) {
				paramMapping.put(type.getName(), i);
			}
		}
		
		Map<String,String[]> reqParameterMap = req.getParameterMap();
		
		Object[] paramValues = new Object[paramTypes.length];
		
		for(Map.Entry<String,String[]> param : reqParameterMap.entrySet()) {
			String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
			if(!paramMapping.containsKey(param.getKey())) { continue; }
			
			int index = paramMapping.get(param.getKey());
			paramValues[index] = caseStringValue(value,paramTypes[index]);
		}
		
		if(paramMapping.containsKey(HttpServletRequest.class.getName())) {
			int reqIndex = paramMapping.get(HttpServletRequest.class.getName());
			paramValues[reqIndex] = req;
		}
		
		if(paramMapping.containsKey(HttpServletResponse.class.getName())) {
			int respIndex = paramMapping.get(HttpServletResponse.class.getName());
			paramValues[respIndex] = resp;
		}
		
		Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
		if(result == null) {return null;}
		
		boolean isModelAndView = handlerMapping.getMethod().getReturnType() == BRModelAndView.class;
		if(isModelAndView) {
			return (BRModelAndView) result;
		}else {
			return null;
		}
	}
	
	private Object caseStringValue(String value, Class<?> clazz) {
		if(clazz == String.class) {
			return value;
		}else if(clazz == Integer.class) {
			return Integer.valueOf(value);
		}else if(clazz == int.class) {
			return Integer.valueOf(value).intValue();
		}else {
			return null;
		}
	}
	 
}

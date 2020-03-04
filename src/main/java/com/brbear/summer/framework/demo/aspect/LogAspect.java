package com.brbear.summer.framework.demo.aspect;

import java.util.Arrays;

import com.brbear.summer.framework.aop.aspect.BRJoinPoint;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogAspect {

	public void before(BRJoinPoint joinPoint) {
		joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(), System.currentTimeMillis());
		log.info("Invoker Before Method => " 
				+ "\nTargetObject: " + joinPoint.getThis() 
				+ "\nArgs: " + Arrays.toString(joinPoint.getArguments()));
	}

	public void after(BRJoinPoint joinPoint) {
		log.info("Invoker After Method => " 
				+ "\nTargetObject: " + joinPoint.getThis() 
				+ "\nArgs: " + Arrays.toString(joinPoint.getArguments()));
		long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
		long endTime = System.currentTimeMillis();
		System.out.println("use time: " + (endTime - startTime));
	}

	public void afterThrowing(BRJoinPoint joinPoint, Throwable ex) {
		log.info("Exception: "
				+ "\nTargetObject: " + joinPoint.getThis() 
				+ "\nArgs: " + Arrays.toString(joinPoint.getArguments())
				+ "\nThrows: " + ex.getMessage());
	}
}

package com.brbear.summer.framework.aop;

import lombok.Data;

@Data
public class BRAopConfig {

	private String pointCut;
	private String aspectClass;
	private String aspectBefore;
	private String aspectAfter;
	private String aspectAfterThrow;
	private String aspectAfterThrowingName;
	
}

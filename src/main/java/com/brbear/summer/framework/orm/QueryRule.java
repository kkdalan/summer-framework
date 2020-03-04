package com.brbear.summer.framework.orm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class QueryRule implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final int ASC_ORDER = 101;
	public static final int DESC_ORDER = 102;
	public static final int AND = 201;
	public static final int OR = 201;
	
	public static final int LIKE = 1;
	public static final int BETWEEN = 2;
	public static final int IN = 3;
	public static final int NOT_IN = 4;
	public static final int EQ = 5;
	public static final int NOT_EQ = 6;
	public static final int GT = 7;
	public static final int GE = 8;
	public static final int LT = 9;
	public static final int LE = 10;
	public static final int IS_NULL = 11;
	public static final int IS_NOT_NULL = 12;
	public static final int IS_EMPTY = 13;
	public static final int IS_NOT_EMPTY = 14;
	
	private List<Rule> ruleList = new ArrayList<Rule>();
	private List<QueryRule> queryRuleList = new ArrayList<QueryRule>();
	private String propertyName;

	public QueryRule addAscOrder(String propertyName) {
		this.ruleList.add(new Rule(ASC_ORDER, propertyName));
		return this;
	}
	
	public QueryRule addDescOrder(String propertyName) {
		this.ruleList.add(new Rule(DESC_ORDER, propertyName));
		return this;
	}
	
	public QueryRule andIsNull(String propertyName) {
		this.ruleList.add(new Rule(IS_NULL, propertyName).setAndOr(AND));
		return this;
	}
	
	public QueryRule andIsNotNull(String propertyName) {
		this.ruleList.add(new Rule(IS_NOT_NULL, propertyName).setAndOr(AND));
		return this;
	}
	
	public QueryRule andIsEmpty(String propertyName) {
		this.ruleList.add(new Rule(IS_EMPTY, propertyName).setAndOr(AND));
		return this;
	}
	
	public QueryRule andIsNotEmpty(String propertyName) {
		this.ruleList.add(new Rule(IS_NOT_EMPTY, propertyName).setAndOr(AND));
		return this;
	}
	
	public QueryRule andLike(String propertyName, Object value) {
		this.ruleList.add(new Rule(LIKE, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(EQ, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andNotEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(NOT_EQ, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andBetween(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(BETWEEN, propertyName, values).setAndOr(AND));
		return this;
	}
	
	public QueryRule andIn(String propertyName, List<Object> values) {
		this.ruleList.add(new Rule(IN, propertyName, new Object[] {values}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andIn(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(IN, propertyName, values).setAndOr(AND));
		return this;
	}
	
	public QueryRule andNotIn(String propertyName, List<Object> values) {
		this.ruleList.add(new Rule(NOT_IN, propertyName, new Object[] {values}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andNotIn(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(NOT_IN, propertyName, values).setAndOr(AND));
		return this;
	}
	
	public QueryRule andGreaterThan(String propertyName, Object value) {
		this.ruleList.add(new Rule(GT, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andGreaterEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(GE, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andLessThan(String propertyName, Object value) {
		this.ruleList.add(new Rule(LT, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	public QueryRule andLessEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(LE, propertyName, new Object[] {value}).setAndOr(AND));
		return this;
	}
	
	

	public QueryRule orIsNull(String propertyName) {
		this.ruleList.add(new Rule(IS_NULL, propertyName).setAndOr(OR));
		return this;
	}
	
	public QueryRule orIsNotNull(String propertyName) {
		this.ruleList.add(new Rule(IS_NOT_NULL, propertyName).setAndOr(OR));
		return this;
	}
	
	public QueryRule orIsEmpty(String propertyName) {
		this.ruleList.add(new Rule(IS_EMPTY, propertyName).setAndOr(OR));
		return this;
	}
	
	public QueryRule orIsNotEmpty(String propertyName) {
		this.ruleList.add(new Rule(IS_NOT_EMPTY, propertyName).setAndOr(OR));
		return this;
	}
	
	public QueryRule orLike(String propertyName, Object value) {
		this.ruleList.add(new Rule(LIKE, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(EQ, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orNotEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(NOT_EQ, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orBetween(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(BETWEEN, propertyName, values).setAndOr(OR));
		return this;
	}
	
	public QueryRule orIn(String propertyName, List<Object> values) {
		this.ruleList.add(new Rule(IN, propertyName, new Object[] {values}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orIn(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(IN, propertyName, values).setAndOr(OR));
		return this;
	}
	
	public QueryRule orNotIn(String propertyName, List<Object> values) {
		this.ruleList.add(new Rule(NOT_IN, propertyName, new Object[] {values}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orNotIn(String propertyName, Object ...values) {
		this.ruleList.add(new Rule(NOT_IN, propertyName, values).setAndOr(OR));
		return this;
	}
	
	public QueryRule orGreaterThan(String propertyName, Object value) {
		this.ruleList.add(new Rule(GT, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orGreaterEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(GE, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orLessThan(String propertyName, Object value) {
		this.ruleList.add(new Rule(LT, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	public QueryRule orLessEqual(String propertyName, Object value) {
		this.ruleList.add(new Rule(LE, propertyName, new Object[] {value}).setAndOr(OR));
		return this;
	}
	
	
	
	public List<Rule> getRuleList() {
		return ruleList;
	}

	public List<QueryRule> getQueryRuleList() {
		return queryRuleList;
	}

	public String getPropertyName() {
		return propertyName;
	}

	
	protected class Rule implements Serializable{
		private static final long serialVersionUID = 1L;
		private int type;
		private String propertyName;
		private Object[] values;
		private int andOr = AND;
		
		public Rule(int paramInt, String paramString) {
			this.type = paramInt;
			this.propertyName = paramString;
		}
		
		public Rule(int paramInt, String paramString,Object[] paramArrayOfObject) {
			this.type = paramInt;
			this.propertyName = paramString;
			this.values = paramArrayOfObject;
		}

		public int getType() {
			return type;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public Object[] getValues() {
			return values;
		}

		public int getAndOr() {
			return andOr;
		}

		public Rule setAndOr(int andOr) {
			this.andOr = andOr;
			return this;
		}
		
	}
}

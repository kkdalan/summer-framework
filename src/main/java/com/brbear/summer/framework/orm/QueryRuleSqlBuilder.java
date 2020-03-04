package com.brbear.summer.framework.orm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class QueryRuleSqlBuilder {

	private int CURR_INDEX = 0;
	private List<String> properties;
	private List<Object> values;
	private List<Order> orders;

	private String whereSql;
	private String orderSql;
	private Object[] valueArr = new Object[] {};
	private Map<Object, Object> valueMap = new HashMap<Object, Object>();

	public String getWhereSql() {
		return whereSql;
	}

	public String getOrderSql() {
		return orderSql;
	}

	public Object[] getValueArr() {
		return valueArr;
	}

	public Map<Object, Object> getValueMap() {
		return valueMap;
	}
	
	public QueryRuleSqlBuilder(QueryRule queryRule) {
		CURR_INDEX = 0;
		properties = new ArrayList<String>();
		values = new ArrayList<Object>();
		orders = new ArrayList<Order>();
		for(QueryRule.Rule rule : queryRule.getRuleList()) {
			processRule(rule);
		}
		appendWhereSql();
		appendOrderSql();
		appendValues();
	}
	
	private void appendWhereSql() {
		StringBuffer whereSql = new StringBuffer();
		for(String p : properties) {
			whereSql.append(p);
		}
		this.whereSql = removeSelect(removeOrders(whereSql.toString()));
	}
	
	private void appendOrderSql() {
		StringBuffer orderSql = new StringBuffer();
		for (int i = 0; i < orders.size(); i++) {
			if (i > 0 && i < orders.size()) {
				orderSql.append(",");
			}
			orderSql.append(orders.get(i).toString());
		}
		this.orderSql = removeSelect(removeOrders(orderSql.toString()));
	}
	
	private void appendValues() {
		Object[] valueArr = new Object[values.size()];
		for (int i = 0; i < values.size(); i++) {
			valueArr[i] = values.get(i);
			valueMap.put(i, values.get(i));
		}
		this.valueArr = valueArr;
	}
	
	protected String removeOrders(String sql) {
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(sql);
		StringBuffer sb = new StringBuffer();
		while(m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	protected String removeSelect(String sql) {
		if(sql.toLowerCase().matches("from\\s+")) {
			int beginPos = sql.toLowerCase().indexOf("from");
			return sql.substring(beginPos);
		}else {
			return sql;
		}
	}
	
	private void processRule(QueryRule.Rule rule) {
		switch(rule.getType()) {
		case QueryRule.BETWEEN:
			processBetween(rule);
			break;
		case QueryRule.LIKE:
			processLike(rule);
			break;
		case QueryRule.GT:
			processGreaterThan(rule);
			break;
		case QueryRule.GE:
			processGreaterEqual(rule);
			break;
		case QueryRule.LT:
			processLessThan(rule);
			break;
		case QueryRule.LE:
			processLessEqual(rule);
			break;
		case QueryRule.EQ:
			processEqual(rule);
			break;
		case QueryRule.NOT_EQ:
			processNotEqual(rule);
			break;
		case QueryRule.IN:
			processIn(rule);
			break;
		case QueryRule.NOT_IN:
			processNotIn(rule);
			break;
		case QueryRule.IS_NULL:
			processIsNull(rule);
			break;
		case QueryRule.IS_NOT_NULL:
			processIsNotNull(rule);
			break;
		case QueryRule.IS_EMPTY:
			processIsEmpty(rule);
			break;
		case QueryRule.IS_NOT_EMPTY:
			processIsNotEmpty(rule);
			break;
		case QueryRule.ASC_ORDER:
			processOrder(rule);
			break;
		case QueryRule.DESC_ORDER:
			processOrder(rule);
			break;
		default:
			throw new IllegalArgumentException("type " + rule.getType() + " not supported. ");
		}
	}
	
	private void processBetween(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues()) || rule.getValues().length != 2) { return; }
		add(rule.getAndOr(), rule.getPropertyName(), "between", rule.getValues()[0] + "and");
		add(0, "", "", "", rule.getValues()[1],"");
	}
	
	private void processLike(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		Object obj = rule.getValues()[0];
		if(obj != null) {
			String value = obj.toString();
			if(StringUtils.isNotEmpty(value)) {
				value = value.replace('*', '%');
				obj = value;
			}
		}
		add(rule.getAndOr(), rule.getPropertyName(), "like", "%"+rule.getValues()[0]+"%");
	}
	
	private void processGreaterThan(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), ">", rule.getValues()[0]);
	}
	
	private void processGreaterEqual(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), ">=", rule.getValues()[0]);
	
	}
	private void processLessThan(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "<", rule.getValues()[0]);
	
	}
	private void processLessEqual(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "<=", rule.getValues()[0]);
	
	}
	
	private void processEqual(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "=", rule.getValues()[0]);
	}
	
	private void processNotEqual(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "<>", rule.getValues()[0]);
	}
	
	private void processIsNull(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "is null", null);
	}
	
	private void processIsNotNull(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "is not null", null);
	}
	
	private void processIsEmpty(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "=", "''");
	}
	
	private void processIsNotEmpty(QueryRule.Rule rule) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		add(rule.getAndOr(), rule.getPropertyName(), "<>", "''");
	}
	
	private void processIn(QueryRule.Rule rule) {
		addInAndNotIn(rule, "in");
	}
	
	private void processNotIn(QueryRule.Rule rule) {
		addInAndNotIn(rule, "not in");
	}
	
	private void addInAndNotIn(QueryRule.Rule rule, String name) {
		if(ArrayUtils.isEmpty(rule.getValues())){ return; }
		
		if((rule.getValues().length == 1) 
				&& (rule.getValues()[0]!=null)
				&& (rule.getValues()[0] instanceof List)) {
			List<Object> list = (List) rule.getValues()[0];
			if(CollectionUtils.isNotEmpty(list)) {
				addInAndNotInList(rule, name, list);
			}
		} else {
			Object[] list = rule.getValues();
			addInAndNotInList(rule, name, Arrays.asList(list));
		}
	}

	private void addInAndNotInList(QueryRule.Rule rule, String name, List<Object> list) {
		for (int i = 0; i < list.size(); i++) {
			Object value = list.get(i);
			if (i == 0 && i == list.size() - 1) {
				add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", value, ")");
			} else if (i == 0 && i < list.size() - 1) {
				add(rule.getAndOr(), rule.getPropertyName(), "", name + " (", value, "");
			}
			if (i > 0 && i < list.size() - 1) {
				add(0, "", ",", "", value, "");
			}
			if (i == list.size() - 1 && i != 0) {
				add(0, "", ",", "", value, ")");
			}
		}
	}
	
	private void processOrder(QueryRule.Rule rule) {
		switch (rule.getType()) {
		case QueryRule.ASC_ORDER:
			if(StringUtils.isNotEmpty(rule.getPropertyName())) {
				orders.add(Order.asc(rule.getPropertyName()));
			}
			break;
		case QueryRule.DESC_ORDER:
			if(StringUtils.isNotEmpty(rule.getPropertyName())) {
				orders.add(Order.desc(rule.getPropertyName()));
			}
			break;
		default:
			break;
		}
	}

	
	private void add(int andOr, String key, String split, Object value) {
		add(andOr, key, split, "", value, "");
	}
	
	private void add(int andOr, String key, String split, String prefix, Object value, String suffix) {
		String andOrStr = (0 == andOr ? "" : (QueryRule.AND == andOr ? " and " : " or "));
		String propertyStr = andOrStr + key + " " + split + prefix + (null !=value ? " ? ":" ") + suffix;
		properties.add(CURR_INDEX, propertyStr);
		if(null != value) {
			values.add(CURR_INDEX,value);
			CURR_INDEX++;
		}
	}
	
}

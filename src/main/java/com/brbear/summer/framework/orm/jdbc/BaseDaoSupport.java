package com.brbear.summer.framework.orm.jdbc;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.brbear.summer.framework.orm.EntityOperation;
import com.brbear.summer.framework.orm.Page;
import com.brbear.summer.framework.orm.PropertyMapping;
import com.brbear.summer.framework.orm.QueryRule;
import com.brbear.summer.framework.orm.QueryRuleSqlBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> 
	implements BaseDao<T, PK> {

	private String tableName;
	
	private JdbcTemplate jdbcTemplateWrite;
	private JdbcTemplate jdbcTemplateReadOnly;
	
	private DataSource dataSourceWrite;
	private DataSource dataSourceReadOnly;
	
	private EntityOperation<T> op;
	
	protected BaseDaoSupport() {
		try {
			Class<T> entityClass = GenericsUtils.getSuperClassGenericType(getClass(),0);
			op = new EntityOperation<T>(entityClass, getPkColumn());
			this.setTableName(op.tableName);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract String getPkColumn();
	protected abstract String setDataSource();
	
	protected void restoreTableName() {
		this.setTableName(op.tableName);
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		if(StringUtils.isEmpty(tableName)) {
			this.tableName = op.tableName;
		}else {
			this.tableName = tableName;
		}
	}

	public DataSource getDataSourceWrite() {
		return dataSourceWrite;
	}

	public DataSource getDataSourceReadOnly() {
		return dataSourceReadOnly;
	}

	public void setDataSourceWrite(DataSource dataSourceWrite) {
		this.dataSourceWrite = dataSourceWrite;
		this.jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
	}
	
	public void setDataSourceReadOnly(DataSource dataSourceReadOnly) {
		this.dataSourceReadOnly = dataSourceReadOnly;
		this.jdbcTemplateReadOnly = new JdbcTemplate(dataSourceReadOnly);
	}

	public JdbcTemplate jdbcTemplateWrite() {
		return jdbcTemplateWrite;
	}

	public JdbcTemplate jdbcTemplateReadOnly() {
		return jdbcTemplateReadOnly;
	}
	
	public List<T> select(QueryRule queryRule) throws Exception {
		QueryRuleSqlBuilder builder = new QueryRuleSqlBuilder(queryRule);
		String ws = removeFirstAnd(builder.getWhereSql());
		String whereSql = ("".equals(ws) ? ws : " where " + ws);
		String sql = "select " + op.allColumn + " from " + getTableName() + whereSql;
		Object[] values = builder.getValueArr();
		String orderSql = builder.getOrderSql();
		orderSql = (StringUtils.isEmpty(orderSql) ? " " : " order by " + orderSql);
		sql += orderSql;
		log.debug(sql);
		return this.jdbcTemplateReadOnly().query(orderSql, this.op.rowMapper, values);
	}

	public List<Map<String, Object>> selectBySql(String sql, Object... args) throws Exception {
		return this.jdbcTemplateReadOnly().queryForList(sql, args);
	}
	
	public Page<T> select(QueryRule queryRule, final int pageNo, final int pageSize, Object... args) throws Exception {
		QueryRuleSqlBuilder builder = new QueryRuleSqlBuilder(queryRule);
		Object[] values = builder.getValueArr();
		String ws = removeFirstAnd(builder.getWhereSql());
		String whereSql = ("".equals(ws) ? ws : " where " + ws);
		String countSql = "select count(1) from " + getTableName() + whereSql;
		int count = (Integer)this.jdbcTemplateReadOnly().queryForMap(countSql, args).get("count(1)");
		if (count == 0) { 
			return new Page<T>();
		}
		int start = (pageNo - 1) * pageSize;
		String orderSql = builder.getOrderSql();
		orderSql = (StringUtils.isEmpty(orderSql) ? " " : " order by " + orderSql);
		String sql = "select " + op.allColumn + " from " + getTableName() + whereSql + orderSql + " limit " + start + "," + pageSize;;
		List<T> list = (List<T>)this.jdbcTemplateReadOnly().query(orderSql, this.op.rowMapper, values);
		log.debug(sql);
		return new Page<T>(start, count, pageSize, list);
	}

	public Page<Map<String, Object>> selectBySqlToPage(String sql, Object[] param, final int pageNo, final int pageSize) throws Exception {
		String countSql = "select count(1) from (" + sql + ") a";
		int count = (Integer)this.jdbcTemplateReadOnly().queryForMap(countSql, param).get("count(1)");
		if (count == 0) { 
			return new Page<Map<String, Object>>();
		}
		int start = (pageNo - 1) * pageSize;
		sql = sql + " limit " + start + "," + pageSize;
		List<Map<String, Object>> list = this.jdbcTemplateReadOnly().queryForList(sql, param);
		log.debug(sql);
		return new Page<Map<String, Object>>(start, count, pageSize, list);
	}
	
	public PK insertAndReturnId(T entity) throws Exception {
		return (PK)doInsertReturnKey(parse(entity));
	}

	public boolean insert(T entity) throws Exception {
		return doInsert(parse(entity));
	}
	
	public boolean doInsert(Map<String, Object> param) throws Exception {
		String sql = makeSimpleInsertSql(getTableName(), param);
		int rows = this.jdbcTemplateWrite().update(sql,param.values().toArray());
		return rows > 0;
	}
	
	// TODO
	private Map<String, Object> parse(T entity) {
		return null;
	}

	// TODO
	private String makeSimpleInsertSql(String tableName, Map<String, Object> param) {
		return null;
	}
	
	//TODO
	private String removeFirstAnd(String str) {
		return str;
	}
	
	//TODO
	private Page<T> pagination(List<T> list, int i, int step){
		return null;
	}
		
	
	private Serializable doInsertReturnKey(Map<String, Object> param) {
		final List<Object> values = new ArrayList<Object>();
		final String sql = makeSimpleInsertSql(getTableName(), param);
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSourceWrite());
		
		try {
			PreparedStatementCreator psc = new PreparedStatementCreator() {
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
					for (int i = 0; i < values.size(); i++) {
						ps.setObject(i + 1, values.get(i) == null ? null : values.get(i));
					}
					return ps;
				}
			};
			jdbcTemplate.update( psc, keyHolder);
		}catch(Exception e) {
			log.error("error",e);
		}
//		if(keyHolder == null) { return ""; }
		
		Map<String, Object> keys = keyHolder.getKeys();
		if(MapUtils.isEmpty(keys)) { return ""; }
		
		Object key = keys.values().toArray()[0];
		if(key == null || !(key instanceof Serializable)) { return ""; }
		
		if(key instanceof Number) {
			Class clazz = key.getClass();
			boolean isIntClass = ( clazz == int.class || clazz == Integer.class );
			return isIntClass ? ((Number)key).intValue():((Number)key).longValue();
		}else if(key instanceof String) {
			return (String)key;
		}else {
			return (Serializable) key;
		}
	}
	
	public int insertAll(List<T> list) throws Exception {
		int count = 0, len = list.size(), step = 50000;
		Map<String, PropertyMapping> pm = op.mappings;
		int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
		for(int i = 0 ; i <= maxPage ; i++) { 
			Page<T> page = pagination(list, i, step);
			String sql = "insert into " + getTableName() + "(" + op.allColumn + ") values ";
			StringBuffer valstr = new StringBuffer();
			Object[] values = new Object[pm.size() * page.getRows().size()];
			for(int j = 0 ; j < page.getRows().size() ; j++) {
				if(j > 0 && j < page.getRows().size()) { valstr.append(","); }
				valstr.append("(");
				int k = 0;
				for(PropertyMapping p : pm.values()) {
					values[j*pm.size() + k] = p.getter.invoke(page.getRows().get(j));
					if(k > 0 && k < pm.size()) { valstr.append(","); }
					valstr.append("?");
					k++;
				}
				valstr.append(")");
			}
			int result = jdbcTemplateWrite().update(sql + valstr, values);
			count += result;
		}
		return count;
	}
	
	//TODO
	public boolean delete(T entity) throws Exception {
//		return this.doDelete(op.pkField.get(entity)) > 0;
		return true;
	}
	
	protected void deleteByPK(PK id) throws Exception {
		this.doDelete(id);
	}
	
	//TODO
	private void doDelete(PK id) throws Exception {
		
	}
	
	private int doDelete(String tableName, String pkName, Object pkValue) {
		StringBuffer sb = new StringBuffer();
		sb.append("delete from ").append(tableName).append(" where ").append(pkName).append(" = ? ");
		int rows = this.jdbcTemplateWrite().update(sb.toString(), pkValue);
		return rows;
	}

	public int deleteAll(List<T> list) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean update(T entity) throws Exception {
		return this.doUpdate(op.pkField.get(entity), parse(entity)) > 0;
	}
	
	public int doUpdate(Object pkValue, Map<String, Object> param) throws Exception {
		String sql = makeDefaultSimpleUpdateSql(pkValue, param);
		param.put(getPkColumn(), pkValue);
		int rows = this.jdbcTemplateWrite().update(sql,param.values().toArray());
		return rows;
	}
	
	//TODO
	private String makeDefaultSimpleUpdateSql(Object pkValue, Map<String, Object> param) {
		return null;
	}
	
}

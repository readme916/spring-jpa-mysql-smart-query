package com.liyang.jpa.smart.query.db.parser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liyang.jpa.smart.query.db.SmartQuery;
import com.liyang.jpa.smart.query.db.parser.Tokenizer.Token;
import com.liyang.jpa.smart.query.db.structure.ColumnJoinType;
import com.liyang.jpa.smart.query.db.structure.ColumnStucture;
import com.liyang.jpa.smart.query.db.structure.EntityStructure;
import com.liyang.jpa.smart.query.exception.QueryException;
import com.liyang.jpa.smart.query.response.HTTPListResponse;
import com.liyang.jpa.smart.query.service.ApplicationContextSupport;
import com.liyang.jpa.smart.query.service.JdbcQueryService;

public class SQLSelectBuilder {

	private final static Logger logger = LoggerFactory.getLogger(SQLSelectBuilder.class);

	private Set<String> fields = new TreeSet<>();
	private Map<String, Token> conditions = new HashMap<>();
	private String mainEntityName;
	private long page;
	private long size;
	private String sort;
	private EntityStructure mainEntityStructure;

	private InnerJoin innerJoin = new InnerJoin();;
	private LeftJoin leftJoin = new LeftJoin(innerJoin);
	private Select select = new Select();
	private From from = new From();
	private Where where = new Where(innerJoin);
	private OrderBy orderBy = new OrderBy(innerJoin, leftJoin);;
	private GroupBy groupBy = new GroupBy();
	private Limit limit = new Limit();
	private boolean isTree = false;
	private PreparedStatements preparedStatements = new PreparedStatements();

	public SQLSelectBuilder feed(TokenizeParameter parameters) {
		if (parameters.getFields() == null || "".equals(parameters.getFields())) {
			fields.add("*");
		} else {
			String[] splits = parameters.getFields().split(",");
			for (String column : splits) {
				fields.add(column);
			}
		}
		conditions = parameters.getTokens();
		mainEntityName = parameters.getEntityName();
		sort = parameters.getSort();
		page = parameters.getPage();
		size = parameters.getSize();
		mainEntityStructure = SmartQuery.getStructure(mainEntityName);
		innerJoin.setMainEntityStructure(mainEntityStructure);
		leftJoin.setMainEntityStructure(mainEntityStructure);
		select.setMainEntityStructure(mainEntityStructure);
		from.setMainEntityStructure(mainEntityStructure);
		where.setMainEntityStructure(mainEntityStructure);
		orderBy.setMainEntityStructure(mainEntityStructure);
		groupBy.setMainEntityStructure(mainEntityStructure);

		return this;
	}

	public SQLSelectBuilder setIsTree(boolean isTree) {
		this.isTree = isTree;
		if (!mainEntityStructure.getObjectFields().containsKey("parent")) {
			throw new QueryException("树形结构，必须包含parent属性");
		}
		return this;
	}

	public SQLSelectBuilder build() {
		fields.add("uuid");
		checkMultiNesting(fields);
		if (isTree) {
			fields.add("parent");
		}

		PreparedStatements preparedStatements = getCache();
		if (preparedStatements != null) {
			this.preparedStatements = preparedStatements;
		} else {
			for (Token token : conditions.values()) {
				where.add(token);
			}
			for (String field : fields) {
				leftJoin.add(mainEntityStructure, field, "", mainEntityName);
				select.add(mainEntityStructure, field, "");
			}
			orderBy.add(sort);

			this.limit = new Limit();

			
			if(sort == null) {
				orderBy.getOrders().add("`" + mainEntityName + "`.uuid DESC");
			}else {
				boolean defaultOrder = false;
				for (String ex : orderBy.getExists()) {
					if (ex.contains("`" + mainEntityName + "`")) {
						defaultOrder = true;
					}
				}
				if (defaultOrder == false) {
					orderBy.getOrders().add("`" + mainEntityName + "`.uuid DESC");
				}
			}
	

			String fetchSql = select.sql() + from.sql() + innerJoin.sql() + leftJoin.sql() + where.sql() + orderBy.sql()
					+ limit.sql();
			this.preparedStatements.setFetchSql(fetchSql);

			String countSql = "SELECT COUNT(*) " + from.sql() + innerJoin.sql() + leftJoin.sql() + where.sql()
					+ groupBy.sql() + orderBy.sql();
			this.preparedStatements.setCountSql(countSql);
			putCache(this.preparedStatements);
		}
		return this;
	}

	public void printSql() {
		ObjectMapper objectMapper = new ObjectMapper();
		String writeValueAsString = null;
		try {
			writeValueAsString = objectMapper.writeValueAsString(preparedStatements);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(writeValueAsString);
	}

	public HTTPListResponse fetchAll() {
		StartAndSize _preCount = _preCount();
		if (_preCount == null) {
			return new HTTPListResponse(Collections.EMPTY_LIST, 0, 0, 0);
		} else {
			Collection<Token> values = conditions.values();
			HashMap<String, Object> preparedParams = new HashMap<>();
			for (Token token : values) {
				preparedParams.put(token.getOrigin(), token.getValue());
			}
			preparedParams.put("pageStart", _preCount.getStart());
			preparedParams.put("pageSize", _preCount.getOffset());

			List<Map<String, Object>> queryForList = JdbcQueryService.query(this.preparedStatements.fetchSql,
					preparedParams);
			
			String property = ApplicationContextSupport.getApplicationContext().getEnvironment()
					.getProperty("spring.jpa.mysql-smart-query.show-sql");
			if(property!=null && property.equals("true")) {
				logger.info(this.preparedStatements.fetchSql);
			}
			
			return new HTTPListResponse(transformAsList(queryForList, mainEntityStructure), _preCount.getTotal(), page,
					size);

		}
	}

	public long fetchCount() {
		StartAndSize _preCount = _preCount();
		if (_preCount == null) {
			return 0;
		} else {

			return _preCount.getTotal();
		}
	}

	// --------------------------------------------------------------------------------------------------------------

	private StartAndSize _preCount() {
		Collection<Token> values = conditions.values();
		HashMap<String, Object> preparedParams = new HashMap<>();
		for (Token token : values) {
			preparedParams.put(token.getOrigin(), token.getValue());
		}
		List<Map<String, Object>> queryForCount = JdbcQueryService.query(this.preparedStatements.countSql,
				preparedParams);
		return countStartAndOffsetOfList(queryForCount, page, size);
	}

	private void checkMultiNesting(Set<String> columns) {
		for (String column : columns) {
			if (column.contains(".")) {
				String[] split = column.split("\\.");
				if (split.length > 2) {
					throw new QueryException("参数" + column + "不允许多级嵌套");
				}
			}
		}
	}

	private PreparedStatements getCache() {
		CacheManager bean = ApplicationContextSupport.getBean(CacheManager.class);
		Cache cache = bean.getCache("mysqlBuilder");
		String key = cacheKey();
		return cache.get(key, PreparedStatements.class);
	}

	private void putCache(PreparedStatements preparedStatements) {
		CacheManager bean = ApplicationContextSupport.getBean(CacheManager.class);
		Cache cache = bean.getCache("mysqlBuilder");
		String key = cacheKey();
		cache.put(key, preparedStatements);
	}

	private String cacheKey() {
		TreeSet<String> treeSet = new TreeSet<>();
		treeSet.add("db." + mainEntityName);
		for (Token token : conditions.values()) {
			treeSet.add("conditions." + token.getOrigin());
		}

		for (String column : fields) {
			treeSet.add("field." + column);
		}

		treeSet.add("sort." + sort);
		treeSet.add("page." + String.valueOf(page));
		treeSet.add("size." + String.valueOf(size));
		return String.join("&", treeSet);
	}

	protected Object transformAsList(List<Map<String, Object>> queryForList, EntityStructure nametostructure) {

		LinkedHashMap<String, Object> mapList = new LinkedHashMap<>();

		for (Map<String, Object> row : queryForList) {
			rowToMapListFirstMerge(row, mapList, nametostructure);
		}
		for (Map<String, Object> row : queryForList) {
			rowToMapListSecondMerge(row, mapList, nametostructure);
		}
		return mapListToMap(mapList, nametostructure);
	}

	protected void rowToMapListFirstMerge(Map<String, Object> row, LinkedHashMap<String, Object> mapList,
			EntityStructure nametostructure) {

		Object old = mapList.get("uuid_" + row.get("uuid"));
		if (old == null) {
			LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
			mapList.put("uuid_" + row.get("uuid"), hashMap);
			_merge(hashMap, row, nametostructure);
		} else {
			_merge((LinkedHashMap<String, Object>) old, row, nametostructure);
		}
	}

	protected void rowToMapListSecondMerge(Map<String, Object> row, LinkedHashMap<String, Object> mapList,
			EntityStructure nametostructure) {
		Set<Entry<String, Object>> entrySet = row.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			String[] split = key.split("\\.");
			if (split.length == 3) {
				Object mapLv1 = mapList.get("uuid_" + row.get("uuid"));
				ColumnStucture columnStucture = nametostructure.getObjectFields().get(split[0]);
				Map mapLv2;
				if (columnStucture.getJoinType().equals(ColumnJoinType.MANY_TO_MANY)
						|| columnStucture.getJoinType().equals(ColumnJoinType.ONE_TO_MANY)) {

					if (row.get(split[0] + ".uuid") == null) {
						continue;
					}
					mapLv2 = (Map) ((Map) ((Map) mapLv1).get(split[0])).get("uuid_" + row.get(split[0] + ".uuid"));
				} else {
					mapLv2 = (Map) ((Map) mapLv1).get(split[0]);
				}

				if (mapLv2 == null || mapLv2.isEmpty()) {
					continue;
				}
				Map mapLv3 = null;
				ColumnStucture columnStucture2 = SmartQuery.getStructure(columnStucture.getTargetEntity())
						.getObjectFields().get(split[1]);
				if (columnStucture2.getJoinType().equals(ColumnJoinType.MANY_TO_MANY)
						|| columnStucture2.getJoinType().equals(ColumnJoinType.ONE_TO_MANY)) {

					mapLv3 = (Map) mapLv2.getOrDefault(split[1], new LinkedHashMap<String, Object>());
					mapLv2.put(split[1], mapLv3);

					Map mapLv4 = null;
					if (row.get(split[0] + "." + split[1] + ".uuid") != null) {
						mapLv4 = (Map) mapLv3.getOrDefault("uuid_" + row.get(split[0] + "." + split[1] + ".uuid"),
								new HashMap<String, Object>());
						((Map) mapLv4).put(split[2], entry.getValue());
					}

					if (row.get(split[0] + "." + split[1] + ".uuid") != null) {
						mapLv3.put("uuid_" + row.get(split[0] + "." + split[1] + ".uuid"), mapLv4);
					}

				} else {

					mapLv3 = (Map) ((Map) mapLv2).getOrDefault(split[1], new HashMap<String, Object>());
					if (entry.getValue() != null) {
						mapLv3.put(split[2], entry.getValue());
					}
					mapLv2.put(split[1], mapLv3);

				}

			}
		}

	}

	protected Object mapListToMap(Object o, EntityStructure structure) {
		if (o instanceof List) {
			ArrayList<Object> arrayList = new ArrayList<Object>();
			for (Object i : (List) o) {
				arrayList.add(mapListToMap(i, structure));
			}
			return arrayList;
		} else if (o instanceof Map) {
			Map origin = (Map) o;
			Set keySet = origin.keySet();
			Iterator iterator = keySet.iterator();
			if (iterator.hasNext()) {
				Object next = iterator.next();
				if (String.valueOf(next).startsWith("uuid_")) {
					ArrayList<Map> arrayList = new ArrayList<Map>();
					arrayList.addAll(origin.values());
					return mapListToMap(arrayList, structure);

				} else {
					HashMap hashMap = new HashMap();
					Set<Entry<String, Object>> entrySet = origin.entrySet();
					for (Entry<String, Object> entry : entrySet) {
						ColumnStucture columnStucture = structure.getObjectFields().get(entry.getKey());
						if (columnStucture != null) {
							EntityStructure targetStructure = SmartQuery
									.getStructure(columnStucture.getTargetEntity());
							hashMap.put(entry.getKey(), mapListToMap(entry.getValue(), targetStructure));
						} else {
							hashMap.put(entry.getKey(), mapListToMap(entry.getValue(), structure));
						}
					}
					return hashMap;
				}
			}
		} else {
			return o;
		}
		return o;
	}

	protected void _merge(LinkedHashMap<String, Object> old, Map<String, Object> row, EntityStructure nametostructure) {

		Set<Entry<String, Object>> entrySet = row.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			String key = entry.getKey();
			String[] split = key.split("\\.");
			if (split.length == 1) {
				old.put(key, entry.getValue());
			} else if (split.length == 2) {
				ColumnStucture columnStucture = nametostructure.getObjectFields().get(split[0]);
				if (columnStucture.getJoinType().equals(ColumnJoinType.MANY_TO_MANY)
						|| columnStucture.getJoinType().equals(ColumnJoinType.ONE_TO_MANY)) {
					Object mapListLv1 = old.getOrDefault(split[0], new LinkedHashMap<>());
					old.put(split[0], mapListLv1);
					Object mapLv2 = ((Map) mapListLv1).getOrDefault("uuid_" + row.get(split[0] + ".uuid"),
							new LinkedHashMap<>());
					((Map) mapLv2).put(split[1], entry.getValue());
					if (row.get(split[0] + ".uuid") != null) {
						((Map) mapListLv1).putIfAbsent("uuid_" + row.get(split[0] + ".uuid"), mapLv2);
					}
				} else {
					Object mapLv1 = old.getOrDefault(split[0], new LinkedHashMap<>());
					if (entry.getValue() != null) {
						((Map) mapLv1).put(split[1], entry.getValue());
					}
					old.put(split[0], mapLv1);
				}
			}
		}
	}

	protected StartAndSize countStartAndOffsetOfList(List<Map<String, Object>> queryForList, long pageNumber,
			long pageSize) {
		if (queryForList == null) {
			return null;
		}

		if (pageNumber * pageSize >= queryForList.size()) {
			return null;
		}

		long start = 0;
		long offset = 0;
		long p = 0;
		for (Map<String, Object> map : queryForList) {
			long row = (long) map.get("COUNT(*)");
			if (p < pageNumber * pageSize) {
				p++;
				start += row;
			} else if (p >= pageNumber * pageSize && p < pageNumber * pageSize + pageSize) {
				p++;
				offset += row;
			} else {
				break;
			}
		}

		StartAndSize startAndSize = new StartAndSize();

		String property = ApplicationContextSupport.getApplicationContext().getEnvironment()
				.getProperty("spring.jpa.mysql-smart-query.max-result-rows");
		int fetchRows = 5000;
		if (property != null) {
			fetchRows = Integer.valueOf(property);
		}
		if (offset > fetchRows) {
			throw new QueryException("查询数据量太大,当前限制"+fetchRows+",可修改配置spring.jpa.mysql-smart-query.max-result-rows");
		}
		startAndSize.setOffset(offset);
		startAndSize.setStart(start);
		startAndSize.setTotal(queryForList.size());
		return startAndSize;

	}

	public static class StartAndSize {
		private long total;
		private long start;
		private long offset;

		public long getTotal() {
			return total;
		}

		public void setTotal(long total) {
			this.total = total;
		}

		public long getStart() {
			return start;
		}

		public void setStart(long start) {
			this.start = start;
		}

		public long getOffset() {
			return offset;
		}

		public void setOffset(long offset) {
			this.offset = offset;
		}

	}

	public static class PreparedStatements implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String fetchSql;
		private String countSql;

		public String getFetchSql() {
			return fetchSql;
		}

		public void setFetchSql(String fetchSql) {
			this.fetchSql = fetchSql;
		}

		public String getCountSql() {
			return countSql;
		}

		public void setCountSql(String countSql) {
			this.countSql = countSql;
		}

	}
}

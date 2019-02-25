package com.liyang.jpa.mysql.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liyang.jpa.mysql.db.parser.SQLSelectBuilder;
import com.liyang.jpa.mysql.db.parser.TokenizeParameter;
import com.liyang.jpa.mysql.exception.GetFormatException;
import com.liyang.jpa.mysql.response.HTTPListResponse;


public class SmartQuery {

	private final static Logger logger = LoggerFactory.getLogger(SmartQuery.class);

	public static long fetchCount(String entityName, String queryString) {
		return _fetchCount(entityName, queryMap(queryString));
	}
	public static long fetchCount(String entityName, HashMap<String, String> queries) {
		return _fetchCount(entityName, queries);
	}
	

	public static Object fetchList(String entityName, String queryString) {
		return _fetchList(entityName, queryMap(queryString));
	}
	public static Object fetchList(String entityName, HashMap<String, String> queries) {
		return _fetchList(entityName, queries);
	}
	

	public static Object fetchTree(String entityName, String queryString) {
		return _fetchTree(entityName, queryMap(queryString));
	}
	public static Object fetchTree(String entityName, HashMap<String, String> queries) {
		return _fetchTree(entityName, queries);
	}

	public static Object fetchGroup(String entityName, String queryString) {
		return _fetchGroup(entityName, queryMap(queryString));
	}
	public static Object fetchGroup(String entityName, HashMap<String, String> queries) {
		return _fetchGroup(entityName, queries);
	}

	public static Object fetchOne(String entityName, String queryString) {
		return _fetchOne(entityName, queryMap(queryString));
	}
	public static Object fetchOne(String entityName, HashMap<String, String> queries) {
		return _fetchOne(entityName, queries);
	}
	
	
	
	
	private static Object _fetchOne(String entityName, HashMap<String, String> queries) {
		TokenizeParameter Parameters = new TokenizeParameter(entityName,queries);
		SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder().feed(Parameters).build();
		List list = (List) sqlSelectBuilder.fetchAll().getItems();
		if (list.isEmpty()) {
			return Collections.EMPTY_MAP;
		}else if(list.size()>1){
			throw new GetFormatException(8661, "查询异常", "查询对象大于1个");
		}else {
			return list.get(0);
		}
	}
	
	private static Object _fetchGroup(String entityName, HashMap<String, String> queries) {
		TokenizeParameter Parameters = new TokenizeParameter(entityName,queries);
		if (Parameters.getGroup() == null || "".equals(Parameters.getGroup())) {
			throw new GetFormatException(5771, "查询异常", "group接口必须包含group参数");
		}
		SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder().feed(Parameters).build();
		HTTPListResponse fetchAll = sqlSelectBuilder.fetchAll();
		return new HTTPListResponse(
				transformAsGroup((List) (fetchAll.getItems()), Parameters.getGroup().toString()),
				fetchAll.getTotal(), fetchAll.getPageNumber(), fetchAll.getPageSize());
	}
	
	private static Object _fetchTree(String entityName, HashMap<String, String> queries) {
		TokenizeParameter Parameters = new TokenizeParameter(entityName,queries);
		SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder().feed(Parameters).setIsTree(true)
				.build();
		HTTPListResponse fetchAll = sqlSelectBuilder.fetchAll();
		return new HTTPListResponse(TreeBuilder.bulid((List) (fetchAll.getItems())), fetchAll.getTotal(),fetchAll.getPageNumber(), fetchAll.getPageSize());
	}

	private static Object _fetchList(String entityName, HashMap<String, String> queries) {
		TokenizeParameter Parameters = new TokenizeParameter(entityName,queries);
		SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder().feed(Parameters).build();
		return sqlSelectBuilder.fetchAll();
	}

	private static long _fetchCount(String entityName, HashMap<String, String> queries) {
		TokenizeParameter Parameters = new TokenizeParameter(entityName,queries);
		SQLSelectBuilder sqlSelectBuilder = new SQLSelectBuilder().feed(Parameters).build();
		return sqlSelectBuilder.fetchCount();
	}


	private static Map transformAsGroup(List queryForList, String group) {
		String[] split = group.split("\\.");
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
		try {

			if (split.length == 1) {
				for (Object o : queryForList) {
					String groupValue = ((Map) o).get(group).toString();
					Object orDefault = linkedHashMap.getOrDefault(groupValue, new ArrayList<>());
					((List) orDefault).add(o);
					if (!linkedHashMap.containsKey(groupValue)) {
						linkedHashMap.put(groupValue, orDefault);
					}
				}

			} else if (split.length == 2) {

				for (Object o : queryForList) {
					if (((Map) o).get(split[0]) instanceof List) {
						throw new GetFormatException(6111, "查询异常", "分组对象" + split[0] + "不允许是列表");
					}
					if (((Map) ((Map) o).get(split[0])) == null) {
						continue;
					}
					if (((Map) o).get(split[0]) != null && ((Map) ((Map) o).get(split[0])).get(split[1]) != null) {
						String groupValue = ((Map) ((Map) o).get(split[0])).get(split[1]).toString();
						Object orDefault = linkedHashMap.getOrDefault(groupValue, new ArrayList<>());

						((List) orDefault).add(o);
						if (!linkedHashMap.containsKey(groupValue)) {
							linkedHashMap.put(groupValue, orDefault);
						}
					} else {
						String groupValue = " ";
						Object orDefault = linkedHashMap.getOrDefault(groupValue, new ArrayList<>());

						((List) orDefault).add(o);
						if (!linkedHashMap.containsKey(groupValue)) {
							linkedHashMap.put(groupValue, orDefault);
						}
					}
				}
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new GetFormatException(6789, "查询异常", split[0] + "不是对象");
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new GetFormatException(5588, "查询异常", "fields中必须包含group的列，且值不能为null");
		}
		return linkedHashMap;
	}
	private static HashMap<String, String> queryMap(String queryString) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (queryString != null && !"".equals(queryString)) {
			String[] splits = queryString.split("&");
			for (String s : splits) {
				String[] equalSplits = s.split("=");
				if (equalSplits.length == 2) {
					hashMap.putIfAbsent(equalSplits[0], equalSplits[1]);
				}
			}
		}
		return hashMap;
	}
}

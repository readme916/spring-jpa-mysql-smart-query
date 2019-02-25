package com.liyang.jpa.mysql.db.parser;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.liyang.jpa.mysql.config.JpaSmartQuerySupport;
import com.liyang.jpa.mysql.db.structure.EntityStructure;
import com.liyang.jpa.mysql.exception.GetFormatException;


public class OrderBy implements Expression {

	private EntityStructure mainEntityStructure;
	private InnerJoin innerJoin;
	private LeftJoin leftJoin;

	Set<String> exists = new HashSet<>();
	Set<String> orders = new LinkedHashSet<>();
	
	
	

	public OrderBy(InnerJoin innerJoin, LeftJoin leftJoin) {
		this.leftJoin = leftJoin;
		this.innerJoin = innerJoin;
	}

	public void add(String sort) {

		if (sort == null || "".equals(sort)) {
			return;
		}
		String property = sort.split(",")[0];
		String direction = " ASC ";
		if (sort.split(",").length == 1) {

		} else {
			direction = sort.split(",")[1];
		}

		String[] split = property.split("\\.");
		if (split.length == 1) {
			if (!mainEntityStructure.getSimpleFields().containsKey(split[0])) {
				throw new GetFormatException(6123, "查询异常", "实体对象" + mainEntityStructure.getName() + "不存在可比较属�??" + split[0]);
			}
			String tableName = mainEntityStructure.getName();
			String tablecolumn = mainEntityStructure.getSimpleFields().get(split[0]).getSimpleColumn();
			if (!exists.contains("`" + tableName + "`." + tablecolumn)) {
				exists.add("`" + tableName + "`." + tablecolumn);
				orders.add("`" + tableName + "`." + tablecolumn + " " + direction);
			}

		} else if (split.length == 2) {
			if (!mainEntityStructure.getObjectFields().containsKey(split[0])) {
				throw new GetFormatException(6212, "查询异常", "实体对象" + mainEntityStructure.getName() + "不存在属�?" + split[0]);
			}
			EntityStructure targetEntitystructure = JpaSmartQuerySupport
					.getStructure(mainEntityStructure.getObjectFields().get(split[0]).getTargetEntity());
			if (!targetEntitystructure.getSimpleFields().containsKey(split[1])) {
				throw new GetFormatException(8321, "查询异常", "实体对象" + targetEntitystructure.getName() + "不存在可比较属�??" + split[1]);
			}
			String alias = split[0];
			String tablecolumn = targetEntitystructure.getSimpleFields().get(split[1]).getSimpleColumn();
			if (!exists.contains("`" + alias + "`." + tablecolumn)) {
				exists.add("`" + alias + "`." + tablecolumn);
				orders.add("`" + alias + "`." + tablecolumn + " " + direction);
				if (leftJoin.getJoinTables().contains(alias) || innerJoin.getJoinTables().contains(alias)) {
				} else {
					innerJoin.add(split[0]);
				}
			}
		}

	}

	@Override
	public String sql() {
		// TODO Auto-generated method stub
		if (orders.isEmpty()) {
			return "";
		} else {
			return " ORDER BY " + String.join(",", orders);
		}

	}

	public Set<String> getExists() {
		return exists;
	}

	public void setExists(Set<String> exists) {
		this.exists = exists;
	}

	public Set<String> getOrders() {
		return orders;
	}

	public void setOrders(Set<String> orders) {
		this.orders = orders;
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;
	}

}
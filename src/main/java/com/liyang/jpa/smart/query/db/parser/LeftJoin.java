package com.liyang.jpa.smart.query.db.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.liyang.jpa.smart.query.db.SmartQuery;
import com.liyang.jpa.smart.query.db.structure.ColumnJoinType;
import com.liyang.jpa.smart.query.db.structure.ColumnStucture;
import com.liyang.jpa.smart.query.db.structure.EntityStructure;
import com.liyang.jpa.smart.query.exception.QueryException;



public class LeftJoin implements Expression {
	private EntityStructure mainEntityStructure;
	private Set<String> joinTables = new HashSet<String>();
	private ArrayList<String> arrayList = new ArrayList<String>();
	private InnerJoin innerJoin;

	public Set<String> getJoinTables() {
		return joinTables;
	}

	public LeftJoin(InnerJoin innerJoin) {
		this.innerJoin = innerJoin;
	}

	public void add(EntityStructure entityStructure, String column, String preColumn, String name) {
		String[] split = column.split("\\.");
		if (split.length == 1) {

			if ("".equals(column)||"*".equals(column)) {
				return;
			}
			if (!entityStructure.getObjectFields().containsKey(column)
					&& !entityStructure.getSimpleFields().containsKey(column)) {
				throw new QueryException("实体对象" + entityStructure.getName() + "不存在属性" + column);
			}
			if (entityStructure.getSimpleFields().containsKey(column)) {
				return;
			} else {
				String alias = null;
				if ("".equals(preColumn)) {
					alias = column;
				} else {
					alias = preColumn + "." + column;
				}

				if (joinTables.contains(alias) || innerJoin.getJoinTables().contains(alias)) {
					return;
				}
				ColumnStucture columnStucture = entityStructure.getObjectFields().get(column);
				Class<?> targetEntity = entityStructure.getObjectFields().get(column).getTargetEntity();
				EntityStructure targetEntityStructure = SmartQuery.getStructure(targetEntity);
				if (columnStucture.getJoinType().equals(ColumnJoinType.MANY_TO_MANY)) {
					innerJoin.getJoinTables().add(alias);
					String joinTableName = preColumn + "." + columnStucture.getJoinTable();
					if ("".equals(preColumn)) {
						preColumn = entityStructure.getName();
						joinTableName = name + "." + columnStucture.getJoinTable();
					}

					arrayList.add(" LEFT JOIN " + columnStucture.getJoinTable() + " AS `" + joinTableName + "` ON `"
							+ preColumn + "`.uuid" + " = `" + joinTableName + "`." + columnStucture.getJoinColumn()
							+ " LEFT JOIN " + targetEntityStructure.getTableName() + " AS `" + alias + "`" + " ON `"
							+ alias + "`.uuid = `" + joinTableName + "`." + columnStucture.getInverseJoinColumn());

				} else {

					joinTables.add(alias);
					arrayList.add(" LEFT JOIN " + targetEntityStructure.getTableName() + " AS `" + alias + "`"
							+ on(entityStructure, column, preColumn));
				}
			}

		} else if (split.length == 2) {
			add(entityStructure, split[0], preColumn, name);
			Class<?> targetEntity = entityStructure.getObjectFields().get(split[0]).getTargetEntity();
			EntityStructure classtostructure = SmartQuery.getStructure(targetEntity);
			if (classtostructure.getObjectFields().containsKey(split[1])) {
				add(classtostructure, split[1], split[0], name);
			}
		}
	}

	private String on(EntityStructure entityStructure, String column, String preColumn) {
		String alias;
		if ("".equals(preColumn)) {
			preColumn = entityStructure.getName();
			alias = column;
		} else {
			alias = preColumn + "." + column;
		}
		String ret = "";
		ColumnStucture columnStucture = entityStructure.getObjectFields().get(column);
		if (columnStucture.getMappedBy()==null) {
			ret = " ON `" + alias + "`.uuid = `" + preColumn + "`."
					+ entityStructure.getObjectFields().get(column).getJoinColumn();
		} else {
			ret = " ON `" + alias + "`." + entityStructure.getObjectFields().get(column).getJoinColumn() + " = `"
					+ preColumn + "`.uuid";
		}

		return ret;
	}

	@Override
	public String sql() {

		return String.join(" ", arrayList);
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}
}
package com.liyang.jpa.mysql.db.parser;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.liyang.jpa.mysql.config.JpaSmartQuerySupport;
import com.liyang.jpa.mysql.db.structure.ColumnStucture;
import com.liyang.jpa.mysql.db.structure.EntityStructure;
import com.liyang.jpa.mysql.exception.GetFormatException;


public class Select implements Expression {

	private EntityStructure mainEntityStructure;
	private HashSet<String> selects = new HashSet<String>();

	public void add(EntityStructure entityStructure, String column, String preColumn) {

		String[] split = column.split("\\.");

		if (split.length == 1) {

			String alias;
			if ("".equals(preColumn)) {
				alias = column;
			} else {
				alias = preColumn + "." + column;
			}

			if ("*".equals(column)) {
				Map<String, ColumnStucture> simpleFields = entityStructure.getSimpleFields();
				Set<Entry<String, ColumnStucture>> entrySet = simpleFields.entrySet();

				if (preColumn.equals("")) {
					for (Entry<String, ColumnStucture> entry : entrySet) {
						selects.add("`" + entityStructure.getName() + "`." + entry.getValue().getSimpleColumn()
								+ " AS `" + entry.getKey() + "`");
					}
				} else {
					for (Entry<String, ColumnStucture> entry : entrySet) {
						selects.add("`" + preColumn + "`." + entry.getValue().getSimpleColumn() + " AS `"
								+ preColumn + "." + entry.getKey() + "`");
					}
				}

			} else {
				if (entityStructure.getObjectFields().containsKey(column)) {
					ColumnStucture columnStucture = entityStructure.getObjectFields().get(column);
					EntityStructure targetEntityStructure = JpaSmartQuerySupport
							.getStructure(columnStucture.getTargetEntity());
					Set<Entry<String, ColumnStucture>> entrySet = targetEntityStructure.getSimpleFields()
							.entrySet();
					for (Entry<String, ColumnStucture> entry : entrySet) {
						selects.add("`" + alias + "`" + "." + entry.getValue().getSimpleColumn() + " AS `" + alias
								+ "." + entry.getKey() + "`");
					}
				} else if (entityStructure.getSimpleFields().containsKey(column)) {
					ColumnStucture columnStucture = entityStructure.getSimpleFields().get(column);
					if (preColumn.equals("")) {
						selects.add("`" + entityStructure.getName() + "`." + columnStucture.getSimpleColumn()
								+ " AS `" + column + "`");
					} else {
						selects.add("`" + preColumn + "`." + columnStucture.getSimpleColumn() + " AS `" + preColumn
								+ "." + column + "`");
					}

				} else {
					throw new GetFormatException(6732, "查询异常", "实体类：" + entityStructure.getTableName() + "，没有属�?:" + column);
				}
			}

		} else if (split.length == 2) {

			// add(entityStructure, split[0], "");
			Class<?> targetEntity = entityStructure.getObjectFields().get(split[0]).getTargetEntity();
			EntityStructure classtostructure = JpaSmartQuerySupport.getStructure(targetEntity);
			add(classtostructure, "*", split[0]);
			add(classtostructure, split[1], split[0]);
		}

	}

	@Override
	public String sql() {
		return "SELECT " + String.join(",", selects);
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}
}
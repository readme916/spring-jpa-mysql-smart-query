package com.liyang.jpa.mysql.db.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.liyang.jpa.mysql.config.JpaSmartQuerySupport;
import com.liyang.jpa.mysql.db.structure.ColumnJoinType;
import com.liyang.jpa.mysql.db.structure.ColumnStucture;
import com.liyang.jpa.mysql.db.structure.EntityStructure;


public class InnerJoin implements Expression {
	private EntityStructure mainEntityStructure;
	private Set<String> joinTables = new HashSet<String>();
	private ArrayList<String> arrayList = new ArrayList<String>();

	public Set<String> getJoinTables() {
		return joinTables;
	}

	@Override
	public String sql() {
		return String.join(" ", arrayList);
	}

	public void add(String alias) {

		if (joinTables.contains(alias)) {
			return;
		}
		joinTables.add(alias);
		EntityStructure targetTableStructure = JpaSmartQuerySupport
				.getStructure(mainEntityStructure.getObjectFields().get(alias).getTargetEntity());
		ColumnStucture columnStucture = mainEntityStructure.getObjectFields().get(alias);
		if (columnStucture.getJoinType().equals(ColumnJoinType.MANY_TO_MANY)) {

			arrayList.add(" INNER JOIN " + columnStucture.getJoinTable() + " ON `" + mainEntityStructure.getName()
					+ "`.uuid" + " = `" + columnStucture.getJoinTable() + "`." + columnStucture.getJoinColumn()
					+ " INNER JOIN " + targetTableStructure.getTableName() + " AS `" + alias + "`" + " ON `" + alias
					+ "`.uuid = `" + columnStucture.getJoinTable() + "`." + columnStucture.getInverseJoinColumn());

		} else {
			arrayList.add(" INNER JOIN " + targetTableStructure.getTableName() + " AS `" + alias + "`" + on(alias));
		}

	}

	private String on(String split0) {

		String ret = "";
		ColumnStucture columnStucture = mainEntityStructure.getObjectFields().get(split0);
		if (columnStucture.getMappedBy()==null) {
			ret = " ON `" + split0 + "`.uuid = `" + mainEntityStructure.getName() + "`."
					+ mainEntityStructure.getObjectFields().get(split0).getJoinColumn();
		} else {
			ret = " ON `" + split0 + "`." + mainEntityStructure.getObjectFields().get(split0).getJoinColumn()
					+ " = `" + mainEntityStructure.getName() + "`.uuid";
		}

		return ret;
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}

}
package com.liyang.jpa.mysql.db.parser;

import com.liyang.jpa.mysql.db.structure.EntityStructure;

public class From implements Expression {

	private EntityStructure mainEntityStructure;

	@Override
	public String sql() {
		// TODO Auto-generated method stub
		return " FROM " + mainEntityStructure.getTableName() + " AS " + mainEntityStructure.getName();
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}
}
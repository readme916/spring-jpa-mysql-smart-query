package com.liyang.jpa.smart.query.db.parser;

import com.liyang.jpa.smart.query.db.structure.EntityStructure;

public class From implements Expression {

	private EntityStructure mainEntityStructure;

	@Override
	public String sql() {
		// TODO Auto-generated method stub
		return " FROM " + mainEntityStructure.getTableName() + " AS `" + mainEntityStructure.getName()+"`";
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}
}
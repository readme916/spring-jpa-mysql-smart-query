package com.liyang.jpa.mysql.db.parser;

import com.liyang.jpa.mysql.db.structure.EntityStructure;

public class GroupBy implements Expression {

	private EntityStructure mainEntityStructure;

	@Override
	public String sql() {
		return " GROUP BY `" + mainEntityStructure.getName() + "`.uuid";
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		// TODO Auto-generated method stub
		this.mainEntityStructure = mainEntityStructure;
	}

}
package com.liyang.jpa.mysql.db.parser;

import com.liyang.jpa.mysql.db.structure.EntityStructure;

public class Limit implements Expression {

	private EntityStructure mainEntityStructure;

	@Override
	public String sql() {

		return " LIMIT :pageStart , :pageSize";
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;
	}

}
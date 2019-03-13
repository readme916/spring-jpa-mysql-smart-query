package com.liyang.jpa.smart.query.db.parser;

import com.liyang.jpa.smart.query.db.structure.EntityStructure;

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
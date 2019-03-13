package com.liyang.jpa.smart.query.db.parser;

import com.liyang.jpa.smart.query.db.structure.EntityStructure;

public interface Expression {

	public String sql();

	public void setMainEntityStructure(EntityStructure mainEntityStructure);
}
package com.liyang.jpa.mysql.db.parser;

import com.liyang.jpa.mysql.db.structure.EntityStructure;

public interface Expression {

	public String sql();

	public void setMainEntityStructure(EntityStructure mainEntityStructure);
}
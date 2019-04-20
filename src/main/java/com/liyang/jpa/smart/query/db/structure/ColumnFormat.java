package com.liyang.jpa.smart.query.db.structure;

import java.lang.reflect.Field;
import java.util.Date;


public enum ColumnFormat{
	DATE,STRING,INTEGER,BOOLEAN,DOUBLE,OBJECT,ENUM;
	
	public static ColumnFormat parseFormat(Field declaredField) {
		if (declaredField.getType().equals(String.class)) {
			return STRING;
		} else if (declaredField.getType().equals(Date.class)) {
			return DATE;
		} else if (declaredField.getType().equals(Integer.class) || declaredField.getType().equals(Long.class)) {
			return INTEGER;
		} else if (declaredField.getType().equals(Boolean.class)) {
			return BOOLEAN;
		} else if (declaredField.getType().equals(Double.class) || declaredField.getType().equals(Float.class)) {
			return DOUBLE;
		} else if(declaredField.getType().isEnum()) {
			return ENUM;
		}
		return null;
	}

}
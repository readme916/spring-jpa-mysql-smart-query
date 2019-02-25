package com.liyang.jpa.mysql.exception;

/**
 * 启动时jpa的entity的数据字段格式检查时候的异常
 * @author liyang
 *
 */
public class StructureException extends RuntimeException {

	public StructureException(String string) {
		super(string);
	}

}

package com.liyang.jpa.smart.query.db.structure;

public class ColumnStucture {
	private ColumnFormat format;
	private String mappedBy;
	private ColumnJoinType joinType;
	private Class<?> targetEntity;
	private String joinTable;
	private String joinColumn;
	private String inverseJoinColumn;
	private String simpleColumn;

	public ColumnStucture(ColumnFormat format, ColumnJoinType joinType, String mappedBy, Class<?> targetEntity,
			String joinTable, String joinColumn, String inverseJoinColumn, String simpleColumn) {
		super();
		this.format = format;
		this.mappedBy = mappedBy;
		this.joinType = joinType;
		this.targetEntity = targetEntity;
		this.joinTable = joinTable;
		this.joinColumn = joinColumn;
		this.inverseJoinColumn = inverseJoinColumn;
		this.simpleColumn = simpleColumn;
	}

	public String getMappedBy() {
		return mappedBy;
	}

	public void setMappedBy(String mappedBy) {
		this.mappedBy = mappedBy;
	}

	public ColumnFormat getFormat() {
		return format;
	}

	public void setFormat(ColumnFormat format) {
		this.format = format;
	}

	public ColumnJoinType getJoinType() {
		return joinType;
	}

	public void setJoinType(ColumnJoinType joinType) {
		this.joinType = joinType;
	}

	public Class<?> getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(Class<?> targetEntity) {
		this.targetEntity = targetEntity;
	}

	public String getJoinTable() {
		return joinTable;
	}

	public void setJoinTable(String joinTable) {
		this.joinTable = joinTable;
	}

	public String getJoinColumn() {
		return joinColumn;
	}

	public void setJoinColumn(String joinColumn) {
		this.joinColumn = joinColumn;
	}

	public String getInverseJoinColumn() {
		return inverseJoinColumn;
	}

	public void setInverseJoinColumn(String inverseJoinColumn) {
		this.inverseJoinColumn = inverseJoinColumn;
	}

	public String getSimpleColumn() {
		return simpleColumn;
	}

	public void setSimpleColumn(String simpleColumn) {
		this.simpleColumn = simpleColumn;
	}


}

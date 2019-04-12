package com.liyang.jpa.smart.query.db.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EntityStructure {

	private String name;
	
	private String tableName;
	
	private Class<?> entityClass;
	
	@JsonIgnore
	private JpaRepository jpaRepository;
	
	private Map<String, ColumnStucture> simpleFields = new HashMap();
	
	private Map<String, ColumnStucture> objectFields = new HashMap();


	public JpaRepository getJpaRepository() {
		return jpaRepository;
	}

	public void setJpaRepository(JpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<?> cls) {
		this.entityClass = cls;
	}

	public Map<String, ColumnStucture> getSimpleFields() {
		return simpleFields;
	}

	public void setSimpleFields(Map<String, ColumnStucture> simpleFields) {
		this.simpleFields = simpleFields;
	}

	public Map<String, ColumnStucture> getObjectFields() {
		return objectFields;
	}

	public void setObjectFields(Map<String, ColumnStucture> objectFields) {
		this.objectFields = objectFields;
	}


}

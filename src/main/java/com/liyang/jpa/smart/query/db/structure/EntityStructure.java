package com.liyang.jpa.smart.query.db.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Transient;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class EntityStructure {

	private String name;
	
	private String tableName;
	
	private Class<?> entityClass;
	
	private EntityType type;
	
	@JsonIgnore
	private JpaRepository jpaRepository;
	
	private Map<String, ColumnStucture> simpleFields = new HashMap();
	
	private Map<String, ColumnStucture> objectFields = new HashMap();
	
	private Map<String, ColumnStucture> transientFields = new HashMap();


	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public Map<String, ColumnStucture> getTransientFields() {
		return transientFields;
	}

	public void setTransientFields(Map<String, ColumnStucture> transientFields) {
		this.transientFields = transientFields;
	}

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

package com.liyang.jpa.mysql.config;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liyang.jpa.mysql.annotation.EnableJpaSmartQuery;
import com.liyang.jpa.mysql.db.structure.ColumnFormat;
import com.liyang.jpa.mysql.db.structure.ColumnJoinType;
import com.liyang.jpa.mysql.db.structure.ColumnStucture;
import com.liyang.jpa.mysql.db.structure.EntityStructure;
import com.liyang.jpa.mysql.db.structure.Stopword;
import com.liyang.jpa.mysql.exception.GetFormatException;
import com.liyang.jpa.mysql.exception.StructureException;


@EnableCaching
@ComponentScan("com.liyang.jpa.mysql.service")
public class JpaSmartQuerySupport {

	protected final static Logger logger = LoggerFactory.getLogger(JpaSmartQuerySupport.class);

	private final static HashMap<String, EntityStructure> nameToStructure = new HashMap();

	private final static HashMap<Class<?>, EntityStructure> classToStructure = new HashMap();



	public static EntityStructure getStructure(String name) {
		if (nameToStructure.containsKey(name)) {
			return nameToStructure.get(name);
		} else {
			throw new GetFormatException(7172, "结构异常", "没有这个实体" + name);
		}
	}

	public static EntityStructure getStructure(Class<?> clz) {
		if (classToStructure.containsKey(clz)) {
			return classToStructure.get(clz);
		} else {
			throw new GetFormatException(7174, "结构异常", "没有这个实体" + clz.getSimpleName());
		}
	}

	public static HashMap<String, EntityStructure> getNametostructure() {
		return nameToStructure;
	}

	public static HashMap<Class<?>, EntityStructure> getClasstostructure() {
		return classToStructure;
	}





	

}

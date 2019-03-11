package com.liyang.jpa.mysql.config;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

import com.liyang.jpa.mysql.db.structure.EntityStructure;
import com.liyang.jpa.mysql.exception.GetFormatException;


@EnableCaching
@ComponentScan("com.liyang.jpa.mysql.service")
public class JpaSmartQuerySupport {

	protected final static Logger logger = LoggerFactory.getLogger(JpaSmartQuerySupport.class);

	private static HashMap<String, EntityStructure> nameToStructure = new HashMap();

	private static HashMap<Class<?>, EntityStructure> classToStructure = new HashMap();



	public static EntityStructure getStructure(String name) {
		if (JpaSmartQuerySupport.nameToStructure.containsKey(name)) {
			return JpaSmartQuerySupport.nameToStructure.get(name);
		} else {
			throw new GetFormatException(7172, "结构异常", "没有这个实体" + name);
		}
	}

	public static EntityStructure getStructure(Class<?> clz) {
		if (JpaSmartQuerySupport.classToStructure.containsKey(clz)) {
			return JpaSmartQuerySupport.classToStructure.get(clz);
		} else {
			throw new GetFormatException(7174, "结构异常", "没有这个实体" + clz.getSimpleName());
		}
	}

	public static HashMap<String, EntityStructure> getNametostructure() {
		return JpaSmartQuerySupport.nameToStructure;
	}

	public static HashMap<Class<?>, EntityStructure> getClasstostructure() {
		return JpaSmartQuerySupport.classToStructure;
	}





	

}

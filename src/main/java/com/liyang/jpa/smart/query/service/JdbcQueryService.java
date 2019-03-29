package com.liyang.jpa.smart.query.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcQueryService implements ApplicationContextAware{

	private static NamedParameterJdbcTemplate jdbcTemplate;
	private static ApplicationContext applicationContext;
	
	@Autowired
	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcQueryService.jdbcTemplate = jdbcTemplate;
	}



	public static List<Map<String, Object>> query(String sql, Map<String, Object> preparedParams){
		
		if(jdbcTemplate==null) {
			jdbcTemplate = JdbcQueryService.applicationContext.getBean(NamedParameterJdbcTemplate.class);
		}
		return  jdbcTemplate.queryForList(sql , preparedParams);
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		JdbcQueryService.applicationContext = applicationContext;
		
	}
}

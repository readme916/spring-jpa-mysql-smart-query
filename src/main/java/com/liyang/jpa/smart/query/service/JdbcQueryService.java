package com.liyang.jpa.smart.query.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class JdbcQueryService {

	private static NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
		JdbcQueryService.jdbcTemplate = jdbcTemplate;
	}



	public static List<Map<String, Object>> query(String sql, Map<String, Object> preparedParams){
		return  jdbcTemplate.queryForList(sql , preparedParams);
	}
}

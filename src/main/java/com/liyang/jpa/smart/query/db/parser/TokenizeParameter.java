package com.liyang.jpa.smart.query.db.parser;

import java.util.HashMap;

import com.liyang.jpa.smart.query.db.parser.Tokenizer.Token;


public class TokenizeParameter {

	private String entityName;
	private String group;
	private String fields = "*";
	private String sort;
	private long page = 0;
	private long size = 20;
	private HashMap<String, Token> tokens = new HashMap<String, Token>();

	public TokenizeParameter(String entityName, HashMap<String, String> queries) {
		this.entityName = entityName;

		if (queries == null || queries.isEmpty()) {
			return;
		}
		queries = new HashMap<String,String>(queries);
		this.sort = queries.remove("sort");
		String page = queries.remove("page");
		if (page != null) {
			this.page = Long.valueOf(page);
		}
		String size = queries.remove("size");
		if (size != null) {
			this.size = Long.valueOf(size);
		}
		String fields = queries.remove("fields");
		if (fields != null && !"".equals(fields)) {
			this.fields = fields;
		}
		String group = queries.remove("group");
		if (group != null && !"".equals(group)) {
			this.group = group;
		}

		this.tokens = Tokenizer.token(queries);

	}

	public HashMap<String, Token> getTokens() {
		return tokens;
	}

	public void setTokens(HashMap<String, Token> tokens) {
		this.tokens = tokens;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}


}

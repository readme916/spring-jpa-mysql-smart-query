package com.liyang.jpa.smart.query.db.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;

public class Tokenizer {

	public static HashMap<String, Token> token(Map<String, String> parameters) {
		HashMap hashMap = new HashMap<String, Token>();

		Set<Entry<String, String>> entrySet = parameters.entrySet();
		for (Entry<String, String> entry : entrySet) {
			Token token = _token(entry);
			hashMap.put(token.getOrigin(), token);
		}
		return hashMap;
	}

	private static Token _token(Entry<String, String> entry) {
		String key = entry.getKey();

		Object value;
//		try {
//			value = Integer.valueOf(entry.getValue());
//		} catch (NumberFormatException e) {
//			value = entry.getValue();
//		}

		value = entry.getValue();
		
		Token token = new Token();
		token.setOrigin(key);
		if (key.endsWith("[gt]")) {
			token.setRelationShip(RelationShip.GT);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol(">");
			token.setValue(value);
		} else if (key.endsWith("[gte]")) {
			token.setRelationShip(RelationShip.GTE);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol(">=");
			token.setValue(value);
		} else if (key.endsWith("[lt]")) {
			token.setRelationShip(RelationShip.LT);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("<");
			token.setValue(value);
		} else if (key.endsWith("[lte]")) {
			token.setRelationShip(RelationShip.LTE);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("<=");
			token.setValue(value);
		} else if (key.endsWith("[not]")) {
			token.setRelationShip(RelationShip.NOT);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("<>");
			token.setValue(value);
		} else if (key.endsWith("[in]")) {
			token.setRelationShip(RelationShip.IN);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("in");
			token.setValue(Arrays.asList(value.toString().split(",")));
		} else if (key.endsWith("[like]")) {
			token.setRelationShip(RelationShip.LIKE);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("like");
			token.setValue("%" + value + "%");
		} else if (key.endsWith("[or]")) {
			token.setRelationShip(RelationShip.OR);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("in");
			token.setValue(Arrays.asList(value.toString().split(",")));
		} else if (key.endsWith("[eq]")) {
			token.setRelationShip(RelationShip.EQ);
			token.setKey(key.substring(0, key.indexOf("[")));
			token.setSymbol("=");
			token.setValue(value);
		} else {
			if (value.toString().indexOf(",") != -1) {
				token.setKey(key);
				token.setRelationShip(RelationShip.IN);
				token.setSymbol("in");
				token.setValue(Arrays.asList(value.toString().split(",")));
			} else {
				token.setRelationShip(RelationShip.EQ);
				token.setKey(key);
				token.setSymbol("=");
				token.setValue(value);
			}
		}

		return token;
	}

	public static class Token {
		private String origin;
		private String key;
		private RelationShip relationShip;
		private String symbol;
		private Object value;

		public String getOrigin() {
			return origin;
		}

		public void setOrigin(String origin) {
			this.origin = origin;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public RelationShip getRelationShip() {
			return relationShip;
		}

		public void setRelationShip(RelationShip relationShip) {
			this.relationShip = relationShip;
		}

		public boolean isSimple() {
			if (relationShip.equals(RelationShip.EQ) || relationShip.equals(RelationShip.GT)
					|| relationShip.equals(RelationShip.LT) || relationShip.equals(RelationShip.NOT)
					|| relationShip.equals(RelationShip.LTE) || relationShip.equals(RelationShip.GTE)) {
				return true;
			} else {
				return false;
			}
		}

		public boolean isLike() {
			return relationShip.equals(RelationShip.LIKE);
		}

		public boolean isIn() {
			return relationShip.equals(RelationShip.IN);
		}

		public boolean isOr() {
			return relationShip.equals(RelationShip.OR);
		}
	}

	public enum RelationShip {
		EQ, GT, GTE, LT, LTE, NOT, IN, LIKE, OR
	}
}

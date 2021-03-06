package com.liyang.jpa.smart.query.db.parser;

import java.util.HashSet;

import com.liyang.jpa.smart.query.db.SmartQuery;
import com.liyang.jpa.smart.query.db.parser.Tokenizer.Token;
import com.liyang.jpa.smart.query.db.structure.EntityStructure;
import com.liyang.jpa.smart.query.exception.QueryException;



public class Where implements Expression {

	private EntityStructure mainEntityStructure;
	private HashSet<String> orConditions = new HashSet<String>();
	private HashSet<String> andConditions = new HashSet<String>();
	private InnerJoin innerJoin;

	public Where(InnerJoin innerJoin) {
		this.innerJoin = innerJoin;
	}

	public void add(Token token) {
		String[] split = token.getKey().split("\\.");

		if (split.length == 1) {

			if (!mainEntityStructure.getSimpleFields().containsKey(token.getKey())) {
				throw new QueryException("实体对象" + mainEntityStructure.getName() + "不存在普通属性" + token.getKey());
			}
			String tableAlias = mainEntityStructure.getName();
			String columnName = mainEntityStructure.getSimpleFields().get(token.getKey()).getSimpleColumn();

			if (token.isSimple() || token.isLike()) {
				andConditions.add(
						"`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " :" + token.getOrigin());
			} else if (token.isIn()) {
				andConditions.add("`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " (:"
						+ token.getOrigin() + ")");
			} else if (token.isOr()) {
				orConditions.add("`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " (:"
						+ token.getOrigin() + ")");
			}

		} else if (split.length == 2) {
			if (!mainEntityStructure.getObjectFields().containsKey(split[0])) {
				throw new QueryException("实体对象" + mainEntityStructure.getName() + "不存在对象属性" + split[0]);
			}
			EntityStructure targetClassstructure = SmartQuery
					.getStructure(mainEntityStructure.getObjectFields().get(split[0]).getTargetEntity());
			if (!targetClassstructure.getSimpleFields().containsKey(split[1])) {
				throw new QueryException("实体对象" + targetClassstructure.getName() + "不存在普通属性" + split[1]);
			}
			String tableAlias = split[0];
			String columnName = targetClassstructure.getSimpleFields().get(split[1]).getSimpleColumn();

			if (token.isSimple() || token.isLike()) {
				andConditions.add(
						"`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " :" + token.getOrigin());
			} else if (token.isIn()) {
				andConditions.add("`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " (:"
						+ token.getOrigin() + ")");
			} else if (token.isOr()) {
				orConditions.add("`" + tableAlias + "`." + columnName + " " + token.getSymbol() + " (:"
						+ token.getOrigin() + ")");
			}

			if (!innerJoin.getJoinTables().contains(tableAlias)) {
				innerJoin.add(tableAlias);
			}
		}
	}

	@Override
	public String sql() {
		String ret = " WHERE  1 ";
		for (String string : andConditions) {
			ret += " AND " + string;
		}
		for (String str : orConditions) {
			ret += " OR " + str;
		}
		return ret;
	}

	@Override
	public void setMainEntityStructure(EntityStructure mainEntityStructure) {
		this.mainEntityStructure = mainEntityStructure;

	}

}
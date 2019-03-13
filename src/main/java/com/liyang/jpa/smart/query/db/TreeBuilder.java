package com.liyang.jpa.smart.query.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

class TreeBuilder {

	/**
	 * 两层循环实现建树
	 * 
	 * @param treeNodes
	 *            传入的树节点列表
	 * @return
	 */
	public static List<Map> bulid(List<Map> treeNodes) {

//		for (Map treeNode : treeNodes) {
//			treeNode.remove("children");
//		}

		List<Map> trees = new ArrayList<Map>();
		for (Map level1 : treeNodes) {
			for (Map level2 : treeNodes) {
				if (level2.get("parent")!=null && ((Map) level2.get("parent")).get("uuid") !=null && ((Map) level2.get("parent")).get("uuid").toString().equals(level1.get("uuid").toString())) {
					if (!level1.containsKey("children")) {
						level1.put("children", new ArrayList<>());
					}
					((List) level1.get("children")).add(level2);
					level2.put("hasParent", true);
				}
			}
		}
		for (Map map : treeNodes) {
			if(map.get("hasParent")==null) {
				trees.add(map);
			}else{
				map.remove("hasParent");
			}
		}
		return trees;
	}

}
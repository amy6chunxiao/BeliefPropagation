package com.cqupt.bp.model;

import java.util.ArrayList;
import java.util.List;

public class BayesNetwork extends ArrayList<PearlNode> {

	/**
	 * 找到网络中所有根节点
	 * 
	 * @return
	 */
	public List<PearlNode> getRoots() {
		List<PearlNode> roots = new ArrayList<PearlNode>();
		for (PearlNode node : this) {
			if (node.getParents().size() == 0)
				roots.add(node);
		}
		return roots;
	}
}

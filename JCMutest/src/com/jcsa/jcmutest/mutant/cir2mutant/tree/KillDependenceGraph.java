package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;

public class KillDependenceGraph {
	
	/* definitions */
	private Map<SymCondition, KillDependenceNode> nodes;
	/**
	 * create an empty graph
	 */
	public KillDependenceGraph() {
		this.nodes = new HashMap<SymCondition, KillDependenceNode>();
	}
	
	/* getters */
	public int size() { return this.nodes.size(); }
	public Iterable<KillDependenceNode> get_nodes() { return this.nodes.values(); }
	public Iterable<SymCondition> get_conditions() { return this.nodes.keySet(); }
	public KillDependenceNode get_node(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			if(!this.nodes.containsKey(condition)) {
				this.nodes.put(condition, new KillDependenceNode(this, condition));
			}
			return this.nodes.get(condition);
		}
	}
	public void clear() { this.nodes.clear(); }
	
}

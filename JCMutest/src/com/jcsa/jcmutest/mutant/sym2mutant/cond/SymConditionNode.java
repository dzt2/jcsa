package com.jcsa.jcmutest.mutant.sym2mutant.cond;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcparse.lang.irlang.graph.CirExecution;


/**
 * It represents an instance representing a symbolic condition used in killing process.
 *
 * @author yukimula
 *
 */
public class SymConditionNode {

	/* definitions */
	/** the hierarchical tree where the node is created **/
	private SymConditionTree tree;
	/** the symbolic condition that defines this node in the tree uniquely **/
	private SymCondition condition;
	/** the edges pointing to the next condition nodes that it depends on **/
	private List<SymConditionNode> children;

	/* constructor */
	/**
	 * @param tree
	 * @param condition
	 * @throws Exception
	 */
	protected SymConditionNode(SymConditionTree tree, SymCondition condition) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			this.tree = tree;
			this.condition = condition;
			this.children = new ArrayList<>();
		}
	}

	/* getters */
	/**
	 * @return the hierarchical tree where the node is created
	 */
	public SymConditionTree get_tree() { return this.tree; }
	/**
	 * @return the symbolic condition that defines this node in the tree uniquely
	 */
	public SymCondition get_condition() { return this.condition; }
	/**
	 * @return the execution point where the condition is evaluated on
	 */
	public CirExecution get_execution() { return this.condition.get_execution(); }
	/**
	 * @return the edges pointing to the next condition nodes that it depends on
	 */
	public Iterable<SymConditionNode> get_children() { return this.children; }
	/**
	 * @param condition
	 * @return create a child w.r.t. condition inputs
	 * @throws Exception
	 */
	protected SymConditionNode add_child(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			/* find the existing assent nodes from this one */
			Queue<SymConditionNode> queue = new LinkedList<>();
			Set<SymConditionNode> records = new HashSet<>();
			queue.add(this); records.add(this); SymConditionNode node;
			while(!queue.isEmpty()) {
				node = queue.poll();
				if(node.condition.equals(condition)) {
					return node;
				}
				else {
					for(SymConditionNode child : node.children) {
						if(!records.contains(child)) {
							records.add(child); queue.add(child);
						}
					}
				}
			}

			/* update the new child w.r.t. input condition in this node */
			SymConditionNode child = this.tree.get_node(condition);
			this.children.add(child); return child;
		}
	}
	public boolean is_leaf() { return this.children.isEmpty(); }

}

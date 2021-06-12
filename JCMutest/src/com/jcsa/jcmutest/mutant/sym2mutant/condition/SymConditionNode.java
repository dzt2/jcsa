package com.jcsa.jcmutest.mutant.sym2mutant.condition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * It represents a unique instance of a symbolic condition in the tree hierarchical model.
 * 
 * @author yukimula
 *
 */
public class SymConditionNode {
	
	/* attributes */
	/** the tree where this node is created **/
	private SymConditionTree tree;
	/** the condition this node represented **/
	private SymCondition condition;
	/** the children on which this node relies **/
	private List<SymConditionNode> children;
	/** the sequence of evaluation results **/
	private List<Boolean> results;
	
	/* constructor */
	/**
	 * create an empty node w.r.t. the given condition in the tree hierarchy
	 * @param tree
	 * @param condition
	 * @throws Exception
	 */
	protected SymConditionNode(SymConditionTree tree, SymCondition condition) throws Exception {
		if(tree == null) {
			throw new IllegalArgumentException("Invalid tree as null");
		}
		else if(condition == null) {
			throw new IllegalArgumentException("Invalid condition as null");
		}
		else {
			this.tree = tree;
			this.condition = condition;
			this.children = new ArrayList<SymConditionNode>();
			this.results = new ArrayList<Boolean>();
		}
	}
	
	/* getters */
	/**
	 * @return the tree where this node is created
	 */
	public SymConditionTree get_tree() { return this.tree; }
	/**
	 * @return the condition this node represented
	 */
	public SymCondition get_condition() { return this.condition; }
	/**
	 * @return the children on which this node relies
	 */
	public Iterable<SymConditionNode> get_children() { return this.children; }
	/**
	 * @return the sequence of evaluation results
	 */
	public Iterable<Boolean> get_evaluation_results() { return this.results; }
	
	/* implifier */
	/**
	 * @return [executes, accepts, rejects]
	 */
	public int[] count_results() {
		int executes = 0, accepts = 0, rejects = 0;
		for(Boolean result : this.results) {
			if(result != null) {
				if(result.booleanValue()) {
					accepts++;
				}
				else {
					rejects++;
				}
			}
			executes++;
		}
		return new int[] { executes, accepts, rejects };
	}
	/**
	 * @return whether the node has been evaluated
	 */
	public boolean is_executed() { return !this.results.isEmpty(); }
	/**
	 * @return whether the node is accepted for once
	 */
	public boolean is_accepted() {
		for(Boolean result : this.results) {
			if(result != null && result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the node can be accepted
	 */
	public boolean is_acceptable() {
		for(Boolean result : this.results) {
			if(result == null || result.booleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return whether the node is a leaf in the tree
	 */
	public boolean is_leaf() {
		for(SymConditionNode child : this.children) {
			if(child.tree == this.tree) {
				return false;
			}
		}
		return true;
	}
	
	/* setters */
	/**
	 * clear the evaluation results accumulated by now
	 */
	protected void clear_results() { this.results.clear(); }
	/**
	 * accumulate the evaluation result to this node
	 * @param result
	 */
	protected void add_result(Boolean result) { this.results.add(result); }
	/**
	 * @param condition
	 * @return the existing node if it exists or create a new child.
	 * @throws Exception
	 */
	protected SymConditionNode add_child(SymCondition condition) throws Exception {
		if(condition == null) {
			throw new IllegalArgumentException("Invalid condition: null");
		}
		else {
			Queue<SymConditionNode> queue = new LinkedList<SymConditionNode>();
			Set<SymConditionNode> records = new HashSet<SymConditionNode>();
			
			queue.add(this); records.add(this);
			while(!queue.isEmpty()) {
				SymConditionNode node = queue.poll();
				if(node.condition.equals(condition)) {
					return node;
				}
				else {
					for(SymConditionNode child : node.children) {
						if(!records.contains(child)) {
							records.add(child);
							queue.add(child);
						}
					}
				}
			}
			
			SymConditionNode child = this.tree.get_node(condition);
			this.children.add(child); 
			return child;
		}
	}

}

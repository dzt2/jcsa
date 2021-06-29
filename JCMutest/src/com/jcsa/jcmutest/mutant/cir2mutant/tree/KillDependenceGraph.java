package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.HashMap;
import java.util.Map;

import com.jcsa.jcmutest.mutant.MutantSpace;
import com.jcsa.jcmutest.mutant.cir2mutant.base.SymCondition;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;

public class KillDependenceGraph {
	
	/* definitions */
	/** the space of mutants on which the graph serves for **/
	private MutantSpace mutant_space;
	/** mapping from symbolic conditions to the node it refers **/
	private Map<SymCondition, KillDependenceNode> nodes;
	
	/* constructor */
	/**
	 * create an empty graph
	 */
	private KillDependenceGraph(MutantSpace mutant_space) throws IllegalArgumentException {
		if(mutant_space == null) {
			throw new IllegalArgumentException("Invalid mutant_space: null");
		}
		else {
			this.mutant_space = mutant_space;
			this.nodes = new HashMap<SymCondition, KillDependenceNode>();
		}
	}
	
	/* getters */
	/**
	 * @return the space of mutants on which the graph serves for
	 */
	public MutantSpace get_mutant_space() { return this.mutant_space; }
	/**
	 * @return AST of C program
	 */
	public AstTree get_ast_tree() { return this.mutant_space.get_ast_tree(); }
	/**
	 * @return CIR of C program
	 */
	public CirTree get_cir_tree() { return this.mutant_space.get_cir_tree(); }
	/**
	 * @return the number of unique nodes in KDG
	 */
	public int size() { return this.nodes.size(); }
	/**
	 * @return the unique nodes in KDG
	 */
	public Iterable<KillDependenceNode> get_nodes() { return this.nodes.values(); }
	/**
	 * @return the conditions of nodes in the graph
	 */
	public Iterable<SymCondition> get_conditions() { return this.nodes.keySet(); }
	/**
	 * @param condition
	 * @return the node w.r.t. condition or null if not defined
	 */
	public KillDependenceNode get_node(SymCondition condition) {
		if(condition == null) {
			return null;
		}
		else if(this.nodes.containsKey(condition)){
			return this.nodes.get(condition);
		}
		else {
			return null;
		}
	}
	
	/* setters */
	/**
	 * remove all the nodes from the graph
	 */
	protected void clear() { this.nodes.clear(); }
	/**
	 * the unique node w.r.t. the condition or existing one
	 * @param condition
	 * @return
	 * @throws IllegalArgumentException
	 */
	protected KillDependenceNode new_node(SymCondition condition) throws IllegalArgumentException {
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
	
}

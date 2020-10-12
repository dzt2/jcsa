package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.lang.irlang.stmt.CirStatement;
import com.jcsa.jcparse.test.state.CStateContexts;

/**
 * CirMutation propagation tree.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* definitions */
	/** trees where the tree belongs to **/
	private CirMutationTrees trees;
	/** constraints for reaching the root of the sub-tree **/
	private Set<CirConstraint> path_constraints;
	/** the root node of this tree **/
	private CirMutationTreeNode root_node;
	
	/* constructor */
	/**
	 * @param cir_tree
	 * @param mutant
	 * @throws Exception
	 */
	protected CirMutationTree(CirMutationTrees trees, CirMutation 
			cir_mutation, CDominanceGraph dominance_graph) throws Exception {
		if(trees == null)
			throw new IllegalArgumentException("Invalid trees: null");
		else if(cir_mutation == null)
			throw new IllegalArgumentException("Invalid cir_mutation");
		else if(dominance_graph == null)
			throw new IllegalArgumentException("Invalid dominance_graph");
		else {
			this.trees = trees;
			this.path_constraints = CirMutationTreeUtils.common_path_constraints(
					dominance_graph, cir_mutation.get_statement(), trees.get_cir_mutations());
			this.root_node = new CirMutationTreeNode(this, cir_mutation);
			CirMutationTreeUtils.utils.set_tree(this);
			CirMutationTreeUtils.utils.build_trees();
		}
	}
	
	/* getters */
	/**
	 * @return the set of trees where it is created
	 */
	public CirMutationTrees get_trees() { return this.trees; }
	/**
	 * @return the statement where the error propagation starts
	 */
	public CirStatement get_cir_statement() { 
		return this.root_node.get_cir_mutation().get_statement();
	}
	/**
	 * @return the mutation that defines the root node in the tree
	 */
	public CirMutation get_root_mutation() { return this.root_node.get_cir_mutation(); }
	/**
	 * @return the root node of the tree
	 */
	public CirMutationTreeNode get_root() { return this.root_node; }
	/**
	 * @return constraints required for reaching the faulty statement of the root
	 */
	public Iterable<CirConstraint> get_path_constraints() { return this.path_constraints; }
	
	/* implication */
	/**
	 * @param contexts
	 * @return mapping from each node to the concrete interpretation of the mutation at the contexts
	 * @throws Exception
	 */
	public Map<CirMutationTreeNode, CirMutation> interpret(CStateContexts contexts) throws Exception {
		Map<CirMutationTreeNode, CirMutation> results = new HashMap<CirMutationTreeNode, CirMutation>();
		Queue<CirMutationTreeNode> queue = new LinkedList<CirMutationTreeNode>();
		queue.add(this.root_node);
		while(!queue.isEmpty()) {
			CirMutationTreeNode node = queue.poll();
			for(CirMutationTreeNode child : node.get_children()) {
				queue.add(child);
			}
			results.put(node, node.optimize_by(contexts));
		}
		return results;
	}
	
}

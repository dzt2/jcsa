package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.cerr.CirMutation;
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
	protected CirMutationTree(CirMutationTrees trees, int rid, CirMutation 
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
			this.root_node = new CirMutationTreeNode(this, rid, cir_mutation);
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
	
	/* analysis algorithms */
	/**
	 * execute the cir-mutations in the tree nodes against the contexts and update
	 * their concrete mutations in the results.
	 * @param statement
	 * @param contexts
	 * @param results mapping from cir-mutation of each tree node to their concrete values
	 * @throws Exception
	 */
	protected void execute_and_update(CirStatement statement, CStateContexts contexts, 
			Map<CirMutationTreeNode, List<CirMutation>> results) throws Exception {
		if(statement == this.root_node.get_cir_mutation().get_statement()) {
			Map<CirMutationTreeNode, CirMutation> local_results = 
					new HashMap<CirMutationTreeNode, CirMutation>();
			this.root_node.execute_and_update(contexts, local_results);
			for(CirMutationTreeNode tree_node : local_results.keySet()) {
				CirMutation conc_mutation = local_results.get(tree_node);
				results.get(tree_node).add(conc_mutation);
			}
		}
	}
	
}

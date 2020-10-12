package com.jcsa.jcmutest.mutant.cir2mutant.ptree;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirConstraint;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.model.CirMutations;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.dominate.CDominanceGraph;
import com.jcsa.jcparse.lang.irlang.CirTree;

/**
 * CirMutation propagation tree.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* definitions */
	/** new library used to create mutations for nodes in the tree **/
	private CirMutations cir_mutations;
	/** the mutant used to create the propagation tree in cir-code **/
	private Mutant mutant;
	/** roots as well as the path constraints for reaching them **/
	protected Map<CirMutationTreeNode, Set<CirConstraint>> roots;
	
	/* constructor */
	/**
	 * @param cir_tree
	 * @param mutant
	 * @throws Exception
	 */
	private CirMutationTree(CirTree cir_tree, Mutant mutant) throws Exception {
		if(cir_tree == null)
			throw new IllegalArgumentException("Invalid cir_tree: null");
		else if(mutant == null)
			throw new IllegalArgumentException("Invalid mutant as null");
		else {
			this.cir_mutations = new CirMutations(cir_tree);
			this.mutant = mutant;
			this.roots = new HashMap<CirMutationTreeNode, Set<CirConstraint>>();
		}
	}
	
	/* getters */
	/**
	 * @return new library used to create mutations for node in this tree
	 */
	public CirMutations get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the mutant used to create the propagation tree in cir-code
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the mutation seeded in abstract syntax tree for this model
	 */
	public AstMutation get_ast_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return the initial cir-mutations directly caused by executing the
	 * 		   faulty statement.
	 */
	public Iterable<CirMutation> get_initial_cir_mutations() {
		return this.mutant.get_cir_mutations();
	}
	/**
	 * @return the root nodes in the tree as one of the initial cir-mutation of the mutant
	 */
	public Iterable<CirMutationTreeNode> get_roots() { return this.roots.keySet(); }
	/**
	 * @param node
	 * @return the path constraints for reaching the root of the tree node as given
	 * @throws Exception
	 */
	public Set<CirConstraint> get_path_constraint(CirMutationTreeNode node) throws Exception {
		if(node == null || node.get_tree() != this)
			throw new IllegalArgumentException("Undefined tree: " + node);
		else {
			return this.roots.get(node.get_root());
		}
	}
	/**
	 * remove all the nodes from the tree
	 */
	protected void clear() {
		for(CirMutationTreeNode root : this.get_roots()) {
			root.delete();
		}
		this.roots.clear();
	}
	
	/* parser */
	/**
	 * @param cir_tree
	 * @param mutant
	 * @param dominance_graph
	 * @return parse from the mutant and cir-tree to generate propagation tree.
	 * @throws Exception
	 */
	public static CirMutationTree parse(CirTree cir_tree, Mutant mutant, 
			CDominanceGraph dominance_graph) throws Exception {
		CirMutationTree tree = new CirMutationTree(cir_tree, mutant);
		CirMutationTreeUtils.utils.set_tree(tree);
		CirMutationTreeUtils.utils.build_roots(dominance_graph);
		CirMutationTreeUtils.utils.build_trees();
		return tree;
	}
	
}

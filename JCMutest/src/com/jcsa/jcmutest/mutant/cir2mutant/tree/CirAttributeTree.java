package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirKillMutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;

/**
 * The tree describes the reaching-infection-propagation process of killing a mutant
 * in symbolic approach.
 * 
 * @author yukimula
 *
 */
public class CirAttributeTree {
	
	/* definitions */
	private Mutant mutant;
	private Collection<CirMutation> cir_mutations;
	private CirAttributeTreeNode root;
	private Collection<CirAttributeTreeNode> reach_nodes;
	
	/* constructor */
	/**
	 * It creates an empty tree for killing target mutation
	 * @param mutant
	 * @throws Exception
	 */
	private CirAttributeTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.mutant = mutant;
			this.cir_mutations = new HashSet<CirMutation>();
			for(CirMutation cir_mutation : CirMutations.parse(mutant)) {
				this.cir_mutations.add(cir_mutation);
			}
			this.root = CirAttributeTreeNode.new_root(this, mutant);
			this.reach_nodes = new ArrayList<CirAttributeTreeNode>();
		}
	}
	/**
	 * it updates the set of tree nodes representing the reachability of mutation
	 */
	protected void update_reach_nodes() {
		this.reach_nodes.clear();
		for(CirAttributeTreeNode node : this.root.get_post_nodes()) {
			if(node.get_attribute() instanceof CirKillMutant) {
				this.reach_nodes.add(node);
			}
		}
	}
	
	/* getters */
	/** 
	 * @return the mutant for being killed
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return the ast-mutation of the mutant
	 */
	public AstMutation get_ast_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether the mutant is valid with cir-based mutations
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	/**
	 * @return the set of cir-based mutations from the mutant
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the root node of the tree
	 */
	public CirAttributeTreeNode get_root() { return this.root; }
	/**
	 * @return the set of nodes created in the tree
	 */
	public Iterable<CirAttributeTreeNode> get_nodes() { return this.root.get_post_nodes(); }
	/**
	 * @return the set of nodes representing the reachability of mutation
	 */
	public Iterable<CirAttributeTreeNode> get_reach_nodes() { return this.reach_nodes; }
	
}

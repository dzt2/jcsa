package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.cir2mutant.base.CirKillMutant;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStatePath;

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
	private Collection<CirAttributeTreeNode> muta_nodes;
	
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
			this.muta_nodes = new ArrayList<CirAttributeTreeNode>();
		}
	}
	/**
	 * it updates the set of tree nodes representing the reachability of mutation
	 */
	protected void update_muta_nodes() {
		this.muta_nodes.clear();
		for(CirAttributeTreeNode node : this.root.get_post_nodes()) {
			if(node.get_attribute() instanceof CirKillMutant) {
				this.muta_nodes.add(node);
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
	 * @return the set of nodes representing the killing of cir-mutations
	 */
	public Iterable<CirAttributeTreeNode> get_muta_nodes() { return this.muta_nodes; }
	/**
	 * @return the set of leafs under the tree
	 */
	public Iterable<CirAttributeTreeNode> get_leafs() {
		Set<CirAttributeTreeNode> leafs = new HashSet<CirAttributeTreeNode>();
		for(CirAttributeTreeNode node : this.get_nodes()) {
			if(node.is_leaf()) {
				leafs.add(node);
			}
		}
		return leafs;
	}
	
	/* factory */
	public static CirAttributeTree new_tree(Mutant mutant) throws Exception {
		CirAttributeTree tree = new CirAttributeTree(mutant);
		CirAttributeTreeUtil.construct(tree, null);
		return tree;
	}
	public static CirAttributeTree new_tree(Mutant mutant, CDependGraph dependence_graph) throws Exception {
		CirAttributeTree tree = new CirAttributeTree(mutant);
		CirAttributeTreeUtil.construct(tree, dependence_graph);
		return tree;
	}
	public static CirAttributeTree new_tree(Mutant mutant, CStatePath state_path) throws Exception {
		CirAttributeTree tree = new CirAttributeTree(mutant);
		CirAttributeTreeUtil.construct(tree, state_path);
		return tree;
	}
	
	/* evaluation */
	/**
	 * clear the status recording each node in the tree
	 */
	public void clc_states() {
		Iterable<CirAttributeTreeNode> nodes = this.get_nodes();
		for(CirAttributeTreeNode node : nodes) {
			node.get_state().clc();
		}
	}
	/**
	 * perform recursive evaluation from the node to its children
	 * @param node
	 * @param context
	 * @throws Exception
	 */
	private void down_state(CirAttributeTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_state().add(context);
		if(result == null || result.booleanValue()) {
			for(CirAttributeTreeEdge edge : node.get_ou_edges()) {
				this.down_state(edge.get_target(), context);
			}
		}
	}
	/**
	 * @param execution				where the evaluation is performed or null for all infection edges
	 * @param max_infecting_times	the maximal number of times for infection each infection edge in the edge
	 * @param context				used to evaluation
	 * @throws Exception
	 */
	public void add_states(CirExecution execution, int max_infecting_times, SymbolProcess context) throws Exception {
		/* 1. it collects the attribute nodes w.r.t. execution and within limit */
		Set<CirAttributeTreeNode> infect_nodes = new HashSet<CirAttributeTreeNode>();
		for(CirAttributeTreeNode mutant_node : this.muta_nodes) {
			if(mutant_node.get_attribute().get_execution() == execution || execution == null) {
				CirAttributeTreeNode infect_node = mutant_node.get_parent();
				if(infect_node.get_state().number_of_acceptions() < max_infecting_times) {
					infect_nodes.add(infect_node);
				}
			}
		}
		if(infect_nodes.isEmpty()) { return; /* none of nodes for evaluation */ }
		
		/* 2. it captures the previous nodes and evaluate them via static way */
		Set<CirAttributeTreeNode> previous_nodes = new HashSet<CirAttributeTreeNode>();
		for(CirAttributeTreeNode infect_node : infect_nodes) {
			CirAttributeTreeNode pred_node = infect_node.get_parent();
			while(pred_node != null) {
				previous_nodes.add(pred_node);
				pred_node = pred_node.get_parent();
			}
		}
		for(CirAttributeTreeNode pred_node : previous_nodes) {
			pred_node.get_state().add(null);
		}
		
		/* 3. recursively evaluate the nodes from  */
		for(CirAttributeTreeNode infect_node : infect_nodes) {
			this.down_state(infect_node, context);
		}
	}
	/**
	 * update and summarize the annotations in each node
	 * @throws Exception
	 */
	public void sum_states() throws Exception {
		Iterable<CirAttributeTreeNode> nodes = this.get_nodes();
		for(CirAttributeTreeNode node : nodes) {
			node.get_state().sum();
		}
	}
	
}

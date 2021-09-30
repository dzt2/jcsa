package com.jcsa.jcmutest.mutant.cir2mutant.stat;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutations;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * It defines the tree structure of reaching, infecting and propagating a mutant
 * for killing it.
 * 
 * @author yukimula
 *
 */
public class CirMutationTree {
	
	/* definitions */
	private Mutant 						mutant;
	private Set<CirMutation>			cir_mutations;
	private CirMutationTreeNode 		root;
	private Set<CirMutationTreeEdge> 	infection_edges;
	
	/* constructor */
	/**
	 * It creates an empty tree with only one root of reaching program entry
	 * @param mutant
	 * @throws Exception
	 */
	private CirMutationTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant as null");
		}
		else {
			this.mutant = mutant;
			this.cir_mutations = new HashSet<CirMutation>();
			try {
				for(CirMutation cir_mutation : CirMutations.parse(mutant)) {
					this.cir_mutations.add(cir_mutation);
				}
			}
			catch(Exception ex) {
				this.cir_mutations.clear();
			}
			this.root = CirMutationTreeNode.new_root(this);
			this.infection_edges = new HashSet<CirMutationTreeEdge>();
		}
	}
	/**
	 * It updates the set of infection edges in the tree
	 */
	protected void update_infection_edges() {
		Iterator<CirMutationTreeNode> iterator = this.get_nodes();
		this.infection_edges.clear();
		while(iterator.hasNext()) {
			CirMutationTreeNode node = iterator.next();
			if(!node.is_root() && node.get_in_edge().get_edge_type() == CirMutationTreeFlow.infection) {
				this.infection_edges.add(node.get_in_edge());
			}
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntax tree
	 */
	public AstTree get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return C-intermediate represent
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the mutant for killing
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return syntactic definition of the mutant
	 */
	public AstMutation get_ast_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether there exist CIR based mutations in the mutant
	 */
	public boolean has_cir_mutations() { return !this.cir_mutations.isEmpty(); }
	/**
	 * @return the set of mutations generated from mutant in C-intermediate code
	 */
	public Iterable<CirMutation> get_cir_mutations() { return this.cir_mutations; }
	/**
	 * @return the root node for reaching the program entry
	 */
	public CirMutationTreeNode get_root() { return this.root; }
	/**
	 * @return the set of nodes created under this tree
	 */
	public Iterator<CirMutationTreeNode> get_nodes() { return this.root.get_post_nodes(); }
	/**
	 * @return the set of edges linking from state infection conditions to their initial errors
	 */
	public Iterable<CirMutationTreeEdge> get_infection_edges() { return this.infection_edges; }
	/**
	 * @return the set of leafs under the tree.
	 */
	public Iterable<CirMutationTreeNode> get_leafs() {
		Iterator<CirMutationTreeNode> iterator = this.get_nodes();
		Set<CirMutationTreeNode> leafs = new HashSet<CirMutationTreeNode>();
		while(iterator.hasNext()) {
			CirMutationTreeNode node = iterator.next();
			if(node.is_leaf()) {
				leafs.add(node);
			}
		}
		return leafs;
	}
	
	/* factory */
	/**
	 * @param mutant
	 * @param context	either CDependGraph or CStatePath or simply null
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, null);
		return tree;
	}
	/**
	 * @param mutant
	 * @param context	either CDependGraph or CStatePath or simply null
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant, CDependGraph dependence_graph) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, dependence_graph);
		return tree;
	}
	/**
	 * @param mutant
	 * @param context	either CDependGraph or CStatePath or simply null
	 * @return
	 * @throws Exception
	 */
	public static CirMutationTree new_tree(Mutant mutant, CStatePath state_path) throws Exception {
		CirMutationTree tree = new CirMutationTree(mutant);
		CirMutationTreeUtil.construct(tree, state_path);
		return tree;
	}
	
	/* evaluation */
	/**
	 * clear the status recording each node in the tree
	 */
	public void clc_states() {
		Iterator<CirMutationTreeNode> nodes = this.get_nodes();
		while(nodes.hasNext()) {
			CirMutationTreeNode node = nodes.next();
			node.get_data().clc();
		}
	}
	/**
	 * perform recursive evaluation from the node to its children
	 * @param node
	 * @param context
	 * @throws Exception
	 */
	private void down_state(CirMutationTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_data().add(context);
		if(result == null || result.booleanValue()) {
			for(CirMutationTreeEdge edge : node.get_ou_edges()) {
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
		/* 1. collect the state infection edges to be evaluated w.r.t. parameters */
		Set<CirMutationTreeEdge> infection_edges = new HashSet<CirMutationTreeEdge>();
		for(CirMutationTreeEdge infection_edge : this.get_infection_edges()) {
			CirMutationTreeNode infection_node = infection_edge.get_target();
			if(infection_node.get_data().number_of_acceptions() < max_infecting_times) {
				if(execution == null || execution == infection_node.get_execution()) {
					infection_edges.add(infection_edge);
				}
			}
		}
		if(infection_edges.isEmpty()) { return; /* no available edge selected */ }
		
		/* 2. collect the pre_condition nodes reaching the infection edges */
		Set<CirMutationTreeNode> previous_nodes = new HashSet<CirMutationTreeNode>();
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			CirMutationTreeNode previous_node = infection_edge.get_source().get_parent();
			while(previous_node != null) {
				previous_nodes.add(previous_node);
				previous_node = previous_node.get_parent();
			}
		}
		for(CirMutationTreeNode previous_node : previous_nodes) {
			previous_node.get_data().add(null);
		}
		
		/* 3. recursively evaluate the nodes from  */
		for(CirMutationTreeEdge infection_edge : infection_edges) {
			this.down_state(infection_edge.get_source(), context);
		}
	}
	/**
	 * update and summarize the annotations in each node
	 * @throws Exception
	 */
	public void sum_states() throws Exception {
		Iterator<CirMutationTreeNode> nodes = this.get_nodes();
		while(nodes.hasNext()) {
			CirMutationTreeNode node = nodes.next();
			node.get_data().sum();
		}
	}
	
}

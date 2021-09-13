package com.jcsa.jcmutest.mutant.cir2mutant.tree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.jcsa.jcmutest.mutant.Mutant;
import com.jcsa.jcmutest.mutant.cir2mutant.CirMutation;
import com.jcsa.jcmutest.mutant.cir2mutant.muta.CirMutationParsers;
import com.jcsa.jcmutest.mutant.mutation.AstMutation;
import com.jcsa.jcparse.flwa.depend.CDependGraph;
import com.jcsa.jcparse.lang.astree.AstTree;
import com.jcsa.jcparse.lang.irlang.CirTree;
import com.jcsa.jcparse.lang.irlang.graph.CirExecution;
import com.jcsa.jcparse.parse.symbol.process.SymbolProcess;
import com.jcsa.jcparse.test.state.CStatePath;

/**
 * state infection tree
 * @author yukimula
 *
 */
public class CirInfectionTree {
	
	/* attributes */
	/** the syntactic mutation that the tree represent **/
	private Mutant mutant;
	/** the set of state infection(s) introduced by the mutant in CIR code **/
	private List<CirMutation> cir_infections;
	/** the root node of tree to execute program entry **/
	private CirInfectionTreeNode root;
	/** the set of infection edges in the tree for updating **/
	private Set<CirInfectionTreeEdge> infection_edges;
	
	/* constructor */
	/**
	 * create and initialize the state infection tree for a mutant by
	 * creating only a root in the program entry coverage
	 * @param mutant
	 * @throws Exception
	 */
	private CirInfectionTree(Mutant mutant) throws Exception {
		if(mutant == null) {
			throw new IllegalArgumentException("Invalid mutant: null");
		}
		else {
			this.mutant = mutant;
			this.cir_infections = new ArrayList<CirMutation>();
			try {
				Iterable<CirMutation> infections = CirMutationParsers.
						parse(this.get_cir_tree(), mutant.get_mutation());
				for(CirMutation infection : infections) {
					this.cir_infections.add(infection);
				}
			}
			catch(Exception ex) {
				ex.printStackTrace(System.err);
				this.cir_infections.clear();
			}
			this.root = CirInfectionTreeNode.new_root(this);
			this.infection_edges = new HashSet<CirInfectionTreeEdge>();
		}
	}
	
	/* getters */
	/**
	 * @return the abstract syntactic tree of the original program to be seeded
	 */
	public AstTree get_ast_tree() { return this.mutant.get_space().get_ast_tree(); }
	/**
	 * @return the C-intermediate representation of the original version to analyze
	 */
	public CirTree get_cir_tree() { return this.mutant.get_space().get_cir_tree(); }
	/**
	 * @return the syntactic mutation that the tree represent
	 */
	public Mutant get_mutant() { return this.mutant; }
	/**
	 * @return  the syntactic mutation that the tree represent
	 */
	public AstMutation get_mutation() { return this.mutant.get_mutation(); }
	/**
	 * @return whether there exists cir-state infection defined for the mutation
	 */
	public boolean has_cir_infections() { return this.cir_infections.size() > 0; }
	/**
	 * @return the set of state infection(s) introduced by the mutant in CIR code
	 */
	public Iterable<CirMutation> get_cir_infections() { return this.cir_infections; }
	/**
	 * @return the root node of tree to execute program entry
	 */
	public CirInfectionTreeNode get_root() { return this.root; }
	
	/* searching */
	/**
	 * It defines the iteration on nodes from root to leafs using BFS traversal
	 * @author yukimula
	 *
	 */
	private static class CirInfectionNodeIterator implements Iterator<CirInfectionTreeNode> {
		
		/* constructor */
		private Queue<CirInfectionTreeNode> queue;
		private CirInfectionNodeIterator(CirInfectionTree tree) {
			this.queue = new LinkedList<CirInfectionTreeNode>();
			this.queue.add(tree.get_root());
		}

		@Override
		public boolean hasNext() {
			return !this.queue.isEmpty();
		}

		@Override
		public CirInfectionTreeNode next() {
			CirInfectionTreeNode node = this.queue.poll();
			for(CirInfectionTreeEdge ou_edge : node.get_ou_edges()) {
				this.queue.add(ou_edge.get_target());
			}
			return node;
		}
		
	}
	/**
	 * It defines the iteration on edges from root until leafs using the BFS
	 * @author yukimula
	 *
	 */
	private static class CirInfectionEdgeIterator implements Iterator<CirInfectionTreeEdge> {
		
		private Queue<CirInfectionTreeEdge> queue;
		private CirInfectionEdgeIterator(CirInfectionTree tree) {
			this.queue = new LinkedList<CirInfectionTreeEdge>();
			for(CirInfectionTreeEdge ou_edge : tree.get_root().get_ou_edges()) {
				this.queue.add(ou_edge);
			}
		}
		
		@Override
		public boolean hasNext() {
			return !this.queue.isEmpty();
		}
		
		@Override
		public CirInfectionTreeEdge next() {
			CirInfectionTreeEdge in_edge = this.queue.poll();
			for(CirInfectionTreeEdge ou_edge : in_edge.get_target().get_ou_edges()) {
				this.queue.add(ou_edge);
			}
			return in_edge;
		}
		
	}
	/**
	 * @return the iterator to traverse the set of nodes created under this tree
	 */
	public Iterator<CirInfectionTreeNode> get_nodes() { return new CirInfectionNodeIterator(this); }
	/**
	 * @return the iterator to traverse the set of edges created under this tree
	 */
	public Iterator<CirInfectionTreeEdge> get_edges() { return new CirInfectionEdgeIterator(this); }
	/**
	 * @return the set of edges representing the infection relationships from
	 * state infection condition(s) to its initial state errors for mutation.
	 */
	public Iterable<CirInfectionTreeEdge> get_infection_edges() { return this.infection_edges; }
	/**
	 * @return the set of leaf nodes in the tree
	 */
	public Iterable<CirInfectionTreeNode> get_leafs() {
		Iterator<CirInfectionTreeNode> nodes = this.get_nodes();
		Set<CirInfectionTreeNode> leafs = new HashSet<CirInfectionTreeNode>();
		while(nodes.hasNext()) {
			CirInfectionTreeNode node = nodes.next();
			if(node.is_leaf()) {
				leafs.add(node);
			}
		}
		return leafs;
	}
	
	/* evaluation */
	/**
	 * clear the status recording each node in the tree
	 */
	public void clc_states() {
		Iterator<CirInfectionTreeNode> nodes = this.get_nodes();
		while(nodes.hasNext()) {
			CirInfectionTreeNode node = nodes.next();
			node.get_state().clc();
		}
	}
	/**
	 * perform recursive evaluation from the node to its children
	 * @param node
	 * @param context
	 * @throws Exception
	 */
	private void down_state(CirInfectionTreeNode node, SymbolProcess context) throws Exception {
		Boolean result = node.get_state().add(context);
		if(result == null || result.booleanValue()) {
			for(CirInfectionTreeEdge edge : node.get_ou_edges()) {
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
		Set<CirInfectionTreeEdge> infection_edges = new HashSet<CirInfectionTreeEdge>();
		for(CirInfectionTreeEdge infection_edge : this.get_infection_edges()) {
			CirInfectionTreeNode infection_node = infection_edge.get_target();
			if(infection_node.get_state().number_of_acceptions() < max_infecting_times) {
				if(execution == null || execution == infection_node.get_execution()) {
					infection_edges.add(infection_edge);
				}
			}
		}
		if(infection_edges.isEmpty()) { return; /* no available edge selected */ }
		
		/* 2. collect the pre_condition nodes reaching the infection edges */
		Set<CirInfectionTreeNode> previous_nodes = new HashSet<CirInfectionTreeNode>();
		for(CirInfectionTreeEdge infection_edge : infection_edges) {
			CirInfectionTreeNode previous_node = infection_edge.get_source().get_parent();
			while(previous_node != null) {
				previous_nodes.add(previous_node);
				previous_node = previous_node.get_parent();
			}
		}
		for(CirInfectionTreeNode previous_node : previous_nodes) {
			previous_node.get_state().add(null);
		}
		
		/* 3. recursively evaluate the nodes from  */
		for(CirInfectionTreeEdge infection_edge : infection_edges) {
			this.down_state(infection_edge.get_source(), context);
		}
	}
	/**
	 * update and summarize the annotations in each node
	 * @throws Exception
	 */
	public void sum_states() throws Exception {
		Iterator<CirInfectionTreeNode> nodes = this.get_nodes();
		while(nodes.hasNext()) {
			CirInfectionTreeNode node = nodes.next();
			node.get_state().sum();
		}
	}
	/**
	 * update the set of infection edges in this tree
	 */
	protected void set_infection_edges() {
		Iterator<CirInfectionTreeEdge> iterator = this.get_edges();
		this.infection_edges.clear();
		while(iterator.hasNext()) {
			CirInfectionTreeEdge edge = iterator.next();
			if(edge.get_type() == CirInfectionTreeFlow.infection) {
				this.infection_edges.add(edge);
			}
		}
	}
	
	/* factory */
	/**
	 * @param mutant
	 * @return construct the state infection tree for mutant using decidable path to reach
	 * @throws Exception
	 */
	public static CirInfectionTree new_tree(Mutant mutant) throws Exception {
		CirInfectionTree tree = new CirInfectionTree(mutant);
		CirInfectionTreeUtil.construct_tree(tree, null);
		return tree;
	}
	/**
	 * @param mutant
	 * @return construct the state infection tree for mutant using dependence path
	 * @throws Exception
	 */
	public static CirInfectionTree new_tree(Mutant mutant, CDependGraph dependence_graph) throws Exception {
		CirInfectionTree tree = new CirInfectionTree(mutant);
		CirInfectionTreeUtil.construct_tree(tree, dependence_graph);
		return tree;
	}
	/**
	 * @param mutant
	 * @return construct the state infection tree for mutant using dynamic path analysis
	 * @throws Exception
	 */
	public static CirInfectionTree new_tree(Mutant mutant, CStatePath state_path) throws Exception {
		CirInfectionTree tree = new CirInfectionTree(mutant);
		CirInfectionTreeUtil.construct_tree(tree, state_path);
		return tree;
	}
	
}
